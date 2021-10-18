package dgac.seguridad;

import static dgac.seguridad.Constants.HEADER_AUTHORIZACION_KEY;
import static dgac.seguridad.Constants.ISSUER_INFO;
import static dgac.seguridad.Constants.SUPER_SECRET_KEY;
import static dgac.seguridad.Constants.TOKEN_BEARER_PREFIX;
import static dgac.seguridad.Constants.TOKEN_EXPIRATION_TIME;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import dgac.seguridad.entidades.BinUsuario;
import dgac.seguridad.grupos.dao.UsuariosDao;
import dgac.utilidades.MD5Digest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	private UsuariosDao usuariosDao;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UsuariosDao usuariosDao) {
		this.authenticationManager = authenticationManager;
		this.usuariosDao = usuariosDao;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder(4);
	}

	/**
	 * Mapea un nuevo objeto con las credenciales de usuario y contrasenia
	 * <p>
	 * Este metodo crea el objeto llamado 'credenciales' que contiene los datos de
	 * usuario, contraseña, email, obeservaciones ...
	 * @return authenticationManager Token de autentificacion de usuario y contrasenia
	 * @throws RuntimeException
	 * @see
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			// String res =
			// request.getInputStream().toString();//{"status":"true","msg":"success"}
			BinUsuario credenciales = new ObjectMapper().readValue(request.getInputStream(), BinUsuario.class);
			String usuario = credenciales.getUsuario();
			String dbPass = usuariosDao.dameContrasenia(credenciales.getUsuario());
			if (dbPass.length() != 60) {// not bcrypt
				String contraseniaMD5 = "";
				try {
					contraseniaMD5 = MD5Digest.convierteToMd5(credenciales.getPassword());
				}
				catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				if (dbPass.equals(contraseniaMD5)) {
					BCryptPasswordEncoder encoder = bCryptPasswordEncoder();
					String passBcrypt = encoder.encode(credenciales.getPassword());
					// usuariosDao.cambiaContrasenia(usuario, contraseniaMD5, passBcrypt);
				}
				else {
					throw new Exception("ERROR: contraseña incorrecta");
				}
			}
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credenciales.getUsuario(),
					credenciales.getPassword(), new ArrayList<>()));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		String token = Jwts.builder()
				// .setIssuedAt(new Date()).setIssuer(ISSUER_INFO)
				.setSubject(((User) auth.getPrincipal()).getUsername())
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256, SUPER_SECRET_KEY.getBytes()).compact();
		response.addHeader(HEADER_AUTHORIZACION_KEY, TOKEN_BEARER_PREFIX + " " + token);
	}

}
