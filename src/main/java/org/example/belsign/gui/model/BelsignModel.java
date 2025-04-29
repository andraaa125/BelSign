package org.example.belsign.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.belsign.be.Admin;
import org.example.belsign.be.Operator;
import org.example.belsign.bll.AdminManager;
import org.example.belsign.bll.OperatorManager;

import java.io.IOException;
import java.util.List;

public class BelsignModel {

    private final AdminManager adminManager = new AdminManager();
    private final ObservableList<Admin> allAdmin = FXCollections.observableArrayList();

    private final OperatorManager operatorManager = new OperatorManager();
    private final ObservableList<Operator> allOperator = FXCollections.observableArrayList();

    /// Admin///
    public ObservableList<Admin> getAllAdmin() throws IOException {
        List<Admin> adminList = adminManager.getAllAdmin();
        allAdmin.setAll(adminList);
        return allAdmin;
    }
    public void createNewAdmin(Admin admin) throws IOException {
        adminManager.createNewAdmin(admin);
    }

    public void updateAdmin(Admin admin) throws IOException {
        adminManager.updateAdmin(admin);
    }

    ///Operator///
    public ObservableList<Operator> getAllOperator() throws IOException {
        List<Operator> operatorList = operatorManager.getAllOperator();
        allOperator.setAll(operatorList);
        return allOperator;
    }
    public void createNewOperator(Operator operator) throws IOException {
        operatorManager.createNewOperator(operator);
    }
    public void updateOperator(Operator operator) throws IOException {
        operatorManager.updateOperator(operator);
    }
}
