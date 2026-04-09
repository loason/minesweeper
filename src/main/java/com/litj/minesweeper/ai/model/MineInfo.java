package com.litj.minesweeper.ai.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MineInfo {

    public static int LEFT_CLICK = 1;

    public static int LEFT_RIGHT_CLICK = 2;

    // 四周地雷数量，也是当前格子显示的数字
    private int mineCount;

    private boolean isOpen;

    private boolean isMine;

    private boolean isFlag;

    // 四周未被打开的方格数量
    private int notOpenCount;

    private int positionX;

    private int positionY;

    // 点击类型，左键或者是左右键双击类型
    private int ClickType;

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public int getNotOpenCount() {
        return notOpenCount;
    }

    public void setNotOpenCount(int notOpenCount) {
        this.notOpenCount = notOpenCount;
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

    public int getClickType() {
        return ClickType;
    }

    public void setClickType(int clickType) {
        ClickType = clickType;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
        isMine = flag;
    }
}
