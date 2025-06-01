package org.example.belsign.dal.db;

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

                System.out.println("Fetched user ID: " + first_name + last_name);

                user.add(new User(userId, username, password, first_name, last_name, role));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch orders", e);
        }

        return user;
    }

    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO [User] (Username, Password, First_Name, Last_Name, Role) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getRole());

            ps.executeUpdate();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM [User] WHERE Username = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        }
        return false;
    }
}