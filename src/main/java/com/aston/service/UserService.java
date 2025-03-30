package com.aston.service;

import com.aston.entity.User;
import com.aston.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.aston.validator.UserValidator.*;

@RequiredArgsConstructor
public class UserService {
    private static final int LEGAL_AGE = 18;

    private final UserRepository userRepository;

    public void saveUser(String name, Integer age) {
        validateName(name);
        validateAge(age);

        userRepository.saveUser(name, age);
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public Optional<User> findUserById(Long id) {
        validateId(id);

        return userRepository.findUserById(id);
    }

    public void updateUser(Long id, String name, Integer age) {
        validateId(id);
        validateName(name);
        validateAge(age);

        userRepository.updateUser(id, name, age);
    }

    public void deleteUser(Long id) {
        validateId(id);

        userRepository.deleteUser(id);
    }

    public boolean isAdultUser(Long id) {
        Optional<User> user = findUserById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        if (user.get().getAge() == null) {
            throw new IllegalStateException("User age is not specified");
        }
        return user.get().getAge() >= LEGAL_AGE;
    }
}