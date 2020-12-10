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
    exports com.ses.app.zxplorer.model;
    exports com.ses.app.zxplorer.model.zxcollection;
    exports com.ses.app.zxplorer.zxcollection;
    exports com.ses.app.zxplorer.ui;
    exports com.ses.app.zxplorer.model.zxdb;

    opens com.ses.app.zxplorer;
}