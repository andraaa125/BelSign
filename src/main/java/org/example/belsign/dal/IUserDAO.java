package org.example.belsign.dal;

import org.example.belsign.be.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    List<User> getAllUsers() throws SQLException, IOException;
}
