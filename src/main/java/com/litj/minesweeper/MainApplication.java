package com.litj.minesweeper;

import com.litj.minesweeper.controller.MineController;
import com.litj.minesweeper.model.MineSquare;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MainApplication extends Application {

    private double ratio;

    private MineController mineController;

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Parent root = fxmlLoader.load();
//        VBox vBox = new VBox();
//        Scene scene = new Scene(vBox, columnCount * MineSquare.width, rowCount * MineSquare.height);
        stage.setTitle("MineSquare");
        mineController = new MineController();
        Scene scene = mineController.initMineSquare();
        stage.setScene(scene);
        stage.setResizable(false);
//        stage.initStyle(StageStyle.UNDECORATED);
//        setStageFixedRatio(stage);
        stage.show();
    }

    static InputStream loadResourceAsStream(String source) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(source);
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
