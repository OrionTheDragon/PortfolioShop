package Ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import static Shop.Categories.Goods.getAllArrGoods;
import static Util.Util.out;

public class DownloadBar {
    private static int downloadScale;

    public static boolean flagProgress = false;

    public static int getDownloadScale() {
        return downloadScale;
    }
    public static void setDownloadScale(int downloadScale) {
        DownloadBar.downloadScale = downloadScale;
    }

    public static void downloadingProgress(VBox root) {
        root.setAlignment(Pos.CENTER);

        Label loadingProgress = new Label("0%");
        Label loadingBar = new Label("━");
        loadingBar.setFont(Font.font(String.valueOf(FontWeight.BOLD), 34));
        loadingProgress.setFont(Font.font(String.valueOf(FontWeight.BOLD), 34));

        Timeline timeline = new Timeline();
        int a = getAllArrGoods().size();

        final int[] lastTenPercent = {0};

        timeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(50), event -> {
            double percent = ((double) getDownloadScale() / a) * 100;
            out("Shop/Categories/Goods.java: Процент загрузки : " + percent + "%");

            String percentText = String.format("%.1f%%", percent);
            loadingProgress.setText(percentText);

            root.getChildren().setAll(loadingProgress, loadingBar);

            int tenPercent = (int) (percent / 10);
            if (tenPercent > lastTenPercent[0]) {
                loadingBar.setText(loadingBar.getText() + "━");
                lastTenPercent[0] = tenPercent;
            }

            if (percent >= 100.0) {
                root.setAlignment(Pos.TOP_CENTER);
                timeline.stop();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setDelay(Duration.seconds(0));
        timeline.play();
    }
}
