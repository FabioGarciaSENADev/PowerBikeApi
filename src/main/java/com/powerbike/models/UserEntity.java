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

@Data                           //Etiqueta para agregar todos los setters y getters
@AllArgsConstructor             //Etiqueta para agregar constructor con argumentos
@NoArgsConstructor              //Etiqueta para agreagar constructor sin argumentos
@Builder                        //Etiqueta para implementra patron de diseño builder
@Entity                         //Configura la clase como una entidad
@Table(name = "users")          //Etiqueta para colocar nombre de la tabla al momento de la creacion en BD
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String name;

    @NotBlank
    @Size(max = 30)
    private String lastname;

    @NotBlank
    @Size(max = 30)
    private String secondLastname;

    @Email
    @NotBlank
    @Size(max = 80)
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String gender;

    @NotBlank
    @Size(max = 80)
    private String phoneNumber;

    @NotBlank
    @Size(max = 80)
    private String address;

    @NotBlank
    private String city;


    private LocalDate dateOfBirth;
    /*//Con este metodo debo parsear la fecha de nacimiento ingresado por el usuario
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString, formatter);*/

    @NotBlank
    private String idCard;

    private LocalDateTime updateDate;

    private boolean activeUser;

    //Con este bloque creamos la tabla intermedia ya que es una relacion muchos a muchos, como es una relacion
    //unidirecional solo traemos los registros de roles, la persistencia es PERSIST ya que si se elimina un usuario
    //no debe borrar el registro en roles
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "IdUser"), inverseJoinColumns = @JoinColumn(name = "IdRol"))
    private Set<RoleEntity> roles;

}