package com.ses.app.zxlauncher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql
public class Main extends Application {
    @Override
    public void start( Stage primaryStage ) throws Exception
    {
        FXMLLoader loader = new FXMLLoader( getClass().getResource( "main.fxml" ) );
        Parent root = loader.load();
        Scene scene = new Scene( root );
        MainController controller = loader.getController();

        primaryStage.setTitle( "Hello World" );
        primaryStage.setScene( scene );
        primaryStage.show();
    }

    public static void main( String[] args )
    {
        launch( args );
    }
}
