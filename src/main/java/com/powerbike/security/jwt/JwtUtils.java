package com.powerbike.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j      //Lombok implementa una interfaz de manejo de errores simple
public class JwtUtils {

    //Se traen los valores del aplicaction properties con la etiqueta value
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    //Metodo para generar un token de acceso con las dependencias de JWT
    public String generateAccesToken(String email) {
        return Jwts.builder()
                .setSubject(email)                                          //Sujeto que crea el token
                .setIssuedAt(new Date(System.currentTimeMillis()))          //Fecha de creacion del token con hora
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)      //se encripta nuevamente en HS256
                .compact();
    }

    //Metodo para validar si el token es valido, recibe el token por parametro
    public boolean isTokenValid(String token) {
        //Tomamos el token y los desestructuramos, Si el bloque try se ejecuta significa que el token es valido y retorna true
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }
        //Si se genera alguna excepcion significa que el token fue manipulado (no valido) y retorna false
        catch (Exception e) {
            //Esta linea la utilizamos gracias a @Slj4j
            log.error(("Token invalido, error: ".concat(e.getMessage())));
            return false;
        }
    }

    //Metodo para obtener los claims(contenido) del token retorna el bogy del token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Metodo para obtener un claim del body del token, se utiliza en el metodo extractAllClaims
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    //Obtener el email del token (subject), se utiliza la funcion de getClaim y recibe subject del token como generico
    public String getEmailFromToken(String token){
        return getClaim(token,Claims::getSubject);
    }

    //Metodo para firmar el token al momento de crear
    public Key getSignatureKey() {
        byte[] keyBites = Decoders.BASE64.decode(secretKey);        //se desencripta la secret key
        return Keys.hmacShaKeyFor(keyBites);                        //se encripta nuevamente
    }

}
