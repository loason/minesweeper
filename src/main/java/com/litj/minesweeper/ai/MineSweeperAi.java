package com.litj.minesweeper.ai;

import com.litj.minesweeper.ai.model.MineInfo;
import com.litj.minesweeper.controller.MineController;
import com.litj.minesweeper.model.MineSquare;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class MineSweeperAi {

    private MineController mineController;

    private Map<String, MineInfo> mineInfoMap;

    private Timeline timeline;

    private int duration = 1000;
    // 行数
    private int rowCount = 16;
    // 列数
    private int columnCount = 30;
    // 地雷总数
    private int mineCount = 99;

    public MineSweeperAi(MineController mineController) {
        this.mineController = mineController;
        this.mineInfoMap = new HashMap();
        rowCount = mineController.getRowCount();
        columnCount = mineController.getColumnCount();
        mineCount = mineController.getMineCount();
    }

    public void run() {
        mineController.setAiRun(true);
        timeline = new Timeline(new KeyFrame(Duration.millis(duration), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doNext();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        mineController.doSquareLeftClicked(15, 8);
        mineController.reFreshMineInfo(mineInfoMap);
    }

    public void doNext() {

    }

    private void doClick() {

    }

    private void refreshMine() {
        for (int i = 0; i < rowCount; i++) {
            for (int e = 0; e < columnCount; e++) {
                MineInfo mineInfo = mineInfoMap.get(e + ","  + i);
                if (mineInfo.isOpen() && mineInfo.getMineCount() > 0) {
                    int notOpenCount = 0;

                }
            }
        }
    }

}
