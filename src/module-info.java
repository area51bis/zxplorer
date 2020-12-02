module app {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires com.google.gson;

    requires kotlin.reflect;
    requires kotlin.stdlib.jdk7;
    requires kotlinx.coroutines.core;

    exports com.ses.zxdb.dao;
    exports com.ses.app.zxbrowser.model;
    exports com.ses.app.zxbrowser.ui;
    exports com.ses.app.zxbrowser.model.zxdb;

    opens com.ses.app.zxbrowser;
}