package org.example.belsign.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.example.belsign.be.Operator;
import org.example.belsign.dal.IOperatorDAO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OperatorDAODB implements IOperatorDAO {
    private final DBConnection con = new DBConnection();

    @Override
    public List<Operator> getAllOperator() throws IOException {
        List<Operator> allOperator = new ArrayList<>();
        String sql = "SELECT * FROM Admin";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int OperatorId = rs.getInt("OperatorID");
                String firstName = rs.getString("First_Name");
                String lastName = rs.getString("Last_Name");

                Operator operator = new Operator(OperatorId, firstName, lastName);
                allOperator.add(operator);
            }

        } catch (SQLServerException e) {
            throw new RuntimeException("SQL Server error: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("SQL error: " + e.getMessage(), e);
        }

        return allOperator;
    }

    @Override
    public List<Operator> getAllOperators() throws IOException {
        return List.of();
    }

    @Override
    public void createNewOperator(Operator operator) throws IOException {
        String sql = "INSERT INTO Admin (OperatorID, First_Name, Last_Name) VALUES (?, ?, ?)";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, operator.getOperatorID());
            ps.setString(2, operator.getOperatorFirstName());
            ps.setString(3, operator.getOperatorLastName());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding operator to the database: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateOperator(Operator operator) throws IOException {
        String sql = "UPDATE Operator SET First_Name = ?, Last_Name = ? WHERE OperatorID = ?";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, operator.getOperatorID());
            ps.setString(2, operator.getOperatorFirstName());
            ps.setString(3, operator.getOperatorLastName());


            int rows = ps.executeUpdate();
            System.out.println("Rows updated: " + rows);
            System.out.println("Attempted to update name to: " + operator.getOperatorFirstName() + " " + operator.getOperatorLastName());

        } catch (SQLException e) {
            throw new RuntimeException("Error updating operator: " + e.getMessage(), e);
        }
    }
}
