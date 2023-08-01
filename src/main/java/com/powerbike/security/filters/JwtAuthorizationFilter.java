package com.powerbike.security.filters;
import com.powerbike.security.jwt.JwtUtils;
import com.powerbike.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Esto clase validara que se envie el token por cada request
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    //Se hace la inyeccion para manipular el token
    @Autowired
    private JwtUtils jwtUtils;

    //Se inyecta para consultar el usuario en la base de datos
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    //Los parametros no pueden ser null al momento de recibir la request
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Se recupera el token del header de la request
        String tokenHeader = request.getHeader("Authorization");

        //Se valida el token que sea valido
        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            String token = tokenHeader.substring(7);    //Se elimina el Bearer_ del tokenheader para dejar solo el token

            //Se valida si el token es valido
            if(jwtUtils.isTokenValid(token)){
                String username = jwtUtils.getUsernameFromToken(token);//Se recupera el username del token
                //Recuperamos todos los datos del user incluidos los permisos ROLES, retorna User de spring
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //Instanciamos y realizamos la validacion con el user de spring
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                //Autenticacion propia, seteamos la configuracion con la nueva autorizacion
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        //Si no entra al if, dejamoseguir y el filter al no encontrar un token invalidara el ingreso
        filterChain.doFilter(request, response);
    }
}