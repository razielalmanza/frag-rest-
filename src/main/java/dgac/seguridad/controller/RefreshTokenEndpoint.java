package dgac.seguridad.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dgac.seguridad.Constants;
import dgac.seguridad.JwtSettings;
import dgac.seguridad.JwtToken;
import dgac.seguridad.JwtTokenFactory;
import dgac.seguridad.RawAccessJwtToken;
import dgac.seguridad.RefreshToken;
import dgac.seguridad.TokenExtractor;
import dgac.seguridad.TokenVerifier;
import dgac.seguridad.UserContext;

/**
 * RefreshTokenEndpoint
 *
 * @author vladimir.stankovic
 *
 * Aug 17, 2016
 */
@RestController
public class RefreshTokenEndpoint {

	@Autowired
	private JwtTokenFactory tokenFactory;

	@Autowired
	private JwtSettings jwtSettings;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private TokenVerifier tokenVerifier;

	@Autowired
	@Qualifier("jwtHeaderTokenExtractor")
	private TokenExtractor tokenExtractor;

	@RequestMapping(value = "/api/auth/token", method = RequestMethod.GET,
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String tokenPayload = tokenExtractor.extract(request.getHeader(Constants.HEADER_AUTHORIZACION_KEY));

		RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
		// RefreshToken refreshToken = RefreshToken.create(rawToken,
		// jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());
		RefreshToken refreshToken;
		try {
			refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey());
		}
		catch (Exception e) {
			throw new Exception("ERROR: InvalidJwtToken");
		}

		String jti = refreshToken.getJti();
		if (!tokenVerifier.verify(jti)) {
			throw new Exception("ERROR: InvalidJwtToken");
		}

		String subject = refreshToken.getSubject();
		UserDetails user;
		// User user = userService.getByUsername(subject).orElseThrow(() -> new
		// UsernameNotFoundException("User not found: " + subject));
		try {
			user = userDetailsService.loadUserByUsername(subject);
		}
		catch (Exception e) {
			throw e;
		}

		// if (user.getRoles() == null) throw new
		// InsufficientAuthenticationException("User has no roles assigned");
		if (user.getAuthorities() == null)
			throw new InsufficientAuthenticationException("User has no roles assigned");
		List<GrantedAuthority> authorities = user.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());

		UserContext userContext = UserContext.create(user.getUsername(), authorities);

		return tokenFactory.createAccessJwtToken(userContext);
	}

}
