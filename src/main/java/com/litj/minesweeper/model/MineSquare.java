package com.litj.minesweeper.model;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.Serializable;

public class MineSquare {

    // 当前方块显示的状态图片
    private ImageView ivInfo;
    // 是否有地雷
    private boolean isMine;
    // 是否被点开
    private boolean isOpen;
    // 周围地雷数量，范围0-8
    private int mineCount;
    // 是否插旗
    private boolean isFlag;
    // 是否问号
    private boolean isQuestionMark;
    // 坐标X，左上角第一个坐标为（0，0）
    private int positionX;
    // 坐标Y，左上角第一个坐标为（0，0）
    private int positionY;
    // 方格宽度
    public static int width = 40;
    // 方格高度
    public static int height = 40;

    public ImageView getIvInfo() {
        return ivInfo;
    }

    public void setIvInfo(ImageView ivInfo) {
        this.ivInfo = ivInfo;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }

    public boolean isQuestionMark() {
        return isQuestionMark;
    }

    public void setQuestionMark(boolean questionMark) {
        isQuestionMark = questionMark;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        MineSquare.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        MineSquare.height = height;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
