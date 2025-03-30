package com.aston;

import com.aston.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryIntegrationTest extends BaseRepositoryTest {
    private static final String BOB_NAME = "Bob";
    private static final Integer BOB_AGE = 18;
    private static final String TED_NAME = "Ted";
    private static final Integer TED_AGE = 20;


    @Test
    void saveUser_whenUserValid_ShouldSaveUserToDataBase() {
        userRepository.saveUser(BOB_NAME, BOB_AGE);

        Optional<User> user = userRepository.findUserById(1L);

        assertTrue(user.isPresent());
        assertEquals(BOB_NAME, user.get().getName());
        assertEquals(BOB_AGE, user.get().getAge());
    }

    @Test
    void findAllUsers_whenUsersExists_shouldReturnAllUsers() {
        userRepository.saveUser(BOB_NAME, BOB_AGE);
        userRepository.saveUser(TED_NAME, TED_AGE);
        userRepository.saveUser(BOB_NAME, TED_AGE);

        List<User> allUsers = userRepository.findAllUsers();

        assertEquals(3, allUsers.size());
    }

    @Test
    void findAllUsers_whenDatabaseEmpty_shouldReturnEmptyList() {
        List<User> allUsers = userRepository.findAllUsers();

        assertEquals(0, allUsers.size());
    }

    @Test
    void findUserById_whenUserExist_shouldReturnUser() {
        userRepository.saveUser(TED_NAME, TED_AGE);

        Optional<User> user = userRepository.findUserById(1L);

        assertTrue(user.isPresent());
    }

    @Test
    void findUserById_whenUserNonExist_shouldReturnEmptyOptional() {
        assertTrue(userRepository.findUserById(999L).isEmpty());
    }

    @Test
    void updateUser_whenUserExist_shouldUpdateUserData() {
        userRepository.saveUser(BOB_NAME, BOB_AGE);
        userRepository.updateUser(1L, TED_NAME, BOB_AGE);
        Optional<User> updatedUser = userRepository.findUserById(1L);

        assertTrue(updatedUser.isPresent());
        assertEquals(TED_NAME, updatedUser.get().getName());
        assertEquals(BOB_AGE, updatedUser.get().getAge());
    }

    @Test
    void updateUser_whenUserNotExist_shouldThrowException() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userRepository.updateUser(999L, BOB_NAME, BOB_AGE)
        );
        assertEquals("User with id 999 not found", exception.getMessage());
    }

    @Test
    void deleteUser_whenUserExist_shouldDeleteUser() {
        userRepository.saveUser(BOB_NAME, BOB_AGE);
        userRepository.deleteUser(1L);
        Optional<User> deletedUser = userRepository.findUserById(1L);

        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void deleteUser_whenUserNonExist_shouldThrowException() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userRepository.deleteUser(999L)
        );
        assertEquals("Failed delete user from database", exception.getMessage());
    }
}