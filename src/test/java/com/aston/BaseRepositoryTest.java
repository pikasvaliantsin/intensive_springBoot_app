package com.aston;

import com.aston.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseRepositoryTest {
    protected UserRepository userRepository;
    protected Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        recreateDatabase();
        userRepository = new UserRepository();

        connection = DriverManager.getConnection(
                UserRepository.URL,
                UserRepository.USERNAME,
                UserRepository.PASSWORD
        );
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS users");
        }
        connection.close();
    }

    private void recreateDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                UserRepository.URL,
                UserRepository.USERNAME,
                UserRepository.PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.execute("DROP TABLE IF EXISTS users");

            statement.execute("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, age INT NOT NULL)");
        }
    }
}