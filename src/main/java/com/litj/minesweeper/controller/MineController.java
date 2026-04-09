package com.litj.minesweeper.controller;

import com.litj.minesweeper.ai.model.MineInfo;
import com.litj.minesweeper.model.MineSquare;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class MineController {

    // 行数
    private int rowCount = 16;
    // 列数
    private int columnCount = 30;
    // 地雷总数
    private int mineCount = 99;
    // 是否第一次点击
    private boolean isFirstClick = true;

    private Map<String, MineSquare> mineSquareMap;

    private Scene scene;

    private Stage stage;

    private Timeline timeline;

    private Label lbTime;

    private Label lbMineCountRemain;

    // 剩下的地雷数量
    private int mineCountRemain;

    private int openCount = 0;

    // 扫雷的时间
    private int timeCount;

    private boolean isAiRun;

    private EventHandler eventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (isAiRun) {
                return;
            }
            ImageView imageView = (ImageView) event.getSource();
            MineSquare mineSquare = mineSquareMap.get(imageView.getId());
            if (event.getButton() == MouseButton.PRIMARY) {
                doSquareLeftClicked(mineSquare.getPositionX(), mineSquare.getPositionY());
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                doSquareRightClicked(mineSquare.getPositionX(), mineSquare.getPositionY());
            }
            if (event.getButton() == MouseButton.MIDDLE || event.getButton() == MouseButton.PRIMARY && event.isSecondaryButtonDown() || event.getButton() == MouseButton.SECONDARY && event.isPrimaryButtonDown()) {
                doSquareMiddleOrLeftRightClicked(mineSquare.getPositionX(), mineSquare.getPositionY());
            }
        }
    };

    public MineController(Stage stage) {
        this.stage = stage;
    }

    public Scene initMineSquare() {
        System.out.print("initMineSquare\n");
        mineCountRemain = mineCount;
        isFirstClick = true;
        openCount = 0;
        timeCount = 0;
        if (mineSquareMap == null) {
            mineSquareMap = new HashMap<>();
        } else {
            mineSquareMap.clear();
        }
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: #acacac");
        vBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < rowCount; i++) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            for (int e = 0; e < columnCount; e++) {
//                Image image = new Image(getClass().getResource("/img/original.png").toExternalForm());
                ImageView imageView = new ImageView();
                imageView.setFitWidth(MineSquare.width);
                imageView.setFitHeight(MineSquare.height);
                hBox.getChildren().add(imageView);
                imageView.setOnMouseClicked(eventHandler);
                imageView.getStyleClass().add("image");
                imageView.setStyle("");
                imageView.setId(e + "," + i);
                MineSquare mineSquare = new MineSquare();
                mineSquare.setIvInfo(imageView);
                mineSquare.setPositionX(e);
                mineSquare.setPositionY(i);
                mineSquareMap.put(e + "," + i, mineSquare);
            }
            vBox.getChildren().add(hBox);
        }
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(25, 0, 0, 0));
        Image imageClock = new Image(getClass().getResource("/img/clock.png").toExternalForm());
        ImageView ivClock = new ImageView(imageClock);
        ivClock.setFitWidth(50);
        ivClock.setFitHeight(50);
        hBox.getChildren().add(ivClock);
        lbTime = new Label("0");
        lbTime.setPrefSize(100, 50);
        lbTime.setTextFill(Color.WHITE);
