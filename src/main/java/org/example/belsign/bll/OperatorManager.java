package org.example.belsign.bll;

import org.example.belsign.be.Operator;
import org.example.belsign.dal.IOperatorDAO;
import org.example.belsign.dal.db.OperatorDAODB;

import java.io.IOException;
import java.util.List;

public class OperatorManager {

    private final IOperatorDAO operatorDAO = new OperatorDAODB();

    public List<Operator> getAllOperator() throws IOException {
        return operatorDAO.getAllOperator();
    }

    public void createNewOperator(Operator operator) throws IOException {
        operatorDAO.createNewOperator(operator);
    }

    public void updateOperator(Operator operator) throws IOException {
        operatorDAO.updateOperator(operator);
    }
}

