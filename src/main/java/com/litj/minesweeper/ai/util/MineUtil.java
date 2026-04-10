package com.litj.minesweeper.ai.util;

import com.litj.minesweeper.ai.model.MineInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     * 传入一组数据和要分割的数量
     * @param positions 数据源
     * @param groupCount 将当前数据源分割成多少个一组
     * @return
     */
    public static List<String> getSubGroupList(String[] positions, int groupCount) {
        List<String> subGroupList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<String> positionList = new ArrayList<>();
        Collections.addAll(positionList, positions);
        doSubGroupList(positionList, groupCount, subGroupList, sb);
        return subGroupList;
    }

    static List<String> subGroupList;
    static StringBuilder sb = new StringBuilder();
    /**
     * 传入一组数据和要分割的数量
     * @param positions 数据源
     * @param groupCount 将当前数据源分割成多少个一组
     * @return
     */
    public static void doSubGroupList(List<String> positions, int groupCount, List<String> subGroupList, StringBuilder sb) {
        for (int i = 0; i <= positions.size() - groupCount; i++) {
            String str = positions.get(i) + ";";
            sb.append(str);
            if (groupCount - 1 > 0) {
                doSubGroupList(positions.subList(i + 1, positions.size()), groupCount - 1, subGroupList, sb);
            } else {
                subGroupList.add(sb.toString());
            }
            sb.replace(sb.length() - str.length(), sb.length(), "");
        }
    }

    // 1,2,3,4,5
    // 2,3,4,5
    // 3,4,5

}
