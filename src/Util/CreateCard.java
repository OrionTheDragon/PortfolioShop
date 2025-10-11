package Util;

import Data.Card;
import Data.User;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static Util.Util.*;
import static Data.Card.*;
import static Util.Util.clearRoot;

/**
 * Построитель UI для привязки банковской карты к пользователю.
 * <p>
 * Создаёт форму ввода (номер, владелец, срок, CVC), валидирует её и сохраняет карту.
 * Внутри методов и обработчиков событий добавлены try/catch для устойчивости UI.
 */
public class CreateCard {

    /**
     * Собирает экран создания/привязки карты и навешивает обработчики.
     *
     * @param root корневой контейнер, в который будет помещён UI формы
     * @param tab  вкладка, из-под которой был вызов (для возврата)
     * @param user текущий пользователь, к которому привязывается карта
     */
    public static void createCard(VBox root, Tab tab, User user) {
        try {
            out("Util/CreateCard.java: Началось создание бокса привязывания банковской карты");

            out("Util/CreateCard.java: ID user: " + user.getUserID());

            // Боксы
            /** Контейнер для ввода номера карты (16 односимвольных полей с разделителями). */
            HBox hBoxNum = new HBox(10);
            /** Контейнер для ввода срока действия карты (формат MM/YY, 4 поля по 1 символу). */
            HBox hBoxValidUtil = new HBox(10);
            /** Контейнер для ввода CVC (3 поля по 1 символу). */
            HBox hBoxCVC = new HBox(10);

            // Кнопки
            /** Кнопка сохранения введённых реквизитов карты (создание/апдейт и запись в файл). */
            Button saveCard = new Button("Сохранить карту");
            /** Кнопка возврата к предыдущему экрану (настройки/ЛК). */
            Button back = new Button("Назад");

            // Этикетки
            /** Набор подписей к полям ввода: номер, владелец, срок действия, CVC (по порядку). */
            Label[] labelInfoCard = {
                    makeLabel("Введите номер карты"),
                    makeLabel("Владелец карты"),
                    makeLabel("Действует до"),
                    makeLabel("Введите CVC карты")
            };

            // Поля ввода
            /** 16 односимвольных полей для набора номера карты по одной цифре. */
            TextField[] textAreasNumber = new TextField[16];
            /** Поле для ввода имени владельца карты. */
            TextField textOwner = new TextField();
            /** 4 односимвольных поля для ввода срока действия (MMYY). */
            TextField[] textValidUtil = new TextField[4];
            /** 3 односимвольных поля для ввода CVC. */
            TextField[] textCVC = new TextField[3];

            // Редактирование полей
            /** Базовая настройка поля владельца: размеры/высота/ширина. */
            editTextFiled(textOwner);
            textOwner.setPrefColumnCount(1);
            textOwner.setMaxWidth(450);
            textOwner.setMaxHeight(25);


            for (int i = 0; i < textAreasNumber.length; i++) {
                out("Util/CreateCard.java: Создание бокса номера карты");
                TextField text = editTextFiled(new TextField());
                int index = i;

                eventFilter(text, textAreasNumber, index);

                textAreasNumber[i] = text;
                hBoxNum.getChildren().add(text);

                if ((i + 1) % 4 == 0 && i != 15) {
                    Label separator = new Label(" — ");
                    separator.setFont(Font.font(16));
                    hBoxNum.getChildren().add(separator);
                }
            }

            for (int i = 0; i < textValidUtil.length; i++) {
                out("Util/CreateCard.java: Создание бокса годности карты");
                TextField text = editTextFiled(new TextField());
                int index = i;

                eventFilter(text, textValidUtil, index);

                textValidUtil[i] = text;
                hBoxValidUtil.getChildren().add(text);

                if ((i + 1) % 2 == 0 && i != 3) {
                    Label separator = new Label("/");
                    separator.setFont(Font.font(16));
                    hBoxValidUtil.getChildren().add(separator);
                }
            }

            for (int i = 0; i < textCVC.length; i++) {
                out("Util/CreateCard.java: Создание бокс CVC");
                TextField text = editTextFiled(new TextField());
                int index = i;

                eventFilter(text, textCVC, index);

                textCVC[i] = text;
                hBoxCVC.getChildren().add(text);
            }

            clearRoot(root);

            centeringBox(hBoxNum);
            centeringBox(hBoxValidUtil);
            centeringBox(hBoxCVC);

            root.getChildren().addAll(labelInfoCard[0],
                    hBoxNum,
                    labelInfoCard[1],
                    textOwner,
                    labelInfoCard[2],
                    hBoxValidUtil,
                    labelInfoCard[3],
                    hBoxCVC,
                    saveCard,
                    back
            );

            saveCard.setOnAction(_ -> {
                try {
                    boolean[] flag = {checkText(textAreasNumber),
                            checkText(textOwner),
                            checkText(textValidUtil),
                            checkText(textCVC)
                    };

                    for (boolean b : flag) {
                        if (!b) {
                            errMess(root, "Не все поля заполнены!");
                            return;
                        }
                    }

                    char[] numbersCard = getCharArrayFromTextFields(textAreasNumber);

                    int userID = user.getUserID();
                    char[] valid = getCharArrayFromTextFields(textValidUtil);

                    // valid — это ровно 4 цифры: MMyy (например "0327")
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMyy");
                    YearMonth now = YearMonth.now();

                    String entered = new String(valid); // если valid — char[] из 4 цифр

                    YearMonth cardYm;
                    try {
                        cardYm = YearMonth.parse(entered, fmt);
                    }
                    catch (DateTimeParseException e) {
                        errMess(root, "Неверный формат срока карты (ожидается ММ/ГГ)");
                        out("Util/CreateCard.java: Некорректная дата: " + entered + " — " + e.getMessage());
                        return;
                    }

                    if (cardYm.isBefore(now)) {
                        errMess(root, "Карточка просрочена");
                        out("Util/CreateCard.java: Истёк срок: " + cardYm.format(fmt) + ", текущая: " + now.format(fmt));
                        return;
                    }

                    String cvc = "";
                    for (TextField textField : textCVC) {
                        cvc += String.valueOf(textField.getText().trim());
                    }

                    String hashedCvc = Card.hashCVC(cvc);
                    double virCash = Math.round((1000 + Math.random() * (1_000_000 - 1000)) * 100.0) / 100.0;
                    String owner = textOwner.getText().trim();
                    Card newCard = new Card(numbersCard, owner, userID, valid, hashedCvc, virCash);
                    ArrayList<Card> card = user.getCard();

                    if (card == null) {
                        card = new ArrayList<>();
                        user.setCard(card);
                    }

                    int idx = -1;
                    for (int i = 0; i < card.size(); i++) {
                        Card c = card.get(i);
                        if (java.util.Arrays.equals(c.getNumber(), numbersCard)) {
                            idx = i; // карта с таким номером уже есть — обновим
                            break;
                        }
                    }

                    if (idx >= 0) {
                        card.set(idx, newCard);
                    }
                    else {
                        card.add(newCard);
                    }

                    append(Path.of(PATH_CARD), newCard);

                    user.getPa().setCard(user.getCard());

                    Label success = new Label("Карта успешно создана");
                    success.setFont(Font.font(String.valueOf(FontWeight.BOLD), 16));
                    success.setTextFill(Color.GREEN);
                    success.setAlignment(Pos.CENTER);
                    success.setMaxWidth(Double.MAX_VALUE);

                    clearRoot(root);
                    out("Util/CreateCard.java: Очистили root");
                    root.getChildren().add(success);
                    out("Util/CreateCard.java: Добавили success");
                    user.getPa().setTittle();
                    // Используем PauseTransition, чтобы не блокировать JavaFX UI поток
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> {
                        try {
                            out("Util/CreateCard.java: Вышли из сна");
                            back.fire();
                        }
                        catch (Exception ex) {
                            out("Util/CreateCard.java: Ошибка в завершении задержки: " + ex.getMessage());
                        }
                    });
                    out("Util/CreateCard.java: Сон");
                    pause.play(); // запускаем задержку
                }
                catch (Exception e) {
                    out("Util/CreateCard.java: Ошибка при сохранении карты: " + e.getMessage());
                    errMess(root, "Не удалось сохранить карту: " + e.getMessage());
                }
            });

            back.setOnAction(_ -> {
                try {
                    out("Util/CreateCard.java: Возвращаемся в settingsMenu");
                    user.getPa().rebuildCardsBox();
                    user.getPa().getSettingsMenu().settingsMenu(root, tab, user.getPa());
                }
                catch (Exception e) {
                    out("Util/CreateCard.java: Ошибка при возврате: " + e.getMessage());
                    errMess(root, "Не удалось вернуться: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            out("Util/CreateCard.java: Критическая ошибка при создании UI: " + e.getMessage());
            try {
                errMess(root, "Ошибка при создании формы карты: " + e.getMessage());
            }
            catch (Exception ignored) {
                // если UI ещё не готов — ограничимся логом
            }
        }
    }

    /**
     * Центрирует переданный контейнер по горизонтали.
     *
     * @param hBox контейнер для центровки
     */
    public static void centeringBox(HBox hBox) {
        try {
            hBox.setAlignment(Pos.CENTER);
        }
        catch (Exception e) {
            out("Util/CreateCard.java: Ошибка центровки: " + e.getMessage());
        }
    }
}