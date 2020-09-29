package no.difi.meldingsutveksling.serviceregistry.config;

import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import no.difi.move.common.config.KeystoreProperties;
import no.difi.move.common.oauth.KeystoreHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.cert.CertificateEncodingException;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class SRConfig implements WebMvcConfigurer {

    @Bean
    HystrixContextInterceptor hystrixContextInterceptor() {
        return new HystrixContextInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hystrixContextInterceptor());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public KeystoreHelper keystoreHelper(ServiceregistryProperties srProps) {
        KeystoreProperties props = new KeystoreProperties();
        props.setAlias(srProps.getSign().getKeystore().getAlias());
        props.setEntryPassword(srProps.getSign().getKeystore().getPassword());
        props.setStorePassword(srProps.getSign().getKeystore().getPassword());
        props.setType(srProps.getSign().getKeystore().getType());
        props.setLocation(srProps.getSign().getKeystore().getPath());

        return new KeystoreHelper(props);
    }

    @Bean
    public RSAKey rsaKey(KeystoreHelper keystoreHelper) throws CertificateEncodingException, JOSEException {
        Base64 encodedCert = Base64.encode(keystoreHelper.getX509Certificate().getEncoded());
        return new RSAKey.Builder((RSAPublicKey) keystoreHelper.getKeyPair().getPublic())
                .x509CertChain(Lists.newArrayList(encodedCert))
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyIDFromThumbprint()
                .build();
    }
}
