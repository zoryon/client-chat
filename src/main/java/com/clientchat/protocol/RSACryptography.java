package com.clientchat.protocol;

import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSACryptography {
    private static RSACryptography instance;
    private PublicKey publicKey;

    private RSACryptography(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public static RSACryptography getInstance(PublicKey publicKey) {
        if (instance == null) {
            instance = new RSACryptography(publicKey);
        }
        return instance;
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM.RSA.toString());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private enum ALGORITHM {
        RSA,
    }
}
