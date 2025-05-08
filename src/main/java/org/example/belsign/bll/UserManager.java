package org.example.belsign.bll;

import org.example.belsign.be.User;
import org.example.belsign.dal.IUserDAO;
import org.example.belsign.dal.db.UserDAODB;
import org.example.belsign.util.PasswordUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserManager {
    private final IUserDAO userDAO = new UserDAODB();

    public List<User> getAllUsers() throws IOException, SQLException {
        return userDAO.getAllUsers();
    }

    public User authenticate(String username, String password) throws IOException, SQLException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                    return user; // valid login
                }
                break;
            }
        }
        return null; // invalid login
    }
}

