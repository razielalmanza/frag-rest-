package dgac.seguridad.grupos.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;

import dgac.seguridad.entidades.BinAreaDgac;
import dgac.seguridad.entidades.BinGrupoUsuario;
import dgac.seguridad.entidades.BinUsuario;

@Configuration
public class UsuariosDao extends JdbcDaoSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private LeeAutoridadDeUnUsuario leeAutoridadDeUnUsuario;

	private DameTodosLosUsuarios dameTodosLosUsuarios;

	private DameEmailsDestinatariosIngresosClaf dameEmailsDestinatariosIngresosClaf;

	private LeeDatosDeUnUsuario leeDatosDeUnUsuario;

	private DameAreasDgacDelUsuario dameAreasDgacDelUsuario;

	// private DamePrivilegiosDelUsuario damePrivilegiosDelUsuario;
	private DameNombresGruposDeUnUsuario dameNombresGruposDeUnUsuario;

	private DameBinGruposDeUnUsuario dameBinGruposDeUnUsuario;

	private DamePrivilegiosGrupalesDelUsuario damePrivilegiosGrupalesDelUsuario;

	private DameContrasenia dameContrasenia;

	@PostConstruct
	protected void initDao() {
		setDataSource(dataSource);
		this.jdbcTemplate = super.getJdbcTemplate();
		this.leeAutoridadDeUnUsuario = new LeeAutoridadDeUnUsuario(getDataSource());
		this.dameTodosLosUsuarios = new DameTodosLosUsuarios(getDataSource());
		this.dameEmailsDestinatariosIngresosClaf = new DameEmailsDestinatariosIngresosClaf(getDataSource());
		this.leeDatosDeUnUsuario = new LeeDatosDeUnUsuario(getDataSource());
		this.dameAreasDgacDelUsuario = new DameAreasDgacDelUsuario(getDataSource());
		// this.damePrivilegiosDelUsuario = new
		// DamePrivilegiosDelUsuario(getDataSource());
		this.dameNombresGruposDeUnUsuario = new DameNombresGruposDeUnUsuario(getDataSource());
		this.dameBinGruposDeUnUsuario = new DameBinGruposDeUnUsuario(getDataSource());
		this.damePrivilegiosGrupalesDelUsuario = new DamePrivilegiosGrupalesDelUsuario(getDataSource());
		this.dameContrasenia = new DameContrasenia(getDataSource());
	}

	private static class BinValues {

		String idTipo = "";

		String tipo = "";

		public String getIdTipo() {
			return idTipo;
		}

		public void setIdTipo(String idTipo) {
			this.idTipo = idTipo;
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

		public static BinValues mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinValues b = new BinValues();
			b.setIdTipo(rs.getString("val"));
			b.setTipo(rs.getString("display_value"));
			return b;
		}

	}

	/**
	 * Lee la lista de usernames y nombre completo de los usuarios activos
	 * @return List<Object> una lista de objetos con user como el idTipo y el
	 * nombreCompleto
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<Object> catalogoUsersAndNombres() throws SQLException {
		final List<Object> lista = new ArrayList<Object>();
		jdbcTemplate.query("SELECT username AS val, nombreCompleto AS display_value FROM users WHERE enabled=1",
				new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						lista.add(BinValues.mapRow(rs, /* no uso row num */0));
					}
				});
		return lista;
	}

	public String leeAutoridadDeUnUsuario(String userName) {
		List<String> lista = null;
		try {
			lista = leeAutoridadDeUnUsuario.execute(userName);
			if (lista.size() == 1) {
				return lista.get(0);
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private class LeeAutoridadDeUnUsuario extends MappingSqlQuery<String> {

		protected LeeAutoridadDeUnUsuario(DataSource ds) {
			super(ds, "SELECT AUTHORITY FROM AUTHORITIES AS A, USERS AS U WHERE "
					+ " A.USERNAME = ? AND A.USERNAME = U.USERNAME AND U.ENABLED = 1");
			declareParameter(new SqlParameter("A.USERNAME", Types.VARCHAR));
			compile();
		}

		protected String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(1);
		}

	}

	/**
	 * Lee todos los usuarios que existen en la DB solo username y nombre completo
	 * @return List<BinUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinUsuario> dameTodosLosUsuarios() throws SQLException {
		List<BinUsuario> usuarios = new ArrayList<BinUsuario>();
		try {
			usuarios = dameTodosLosUsuarios.execute();
			return usuarios;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameTodosLosUsuarios extends MappingSqlQuery {

		protected DameTodosLosUsuarios(DataSource ds) {
			super(ds, "SELECT username, nombreCompleto, enabled FROM users");
			compile();
		}

		protected BinGrupoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinGrupoUsuario b = new BinGrupoUsuario();
			b.setId_grupo(rs.getByte("id_grupo"));
			b.setNombre_grupo(rs.getString("nombre_grupo"));
			return b;
		}

	}

	public List<String> dameEmailsDestinatariosIngresosClaf() {
		List<String> lista = null;
		try {
			lista = dameEmailsDestinatariosIngresosClaf.execute();
			if (lista.size() == 0)
				lista = new ArrayList<String>();
		}
		catch (Exception e) {
			logger.error(e);
		}
		return lista;
	}

	private class DameEmailsDestinatariosIngresosClaf extends MappingSqlQuery<String> {

		protected DameEmailsDestinatariosIngresosClaf(DataSource ds) {
			super(ds, "SELECT email FROM users WHERE " + "recibe_mail_ingresos_claf=1 AND enabled = 1");
			compile();
		}

		protected String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(1);
		}

	}

	public BinUsuario leeDatosDeUnUsuario(String userName) {
		List<BinUsuario> lista = null;
		lista = (List<BinUsuario>) leeDatosDeUnUsuario.execute(userName);
		if (lista.size() == 1) {
			return (BinUsuario) lista.get(0);
		}
		return null;

	}

	private class LeeDatosDeUnUsuario extends MappingSqlQuery<BinUsuario> {

		protected LeeDatosDeUnUsuario(DataSource ds) {
			super(ds,
					"SELECT U.USERNAME, A.AUTHORITY, U.ENABLED, U.NOMBRECOMPLETO, U.OBSERVACIONES, U.EMAIL, U.RUTA_FTP, U.recibe_mail_ingresos_claf "
							+ " FROM AUTHORITIES AS A, USERS AS U " + " WHERE "
							+ "A.USERNAME = ? AND A.USERNAME = U.USERNAME AND U.ENABLED=1");
			declareParameter(new SqlParameter("A.USERNAME", Types.VARCHAR));
			compile();
		}

		protected BinUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinUsuario u = new BinUsuario(rs.getString("U.USERNAME"), rs.getString("A.AUTHORITY"),
					rs.getShort("U.ENABLED"), rs.getString("U.NOMBRECOMPLETO"), rs.getString("U.OBSERVACIONES"),
					rs.getString("U.EMAIL"), rs.getString("U.RUTA_FTP"), rs.getBoolean("U.recibe_mail_ingresos_claf"));
			return u;
		}

	}

	/**
	 * dado el username entrega la lista de areas de la DGAC a la que pertenece
	 * @param usuario
	 * @return List<BinAreaDgac>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinAreaDgac> dameAreasDgacDelUsuario(String usuario) {
		List<BinAreaDgac> lista = null;
		try {
			lista = dameAreasDgacDelUsuario.execute(usuario);
			if (lista.size() == 0) {
				return new ArrayList<BinAreaDgac>();
			}
			else // if (lista.size() == 1) {
				return lista;
			// } else
			// throw new ClafException("ERROR: el suaurio esta en mas de un area");
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameAreasDgacDelUsuario extends MappingSqlQuery {

		protected DameAreasDgacDelUsuario(DataSource ds) {
			super(ds,
					"SELECT a.id_area_dgac, a.nombre_area, a.cb_ubicacion "
							+ "FROM areas_dgac a, trans_users_areas_dgac t, users u "// ,
																						// "
							+ " WHERE " + " a.id_area_dgac=t.id_area_dgac AND" + " t.username=u.username AND "
							+ " u.username = ?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected BinAreaDgac mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinAreaDgac b = new BinAreaDgac();
			b.setId_area_dgac(rs.getByte("id_area_dgac"));
			b.setNombre_area(rs.getString("nombre_area"));
			b.setCb_ubicacion(rs.getString("cb_ubicacion"));
			return b;
		}

	}

	/**
	 * dado el username entrega los privilegios que tiene
	 * @param usuario
	 * @return long
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	// public long damePrivilegiosDelUsuario(String usuario){
	// List<Long> lista = null;
	// try {
	// lista = damePrivilegiosDelUsuario.execute(usuario);
	// if(lista.size() == 1) {
	// return lista.get(0).longValue();
	// } else
	// throw new RuntimeException("mas de un registro o cero registros enontrados");
	// } catch (Exception e) {
	// logger.error(e);
	// throw e;
	// }
	// }
	//
	// private class DamePrivilegiosDelUsuario extends MappingSqlQuery {
	// protected DamePrivilegiosDelUsuario(DataSource ds) {
	// super(ds,"SELECT u.privilegios FROM users u "//, "
	// + " WHERE "
	// + " u.username = ?");
	// declareParameter(new SqlParameter(Types.VARCHAR));
	// compile();
	// }
	//
	// protected Long mapRow(ResultSet rs, int rowNum) throws SQLException {
	// return rs.getLong(1);
	// }
	// }

	/**
	 * dado un usuario, obtiene y le asigna los nombres de los grupos a los que pertenece
	 * y los privilegios que tiene
	 * @param usuario
	 * @return UsuarioClaf
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public BinUsuario dameNombresGruposAndPrivilegiosDelUsuario(String usuario) {
		List<BinGrupoUsuario> grupos = new ArrayList<BinGrupoUsuario>();
		List<Long> listaPrivilegios = new ArrayList<Long>();
		BinUsuario binUsuario = new BinUsuario(usuario);

		try {
			grupos = dameNombresGruposDeUnUsuario.execute(usuario);
			if (grupos.size() > 0) {
				binUsuario.setGrupos(grupos);
			}
			else {
				throw new RuntimeException("El usuario: " + usuario + " no tiene grupos asociados");
			}
			listaPrivilegios = damePrivilegiosGrupalesDelUsuario.execute(usuario);
			if (listaPrivilegios.size() > 0) {
				long privs = 0;
				for (long privilegio : listaPrivilegios) {
					privs = privs | privilegio;
				}
				binUsuario.setPrivilegios(privs);
			}
			else {
				throw new RuntimeException("El usuario: " + usuario + " no tiene privilegios");
			}
			return binUsuario;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameNombresGruposDeUnUsuario extends MappingSqlQuery {

		protected DameNombresGruposDeUnUsuario(DataSource ds) {
			super(ds,
					"SELECT g.nombre_grupo " + " FROM grupos_privilegios g, trans_users_grupos_privilegios t, users u "
							+ " WHERE u.username=? AND " + " u.username=t.username AND " + " t.id_grupo=g.id_grupo");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(1);
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
		List<BinGrupoUsuario> grupos = new ArrayList<BinGrupoUsuario>();
		List<Long> listaPrivilegios = new ArrayList<Long>();
		BinUsuario binUsuario = new BinUsuario(usuario);

		try {
			grupos = dameBinGruposDeUnUsuario.execute(usuario);
			if (grupos.size() > 0) {
				binUsuario.setGrupos(grupos);
			}
			else {
				throw new RuntimeException("El usuario: " + usuario + " no tiene grupos asociados");
			}
			listaPrivilegios = damePrivilegiosGrupalesDelUsuario.execute(usuario);
			if (listaPrivilegios.size() > 0) {
				long privs = 0;
				for (long privilegio : listaPrivilegios) {
					privs = privs | privilegio;
				}
				binUsuario.setPrivilegios(privs);
			}
			else {
				throw new RuntimeException("El usuario: " + usuario + " no tiene privilegios");
			}
			return binUsuario;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameBinGruposDeUnUsuario extends MappingSqlQuery {

		protected DameBinGruposDeUnUsuario(DataSource ds) {
			super(ds,
					"SELECT g.id_grupo, g.nombre_grupo "
							+ " FROM grupos_privilegios g, trans_users_grupos_privilegios t, users u "
							+ " WHERE u.username=? AND " + " u.username=t.username AND " + " t.id_grupo=g.id_grupo");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected BinGrupoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinGrupoUsuario b = new BinGrupoUsuario();
			b.setId_grupo(rs.getByte("id_grupo"));
			b.setNombre_grupo(rs.getString("nombre_grupo"));
			return b;
		}

	}

	private class DamePrivilegiosGrupalesDelUsuario extends MappingSqlQuery {

		protected DamePrivilegiosGrupalesDelUsuario(DataSource ds) {
			super(ds, "SELECT g.privilegios " + " FROM grupos_privilegios g, trans_users_grupos_privilegios t, users u "
					+ " WHERE u.username=? AND " + " u.username=t.username AND " + " t.id_grupo=g.id_grupo");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getLong(1);
		}

	}

	/**
	 * regresa el catalogo de usuarios con su nombre completo a modo de hashmap
	 * @return Map<usuario, nombre>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public Map<String, String> dameCatalogoUsuarios() {
		Map<String, String> mapReturn = new HashMap<String, String>();
		try {
			mapReturn = jdbcTemplate.query("SELECT username, nombrecompleto FROM users WHERE enabled=1",
					new ResultSetExtractor<Map<String, String>>() {
						@Override
						public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
							HashMap<String, String> mapRet = new HashMap<String, String>();
							while (rs.next()) {
								mapRet.put(rs.getString("username"), rs.getString("nombrecompleto"));
							}
							return mapRet;
						}
					});
			return mapReturn;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	/**
	 * Lee la lista de usuarios asociados a un grupo.
	 * @param idGrupo
	 * @return List<BinUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public Map<String, String> dameCatalogoUsuariosDeGrupo(byte idGrupo) {
		Map<String, String> mapReturn = new HashMap<String, String>();
		try {
			mapReturn = jdbcTemplate.query(
					"SELECT u.username, u.nombreCompleto " + " FROM users u, trans_users_grupos_privilegios t "
							+ " WHERE u.username=t.username AND " + " t.id_grupo=" + idGrupo,
					new ResultSetExtractor<Map<String, String>>() {
						@Override
						public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
							HashMap<String, String> mapRet = new HashMap<String, String>();
							while (rs.next()) {
								mapRet.put(rs.getString("username"), rs.getString("nombrecompleto"));
							}
							return mapRet;
						}
					});
			return mapReturn;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	/**
	 * Lee la contraseña de un usuario
	 * @param usuario
	 * @return String
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public String dameContrasenia(String usuario) {
		List<String> pass = null;
		try {
			pass = dameContrasenia.execute(usuario);
			if (pass != null)
				return pass.get(0);
			else
				throw new RuntimeException();
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameContrasenia extends MappingSqlQuery {

		protected DameContrasenia(DataSource ds) {
			super(ds, "SELECT password FROM users WHERE username = ? AND enabled=1");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(1);
		}

	}

	/**
	 * Genera una nueva contraseña de un usuario, si el email corresponde al registrado
	 * para el usuario, se le envia su contrasenia por correo
	 * @param binUsuario
	 * @param email
	 * @return true si los datos son correctos y el correo se pudo enviar
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public boolean recuperaContrasenia(BinUsuario binUsuario, String email) {
		try {
			jdbcTemplate.update("UPDATE users SET password=? WHERE username=? AND email=? AND enabled=1",
					binUsuario.getPassword(), binUsuario.getUsuario(), email);
			return true;
		}
		catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	/**
	 * Cambia la contraseña de un usuario por una nueva
	 * @param usuario
	 * @param contraseniaOld
	 * @param contraseniaNew1
	 * @param contraseniaNew2
	 * @return true si los datos son correctos y la contraseña fue cambiada
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public boolean cambiaContrasenia(String usuario, String contraseniaOld, String contraseniaNew) {
		try {
			jdbcTemplate.update("UPDATE users SET password=? WHERE username=? AND password=? AND enabled=1",
					contraseniaNew, usuario, contraseniaOld);
			return true;
		}
		catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

}
