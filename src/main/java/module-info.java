module Game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires javafx.graphics;
    //requires Game;

    opens Game to com.google.gson, javafx.fxml;
//    opens Game to javafx.fxml;
    exports Game;
}