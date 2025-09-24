package Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import Data.Cabinet.PA;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

import static Util.Util.*;
import static Data.Card.*;

/**
 * Модель пользователя.
 * <p>
 * Содержит основные данные профиля (имя, возраст, наличные, пароль-хэш),
 * а также связанные сущности: личный кабинет {@link PA} и список карт {@link Card}.
 * <br>
 * Конструкторы и методы логируют ключевые действия через {@code out(...)}.
 */
public class User {

    /** Уникальный ID пользователя */
    private int userID;
    /** Имя пользователя */
    private String name;
    /** Возраст пользователя */
    private byte age;
    /** Наличные деньги пользователя */
    private double cash;
    /** Пароль пользователя, закодирован с помощью SHA-512 */
    private String password;

    /** Личный кабинет пользователя */
    @JsonIgnore
    private PA pa;

    /** Список карт пользователя */
    @JsonIgnore
    private ArrayList<Card> card = new ArrayList<>();

    /**
     * Конструктор по умолчанию.
     * Логирует успешную загрузку экземпляра.
     */
    public User() {
        try {
            out("Data/User.java: Юзер успешно загружен");
        }
        catch (Exception e) {
            out("Data/User.java: Ошибка логирования в конструкторе по умолчанию: " + e.getMessage());
        }
    }

    /**
     * Конструктор c инициализацией полей.
     *
     * @param userID     индекс пользователя (внутренний ID/валидатор)
     * @param name       имя пользователя
     * @param password   хэш пароля (или исходный, если так используется на уровне вызова)
     * @param age        возраст
     * @param cash       количество наличных средств
     */
    public User(int userID, String name, String password, byte age, double cash) {
        try {
            setUserID(userID);
            setName(name);
            setPassword(password);
            setAge(age);
            setCash(cash);
            out("Data/User.java: Юзер успешно создан: " + getName() + ", индекс: " + getUserID());
        }
        catch (Exception e) {
            out("Data/User.java: Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        out("Data/User.java: Имя установлено: " + name);
    }
    public byte getAge() {
        return age;
    }
    public void setAge(byte age) {
        this.age = age;
    }
    public double getCash() {
        return cash;
    }
    public void setCash(double cash) {
        this.cash = cash;
    }
    public ArrayList<Card> getCard() {
        return card;
    }
    public void setCard(ArrayList<Card> card) {
        this.card.clear();
        if (card != null) this.card.addAll(card);
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public PA getPa() {
        return pa;
    }
    public void setPa(PA pa) {
        this.pa = pa;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
        out("Data/User.java: Индекс установлен: " + userID);
    }

    /**
     * Загружает из файла все карты и привязывает к текущему пользователю только его карты.
     * <p>
     * Логирует найденные карты и итоговое количество карт, назначенных пользователю.
     *
     * @param u пользователь, для которого подгружаются карты
     * @return количество загруженных карт пользователя (0 в случае ошибок)
     */
    public int loadCards(User u) {
        try {
            for (Card c : getCardList()) {
                out("Data/User.java: Загружена карта: ID пользователя: " + c.getUserID() + ", ID Карты: " + c.getIndexValid());
            }

            ArrayList<Card> mine = getCardList().stream()
                    .filter(c -> c.getUserID() == u.getUserID())
                    .collect(Collectors.toCollection(ArrayList::new));

            u.setCard(mine);
            Optional.ofNullable(u.getPa()).ifPresent(pa -> pa.setCard(mine));

            out("Data/User.java: Загружено карт пользователя " + u.getName() + ": " + mine.size());
            return mine.size();
        }
        catch (Exception e) {
            out("Data/User.java: Ошибка при загрузке карт: " + e.getMessage());
            return 0;
        }
    }
}
