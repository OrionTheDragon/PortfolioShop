package Ui;

import Data.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static Util.Util.*;

public class Password {
    /**
     * Хэширует пароль алгоритмом SHA-512 и возвращает результат в виде шестнадцатеричной строки (нижний регистр, без разделителей).
     *
     * <p>Особенности:
     * <ul>
     *   <li>Кодировка исходной строки — UTF-8.</li>
     *   <li>При недоступности алгоритма SHA-512 все исключения логируются и пробрасываются
     *       как {@link RuntimeException} </li>
     * </ul>
     *
     * @param password исходный пароль (строка UTF-8)
     * @return хэш SHA-512 в hex-формате (нижний регистр)
     * @throws RuntimeException если алгоритм SHA-512 недоступен
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hash : encodedHash) {
                String hex = Integer.toHexString(0xff & hash);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            out("Ui/Password.java: Пароль успешно закодирован");
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            out("Ui/Password.java: Не найден алгоритм SHA-512: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Проверяет логин по имени и паролю (строка пароля хэшируется перед сравнением).
     *
     * <p>Безопасность:
     * <ul>
     *   <li>Валидирует вход (null/blank) — мгновенный отказ без ошибок.</li>
     *   <li>Все исключения внутри — перехватываются и логируются; метод возвращает {@code false}.</li>
     * </ul>
     *
     * @param name имя пользователя (логин)
     * @param pass исходный пароль (будет преобразован в хэш)
     * @return {@code true}, если найден пользователь с совпадающими именем и хэшем пароля; иначе {@code false}
     */
    public static boolean examLogin(String name, String pass) {
        try {
            if (name == null || pass == null || name.isBlank()) {
                return false;
            }

            String hashed = hashPassword(pass);

            return checkInFile(Main.PATH, User.class, u ->
                    name.equals(u.getName()) && hashed.equals(u.getPassword()));
        }
        catch (Exception e) {
            out("Ui/Password.java: Ошибка в examLogin(name, pass): " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет пароль для уже известного пользователя.
     *
     * <p>Безопасность:
     * <ul>
     *   <li>Все исключения (например, {@code user == null}) — перехватываются и логируются; метод возвращает {@code false}.</li>
     * </ul>
     *
     * @param pass исходный пароль (будет преобразован в хэш)
     * @param user пользователь, с которым сравнивается пароль
     * @return {@code true}, если хэш пароля совпадает с сохранённым у пользователя; иначе {@code false}
     */
    public static boolean examLogin(String pass, User user) {
        try {
            pass = hashPassword(pass);
            if (pass.equals(user.getPassword())) {
                out("Ui/Password.java: Пароль верный");
                return true;
            }
            else {
                out("Ui/Password.java: Пароль неверный");
                return false;
            }
        }
        catch (Exception e) {
            out("Ui/Password.java: Ошибка в examLogin(pass, user): " + e.getMessage());
            return false;
        }
    }
}
