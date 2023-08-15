
package com.powerbike.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

    @GetMapping("/accessAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    //@PreAuthorize("hasAnyRole('ADMIN',USER)") Esta es la forma de dar varios roles a un acceso
    public String accessAdmin(){
        return "Hola, has accedido como ADMIN";
    }

    @GetMapping("/accessUser")
    @PreAuthorize("hasRole('USER')")
    public String accessUser(){
        return "Hola, has accedido como USER";
    }

    @GetMapping("/accessAcudiente")
    @PreAuthorize("hasRole('ACUDIENTE')")
    public String accessAcudiente(){
        return "Hola, has accedido como ACUDIENTE";
    }
}

