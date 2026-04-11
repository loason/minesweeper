package com.litj.minesweeper.ai;

import com.litj.minesweeper.ai.model.MineGroupInfo;
import com.litj.minesweeper.ai.model.MineInfo;
import com.litj.minesweeper.ai.model.MineSubGroupInfo;
import com.litj.minesweeper.ai.util.MineUtil;
import com.litj.minesweeper.controller.MineController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MineSweeperAi {

    private MineController mineController;

    private Map<String, MineInfo> mineInfoMap;

    private Timeline timeline;

    private int duration = 10;
    // 行数
    private int rowCount = 16;
    // 列数
    private int columnCount = 30;
    // 地雷总数
    private int mineCount = 99;

    private Stack<MineInfo> doClickStack;

    private Stack<MineInfo> doFlagStack;

    private Map<String, MineGroupInfo> mineGroupInfoMap;

    private Map<String, List<MineSubGroupInfo>> mineSubGroupInfoMap;

    private Task<Integer> task;

    public MineSweeperAi(MineController mineController) {
        this.mineController = mineController;
        this.mineInfoMap = new HashMap<>();
        rowCount = mineController.getRowCount();
        columnCount = mineController.getColumnCount();
        mineCount = mineController.getMineCount();
        mineController.setOnStatusListener(new MineController.OnStatusListener() {
            @Override
            public void onSuccess() {
                if (MineController.showAiPlaying) {
                    timeline.stop();
                }
            }

            @Override
            public void onGameOver() {
                if (MineController.showAiPlaying) {
                    timeline.stop();
                }
            }

            @Override
            public void onGameReset() {
                System.out.print("onGameReset\n");
                if (doClickStack != null) {
                    doClickStack.clear();
                }
                if (doFlagStack != null) {
                    doFlagStack.clear();
                }
                if (mineInfoMap != null) {
                    mineInfoMap.clear();
                }
                if (mineGroupInfoMap != null) {
                    mineGroupInfoMap.clear();
                }
                if (mineSubGroupInfoMap != null) {
                    mineSubGroupInfoMap.clear();
                }
                mineController.doSquareLeftClicked(15, 8);
                if (MineController.showAiPlaying) {
                    timeline.play();
                }
            }
        });
    }

    public void run() {
        mineController.setAiRun(true);
        doClickStack = new Stack<>();
        doFlagStack = new Stack<>();
        mineController.doSquareLeftClicked(15, 8);
        if (MineController.showAiPlaying) {
            timeline = new Timeline(new KeyFrame(Duration.millis(duration), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    doNext();
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        } else {
            loop();
        }

    }

    public void loop() {
        ExecutorService executor = Executors.newCachedThreadPool();
        task = new Task<>() {
            @Override
            protected Integer call() {
                while (true) {
                    doNext();
                    System.out.print("loop\n");
                }
            }
        };
        executor.submit(task);
    }

    public void doNext() {
        if (doFlagStack != null && !doFlagStack.isEmpty()) {
            System.out.print("doFlag" + doFlagStack.size() + "\n");
            MineInfo mineInfo = doFlagStack.pop();
            mineController.doSquareRightClicked(mineInfo.getPositionX(), mineInfo.getPositionY());
            return;
        }
        if (doClickStack != null && !doClickStack.isEmpty()) {
            //点击序列里面如果还有可点击数据，则先做点击
            doClick();
            return;
        }
        //如无可点击位置，则继续分析
        doAnalyse();

    }

    /**
     * 分析局势，判断后续的点击位置
     */
    private void doAnalyse() {
        mineController.reFreshMineInfo(mineInfoMap);
        refreshFlagMine();
        refreshCanLeftRightClick();
        if (doFlagStack.isEmpty() && doClickStack.isEmpty()) {
            collectGroup();
            analyseGroup();
        }
        if (doFlagStack.isEmpty() && doClickStack.isEmpty()) {
            collectSubGroup();
            analyseSubGroup();
        }
        if (doFlagStack.isEmpty() && doClickStack.isEmpty()) {
            guess();
        }
    }

    /**
     * 牢住了，猜吧
     */
    private void guess() {
        int x = 0;
        int y = 0;
        float ratio = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                if (x == 0 && y == 0 && !mineInfoMap.get(e + "," + i).isOpen() && !mineInfoMap.get(e + "," + i).isFlag()) {
                    // 找第一个没开过的格子为初始值
                    x = e;
                    y = i;
                }
                MineGroupInfo mineGroupInfo = mineGroupInfoMap.get(e + "," + i);
                if (mineGroupInfo == null) {
                    continue;
                }
                float mineRatio = (float) mineGroupInfo.getMineCount() / mineGroupInfo.getMineInfoMap().size();
                if (ratio == 0) {
                    ratio = mineRatio;
                }
                if (mineRatio > ratio) {
                    ratio = mineRatio;
                    MineInfo mineInfo = MineUtil.getFirstMineInfo(mineGroupInfo);
                    x = mineInfo.getPositionX();
                    y = mineInfo.getPositionY();
                }
            }
        }
        System.out.print("guessAndPushClickStack:" + x + "," + y + "\n");
        MineInfo mineInfo = mineInfoMap.get(x + "," + y);
        mineInfo.setClickType(MineInfo.LEFT_CLICK);
        doClickStack.push(mineInfo);
    }

    private void collectSubGroup() {
        if (mineSubGroupInfoMap == null) {
            mineSubGroupInfoMap = new HashMap<>();
        } else {
            mineSubGroupInfoMap.clear();
        }
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineGroupInfo mineGroupInfo = mineGroupInfoMap.get(e + "," + i);
                List<MineSubGroupInfo> mineSubGroupInfoList = new ArrayList<>();
                if (mineGroupInfo != null && mineGroupInfo.getMineCount() > 1) {
                    String[] mineGroupInfoPositions = mineGroupInfo.getGroupId().split(";");
                    for (int j = 1; mineGroupInfo.getMineCount() - j > 0; j++) {
                        List<String> subGroupIdList = MineUtil.getSubGroupList(mineGroupInfoPositions, mineGroupInfoPositions.length - j);
                        for (String s : subGroupIdList) {
                            MineSubGroupInfo mineSubGroupInfo = new MineSubGroupInfo();
                            mineSubGroupInfo.setMineCountAtLeast(mineGroupInfo.getMineCount() - j);
                            mineSubGroupInfo.setGroupId(s);
                            String[] mineSubGroupInfoPositions = s.split(";");
                            Map<String, MineInfo> subGroupMineInfoMap = new HashMap<>();
                            for (int k = 0; k < mineSubGroupInfoPositions.length; k++) {
                                subGroupMineInfoMap.put(mineSubGroupInfoPositions[k], mineInfoMap.get(mineSubGroupInfoPositions[k]));
                            }
                            mineSubGroupInfo.setMineInfoMap(subGroupMineInfoMap);
                            mineSubGroupInfoList.add(mineSubGroupInfo);
                        }
                    }
                    mineSubGroupInfoMap.put(e + "," + i, mineSubGroupInfoList);
                }
            }
        }
    }

    private void analyseSubGroup() {
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineInfo mineInfo = mineInfoMap.get(e + "," + i);
                MineGroupInfo mineGroupInfo = mineGroupInfoMap.get(e + "," + i);
                if (mineInfo.isOpen() && mineInfo.getMineCount() > 0 && mineGroupInfo != null) {
                    for (int j = i - 2; j <= i + 2; j++) {
                        for (int k = e - 2; k <= e + 2; k++) {
                            if (j == i && k == e) {
                                continue;
                            }
                            List<MineSubGroupInfo> mineSubGroupInfoList = mineSubGroupInfoMap.get(k + "," + j);
                            if (mineSubGroupInfoList == null) {
                                continue;
                            }
                            for (MineSubGroupInfo mineSubGroupInfo : mineSubGroupInfoList) {
                                if (MineUtil.contains(mineGroupInfo.getGroupId(), mineSubGroupInfo.getGroupId())) {
                                    String groupId = MineUtil.replace(mineGroupInfo.getGroupId(), mineSubGroupInfo.getGroupId());
                                    String[] positions = groupId.split(";");
                                    if (mineGroupInfo.getMineCount() == mineSubGroupInfo.getMineCountAtLeast()) {
                                        for (int y = 0; y < positions.length; y++) {
                                            System.out.print("analyseSubGroupAndPushClickStack:" + positions[y] + "\n");
                                            MineInfo mineInfoTemp = mineInfoMap.get(positions[y]);
                                            mineInfoTemp.setClickType(MineInfo.LEFT_CLICK);
                                            doClickStack.push(mineInfoTemp);
                                        }
                                    }
                                    if (mineGroupInfo.getMineCount() - mineSubGroupInfo.getMineCountAtLeast() == positions.length) {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 遍历所有已经打开，并且有数字的方格，统计该方格四周未被打开的方格编为一组，并且记录这组方格含有多少个地雷，并且将该组方格编号
     */
    private void collectGroup() {
        System.out.print("collectGroup\n");
        if (mineGroupInfoMap == null) {
            mineGroupInfoMap = new HashMap<>();
        } else {
            mineGroupInfoMap.clear();
        }
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineInfo mineInfo = mineInfoMap.get(e + "," + i);
                int mineUnconfirmedCount = MineUtil.getMineUnconfirmedCount(mineInfoMap, mineInfo);
                if (mineInfo.isOpen() && mineInfo.getMineCount() > 0 && mineUnconfirmedCount > 0) {
                    MineGroupInfo mineGroupInfo = new MineGroupInfo();
                    mineGroupInfo.setMineCount(mineUnconfirmedCount);
                    StringBuilder groupId = new StringBuilder();
                    addGroupInfo(e - 1, i - 1, groupId, mineGroupInfo);
                    addGroupInfo(e, i - 1, groupId, mineGroupInfo);
                    addGroupInfo(e + 1, i - 1, groupId, mineGroupInfo);
                    addGroupInfo(e - 1, i, groupId, mineGroupInfo);
                    addGroupInfo(e + 1, i, groupId, mineGroupInfo);
                    addGroupInfo(e - 1, i + 1, groupId, mineGroupInfo);
                    addGroupInfo(e, i + 1, groupId, mineGroupInfo);
                    addGroupInfo(e + 1, i + 1, groupId, mineGroupInfo);
                    mineGroupInfo.setGroupId(groupId.toString());
                    mineGroupInfoMap.put(e + "," + i, mineGroupInfo);
                }

            }
        }
    }

    /**
     * 根据收集的组信息，分析哪些方格可以点击，哪些方格可以插旗
     */
    private void analyseGroup() {
        System.out.print("analyseGroup\n");
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineInfo mineInfo = mineInfoMap.get(e + "," + i);
                if (mineInfo.isOpen() && mineInfo.getMineCount() > 0 && MineUtil.getMineUnconfirmedCount(mineInfoMap, mineInfo) > 0) {
                    MineGroupInfo mineGroupInfo = mineGroupInfoMap.get(e + "," + i);
                    if (mineGroupInfo == null) {
                        continue;
                    }
                    for (int j = i - 2; j <= i + 2; j++){
                        for (int k = e - 2; k <= e + 2; k++){
                            if (j == i && k == e) {
                                continue;
                            }
                            doAnalyseGroupItem(k, j, mineGroupInfo, mineInfo);
                        }
                    }
                }
            }
        }
    }

    private void doAnalyseGroupItem(int x, int y, MineGroupInfo mineGroupInfo, MineInfo mineInfo) {
        String groupId = mineGroupInfo.getGroupId();
        MineGroupInfo mineGroupInfoTemp = mineGroupInfoMap.get(x + "," + y);
        if (mineGroupInfoTemp != null) {
            String groupIdTemp = mineGroupInfoTemp.getGroupId();
            if (MineUtil.contains(groupId, groupIdTemp)) {
                groupId = MineUtil.replace(groupId, groupIdTemp);
                String[] keyList = groupId.split(";");
                if (keyList.length > 0 && mineGroupInfo.getMineCount() == mineGroupInfoTemp.getMineCount()) {
                    for (String s : keyList) {
                        MineInfo mineInfoTemp = mineInfoMap.get(s);
                        if (mineInfoTemp != null) {
                            System.out.print("analyseGroupAndPushClickStack:" + s + "    " + mineInfo.getPositionX() + "," + mineInfo.getPositionY() + "\n");
                            mineInfoTemp.setClickType(MineInfo.LEFT_CLICK);
                            doClickStack.push(mineInfoTemp);
                        }
                    }
                }
                if (keyList.length > 0 && mineGroupInfo.getMineCount() - mineGroupInfoTemp.getMineCount() == keyList.length) {
                    for (String s : keyList) {
                        MineInfo mineInfoTemp = mineInfoMap.get(s);
                        if (mineInfoTemp != null && !mineInfoTemp.isFlag()) {
                            System.out.print("analyseGroupAndPushFlagStack:" + s + "\n");
                            mineInfoTemp.setFlag(true);
                            doFlagStack.push(mineInfoTemp);
                        }
                    }
                }
            }
        }
    }

    private void addGroupInfo(int x, int y, StringBuilder groupId, MineGroupInfo mineGroupInfo) {
        MineInfo mineInfo = mineInfoMap.get(x + "," + y);
        if (mineInfo != null && !mineInfo.isOpen() && !mineInfo.isFlag()) {
            mineGroupInfo.addMineInfo(mineInfo);
            groupId.append(mineInfo.getPositionX() + "," + mineInfo.getPositionY() + ";");
        }
    }

    private void doClick() {
        MineInfo mineInfo = doClickStack.pop();
        if (mineInfo.getClickType() == MineInfo.LEFT_RIGHT_CLICK) {
            System.out.print("doLeftRightClick" + doClickStack.size() + "  " + mineInfo.getPositionX() + "," + mineInfo.getPositionY() +"\n");
            mineController.doSquareMiddleOrLeftRightClicked(mineInfo.getPositionX(), mineInfo.getPositionY());
        } else if (mineInfo.getClickType() == MineInfo.LEFT_CLICK) {
            System.out.print("doLeftClick" + doClickStack.size() + "  " + mineInfo.getPositionX() + "," + mineInfo.getPositionY() +"\n");
            mineController.doSquareLeftClicked(mineInfo.getPositionX(), mineInfo.getPositionY());
        }
    }

    /**
     * 检索更新所有可以被标记为地雷的位置
     */
    private void refreshFlagMine() {
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineInfo mineInfo = mineInfoMap.get(e + ","  + i);
                if (mineInfo.isOpen() && mineInfo.getMineCount() > 0) {
                    int notOpenCount = 0;
                    notOpenCount = isNotOpen(e - 1, i - 1) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e, i - 1) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e + 1, i - 1) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e - 1, i) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e + 1, i) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e - 1, i + 1) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e, i + 1) ? notOpenCount + 1 : notOpenCount;
                    notOpenCount = isNotOpen(e + 1, i + 1) ? notOpenCount + 1 : notOpenCount;
                    if (notOpenCount == mineInfo.getMineCount()) {
                        setMineIfIsNotOpen(e - 1, i - 1, mineInfo);
                        setMineIfIsNotOpen(e, i - 1, mineInfo);
                        setMineIfIsNotOpen(e + 1, i - 1, mineInfo);
                        setMineIfIsNotOpen(e - 1, i, mineInfo);
                        setMineIfIsNotOpen(e + 1, i, mineInfo);
                        setMineIfIsNotOpen(e - 1, i + 1, mineInfo);
                        setMineIfIsNotOpen(e, i + 1, mineInfo);
                        setMineIfIsNotOpen(e + 1, i + 1, mineInfo);
                    }
                    mineInfo.setNotOpenCount(notOpenCount);
                }
            }
        }
    }

    /**
     * 检索更新所有可以被左右键点击的区域
     */
    private void refreshCanLeftRightClick() {
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineInfo mineInfo = mineInfoMap.get(e + "," + i);
                if (mineInfo.getMineCount() == 0 || !mineInfo.isOpen()) {
                    continue;
                }
                int mineCount =  0;
                mineCount = isMine(e - 1, i - 1) ? mineCount + 1 : mineCount;
                mineCount = isMine(e, i - 1) ? mineCount + 1 : mineCount;
                mineCount = isMine(e + 1, i - 1) ? mineCount + 1 : mineCount;
                mineCount = isMine(e - 1, i) ? mineCount + 1 : mineCount;
                mineCount = isMine(e + 1, i) ? mineCount + 1 : mineCount;
                mineCount = isMine(e - 1, i + 1) ? mineCount + 1 : mineCount;
                mineCount = isMine(e, i + 1) ? mineCount + 1 : mineCount;
                mineCount = isMine(e + 1, i + 1) ? mineCount + 1 : mineCount;
                if (mineCount == mineInfo.getMineCount() && mineInfo.getNotOpenCount() != mineInfo.getMineCount()) {
                    // 如果周围已经确认的地雷数量等于当前格子显示的数量
                    mineInfo.setClickType(MineInfo.LEFT_RIGHT_CLICK);
                    doClickStack.push(mineInfo);
                }
            }
        }
    }

    private boolean isMine(int x, int y) {
        MineInfo mineInfo = mineInfoMap.get(x + "," + y);
        return mineInfo != null && mineInfo.isMine();
    }

    private boolean isNotOpen(int x, int y) {
        MineInfo mineInfo = mineInfoMap.get(x + "," + y);
        return mineInfo != null && !mineInfo.isOpen();
    }

    private void setMineIfIsNotOpen(int x, int y, MineInfo mineInfo) {
        MineInfo mineInfoTemp = mineInfoMap.get(x + "," + y);
        if (mineInfoTemp != null && !mineInfoTemp.isOpen()) {
            if (!mineInfoTemp.isFlag()) {
                mineInfoTemp.setFlag(true);
//                System.out.print(mineInfo.getPositionX() + "," + mineInfo.getPositionY() + "  addMineConfirmed:" + mineInfoTemp.getPositionX() + "," + mineInfoTemp.getPositionY() + "\n");
                doFlagStack.push(mineInfoTemp);
            }
        }
    }

}
