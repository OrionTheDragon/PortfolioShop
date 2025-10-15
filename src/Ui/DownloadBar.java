package Ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import static Shop.Categories.Goods.getAllArrGoods;
import static Util.Util.out;

public class DownloadBar {
    private int downloadScale;

    public int getDownloadScale() {
        return downloadScale;
    }
    public void setDownloadScale(int downloadScale) {
        this.downloadScale = downloadScale;
    }

    public void downloadingProgress(VBox root) {

        Label loadingProgress = new Label("0%");
        Label loadingBar = new Label("━");
        loadingBar.setFont(Font.font(String.valueOf(FontWeight.BOLD), 24));
        loadingProgress.setFont(Font.font(String.valueOf(FontWeight.BOLD), 24));

        Platform.runLater(() -> root.getChildren().setAll(loadingProgress, loadingBar));

        Timeline timeline = new Timeline();
        int a = getAllArrGoods().size();

        timeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(0.5), event -> {
            double percent = ((double) getDownloadScale() / a) * 100;
            out("Shop/Categories/Goods.java: Процентаж закгрузки : " + percent + "%");

            loadingProgress.setText(percent + "%");

            if ((int) percent % 10 == 0) {
                loadingBar.setText(loadingBar.getText() + "━");
            }

            if (percent >= 100.0) {
                timeline.stop();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setDelay(Duration.seconds(0));
        timeline.play();
    }
}
