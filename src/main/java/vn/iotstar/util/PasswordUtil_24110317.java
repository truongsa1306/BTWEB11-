package vn.iotstar.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Hash mat khau bang PBKDF2-HmacSHA256 + salt ngau nhien. Dinh dang luu: pbkdf2$iter$salt$hash. */
public final class PasswordUtil_24110317 {
    private static final int ITERATIONS = 65536;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil_24110317() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return format(ITERATIONS, salt, derive(password, salt, ITERATIONS));
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String format(int iterations, byte[] salt, byte[] hash) {
        Base64.Encoder enc = Base64.getEncoder();
        return "pbkdf2$" + iterations + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(hash);
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Khong the hash mat khau", e);
        }
    }

    /** Tien ich sinh hash cho du lieu mau: java ... PasswordUtil_24110317 <password> */
    public static void main(String[] args) {
        for (String a : args) {
            System.out.println(a + " -> " + hash(a));
        }
    }
}
