package org.example.HW19.service;

import org.example.HW19.entity.User;
import org.example.HW19.exceptions.*;
import org.example.HW19.repository.UserRepository;
import org.example.HW19.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        boolean exist = userRepository.existsByEmail(user.getEmail());
        if (!exist) {
            return userRepository.save(user);
        } else throw new UserExistException("user with given email :-- " + user.getEmail() + " -- has already registered, please login.");
    }

    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UserNotFoundException("user with given email :-- " + email + " -- not found.");

        if (user.getPassword().equals(password)) {
            SecurityUtils.setUser(user);
            return true;
        } else {
            throw new InvalidPasswordException("password is wrong.");
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("no user found with this ID."));
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User changePassword(Long id, String email, String newPassword, String repeatPassword) {
        User user = userRepository.findByEmailAndId(email, id);
        if (user != null) {
            if (newPassword.equals(repeatPassword)) {
                user.setPassword(newPassword);
                return userRepository.save(user);
            } else throw new PasswordNotMatchException("the password and its repetition are not the same.");
        } else throw new WrongUserEmailException("the email entered does not belong to you.");
    }
}
