package com.powerbike.controller;

import com.powerbike.models.UserEntity;
import com.powerbike.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/User")    //URL donde se va a atacar el controlador
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class UserEntityController {

    @Autowired
    private UserEntityService userEntityService;

    //Peticion GET Consultar todo
    @GetMapping("/all")
    public List<UserEntity> getClients() {
        return userEntityService.getAll();
    }

    //Peticion GET Consultar con id
    @GetMapping("/{idUser}")
    public Optional<UserEntity> getClient(@PathVariable("idUser") Long id) {
        return userEntityService.getUser(id);
    }

    //Peticion POST Crear
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity save(@RequestBody UserEntity user) {
        return userEntityService.save(user);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity update(@RequestBody UserEntity user) {
        return userEntityService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@PathVariable("id") Long idUser) {
        return userEntityService.deleteUser(idUser);
    }

}
