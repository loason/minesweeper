package com.litj.minesweeper.controller;

import com.litj.minesweeper.model.MineSquare;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
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

    private EventHandler eventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            ImageView imageView = (ImageView) event.getSource();
            MineSquare mineSquare = mineSquareMap.get(imageView.getId());
            if (event.getButton() == MouseButton.PRIMARY) {
                if (isFirstClick) {
                    initMineAfterFirstClick(mineSquare.getPositionX(), mineSquare.getPositionY());
                } else {
                    doSquareLeftClicked(mineSquare.getPositionX(), mineSquare.getPositionY());
                }
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                doSquareRightClicked(mineSquare.getPositionX(), mineSquare.getPositionY());
            }
            if (event.getButton() == MouseButton.MIDDLE || event.getButton() == MouseButton.PRIMARY && event.isSecondaryButtonDown() || event.getButton() == MouseButton.SECONDARY && event.isPrimaryButtonDown()) {
                doSquareMiddleOrLeftRightClicked(mineSquare.getPositionX(), mineSquare.getPositionY());
            }
        }
    };

    public Scene initMineSquare() {
        mineSquareMap = new HashMap<>();
        VBox vBox = new VBox();
        for (int i = 0; i < rowCount; i++) {
            HBox hBox = new HBox();
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
        double width = columnCount * MineSquare.width;
        double height = rowCount * MineSquare.height;
        Scene scene = new Scene(vBox, width, height);
        scene.getStylesheets().add(getClass().getResource("/css/original.css").toExternalForm());
        return scene;
    }

    /**
     * 第一次点击后生成地雷，第一次点击区域总是为空白区域
     */
    private void initMineAfterFirstClick(int x, int y) {
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
    private void doSquareLeftClicked(int x, int y) {
        if (x < 0 || x >= columnCount || y < 0 || y >= rowCount) {
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

    }

    /**
     * 方块右键点击
     * @param x X坐标
     * @param y Y坐标
     */
    private void doSquareRightClicked(int x, int y) {
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
        }
    }

    /**
     * 方块滚轮键或者左右键同时点击
     * @param x X坐标
     * @param y Y坐标
     */
    private void doSquareMiddleOrLeftRightClicked(int x, int y) {
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

    /**
     * 游戏失败
     */
    public void gameover() {
        System.out.println("游戏失败");
    }

    /**
     * 游戏通关
     */
    public void success() {
        System.out.println("游戏通关");
    }


    public Map<String, MineSquare> getMineSquareMap() {
        return mineSquareMap;
    }

    public void setMineSquareMap(Map<String, MineSquare> mineSquareMap) {
        this.mineSquareMap = mineSquareMap;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
}
