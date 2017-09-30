package com.nohana.http;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyMaterial {

    private static final Logger logger = LogManager.getLogger(KeyMaterial.class);

    private KeyStore keyStore;
    private char[] password;

    public static KeyMaterial create(byte[] cert,char[] password){
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = new ByteArrayInputStream(cert);
            keyStore.load(inputStream,password);

            return create(keyStore,password);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.printf(Level.ERROR,"Failed to create keyStore :%s", ExceptionUtils.getMessage(e));
            return null;
        }

    }

    private static KeyMaterial create(KeyStore keyStore,char[] password){
        KeyMaterial keyMaterial = new KeyMaterial();
        keyMaterial.keyStore = keyStore;
        keyMaterial.password = password;
        return keyMaterial;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public char[] getPassword() {
        return password;
    }
}
