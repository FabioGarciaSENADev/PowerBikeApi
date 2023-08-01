package com.powerbike.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data                       //Agrega todos los setters y getters
@AllArgsConstructor         //agrega constructor con argumentos
@NoArgsConstructor          //agreaga constructor sin argumentos
@Entity                     //Configura la clase como una entidad
@Builder
@Table(name = "usuario")      //El nombre de la tabla al momento de la creacion
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Size(max = 80)
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    private String password;

    //Con este bloque creamos la tabla intermedia ya que es una relacion muchos a muchos, como es una relacion
    //unidirecional solo traemos los registros de roles, la persistencia es PERSIST ya que si se elimina un usuario
    //no debe borrar el registro en roles
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "IdUsuario"), inverseJoinColumns = @JoinColumn(name = "IdRol"))
    private Set<RoleEntity> roles;

}