//        textTime.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        lbTime.setFont(Font.font(null, FontWeight.BOLD, 32));
        hBox.getChildren().add(lbTime);
        HBox.setMargin(lbTime, new Insets(0, 0, 0, 10));
        Image imageMine = new Image(getClass().getResource("/img/mine.png").toExternalForm());
        ImageView ivMine = new ImageView(imageMine);
        ivMine.setFitWidth(50);
        ivMine.setFitHeight(50);
        hBox.getChildren().add(ivMine);
        HBox.setMargin(ivMine, new Insets(0, 0, 0, 400));
        lbMineCountRemain = new Label("" + mineCount);
        lbMineCountRemain.setTextFill(Color.WHITE);
        lbMineCountRemain.setFont(Font.font(null, FontWeight.BOLD, 32));
        hBox.getChildren().add(lbMineCountRemain);
        HBox.setMargin(lbMineCountRemain, new Insets(0, 0, 0, 10));
        vBox.getChildren().add(hBox);
        double width = columnCount * MineSquare.width + 100;
        double height = rowCount * MineSquare.height + 150;
        scene = new Scene(vBox, width, height);
        scene.getStylesheets().add(getClass().getResource("/css/original.css").toExternalForm());
        return scene;
    }

    private void reset() {
        System.out.print("reset\n");
        initMineSquare();
        stage.setScene(scene);
    }

    /**
     * 第一次点击后生成地雷，第一次点击区域总是为空白区域
     */
    public void initMineAfterFirstClick(int x, int y) {
        System.out.print("initMineAfterFirstClick\n");
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeCount++;
                lbTime.setText(timeCount + "");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        Random random = new Random();
        int genarateMineCount = 0;
        while (true) {
            int randomX = random.nextInt(columnCount);
            int randomY = random.nextInt(rowCount);
            // 初次点击，点击区域包括周围8格，一共9格，不能有雷
            if (randomX >= x - 1 && randomX <= x + 1 && randomY >= y - 1 && randomY <= y + 1) {
                continue;
            }
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
            for (int e = 0; e < columnCount; e++) {
                int mineCount = 0;
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
        isFirstClick = false;
        doSquareLeftClicked(x, y);
    }

    /**
     * 方块左键点击
     * @param x X坐标
     * @param y Y坐标
     */
    public void doSquareLeftClicked(int x, int y) {
        if (isFirstClick) {
            initMineAfterFirstClick(x, y);
            return;
        }
        if (x < 0 || x >= columnCount || y < 0 || y >= rowCount || isFirstClick) {
            return;
        }
        MineSquare mineSquare = mineSquareMap.get(x + "," + y);
        // 问号、旗子、已点开区域，左键点击无效
        if (mineSquare.isFlag() || mineSquare.isQuestionMark() || mineSquare.isOpen()) {
            return;
        }
        if (mineSquare.isMine()) {
            gameover();
            return;
        }
        mineSquare.setOpen(true);
        if (mineSquare.getMineCount() != 0) {
            Image image = new Image(getClass().getResource("/img/" + mineSquare.getMineCount() + ".png").toExternalForm());
            mineSquare.getIvInfo().getStyleClass().remove("image");
            mineSquare.getIvInfo().setImage(image);
        } else {
            Image image = new Image(getClass().getResource("/img/empty.png").toExternalForm());
            mineSquare.getIvInfo().getStyleClass().remove("image");
            mineSquare.getIvInfo().setImage(image);
            doSquareLeftClicked(x - 1, y - 1);
            doSquareLeftClicked(x, y - 1);
            doSquareLeftClicked(x + 1, y - 1);
            doSquareLeftClicked(x - 1, y);
            doSquareLeftClicked(x + 1, y);
            doSquareLeftClicked(x - 1, y + 1);
            doSquareLeftClicked(x, y + 1);
            doSquareLeftClicked(x + 1, y + 1);
        }
        openCount++;
        System.out.print(openCount + "doSquareLeftClicked\n");
        if (openCount == rowCount * columnCount - mineCount) {
            success();
        }
    }

    /**
     * 方块右键点击
     * @param x X坐标
     * @param y Y坐标
     */
    public void doSquareRightClicked(int x, int y) {
        MineSquare mineSquare = mineSquareMap.get(x + "," + y);
        if (mineSquare.isOpen()) {
            return;
        }
        if (mineSquare.isFlag()) {
            mineSquare.setFlag(false);
            mineSquare.setQuestionMark(true);
            Image image = new Image(getClass().getResource("/img/question_mark.png").toExternalForm());
            mineSquare.getIvInfo().getStyleClass().remove("image");
            mineSquare.getIvInfo().setImage(image);
            mineCountRemain++;
            lbMineCountRemain.setText(mineCountRemain + "");
        } else if (mineSquare.isQuestionMark()) {
            mineSquare.setFlag(false);
            mineSquare.setQuestionMark(false);
            Image image = new Image(getClass().getResource("/img/original.png").toExternalForm());
            mineSquare.getIvInfo().getStyleClass().add("image");
            mineSquare.getIvInfo().setImage(image);
        } else {
            mineSquare.setFlag(true);
            mineSquare.setQuestionMark(false);
            mineSquare.getIvInfo().getStyleClass().remove("image");
            Image image = new Image(getClass().getResource("/img/flag.png").toExternalForm());
            mineSquare.getIvInfo().setImage(image);
            mineCountRemain--;
            lbMineCountRemain.setText(mineCountRemain + "");
        }
    }

    /**
     * 方块滚轮键或者左右键同时点击
     * @param x X坐标
     * @param y Y坐标
     */
    public void doSquareMiddleOrLeftRightClicked(int x, int y) {
        MineSquare mineSquare = mineSquareMap.get(x + "," + y);
        if (!mineSquare.isOpen() || mineSquare.isFlag() || mineSquare.isQuestionMark()) {
            return;
        }
        int flagCount = 0;
        if (isFlag(x - 1, y - 1)) {
            flagCount++;
        }
        if (isFlag(x, y - 1)) {
            flagCount++;
        }
        if (isFlag(x + 1, y - 1)) {
            flagCount++;
        }
        if (isFlag(x - 1, y)) {
            flagCount++;
        }
        if (isFlag(x + 1, y)) {
            flagCount++;
        }
        if (isFlag(x - 1, y + 1)) {
            flagCount++;
        }
        if (isFlag(x, y + 1)) {
            flagCount++;
        }
        if (isFlag(x + 1, y + 1)) {
            flagCount++;
        }
        if (flagCount == mineSquare.getMineCount()) {
            doSquareLeftClicked(x - 1, y - 1);
            doSquareLeftClicked(x, y - 1);
            doSquareLeftClicked(x + 1, y - 1);
            doSquareLeftClicked(x - 1, y);
            doSquareLeftClicked(x + 1, y);
            doSquareLeftClicked(x - 1, y + 1);
            doSquareLeftClicked(x, y + 1);
            doSquareLeftClicked(x + 1, y + 1);
        }
    }

    private boolean isFlag(int x, int y) {
        MineSquare mineSquare = mineSquareMap.get(x + "," + y);
        if (mineSquare == null) {
            return false;
        }
        return mineSquare.isFlag();
    }

    private void showAllMine() {
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineSquare mineSquare = mineSquareMap.get(e + ","  + i);
                if (mineSquare.isMine()) {
                    Image image = new Image(getClass().getResource("/img/mine_square.png").toExternalForm());
                    mineSquare.getIvInfo().getStyleClass().remove("image");
                    mineSquare.getIvInfo().setImage(image);
                }
            }
        }
    }

    /**
     * 游戏失败
     */
    private void gameover() {
        showAllMine();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("游戏失败");
        alert.setHeaderText("游戏失败");
        timeline.stop();
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType buttonType = result.get();
        if (buttonType == ButtonType.OK){
            reset();
        }
    }

    /**
     * 游戏通关
     */
    private void success() {
        System.out.print("success\n");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("游戏通关");
        alert.setHeaderText("游戏通关");
        timeline.stop();
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType buttonType = result.get();
        if (buttonType == ButtonType.OK){
            reset();
        }
    }

    public void reFreshMineInfo(Map<String, MineInfo> mineInfoMap) {
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineSquare mineSquare = mineSquareMap.get(e + ","  + i);
                MineInfo mineInfo = mineInfoMap.get(e + ","  + i);
                if (mineInfo == null) {
                    mineInfo = new MineInfo();
                    mineInfoMap.put(e + ","  + i, mineInfo);
                }
                mineInfo.setOpen(mineSquare.isOpen());
                mineInfo.setMineCount(mineSquare.getMineCount());
            }
        }
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public boolean isAiRun() {
        return isAiRun;
    }

    public void setAiRun(boolean aiRun) {
        isAiRun = aiRun;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }
}
