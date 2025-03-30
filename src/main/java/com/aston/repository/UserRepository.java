package com.aston.repository;

import com.aston.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    public static final String URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "1234";

    private static final String INIT_DATABASE_SQL = "CREATE TABLE IF NOT EXISTS users (id BIGINT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(255) NOT NULL, age  INT NOT NULL)";
    private static final String SAVE_USER_SQL = "INSERT INTO users (name, age) VALUES (?, ?)";
    private static final String FIND_ALL_SQL = "SELECT * FROM users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_USER_SQL = "UPDATE users SET name = ?, age = ? WHERE id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";

    public UserRepository() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.execute(INIT_DATABASE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init database");
        }
    }

    public void saveUser(String name, Integer age) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(SAVE_USER_SQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user to database", e);
        }
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL)) {
            while (resultSet.next()) {
                users.add(new User(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getInt("age")));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all users from database", e);
        }
    }

    public Optional<User> findUserById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(
                                    resultSet.getLong("id"),
                                    resultSet.getString("name"),
                                    resultSet.getInt("age")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user by id from database", e);
        }
        return Optional.empty();
    }

    public void updateUser(Long id, String name, Integer age) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setLong(3, id);

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new RuntimeException("User with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed update user to database", e);
        }
    }

    public void deleteUser(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {
            preparedStatement.setLong(1, id);

            int changedRows = preparedStatement.executeUpdate();
            if (changedRows == 0) {
                throw new SQLException("Incorrect id=" + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed delete user from database", e);
        }
    }
}