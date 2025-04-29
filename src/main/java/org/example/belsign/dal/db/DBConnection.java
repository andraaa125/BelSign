package org.example.belsign.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class DBConnection {
    public Connection getConnection() throws SQLServerException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setDatabaseName("BelsignAH");
        ds.setServerName("EASV-DB4");
        ds.setPortNumber(1433);
        ds.setUser("CSe2024b_e_4");
        ds.setPassword("CSe2024bE4!24");
        ds.setTrustServerCertificate(true);
        return ds.getConnection();
    }
}
