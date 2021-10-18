package dgac.seguridad;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;

import dgac.seguridad.JwtExpiredTokenException;
import dgac.seguridad.Scopes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * RefreshToken
 *
 * @author vladimir.stankovic
 *
 * Aug 19, 2016
 */
@SuppressWarnings("unchecked")
public class RefreshToken implements JwtToken {

	private Jws<Claims> claims;

	private RefreshToken(Jws<Claims> claims) {
		this.claims = claims;
	}

	/**
	 * Creates and validates Refresh token
	 * @param token
	 * @param signingKey
	 * @throws BadCredentialsException
	 * @throws JwtExpiredTokenException
	 * @return
	 */
	// public static Optional<RefreshToken> create(RawAccessJwtToken token, String
	// signingKey) {
	public static RefreshToken create(RawAccessJwtToken token, String signingKey) {
		Jws<Claims> claims = token.parseClaims(signingKey);

		List<String> scopes = claims.getBody().get("scopes", List.class);
		if (scopes == null || scopes.isEmpty() || !scopes.stream()
				.filter(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope)).findFirst().isPresent()) {
			// return Optional.empty();
			return null;
		}

		// return Optional.of(new RefreshToken(claims));
		return new RefreshToken(claims);
	}

	@Override
	public String getToken() {
		return null;
	}

	public Jws<Claims> getClaims() {
		return claims;
	}

	public String getJti() {
		return claims.getBody().getId();
	}

	public String getSubject() {
		return claims.getBody().getSubject();
	}

}
