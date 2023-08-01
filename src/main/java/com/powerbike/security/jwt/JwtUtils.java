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

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    //Metodo para generar un token de acceso
    public String generateAccesToken(String username) {
        return Jwts.builder()
                .setSubject(username)       //Sujeto que crea el token
                .setIssuedAt(new Date(System.currentTimeMillis()))      //momento actual en miliseg
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)  //se encripta nuevamente la llave atributo
                .compact();
    }

    //Metodo para validar si el token es valido
    public boolean isTokenValid(String token) {
        //Si el bloque try se ejecuta significa que el token es valido y retorna true
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            log.error(("Token invalido, error: ".concat(e.getMessage())));
            return false;
        }
    }

    //Metodo para obtener los claims(contenido) del token investigar si es un json
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Metodo para obtener un claim de una lista de claims, se utiliza en el metodo extractAllClaims
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    //Obtener el username del token, se utiliza la funcion de getClaim
    public String getUsernameFromToken(String token){
        return getClaim(token,Claims::getSubject);
    }

    //Metodo para firmar el token
    public Key getSignatureKey() {
        byte[] keyBites = Decoders.BASE64.decode(secretKey);        //se desencripta la key anterior
        return Keys.hmacShaKeyFor(keyBites);        //se encripta nuevamente
    }

}
