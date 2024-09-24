package org.example.basewebsub.auth.beans;


import io.jsonwebtoken.*;
import org.example.basewebsub.auth.enums.TokenType;
import org.example.basewebsub.auth.models.UserPrincipal;
import org.example.basewebsub.comon.ErrorCode;
import org.example.basewebsub.exception.CustomServiceBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
@Component
public class JWTTokenProvider {

    public static final String SCOPES = "scope";
    public static final String AUTHORITIES = "authorities";
    public static final String CLIENT_GROUP = "client_group";
    public static final String CLIENT_ID = "client_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String SITE_ID = "site_id";
    public static final String SITE_CODE = "site_code";
    public static final String RM_CODE = "rm_code";
    public static final String USER_T24 = "user_t24";
    private static final String FILE_PRIVATE_KEY = "privatekey.jks";
    private static final String STORE_PASS = "secret";
    private static final String ALIAS = "odakota";

    @Value("${rsa.private.key:empty}")
    private String privateKey;

    @Value("${jwt.access.validity}")
    private long jwtAccessValidity;

    @Value("${jwt.refresh.validity}")
    private long jwtRefreshValidity;

    @Value("${jwt.check.validity}")
    private long jwtCheckValidity;

    @Value("${jwt.client.group}")
    private String clientGroup;

    public String generateToken(UserPrincipal data) throws CustomServiceBusinessException {
        Claims claims = Jwts.claims();
        if (data.getTokenType().equals(TokenType.ACCESS_TOKEN)) {
            claims.put(SCOPES, data.getScopes());
            claims.put(AUTHORITIES, data.getAuthorities());
            claims.put(CLIENT_GROUP, clientGroup);
        }
        claims.put(CLIENT_ID, data.getClientId());
        claims.put(USER_NAME, data.getUsername());
        claims.put(USER_ID, data.getUserId());
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(data.getExpireAt())
                .addClaims(claims)
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .setId(data.getUuId())
                .compact();
    }

    public UserPrincipal getUserPrincipalFromToken(String token) throws CustomServiceBusinessException {
        Claims claims = getAllClaimsFromToken(token);
        if(claims == null){
            return null;
        }
        UserPrincipal userPrincipal = new UserPrincipal();
        //noinspection unchecked
        userPrincipal.setScopes(claims.get(SCOPES, ArrayList.class));
        //noinspection unchecked
        userPrincipal.setAuthorities((ArrayList<? extends GrantedAuthority>)claims.get(AUTHORITIES, ArrayList.class));
        userPrincipal.setClientId(claims.get(CLIENT_ID, String.class));
        userPrincipal.setUsername(claims.get(USER_NAME, String.class));
        userPrincipal.setUserId(claims.get(USER_ID, Long.class));
        return userPrincipal;

    }

    public Date generateExpireAt(TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            return new Date(new Date().getTime() + jwtAccessValidity);
        }
        if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            return new Date(new Date().getTime() + jwtRefreshValidity);
        }
        return new Date(new Date().getTime() + jwtCheckValidity);
    }

    private Claims getAllClaimsFromToken(String token) throws CustomServiceBusinessException {
        try {
            return Jwts.parser().setSigningKey(getPrivateKey()).parseClaimsJws(token).getBody();
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            //throw new BusinessException(ErrorCode.TOKEN_INVALID);
            return null;
        } catch (ExpiredJwtException e) {
            //throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
            return null;
        }
    }


    private PrivateKey getPrivateKey() throws CustomServiceBusinessException {
        if (!"empty".equals(privateKey)) {
            try {
                privateKey = privateKey.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                        .replaceAll(System.lineSeparator(), "")
                        .replace("-----END RSA PRIVATE KEY-----", "");
                byte[] encoded = Base64.getDecoder().decode(privateKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
                return keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new CustomServiceBusinessException(ErrorCode.SERVER_ERROR);
            }

        }
        return new KeyStoreKeyFactory(new ClassPathResource(FILE_PRIVATE_KEY),
                STORE_PASS.toCharArray()).getKeyPair(ALIAS).getPrivate();
    }
}