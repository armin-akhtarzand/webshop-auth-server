package se.iths.armin.webshopauthserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@SpringBootApplication
public class WebshopAuthServerApplication {

    public static void main(String[] args) throws Exception {
        //printJwtKeys();
        SpringApplication.run(WebshopAuthServerApplication.class, args);

    }


    private static void printJwtKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        String privateKey = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());
        System.out.println("Private key att kopiera:");
        System.out.println(privateKey);
        System.out.println("Public key att kopiera:");
        System.out.println(publicKey);
    }

}
