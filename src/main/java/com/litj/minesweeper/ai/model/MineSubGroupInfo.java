package com.litj.minesweeper.ai.model;

import java.util.Map;

public class MineSubGroupInfo {

    private String groupId;

    // 该组数据里面至少有多少个地雷
    private int mineCountAtLeast;

    private Map<String, MineInfo> mineInfoMap;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getMineCountAtLeast() {
        return mineCountAtLeast;
    }

    public void setMineCountAtLeast(int mineCountAtLeast) {
        this.mineCountAtLeast = mineCountAtLeast;
    }

    public Map<String, MineInfo> getMineInfoMap() {
        return mineInfoMap;
    }

    public void setMineInfoMap(Map<String, MineInfo> mineInfoMap) {
        this.mineInfoMap = mineInfoMap;
    }
}
