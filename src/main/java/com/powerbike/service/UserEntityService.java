package com.powerbike.service;

import com.powerbike.models.ERole;
import com.powerbike.models.RoleEntity;
import com.powerbike.models.UserEntity;
import com.powerbike.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserEntityService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> getAll() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public Optional<UserEntity> getUser(Long id) {
        return userRepository.findById(id);
    }

    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            return userRepository.save(user);
        } else {
            Optional<UserEntity> userAux = userRepository.findById(user.getId());
            if (userAux.isEmpty()) {
                return userRepository.save(user);
            } else {
                return user;
            }
        }
    }

    public UserEntity update(UserEntity user) {
        if (user.getId() != null) {
            Optional<UserEntity> userOptional = userRepository.findById(user.getId());
            if (!userOptional.isEmpty()) {
                if (user.getEmail() != null) {
                    userOptional.get().setEmail(user.getEmail());
                }
                if (user.getUsername() != null) {
                    userOptional.get().setUsername(user.getUsername());
                }
                if (user.getPassword() != null) {
                    userOptional.get().setPassword(user.getPassword());
                }

                userRepository.save(userOptional.get());
                return userOptional.get();
            } else {
                return user;
            }
        } else {
            return user;
        }
    }

    public boolean deleteUser(Long userId) {
        Boolean aBoolean = getUser(userId).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
        return aBoolean;
    }


}
