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
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  //Habilitamos anotaciones para uso en lso roles de los controladores
public class SecurityConfig {

    //Inyectamos nuestra implementacion que es la que trae el usuario de la base d e datos
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    //Inyectamos el nuevo filtro que valida el token
    @Autowired
    JwtAuthorizationFilter authorizationFilter;

    @Autowired
    JwtUtils jwtUtils;

    //Con este bean configuramos la seguridad de la aplicacion
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                            AuthenticationManager authenticationManager) throws Exception {

        //Agregamos nuestro filtro, como se necesita agregar parametro no lo podemos manejar como un bean
        JwtAuthenticationFilter jwtAuthenticationFilter =new JwtAuthenticationFilter(jwtUtils);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");    //Esta linea se puede omitir ya que viene por defecto

        return httpSecurity
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login").permitAll();
                    //Esta es una forma de agregar persmisos por roles, no es la mas optima, con hasany podemos agregar un
                    //arreglo de roles
                    //auth.requestMatchers("/accessAdmin").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

/*    //Con este metodo creamos un usuario en memoria
    @Bean
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
       // return NoOpPasswordEncoder.getInstance();       //de momento no se maneja la encriptacion de la contraseña
    }

    //Es el metodo, objeto que maneja la autenticacion, exige un PasswordEncoder
    //puede devolver una excepcion y el throws del mismo metodo la maneja
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)     //
                .userDetailsService(userDetailsService)       //usuario a autenticar se busca en la base de datos
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

}
