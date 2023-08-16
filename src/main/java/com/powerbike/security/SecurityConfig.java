package com.powerbike.security;

import com.powerbike.security.filters.JwtAuthenticationFilter;
import com.powerbike.security.filters.JwtAuthorizationFilter;
import com.powerbike.security.jwt.JwtUtils;
import com.powerbike.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

    //Inyectamos nuestra implementacion que es la que trae el usuario de la base d e datos
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    //Inyectamos el nuevo filtro que valida el token
    @Autowired
    JwtAuthorizationFilter authorizationFilter;

    @Autowired
    JwtUtils jwtUtils;


    //Este metodo maneja todos los filtros
    //se agrega el authenticationManager para utilizar el filtro authentication de jwt
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                            AuthenticationManager authenticationManager) throws Exception {

        //Agregamos nuestro filtro, como se necesita agregar parametros no lo podemos manejar como un bean
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");   //Sepuede cambiar la ruta de logueo, investigar mas

        return httpSecurity
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/hello").permitAll();
                    auth.requestMatchers("/createUser").permitAll();
                    auth.anyRequest().authenticated();
                    //Esta es una forma de agregar persmisos por roles, no es la mas optima, con hasany podemos agregar un
                    //arreglo de roles
                    //auth.requestMatchers("/accessAdmin").hasRole("ADMIN");
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                //Esto se ejecuta antes de envio de credenciales, clase JwtAutenticationFilter por herencia
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //Con este metodo creamos un usuario en memoria, este metodo me serviria para recuperacion de contraseña
 /*   @Bean
    UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("Fabio")
                .password("1234")
                .roles()
                .build());
        return manager;
    }*/

    //Este metodo es para la encryptacion de contraseñas
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        //return NoOpPasswordEncoder.getInstance();       //con esto se crea una contraseña encriptada por defecto
    }

    //Es el metodo, objeto que maneja la autenticacion, exige un PasswordEncoder
    //Este metodo recibe el los datos del usuario para darle autenticacion
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)     //
                .userDetailsService(userDetailsService)       //usuario a autenticar se busca en la base de datos
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

}
