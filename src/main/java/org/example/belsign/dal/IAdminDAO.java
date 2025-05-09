package org.example.belsign.dal;

import org.example.belsign.be.Admin;

import java.io.IOException;
import java.util.List;

public interface IAdminDAO {

    List<Admin> getAllAdmins() throws IOException;

    List<Admin> getAllAdmin() throws IOException;
    void createNewAdmin(Admin admin)  throws IOException;
    void updateAdmin(Admin admin)   throws IOException;

}
