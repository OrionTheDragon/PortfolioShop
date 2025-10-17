import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public final class CryptoUtil {
    static final String KDF_ALG = "PBKDF2WithHmacSHA256";
    static final String CIPHER = "AES/GCM/NoPadding";
    static final int GCM_TAG_BITS = 128;
    static final int KEY_LEN_BYTES = 32;   // 256-bit AES
    static final int SALT_LEN = 16;
    static final int IV_LEN = 12;
    static final int ITERATIONS = 200_000; // можно больше, если устраивает скорость

    static byte[] rnd(int len) {
        byte[] b = new byte[len];
        new SecureRandom().nextBytes(b);
        return b;
    }

    static SecretKey deriveKey(char[] pass, byte[] salt, int iterations) throws Exception {
        SecretKeyFactory f = SecretKeyFactory.getInstance(KDF_ALG);
        PBEKeySpec spec = new PBEKeySpec(pass, salt, iterations, KEY_LEN_BYTES * 8);
        return new SecretKeySpec(f.generateSecret(spec).getEncoded(), "AES");
    }

    public static class EncBundle {
        public final byte[] salt, iv, ct; public final int iterations;
        public EncBundle(byte[] salt, byte[] iv, byte[] ct, int iterations) {
            this.salt = salt; this.iv = iv; this.ct = ct; this.iterations = iterations;
        }
    }

    public static EncBundle encrypt(byte[] plaintext, char[] pass, byte[] aad) throws Exception {
        byte[] salt = rnd(SALT_LEN), iv = rnd(IV_LEN);
        SecretKey key = deriveKey(pass, salt, ITERATIONS);
        Cipher c = Cipher.getInstance(CIPHER);
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        if (aad != null) c.updateAAD(aad);
        byte[] ct = c.doFinal(plaintext);
        return new EncBundle(salt, iv, ct, ITERATIONS);
    }

    public static byte[] decrypt(byte[] ct, byte[] iv, byte[] salt, int iterations, char[] pass, byte[] aad) throws Exception {
        SecretKey key = deriveKey(pass, salt, iterations);
        Cipher c = Cipher.getInstance(CIPHER);
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        if (aad != null) c.updateAAD(aad);
        return c.doFinal(ct);
    }

    public static String b64(byte[] b){ return Base64.getEncoder().encodeToString(b); }
    public static byte[] b64d(String s){ return Base64.getDecoder().decode(s); }
}
