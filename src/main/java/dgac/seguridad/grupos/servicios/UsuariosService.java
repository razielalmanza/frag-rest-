package dgac.seguridad.grupos.servicios;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dgac.seguridad.entidades.BinAreaDgac;
import dgac.seguridad.entidades.BinUsuario;
import dgac.seguridad.grupos.dao.UsuariosDao;
import dgac.seguridad.grupos.dao.log.LogUsuarios;
import dgac.utilidades.MD5Digest;

@Service("usuariosService")
public class UsuariosService {

	protected final Log log = LogFactory.getLog(getClass());

	public static final byte ERROR_INTERNO = -1;

	public static final byte SIN_ERRORES = 0;

	public static final byte ERROR_CONTRASENIA_INCORRECTA = 1;

	public static final byte ERROR_CONTRASENIAS_NUEVAS_NO_COINCIDEN = 2;

	public static final byte ERROR_EMAIL_INCORRECTO = 3;

	public static final byte ERROR_EMAIL_NO_REGISTRADO = 4;

	/**
	 * Lee los emails de los usuarios que reciben notificaciones por email sobre los
	 * ingresos de CLAF
	 * @return List<String>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<String> leeEmailsDestinatariosIngresosClaf() {
		try {
			return usuariosDao.dameEmailsDestinatariosIngresosClaf();
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeEmailsDestinatariosIngresosClaf");
		}
	}

	/**
	 * Lee todos los usuarios que existen en la DB solo username y nombre completo
	 * @return List<BinUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinUsuario> leeTodosLosUsuarios() {
		try {
			return usuariosDao.dameTodosLosUsuarios();
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeTodosLosUsuarios");
		}
	}

	/**
	 * dado el username o nombre corto para login de un usuario, entrega una instancia de
	 * dgac.presta.entidades.Usuario con todos los datos del usuario EXCEPTO el password
	 * (por ahora).
	 * @param userName un string que es el nombre con que se identifica al usuario en el
	 * sistema.
	 */
	public BinUsuario leeDatosDelUsuario(String username) {
		BinUsuario u = null;
		try {
			u = usuariosDao.leeDatosDeUnUsuario(username);
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
		return u;

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * dado el username o nombre corto para login de un usuario, entrega el grupo de
	 * autoridad al que pertenece
	 * @param userName un string que es el nombre con que se identifica al usuario en el
	 * sistema.
	 */
	public String getAutoridadDeUnUsuario(String userName) {
		String authority = null;
		try {
			authority = usuariosDao.leeAutoridadDeUnUsuario(userName);
		}
		catch (Exception e) {
			log.error(e);
			return "error interno";
		}
		return authority;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * dado el username o nombre corto para login de un usuario, entrega su nombre
	 * completo
	 * @param userName un string que es el nombre con que se identifica al usuario en el
	 * sistema.
	 */
	public String leeNombreCompleto(String username) {
		BinUsuario u = null;
		try {
			u = usuariosDao.leeDatosDeUnUsuario(username);
		}
		catch (Exception e) {
			log.error(e);
			return "error interno";
		}
		return u.getNombreCompleto();

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * dado el username o nombre corto para login de un usuario, entrega su dirección de
	 * correo electrónico
	 * @param userName un string que es el nombre con que se identifica al usuario en el
	 * sistema.
	 */
	public String leeEmailDelUsuario(String username) {
		BinUsuario u = null;
		try {
			u = usuariosDao.leeDatosDeUnUsuario(username);
		}
		catch (Exception e) {
			log.error(e);
			return "error interno";
		}
		return u.getEmail();
	}

	/**
	 * regresa la ruta ftp del usuario
	 * @param username
	 * @return String
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public String leeFtpDelUsuario(String username) {
		BinUsuario u = null;
		try {
			u = usuariosDao.leeDatosDeUnUsuario(username);
		}
		catch (Exception e) {
			log.error(e);
			return "error interno";
		}
		return u.getRuta_ftp();
	}

	/**
	 * regresa el catalogo de usuarios con su nombre completo a modo de hashmap
	 * @return Map<usuario, nombre>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public Map<String, String> leeCatalogoUsuarios() {
		try {
			return usuariosDao.dameCatalogoUsuarios();
		}
		catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * Lee la lista de usuarios asociados a un grupo.
	 * @param idGrupo
	 * @return List<BinUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public Map<String, String> leeCatalogoUsuariosDeGrupo(byte idGrupo) {
		try {
			return usuariosDao.dameCatalogoUsuariosDeGrupo(idGrupo);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeCatalogoUsuariosDeGrupo");
		}
	}

	/**
	 * dado el username entrega la lista de areas de la DGAC a la que pertenece
	 * @param usuario
	 * @return List<BinAreaDgac>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinAreaDgac> leeAreasDgacDelUsuario(String usuario) {
		try {
			return usuariosDao.dameAreasDgacDelUsuario(usuario);
		}
		catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * dado el username entrega los privilegios que tiene
	 * @param usuario
	 * @return long
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	// public long leePrivilegiosDelUsuario(String usuario){
	// try {
	// return usuariosDao.damePrivilegiosDelUsuario(usuario);
	// } catch (Exception e) {
	// log.error(e);
	// throw e;
	// }
	// }

	/**
	 * dado un usuario, obtiene y le asigna los nombres de los grupos a los que pertenece
	 * y los privilegios que tiene
	 * @param usuario
	 * @return UsuarioClaf
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public BinUsuario leeNombresGruposAndPrivilegiosDelUsuario(String usuario) {
		try {
			return usuariosDao.dameNombresGruposAndPrivilegiosDelUsuario(usuario);
		}
		catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * dado un usuario, obtiene y le asigna los grupos a los que pertenece y los
	 * privilegios que tiene
	 * @param usuario
	 * @return UsuarioClaf
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public BinUsuario leeBinGruposAndPrivilegiosDelUsuario(String usuario) {
		try {
			return usuariosDao.leeBinGruposAndPrivilegiosDelUsuario(usuario);
		}
		catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * Lee la contraseña de un usuario
	 * @param usuario
	 * @return String
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public String leeContrasenia(String usuario) {
		try {
			return usuariosDao.dameContrasenia(usuario);
		}
		catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	@Autowired
	private UsuariosDao usuariosDao;

	@Autowired
	private LogUsuarios logUsuarios;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// @Bean
	// public BCryptPasswordEncoder bCryptPasswordEncoder() {
	// return new BCryptPasswordEncoder(4);
	// }
	@Autowired
	private MD5Digest md5Digest;

}
