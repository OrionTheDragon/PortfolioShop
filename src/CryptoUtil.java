public final class CryptoUtil {
    static final String KDF_ALG = "PBKDF2WithHmacSHA256";
    static final String CIPHER = "AES/GCM/NoPadding";
    static final int GCM_TAG_BITS = 128;
    static final int KEY_LEN_BYTES = 32;   // 256-bit AES
    static final int SALT_LEN = 16;
    static final int IV_LEN = 12;
    static final int ITERATIONS = 200_000; // можно больше, если устраивает скорость

    
}
