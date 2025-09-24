package Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static Util.Util.*;

/**
 * Банковская карта пользователя.
 * <p>
 * Хранит номер (16 символов), владельца, привязанный {@code userID},
 * срок действия (ММГГ в 4 символах), CVC, виртуальные средства и служебный индекс.
 */
public class Card {
    /** Номер карты (16 символов, по одной цифре на ячейку). */
    private char[] number = new char[16];

    /** Имя держателя карты (Может быть любым, не привязано к имени пользователя). */
    private String owner;

    /** Идентификатор пользователя–владельца карты (ссылка на User {@link User}). */
    private int userID;

    /** Срок действия карты (формат MMYY, 4 символа). */
    private char[] validUntil = new char[4];

    /** CVC-код карты (хранится в виде SHA-512 хеша). */
    private String CVC;

    /** Виртуальный баланс на карте. */
    private double virtualCash;

    /** Внутренний индекс/ID карты (автоприсваиваемый). */
    private int indexValid;

    /** Путь к JSON-файлу с картами. */
    @JsonIgnore
    public static final String PATH_CARD = "Card.json";

    /**
     * Кэш всех карт, считанный из {@link #PATH_CARD}.
     * Используется для быстрых операций без повторного чтения файла.
     */
    @JsonIgnore
    private static List<Card> cardList = safeReadList(PATH_CARD, Card.class);

    /**
     * Конструктор по умолчанию.
     * Ничего не инициализирует дополнительно.
     * Используется для загрузки карт пользователей
     */
    public Card() {}

    /**
     * Конструктор с заполнением всех ключевых полей.
     * В случае ошибок логирует исключение и не изменяет общую логику.
     *
     * @param number       номер карты (16 символов)
     * @param owner        владелец карты (ФИО/имя владельца)
     * @param userID       идентификатор пользователя-владельца
     * @param validUntil   срок действия (ММГГ), 4 символа
     * @param CVC          CVC-код (рекомендовано хранить хэш)
     * @param virtualCash  баланс виртуальных средств
     */
    public Card(char[] number, String owner, int userID, char[] validUntil, String CVC, double virtualCash) {
        try {
            setNumber(number);
            setOwner(owner);
            setUserID(userID);
            setValidUntil(validUntil);
            setCVC(CVC);
            setVirtualCash(virtualCash);
            out("Data/Card.java: Карта успешно создана, ID держателя карты: " + getUserID() + ", ID карты: " + getIndexValid());
        }
        catch (Exception e) {
            out("Data/Card.java: Ошибка при создании карты: " + e.getMessage());
        }
    }

    public double getVirtualCash() {
        return virtualCash;
    }
    public void setVirtualCash(double virtualCash) {
        this.virtualCash = virtualCash;
    }
    public char[] getNumber() {
        return number;
    }
    public void setNumber(char[] number) {
        this.number = number;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public char[] getValidUntil() {
        return validUntil;
    }
    public void setValidUntil(char[] validUntil) {
        this.validUntil = validUntil;
    }
    public String getCVC() {
        return CVC;
    }
    public void setCVC(String CVC) {
        this.CVC = CVC;
    }
    public int getIndexValid() {
        return indexValid;
    }
    public void setIndexValid(int indexValid) {
        this.indexValid = indexValid;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public static List<Card> getCardList() {
        return cardList;
    }
    public static void setCardList(List<Card> cardList) {
        Card.cardList = cardList;
    }

    /**
     * Хэширует CVC алгоритмом SHA-512 и возвращает hex-строку (нижний регистр).
     * <p>
     * Кодировка исходной строки — UTF-8.
     * При недоступности алгоритма исключение логируется и пробрасывается как {@link RuntimeException}.
     *
     * @param CVC исходный CVC-код
     * @return хэш SHA-512 в шестнадцатеричном виде
     * @throws RuntimeException если алгоритм SHA-512 недоступен в среде выполнения
     */
    public static String hashCVC(String CVC) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] encodedHash = digest.digest(CVC.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hash : encodedHash) {
                String hex = Integer.toHexString(0xff & hash);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            out("Data/Card.java: CVC успешно закодирован");
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            out("Data/Card.java: Не найден алгоритм SHA-512: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}