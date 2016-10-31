package no.difi.meldingsutveksling.ptp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import net.logstash.logback.marker.Markers;
import no.difi.ptp.sikkerdigitalpost.HentPersonerForespoersel;
import no.difi.ptp.sikkerdigitalpost.HentPersonerRespons;
import no.difi.ptp.sikkerdigitalpost.Informasjonsbehov;
import no.difi.webservice.support.SoapFaultInterceptorLogger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.FileCopyUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;

public class OppslagstjenesteClient {

    private Configuration conf;

    public OppslagstjenesteClient(Configuration configuration) {
        this.conf = configuration;
    }

    public KontaktInfo hentKontaktInformasjon(String pid) {
        final HentPersonerForespoersel hentPersonerForespoersel = HentPersonerForespoersel.builder()
                .addInformasjonsbehov(Informasjonsbehov.KONTAKTINFO, Informasjonsbehov.SIKKER_DIGITAL_POST, Informasjonsbehov.SERTIFIKAT)
                .addPersonidentifikator(pid)
                .build();

        WebServiceTemplate template = createWebServiceTemplate(HentPersonerRespons.class.getPackage().getName());

        final HentPersonerRespons hentPersonerRespons = (HentPersonerRespons) template.marshalSendAndReceive(conf.url, hentPersonerForespoersel);
        return KontaktInfo.from(hentPersonerRespons);

    }

    private WebServiceTemplate createWebServiceTemplate(String contextPath) {
        AxiomSoapMessageFactory messageFactory = new AxiomSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        WebServiceTemplate template = new WebServiceTemplate(messageFactory);

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        final ClientInterceptor[] interceptors = new ClientInterceptor[2];
        interceptors[0] = createSecurityInterceptor();
        interceptors[1] = SoapFaultInterceptorLogger.withLogMarkers(Markers.append("some marker", ""));

        template.setInterceptors(interceptors);
        return template;
    }

    private ClientInterceptor createSecurityInterceptor() {
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setSecurementActions("Signature Timestamp");
        securityInterceptor.setFutureTimeToLive(120);
        securityInterceptor.setSecurementSignatureKeyIdentifier("DirectReference");
        securityInterceptor.setSecurementUsername(conf.clientAlias);
        securityInterceptor.setSecurementPassword(conf.password);
        securityInterceptor.setValidationActions("Signature Timestamp Encrypt");

        securityInterceptor.setValidationCallbackHandler(new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                Arrays.stream(callbacks).filter(c -> c instanceof WSPasswordCallback).forEach(c -> ((WSPasswordCallback) c).setPassword(conf.password));
            }
        });

        securityInterceptor.setSecurementSignatureAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        securityInterceptor.setSecurementSignatureParts("{}{http://www.w3.org/2003/05/soap-envelope}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp}");
        final Crypto clientCrypto = getCryptoFactoryBean(conf.getClientJksLocation(), conf.password, conf.clientAlias);
        final Crypto serverCrypto = getCryptoFactoryBean(conf.getServerJksLocation(), conf.password, conf.serverAlias);
        securityInterceptor.setSecurementSignatureCrypto(clientCrypto);
        securityInterceptor.setValidationSignatureCrypto(serverCrypto);
        securityInterceptor.setValidationDecryptionCrypto(clientCrypto);
        securityInterceptor.setSecurementEncryptionCrypto(clientCrypto);

        return securityInterceptor;
    }

    private Crypto getCryptoFactoryBean(Resource keystore, String password, String alias) {
        final CryptoFactoryBean cryptoFactoryBean = new CryptoFactoryBean();
        try {
            cryptoFactoryBean.setKeyStoreLocation(keystore);
            cryptoFactoryBean.setKeyStoreType("jks");
            cryptoFactoryBean.setCryptoProvider(Merlin.class);
            cryptoFactoryBean.setKeyStorePassword(password);
            cryptoFactoryBean.setDefaultX509Alias(alias);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create security interceptor due to problems with keystore file...", e);
        }
        try {
            cryptoFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("ERROR", e);
        }
        try {
            return cryptoFactoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException("Could not create CryptoFactoryBean", e);
        }

    }

    /**
     * Parameter object to contain configuration needed to invoke the service oppslagstjeneste
     */
    public static class Configuration {

        private final String url;
        private final String password;
        private final String clientAlias;
        private final String serverAlias;
        private Resource clientJksLocation;
        private Resource serverJksLocation;

        /**
         * Needed to construct OppslagstjenesteClient
         *
         * @param url Url to the Oppslagstjeneste endpoint
         * @param password password for the JKS file
         * @param clientAlias for the JKS entry
         * @param clientJksLocation path to the jks file that contain the client certificate
         * @param serverJksLocation path to the jks file that contain the server certificate
         */
        public Configuration(String url, String password, String clientAlias, String serverAlias, Resource clientJksLocation, Resource serverJksLocation) {
            this.url = url;
            this.password = password;
            this.clientAlias = clientAlias;
            this.serverAlias = serverAlias;
            this.clientJksLocation = clientJksLocation;
            this.serverJksLocation = serverJksLocation;
        }

        public Resource getClientJksLocation() {
            if (clientJksLocation instanceof FileSystemResource) {
                return clientJksLocation;
            }
            try {
                File tmp = File.createTempFile("difi-move", "jks");
                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tmp))) {
                    tmp.deleteOnExit();
                    FileCopyUtils.copy(clientJksLocation.getInputStream(), output);
                }
                return new FileSystemResource(tmp);
            } catch (IOException ex) {
                Logger.getLogger(OppslagstjenesteClient.class.getName()).log(Level.SEVERE, "Can't read keystore", ex);
            }
            return null;
        }

        public Resource getServerJksLocation() {
            if (serverJksLocation instanceof FileSystemResource) {
                return serverJksLocation;
            }
            try {
                File tmp = File.createTempFile("difi-move", "jks");
                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tmp))) {
                    tmp.deleteOnExit();
                    FileCopyUtils.copy(serverJksLocation.getInputStream(), output);
                }
                return new FileSystemResource(tmp);
            } catch (IOException ex) {
                Logger.getLogger(OppslagstjenesteClient.class.getName()).log(Level.SEVERE, "Can't read keystore", ex);
            }
            return null;
        }
    }

}
