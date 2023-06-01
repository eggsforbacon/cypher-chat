package co.edu.icesi.model;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.*;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class DiffieHellman {

    private PrivateKey privateKey;
    private PublicKey  publicKey;
    private PublicKey  receivedPublicKey;
    private byte[] secretKey;
    private String secretMessage;
    public byte[] encryptMessage(String message) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = generateIV();

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            System.out.println("Encrypted " + new String (encryptedMessage));

            byte[] encryptedData = new byte[iv.length + encryptedMessage.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(encryptedMessage, 0, encryptedData, iv.length, encryptedMessage.length);

            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    public void generateCommonSecretKey() {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, true);
//            secretKey = keyAgreement.generateSecret();

            byte[] sharedSecret = keyAgreement.generateSecret();

            // Truncate or pad the shared secret to 32 bytes
            byte[] secretKeyBytes = new byte[32];
            System.arraycopy(sharedSecret, 0, secretKeyBytes, 0, Math.min(sharedSecret.length, secretKeyBytes.length));
            secretKey = secretKeyBytes;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey  = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String decryptMessage(String message) {
        if (message.contains("ACK ")){
            return message;
        }
        try {
            System.out.println(message);
            byte[] messageArray = message.getBytes();
            byte[] iv = Arrays.copyOfRange(messageArray, 0, 16);
            byte[] encryptedMessage = Arrays.copyOfRange(messageArray, 16, messageArray.length);


            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));

            secretMessage = new String(cipher.doFinal(encryptedMessage));
            return secretMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PublicKey parsePublicKey(String publicKey) {
        PublicKey pubKey = null;
        try {
            byte[] publicBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pubKey;
    }

    public void receivePublicKeyFrom(String publicKey) throws IOException {
        receivedPublicKey = parsePublicKey(publicKey);
        generateCommonSecretKey();
    }
}
