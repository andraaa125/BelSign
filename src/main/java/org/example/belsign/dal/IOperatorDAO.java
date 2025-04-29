package org.example.belsign.dal;

import org.example.belsign.be.Operator;

import java.io.IOException;
import java.util.List;

public interface IOperatorDAO {

    List<Operator> getAllOperator() throws IOException;

    List<Operator> getAllOperators() throws IOException;

    void createNewOperator(Operator operator)  throws IOException;
    void updateOperator(Operator operator)   throws IOException;


}
