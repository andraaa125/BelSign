package org.example.belsign.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.example.belsign.be.Admin;
import org.example.belsign.dal.IAdminDAO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAODB implements IAdminDAO {
    private final DBConnection con = new DBConnection();

    @Override
    public List<Admin> getAllAdmins() throws IOException {
        List<Admin> allAdmin = new ArrayList<>();
        String sql = "SELECT * FROM Admin";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int adminId = rs.getInt("ID");
                String firstName = rs.getString("First_name");
                String lastName = rs.getString("Last_name");

                Admin admin = new Admin(adminId, firstName, lastName);
                allAdmin.add(admin);
            }

        } catch (SQLServerException e) {
            throw new RuntimeException("SQL Server error: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("SQL error: " + e.getMessage(), e);
        }

        return allAdmin;
    }

    @Override
    public List<Admin> getAllAdmin() {
        return List.of();
    }

    @Override
    public void createNewAdmin(Admin admin) throws IOException {
        String sql = "INSERT INTO Admin (First_name, Last_name, ID) VALUES (?, ?, ?)";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, admin.getAdminFirstName());
            ps.setString(2, admin.getAdminLastName());
            ps.setInt(3, admin.getAdminID());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding admin to the database: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAdmin(Admin admin) throws IOException {
        String sql = "UPDATE Admin SET First_name = ?, Last_name = ? WHERE ID = ?";

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, admin.getAdminFirstName());
            ps.setString(2, admin.getAdminLastName());
            ps.setInt(3, admin.getAdminID());

            int rows = ps.executeUpdate();
            System.out.println("Rows updated: " + rows);
            System.out.println("Attempted to update name to: " + admin.getAdminFirstName() + " " + admin.getAdminLastName());

        } catch (SQLException e) {
            throw new RuntimeException("Error updating admin: " + e.getMessage(), e);
        }
    }
}