package com.aston;

import com.aston.entity.User;
import com.aston.repository.UserRepository;
import com.aston.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Integer LEGAL_AGE = 18;
    private static final String EMPTY_NAME = "";

    private static final String ID_NULL_EXCEPTION_MESSAGE = "User id cannot be null";
    private static final String NAME_EMPTY_EXCEPTION_MESSAGE = "User name cannot be empty";
    private static final String NAME_NULL_EXCEPTION_MESSAGE = "User name cannot be null";
    private static final String AGE_NEGATIVE_EXCEPTION_MESSAGE = "User age cannot be negative";
    private static final String AGE_NULL_EXCEPTION_MESSAGE = "User age is not specified";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static Stream<Arguments> invalidUserDataProvider() {
        return Stream.of(
                Arguments.of("saveUser", null, LEGAL_AGE, IllegalArgumentException.class, NAME_NULL_EXCEPTION_MESSAGE),
                Arguments.of("saveUser", EMPTY_NAME, LEGAL_AGE, IllegalArgumentException.class, NAME_EMPTY_EXCEPTION_MESSAGE),
                Arguments.of("saveUser", "ValidName", -1, IllegalArgumentException.class, AGE_NEGATIVE_EXCEPTION_MESSAGE),
                Arguments.of("updateUser", null, LEGAL_AGE, IllegalArgumentException.class, NAME_NULL_EXCEPTION_MESSAGE),
                Arguments.of("updateUser", EMPTY_NAME, LEGAL_AGE, IllegalArgumentException.class, NAME_EMPTY_EXCEPTION_MESSAGE),
                Arguments.of("updateUser", "ValidName", -1, IllegalArgumentException.class, AGE_NEGATIVE_EXCEPTION_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUserDataProvider")
    void saveUserAndUpdateUser_whenNameOrAgeInvalid_shouldThrowException(
            String methodName,
            String name,
            Integer age,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) {
        Executable action = () -> {
            switch (methodName) {
                case "saveUser" -> userService.saveUser(name, age);
                case "updateUser" -> userService.updateUser(1L, name, age);
                default -> throw new IllegalArgumentException("Unknown method: " + methodName);
            }
        };

        Exception exception = assertThrows(expectedException, action);

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidUserIdDataProvider() {
        return Stream.of(
                Arguments.of("updateUser", null, IllegalArgumentException.class, ID_NULL_EXCEPTION_MESSAGE),
                Arguments.of("findUserById", null, IllegalArgumentException.class, ID_NULL_EXCEPTION_MESSAGE),
                Arguments.of("deleteUser", null, IllegalArgumentException.class, ID_NULL_EXCEPTION_MESSAGE),
                Arguments.of("isAdultUser", null, IllegalArgumentException.class, ID_NULL_EXCEPTION_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUserIdDataProvider")
    void allMethodsWithIdArgument_whenIdIsNull_shouldThrowException(
            String methodName,
            Long id,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) {
        Executable action = () -> {
            switch (methodName) {
                case "updateUser" -> userService.updateUser(id, "Valid name", LEGAL_AGE);
                case "findUserById" -> userService.findUserById(id);
                case "deleteUser" -> userService.deleteUser(id);
                case "isAdultUser" -> userService.isAdultUser(id);
                default -> throw new IllegalArgumentException("Unknown method: " + methodName);
            }
        };

        Exception exception = assertThrows(expectedException, action);

        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, never()).updateUser(any(), any(), any());
        verify(userRepository, never()).findUserById(any());
        verify(userRepository, never()).deleteUser(any());
    }

    @Test
    void isAdultUser_whenUserIsAdult_shouldReturnTrue() {
        Long userId = 1L;
        User adultUser = new User(userId, "Adult", LEGAL_AGE);

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(adultUser));

        boolean result = userService.isAdultUser(userId);

        assertTrue(result);
        verify(userRepository).findUserById(userId);
    }

    @Test
    void isAdultUser_whenUserIsNotAdult_shouldReturnFalse() {
        Long userId = 2L;
        User minorUser = new User(userId, "Minor", LEGAL_AGE - 1);

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(minorUser));

        boolean result = userService.isAdultUser(userId);

        assertFalse(result);
        verify(userRepository).findUserById(userId);
    }

    @Test
    void isAdultUser_whenUserIsNotFound_shouldThrowException() {
        Long userId = 999L;

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.isAdultUser(userId)
        );
        assertEquals("User with id 999 not found", exception.getMessage());
    }

    @Test
    void isAdultUser_whenUserAgeIsNull_shouldThrowException() {
        Long userId = 3L;
        User userWithNullAge = new User();
        userWithNullAge.setAge(null);
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(userWithNullAge));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.isAdultUser(userId)
        );

        assertEquals(AGE_NULL_EXCEPTION_MESSAGE, exception.getMessage());
    }
}