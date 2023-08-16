package com.powerbike.security.filters;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerbike.models.UserEntity;
import com.powerbike.repositories.UserRepository;
import com.powerbike.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtils jwtUtils;

    //Se hace inyeccion de jwtUtils por constructor
    public JwtAuthenticationFilter(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    //Esto metodo se realizaara cuando el usuario intente registrase, que se va hacer cuando se intenta autenticar
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        UserEntity userEntity = null;
        String username = "";
        String password = "";

        try{
            //Utilizamos la libreria de jackson (Spring) para mapear de Json al objeto UserEntity
            //que viene el request y recuperamos el email(username) y el password
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            username = userEntity.getEmail();
            password = userEntity.getPassword();
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Si el try no da ninguna excepcion podremos autenticarnos
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        //Este es el objeto que se encarga de dar autenticacion, pasamos el objeto autenticationToken que contiene las
        //las credenciales del ususario
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    //Que se hace si la autenticacion es correcta
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {


        //Utilizamso el User de Spring Recuperamos el objeto que contiene los detalles del usuario
        User user = (User) authResult.getPrincipal();

        //Procedemos a crear el token con el username del userSpring (por eso no es email de UserEntity)
        String token = jwtUtils.generateAccesToken(user.getUsername());

        //En el header de la respuesta se devuelve el token
        response.addHeader("Authorization", token);

        //Mapeamos la respuesta (response), devolvera el token, un mensaje, y el username al que se le creo el token
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("Message", "Autenticacion Correcta");
        httpResponse.put("Username", user.getUsername());
        httpResponse.put("Role", "ROLE_USER");
        httpResponse.put("token", token);

        //Convertimos la respuesta en Json
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse)); //Convertimos el mapa en Json
        response.setStatus(HttpStatus.OK.value());      //Retorna 200 en la respuesta
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);      //retornara un aplycation/json esto es un Enum
        response.getWriter().flush();       //se genera correctamente la respuesta

        super.successfulAuthentication(request, response, chain, authResult);
    }
}