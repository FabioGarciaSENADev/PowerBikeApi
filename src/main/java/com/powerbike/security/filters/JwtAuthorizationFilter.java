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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Esto clase validara que se envie el token por cada request
//Se valida que el token sea correcto para dar autorizacion
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    //Se hace la inyeccion para manipular el token
    @Autowired
    private JwtUtils jwtUtils;

    //Se inyecta para consultar el usuario en la base de datos
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    //Los parametros no pueden ser null al momento de recibir la request se Utiliza @NonNull
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Se recupera el token del header de la request
        String tokenHeader = request.getHeader("Authorization");

        //Se valida el token no sea null y que empieze con Bearer_
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            //Se elimina el Bearer_ del tokenheader para dejar solo el token
            String token = tokenHeader.substring(7);

            //Se valida si el token es valido
            if (jwtUtils.isTokenValid(token)) {
                String email = jwtUtils.getEmailFromToken(token);//Se recupera el email(username) del token
                //Recuperamos todos los datos del user incluidos los permisos ROLES, retorna User de spring
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                //Instanciamos y realizamos la validacion con el user de spring
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());

                //Autenticacion propia, seteamos la configuracion con la nueva autorizacion
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        //Si no entra al if, dejamoseguir y el filter al no encontrar un token invalidara el ingreso
        filterChain.doFilter(request, response);
    }

}