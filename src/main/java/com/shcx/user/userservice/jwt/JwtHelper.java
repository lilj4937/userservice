package com.shcx.user.userservice.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT
 * 
 * @Title JwtHelper.java
 * @description TODO
 * @time 2017年11月22日 下午2:19:12
 * @author LILJ
 * @version 1.0
 */
public class JwtHelper {

	public final static String JWTSECURITYKEY = "Yo4gwv8XVkaWVuY2UiXJuYW1lIiwidXNlIjoiaXNzdWV";

	/**
	 * 验证token
	 * 
	 * @param jsonWebToken
	 * @return Claims
	 * @time 2017年11月22日 下午2:19:22
	 * @author LILJ
	 */
	public static Claims parseJWT(String jsonWebToken) {
		try {
			Claims claims = Jwts
					.parser()
					.setSigningKey(
							DatatypeConverter.parseBase64Binary(JWTSECURITYKEY))
					.parseClaimsJws(jsonWebToken).getBody();
			return claims;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 创建token
	 * 
	 * @param userName
	 * @param userId
	 * @param roleName
	 * @param audience
	 * @param issuer
	 * @param nowDate
	 * @param expDate
	 * @return String
	 * @time 2017年11月22日 下午5:13:13
	 * @author LILJ
	 */
	public static String createJWT(String userName, String userId,
			String roleName, String audience, String issuer, Date nowDate,
			Date expDate) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		// 生成签名密钥
		byte[] apiKeySecretBytes = DatatypeConverter
				.parseBase64Binary(JWTSECURITYKEY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes,
				signatureAlgorithm.getJcaName());

		// 添加构成JWT的参数
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
				.claim("role", roleName).claim("username", userName)
				.claim("userid", userId).setIssuer(issuer)
				.setAudience(audience).signWith(signatureAlgorithm, signingKey)
				.setExpiration(expDate).setNotBefore(nowDate);

		// 生成JWT
		return builder.compact();
	}
}
