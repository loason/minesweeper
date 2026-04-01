package com.litj.minesweeper;

import com.litj.minesweeper.model.MineSquare;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {

    // 行数
    private int rowCount = 16;
    // 列数
    private int columnCount = 30;

    private double ratio;

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Parent root = fxmlLoader.load();
//        VBox vBox = new VBox();
//        Scene scene = new Scene(vBox, columnCount * MineSquare.width, rowCount * MineSquare.height);
        stage.setTitle("MineSquare");
        Scene scene = initMineSquare();
        stage.setScene(scene);
        stage.setResizable(false);
//        stage.initStyle(StageStyle.UNDECORATED);
//        setStageFixedRatio(stage);
        stage.show();
    }

    static InputStream loadResourceAsStream(String source) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(source);
    }

    private Scene initMineSquare() {
        VBox vBox = new VBox();
        for (int i = 0; i < rowCount; i++) {
            HBox hBox = new HBox();
            for (int e = 0; e < columnCount; e++) {
                Image image = new Image(getClass().getResource("/img/button.jpg").toExternalForm());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(MineSquare.width);
                imageView.setFitHeight(MineSquare.height);
                hBox.getChildren().add(imageView);
            }
            vBox.getChildren().add(hBox);
        }
        double width = columnCount * MineSquare.width;
        double height = rowCount * MineSquare.height;
        ratio = width / height;
        return new Scene(vBox, width, height);
    }

    /*
    设置固定的宽高比
     */
    private void setStageFixedRatio(Stage stage) {
        ChangeListener changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double width = stage.getWidth();
                double height = stage.getHeight();
                if (width / height > ratio) {
                    height = width / ratio;
                } else {
                    width = height * ratio;
                }
                stage.setWidth(width);
                stage.setHeight(height);
            }
        };
        stage.widthProperty().addListener(changeListener);
        stage.heightProperty().addListener(changeListener);
    }

}
