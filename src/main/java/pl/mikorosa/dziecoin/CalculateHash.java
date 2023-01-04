package pl.mikorosa.dziecoin;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class CalculateHash {
    public static String sha256sum(String input) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        byte[] sha256sum = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        for(byte b : sha256sum) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String md5sum(String input) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        byte[] md5sum = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        for(byte b : md5sum) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
