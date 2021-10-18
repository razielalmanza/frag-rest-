package dgac.seguridad;

public class Constants {

	// Spring Security
	public static final String LOGIN_URL = "/login";

	public static final String HEADER_AUTHORIZACION_KEY = "Authorization";

	public static final String TOKEN_BEARER_PREFIX = "Bearer ";

	// JWT
	public static final String ISSUER_INFO = "https://www.filmoteca.com/";

	public static final String SUPER_SECRET_KEY = "safe-holder";

	// public static final long TOKEN_EXPIRATION_TIME = 2700000; // = 45 min
	public static final long TOKEN_EXPIRATION_TIME = 43200000; // = 12 h

}
