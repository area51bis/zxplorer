@echo off
set JAVAFX_HOME=C:\dev\java\jdk\javafx15
java --module-path %JAVAFX_HOME%\lib --add-modules javafx.controls,javafx.graphics,javafx.fxml -jar ZXBrowser.jar
