package org.example.belsign.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.example.belsign.be.User;
import org.example.belsign.dal.db.UserDAODB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserListViewController {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colFirstName;
    @FXML
    private TableColumn<User, String> colLastName;
    @FXML
    private BorderPane mainBorderPane;

    private Node mainView;
    private final UserDAODB userDAO = new UserDAODB();

    @FXML
    public void initialize() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            ObservableList<User> userList = FXCollections.observableArrayList(users);
            userTable.setItems(userList);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickBack() {
        if (mainBorderPane != null && mainView != null) {
            mainBorderPane.setCenter(mainView);
        }
    }

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    public void setMainView(Node dashboardMainView) {
        this.mainView = dashboardMainView;
    }
}
