package com.powerbike.security.filters;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerbike.models.UserEntity;
import com.powerbike.security.jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
//Esto metodo se realizaara cuando el usuario inyente registrase
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        UserEntity userEntity = null;
        String username = "";
        String password = "";
        try{
            //Con este posemos poblar el objeto UserEntity, si no hay ningun error nos podemos loggear
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            username = userEntity.getUsername();
            password = userEntity.getPassword();
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Con esto nos autenticamos
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    //Que se hace si la autenticacion es correcta
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        //Recuperamos el objeto con los dettales del usuario
        User user = (User) authResult.getPrincipal();
        //Procedemos a crear el token con el username del userSpring
        String token = jwtUtils.generateAccesToken(user.getUsername());

        //En el header de la respuesta se devuelve el token
        response.addHeader("Authorization", token);

        //Mapeamos la respuesta, devolvera el token, un mensaje, y el username al que se le creo el token
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("Message", "Autenticacion Correcta");
        httpResponse.put("Username", user.getUsername());

        //Convertimos la respuesta en Json
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse)); //Convertimos el mapa en Json
        response.setStatus(HttpStatus.OK.value());      //Retorna 200 en la respuesta
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);      //retornara un aplycation/json
        response.getWriter().flush();       //se genera correctamente la respuesta

        super.successfulAuthentication(request, response, chain, authResult);
    }
}