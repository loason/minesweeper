module com.litj.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.litj.minesweeper to javafx.fxml;
    exports com.litj.minesweeper;
}