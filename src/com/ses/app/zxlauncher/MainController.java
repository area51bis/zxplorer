package com.ses.app.zxlauncher;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private TreeView<Category> treeView;

    public MainController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTree();
    }

    private void createTree() {
        TreeItem<Category> root = new TreeItem<>(new Category("root"));
        root.setExpanded(true);
        root.getChildren().addAll(
                new TreeItem<>(new Category("Arcade")),
                new TreeItem<>(new Category("Adventure")),
                new TreeItem<>(new Category("Puzzle"))
        );

        treeView.setRoot(root);

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Category category = newValue.getValue();
            System.out.println("Category: "+category.name);
        });
    }
}
