package com.powerbike.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data                       //Agrega todos los setters y getters
@AllArgsConstructor         //agrega constructor con argumentos
@NoArgsConstructor          //agreaga constructor sin argumentos
@Builder
@Entity                     //Configura la clase como una entidad
@Table(name = "usuario")      //El nombre de la tabla al momento de la creacion
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String username;

    private String lastname;
    private String secondLastname;

    @Email
    @NotBlank
    @Size(max = 80)
    private String email;

    @NotBlank
    private String password;
    private String gender;
    private String phoneNumber;
    private String address;
    private String city;
    private LocalDateTime creationDate;
    private LocalDate dateOfBirth;
    /*//Con este metodo debo parsear la fecha de nacimiento ingresado por el usuario
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString, formatter);*/


    //Con este bloque creamos la tabla intermedia ya que es una relacion muchos a muchos, como es una relacion
    //unidirecional solo traemos los registros de roles, la persistencia es PERSIST ya que si se elimina un usuario
    //no debe borrar el registro en roles
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "IdUsuario"), inverseJoinColumns = @JoinColumn(name = "IdRol"))
    private Set<RoleEntity> roles;

}