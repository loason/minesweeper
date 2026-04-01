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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MainApplication extends Application {

    // 行数
    private int rowCount = 16;
    // 列数
    private int columnCount = 30;
    // 地雷总数
    private int mineCount = 99;

    private double ratio;

    private Map<String, MineSquare> mineSquareMap;

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
        mineSquareMap = new HashMap<>();
        VBox vBox = new VBox();
        for (int i = 0; i < rowCount; i++) {
            HBox hBox = new HBox();
            for (int e = 0; e < columnCount; e++) {
                Image image = new Image(getClass().getResource("/img/original.png").toExternalForm());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(MineSquare.width);
                imageView.setFitHeight(MineSquare.height);
                hBox.getChildren().add(imageView);
                MineSquare mineSquare = new MineSquare();
                mineSquare.setIvInfo(imageView);
                mineSquare.setPositionX(columnCount);
                mineSquare.setPositionY(rowCount);
                mineSquareMap.put(rowCount + "," + columnCount, mineSquare);
            }
            vBox.getChildren().add(hBox);
        }
        double width = columnCount * MineSquare.width;
        double height = rowCount * MineSquare.height;
        ratio = width / height;
        return new Scene(vBox, width, height);
    }

    /**
     * 第一次点击后生成地雷，第一次点击区域总是为空白区域
     */
    private void initMineAfterFirstClick() {
        Random random = new Random();
        int genarateMineCount = 0;
        while (true) {
            int randomX = random.nextInt(columnCount);
            int randomY = random.nextInt(rowCount);
            MineSquare mineSquare = mineSquareMap.get(randomX + "," + randomY);
            if (!mineSquare.isMine()) {
                mineSquare.setMine(true);
                genarateMineCount++;
                if (genarateMineCount == mineCount) {
                    break;
                }
            }
        }
        for (int i = 0; i < rowCount; i++) {
            int mineCount = 0;
            for (int e = 0; e < columnCount; e++) {
                MineSquare mineSquare1 = mineSquareMap.get((e - 1) + "," + (i - 1));
                if (mineSquare1 != null && mineSquare1.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare2 = mineSquareMap.get(e + "," + (i - 1));
                if (mineSquare2 != null && mineSquare2.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare3 = mineSquareMap.get((e + 1) + "," + (i - 1));
                if (mineSquare3 != null && mineSquare3.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare4 = mineSquareMap.get((e - 1) + "," + i);
                if (mineSquare4 != null && mineSquare4.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare5 = mineSquareMap.get((e + 1) + "," + i);
                if (mineSquare5 != null && mineSquare5.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare6 = mineSquareMap.get((e - 1) + "," + (i + 1));
                if (mineSquare6 != null && mineSquare6.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare7 = mineSquareMap.get(e + "," + (i + 1));
                if (mineSquare7 != null && mineSquare7.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare8 = mineSquareMap.get((e + 1) + "," + (i + 1));
                if (mineSquare8 != null && mineSquare8.isMine()) {
                    mineCount++;
                }
                MineSquare mineSquare = mineSquareMap.get(e + "," + i);
                mineSquare.setMineCount(mineCount);
            }
        }
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
