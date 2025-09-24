package Util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;

/**
 * Утилиты визуальной отладки JavaFX-лейаута.
 * <p>
 * {@link #debugBordersFull(Node)} подсвечивает сам узел (красная рамка/фон) и
 * визуализирует его {@code margin} через обёртку (синяя пунктирная рамка и padding),
 * чтобы наглядно увидеть занимаемое пространство.
 * <br>Повторная обёртка предотвращается через служебный флаг в {@code Node.getProperties()}.
 */
public final class DebugFX {
    /** Ключ для пометки, что узел уже был обёрнут отладочной обёрткой. */
    private static final String KEY_WRAPPED = "debug_wrapped";

    /**
     * Подсветить бордер/фон и визуализировать margin. Возвращает фактический узел в дереве
     * (обёртка или исходник, если обёртка не нужна/невозможна).
     *
     * @param node исходный узел
     * @return узел, который теперь находится в дереве (обёртка либо сам {@code node})
     */
    public static Node debugBordersFull(Node node) {
        try {
            if (node.getProperties().getOrDefault(KEY_WRAPPED, false).equals(Boolean.TRUE)) {
                return node.getParent() != null ? node.getParent() : node; // уже обёрнут
            }

            // 1) Подсветка самой ноды: border + background
            node.setStyle(
                    "-fx-border-color: red;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-style: solid;" +
                            "-fx-background-color: rgba(255,0,0,0.10);"
            );

            Parent parent = node.getParent();
            Insets margin = getMarginFromParent(parent, node);

            // Если margin не задан — просто пометим и выйдем
            if (isZero(margin) || parent == null) {
                node.getProperties().put(KEY_WRAPPED, true);
                return node;
            }

            // 2) Обёртка, которая визуализирует margin как padding
            StackPane wrapper = new StackPane(node);
            wrapper.setPadding(margin); // визуализация margin
            wrapper.setStyle(
                    "-fx-border-color: blue;" +           // рамка вокруг «зоны margin»
                            "-fx-border-width: 1;" +
                            "-fx-border-style: dashed;" +
                            "-fx-background-color: rgba(0,0,255,0.05);" // лёгкая подсветка зоны margin
            );

            // 3) Подмена в родителе + перенос grow/align по возможности
            replaceInParent(parent, node, wrapper);

            // 4) Сброс margin у новой ноды в родителе, чтобы не удваивать отступ
            setMarginOnParent(parent, wrapper, Insets.EMPTY);

            node.getProperties().put(KEY_WRAPPED, true);
            return wrapper;
        }
        catch (Exception e) {
            Util.out("Util/DebugFX.java: Ошибка в debugBordersFull: " + e.getMessage());
            return node;
        }
    }

    // ---------- helpers ----------

    /**
     * Проверяет, что инсет равен нулю либо отсутствует.
     * @param in инсет
     * @return true, если нулевой/пустой
     */
    private static boolean isZero(Insets in) {
        try {
            return in == null || (in.getTop()==0 && in.getRight()==0 && in.getBottom()==0 && in.getLeft()==0);
        }
        catch (Exception e) {
            Util.out("Util/DebugFX.java: Ошибка в isZero: " + e.getMessage());
            return true;
        }
    }

    /**
     * Возвращает {@code margin} дочернего узла у конкретного типа родителя.
     *
     * @param parent родитель
     * @param child  потомок
     * @return Insets margin или {@link Insets#EMPTY}, если не поддерживается
     */
    private static Insets getMarginFromParent(Parent parent, Node child) {
        try {
            if (parent instanceof VBox) {
                return VBox.getMargin(child);
            }
            if (parent instanceof HBox) {
                return HBox.getMargin(child);
            }
            if (parent instanceof FlowPane) {
                return FlowPane.getMargin(child);
            }
            if (parent instanceof TilePane) {
                return TilePane.getMargin(child);
            }
            if (parent instanceof StackPane) {
                return StackPane.getMargin(child);
            }
            if (parent instanceof GridPane) {
                return GridPane.getMargin(child);
            }
            if (parent instanceof BorderPane) {
                return BorderPane.getMargin(child);
            }
            // AnchorPane/Pane — margin не поддерживают
            return Insets.EMPTY;
        }
        catch (Exception e) {
            Util.out("Util/DebugFX.java: Ошибка в getMarginFromParent: " + e.getMessage());
            return Insets.EMPTY;
        }
    }

    /**
     * Устанавливает {@code margin} новой ноде у конкретного типа родителя.
     *
     * @param parent родитель
     * @param child  потомок
     * @param insets отступы
     */
    private static void setMarginOnParent(Parent parent, Node child, Insets insets) {
        try {
            if (parent instanceof VBox){
                VBox.setMargin(child, insets);
            }
            else if (parent instanceof HBox) {
                HBox.setMargin(child, insets);
            }
            else if (parent instanceof FlowPane) {
                FlowPane.setMargin(child, insets);
            }
            else if (parent instanceof TilePane) {
                TilePane.setMargin(child, insets);
            }
            else if (parent instanceof StackPane) {
                StackPane.setMargin(child, insets);
            }
            else if (parent instanceof GridPane) {
                GridPane.setMargin(child, insets);
            }
            else if (parent instanceof BorderPane) {
                BorderPane.setMargin(child, insets);
            }
        }
        catch (Exception e) {
            Util.out("Util/DebugFX.java: Ошибка в setMarginOnParent: " + e.getMessage());
        }
    }

    /**
     * Заменяет {@code oldNode} в родителе на {@code newNode}, стараясь сохранить позицию и grow/align.
     *
     * @param parent  родитель
     * @param oldNode исходный узел
     * @param newNode новый узел
     */
    private static void replaceInParent(Parent parent, Node oldNode, Node newNode) {
        try {
            if (parent instanceof Pane pane) {
                int idx = pane.getChildren().indexOf(oldNode);
                if (idx >= 0) {
                    pane.getChildren().set(idx, newNode);
                }
                // перенос V/H grow
                if (parent instanceof VBox) {
                    Priority grow = VBox.getVgrow(oldNode);
                    if (grow != null) VBox.setVgrow(newNode, grow);
                }
                else if (parent instanceof HBox) {
                    Priority grow = HBox.getHgrow(oldNode);
                    if (grow != null) HBox.setHgrow(newNode, grow);
                }
            }
            else if (parent instanceof BorderPane bp) {
                if (bp.getTop() == oldNode) {
                    bp.setTop(newNode);
                }
                else if (bp.getBottom() == oldNode) {
                    bp.setBottom(newNode);
                }
                else if (bp.getLeft() == oldNode) {
                    bp.setLeft(newNode);
                }
                else if (bp.getRight() == oldNode) {
                    bp.setRight(newNode);
                }
                else if (bp.getCenter() == oldNode) {
                    bp.setCenter(newNode);
                }
                BorderPane.setAlignment(newNode, BorderPane.getAlignment(oldNode));
            }
            else {
                // Родитель не поддержан: попробовать общий случай для Pane
                if (parent instanceof Pane p) {
                    int idx = p.getChildren().indexOf(oldNode);
                    if (idx >= 0) p.getChildren().set(idx, newNode);
                }
                else {
                    // Как fallback — ничего не делаем, пусть вызывающий сам вставит newNode
                }
            }
        }
        catch (Exception e) {
            Util.out("Util/DebugFX.java: Ошибка в replaceInParent: " + e.getMessage());
        }
    }
}