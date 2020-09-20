module app {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;

    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires kotlin.stdlib.jdk7;

    exports com.ses.app.zxdb;

    opens com.ses.app.zxlauncher;
}