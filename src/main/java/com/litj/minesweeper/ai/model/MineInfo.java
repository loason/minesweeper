package com.litj.minesweeper.ai.model;

public class MineInfo {

    private int mineCount;

    private boolean isOpen;

    private boolean isMine;

    private int notOpenCount;

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
}
