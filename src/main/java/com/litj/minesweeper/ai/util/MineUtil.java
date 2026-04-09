package com.litj.minesweeper.ai.util;

import com.litj.minesweeper.ai.model.MineInfo;

import java.util.Map;

public class MineUtil {

    public static int getMineUnconfirmedCount(Map<String, MineInfo> mineInfoMap, MineInfo mineInfo) {
        int x = mineInfo.getPositionX();
        int y = mineInfo.getPositionY();
        int confirmedCount = 0;
        MineInfo temp = mineInfoMap.get((x - 1) + "," + (y - 1));
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get((x) + "," + (y - 1));
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get((x + 1) + "," + (y - 1));
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get((x - 1) + "," + y);
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get((x + 1) + "," + y);
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get((x - 1) + "," + (y + 1));
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get(x + "," + (y + 1));
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        temp = mineInfoMap.get((x + 1) + "," + (y + 1));
        if (temp != null && temp.isFlag()) {
            confirmedCount++;
        }
        return mineInfo.getMineCount() - confirmedCount;
    }

}
