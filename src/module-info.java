module app {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.json;

    requires kotlin.reflect;
    requires kotlin.stdlib.jdk7;
    requires kotlinx.coroutines.core;

    exports com.ses.zxdb.dao;
    exports com.ses.app.zxlauncher.model;
    exports com.ses.app.zxlauncher.ui;
    exports com.ses.app.zxlauncher.model.zxdb;

    opens com.ses.app.zxlauncher;
}