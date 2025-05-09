package org.example.belsign.bll;

import org.example.belsign.be.Admin;
import org.example.belsign.dal.IAdminDAO;
import org.example.belsign.dal.db.AdminDAODB;

import java.io.IOException;
import java.util.List;

public class AdminManager {

        private final IAdminDAO adminDAO = new AdminDAODB();

        public List<Admin> getAllAdmin() throws IOException {
            return adminDAO.getAllAdmin();
        }

        public void createNewAdmin(Admin admin) throws IOException {
            adminDAO.createNewAdmin(admin);
        }

        public void updateAdmin(Admin admin) throws IOException {
            adminDAO.updateAdmin(admin);
        }
    }

