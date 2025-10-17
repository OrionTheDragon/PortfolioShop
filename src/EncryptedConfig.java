public class EncryptedConfig {
    public int version = 1;
    public String cipher = CryptoUtil.CIPHER;
    public String aad = "db-config:v1";
    public Kdf kdf = new Kdf();
    public Data data = new Data();

    public static class Kdf {
        public String algo = CryptoUtil.KDF_ALG;
        public int iterations;
        public int keyLen = CryptoUtil.KEY_LEN_BYTES;
        public String salt; // base64
    }
    public static class Data {
        public String iv;  // base64
        public String ct;  // base64 (ciphertext + tag)
        public int tagBits = CryptoUtil.GCM_TAG_BITS;
    }
}