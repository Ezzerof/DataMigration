package com.dataMigration.database;

import com.dataMigration.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {

    private static final String URL = "jdbc:mysql://localhost:3306/employeemigration";
    private static Connection connection;
    private static Properties properties = new Properties();
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private ConnectionProvider() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                properties.load(new BufferedReader(new FileReader("src\\main\\resources\\login.properties")));
                connection = DriverManager.getConnection(URL, properties.getProperty("username"), properties.getProperty("password"));
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }
        return connection;
    }
}
