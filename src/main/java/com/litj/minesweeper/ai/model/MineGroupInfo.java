package com.litj.minesweeper.ai.model;

import java.util.HashMap;
import java.util.Map;

public class MineGroupInfo {

    private String groupId;

    private int mineCount;

    private Map<String, MineInfo> mineInfoMap;

    public void addMineInfo(MineInfo mineInfo) {
        if (mineInfoMap == null) {
            mineInfoMap = new HashMap<>();
        }
        mineInfoMap.put(mineInfo.getPositionX() + "," + mineInfo.getPositionY(), mineInfo);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, MineInfo> getMineInfoMap() {
        return mineInfoMap;
    }

    public void setMineInfoMap(Map<String, MineInfo> mineInfoMap) {
        this.mineInfoMap = mineInfoMap;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
    }
}
