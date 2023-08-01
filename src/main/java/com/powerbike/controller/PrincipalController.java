package com.powerbike.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrincipalController {

    //Pendiente crear el enpoint para guardar usuarios al momento de emandar contraseña encriptar con
    //con bcryencoder creando un objeto del mismo tipo

    @GetMapping("/hello")
    public String hello(){
        return "Hello World NOT SECURED";
    }

    @GetMapping("/helloSecured")
    public String helloSecured(){
        return "Hello World SECURED";
    }


}
