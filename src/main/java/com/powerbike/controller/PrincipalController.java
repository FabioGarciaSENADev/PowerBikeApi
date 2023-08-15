package com.powerbike.controller;

import com.powerbike.models.ERole;
import com.powerbike.models.RoleEntity;
import com.powerbike.models.UserEntity;
import com.powerbike.objetsDTO.UserDTO;
import com.powerbike.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@RestController
public class PrincipalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World NOT SECURED";
    }

    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello World SECURED";
    }

    @PostMapping("createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO){

        //Capturo fecha String y la convierto a LocalDate para almacenar el usuario
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth = LocalDate.parse(userDTO.getDateOfBirth(), formatter);

        //Construccion del user para almacenar en la BD
        UserEntity userEntity = UserEntity.builder()
                .name(userDTO.getName())
                .lastname(userDTO.getLastname())
                .secondLastname(userDTO.getSecondLastname())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .gender(userDTO.getGender())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .city(userDTO.getCity())
                .dateOfBirth(dateOfBirth)
                .idCard(userDTO.getIdCard())
                .updateDate(LocalDateTime.now())
                .activeUser(true)
                .roles(Set.of(RoleEntity.builder()
                        .name(ERole.valueOf(ERole.USER.name()))
                        .build()))
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok(userEntity);
    }

    @DeleteMapping("deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long idUser) {
        userRepository.deleteById(idUser);
        return "Se a borrado el user con usuario  con id " + idUser;
    }

}
