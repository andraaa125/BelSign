package org.example.belsign.dal.db;

import org.example.belsign.be.Order;
import org.example.belsign.be.User;
import org.example.belsign.dal.IUserDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAODB implements IUserDAO {
    private final DBConnection con = new DBConnection();

    @Override
    public List<User> getAllUsers() throws SQLException, IOException {
        List<User> user = new ArrayList<>();

        String sql = "SELECT UserID, Username, Password, First_Name, Last_Name, Role FROM [User]";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String userId = rs.getString("UserID");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                String first_name = rs.getString("First_Name");
                String last_name = rs.getString("Last_Name");
                String role = rs.getString("Role");


                // Debugging output
                System.out.println("Fetched user ID: " + first_name + last_name);

                // Create Order object with more fields, including images
                user.add(new User(userId, username, password, first_name, last_name, role));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders", e);
        }

        return user;
    }

}
