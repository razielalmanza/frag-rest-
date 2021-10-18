package dgac.seguridad.grupos.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import dgac.seguridad.entidades.BinGrupoUsuario;
import dgac.seguridad.entidades.BinUsuario;

@Configuration
public class GruposDao extends JdbcDaoSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private DameTodosLosGrupos dameTodosLosGrupos;

	private DameDatosDeGrupo dameDatosDeGrupo;

	private DamePrivilegiosDeGrupo damePrivilegiosDeGrupo;

	private DameUsuariosDeGrupo dameUsuariosDeGrupo;

	private DameUsuariosFueraDeGrupo dameUsuariosFueraDeGrupo;

	private InsertaNuevoGrupo insertaNuevoGrupo;

	private ModificaGrupo modificaGrupo;

	private ModificaPrivilegiosDeGrupo modificaPrivilegiosDeGrupo;

	private BorraGrupo borraGrupo;

	private AsociaUsuariosToGrupo asociaUsuariosToGrupo;

	private DesasociaUsuariosDeGrupo desasociaUsuariosDeGrupo;

	@PostConstruct
	protected void initDao() {
		try {
			setDataSource(dataSource);
			this.jdbcTemplate = super.getJdbcTemplate();

			this.dameTodosLosGrupos = new DameTodosLosGrupos(getDataSource());
			this.dameDatosDeGrupo = new DameDatosDeGrupo(getDataSource());
			this.damePrivilegiosDeGrupo = new DamePrivilegiosDeGrupo(getDataSource());
			this.dameUsuariosDeGrupo = new DameUsuariosDeGrupo(getDataSource());
			this.dameUsuariosFueraDeGrupo = new DameUsuariosFueraDeGrupo(getDataSource());
			this.insertaNuevoGrupo = new InsertaNuevoGrupo(getDataSource());
			this.modificaGrupo = new ModificaGrupo(getDataSource());
			this.modificaPrivilegiosDeGrupo = new ModificaPrivilegiosDeGrupo(getDataSource());
			this.borraGrupo = new BorraGrupo(getDataSource());
			this.asociaUsuariosToGrupo = new AsociaUsuariosToGrupo(getDataSource());
			this.desasociaUsuariosDeGrupo = new DesasociaUsuariosDeGrupo(getDataSource());
		}
		catch (Exception e) {
			logger.error("falla initDao", e);
		}
	}

	/**
	 * Lee todos los grupos que existen en la DB sin leer sus privilegios.
	 * @return List<BinGrupoUsuario>
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinGrupoUsuario> dameTodosLosGrupos() throws SQLException {
		List<BinGrupoUsuario> grupos = new ArrayList<BinGrupoUsuario>();
		try {
			grupos = dameTodosLosGrupos.execute();
			return grupos;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameTodosLosGrupos extends MappingSqlQuery {

		protected DameTodosLosGrupos(DataSource ds) {
			super(ds, "SELECT g.id_grupo, g.nombre_grupo " + " FROM grupos_privilegios g ");
			compile();
		}

		protected BinGrupoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinGrupoUsuario b = new BinGrupoUsuario();
			b.setId_grupo(rs.getByte("id_grupo"));
			b.setNombre_grupo(rs.getString("nombre_grupo"));
			return b;
		}

	}

	/**
	 * Lee el nombre y los privilegios de un grupo por su idGrupo.
	 * @param idGrupo
	 * @return nombreGrupo
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public BinGrupoUsuario dameDatosDeGrupo(byte idGrupo) throws SQLException {
		List<BinGrupoUsuario> lista = new ArrayList<BinGrupoUsuario>();
		try {
			lista = dameDatosDeGrupo.execute(idGrupo);
			if (lista.size() == 1)
				return lista.get(0);
			else
				throw new RuntimeException("obtuve mas de un registro o ningun registro, fueron:" + lista.size()
						+ " para idGrupo: " + idGrupo);
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameDatosDeGrupo extends MappingSqlQuery {

		protected DameDatosDeGrupo(DataSource ds) {
			super(ds, "SELECT g.id_grupo, g.nombre_grupo, g.privilegios" + " FROM grupos_privilegios g "
					+ " WHERE g.id_grupo=? ");
			declareParameter(new SqlParameter(Types.TINYINT));
			compile();
		}

		protected BinGrupoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinGrupoUsuario b = new BinGrupoUsuario();
			b.setId_grupo(rs.getByte("id_grupo"));
			b.setNombre_grupo(rs.getString("nombre_grupo"));
			b.setPrivilegios(rs.getLong("privilegios"));
			return b;
		}

	}

	/**
	 * Lee el nombre de un grupo por su idGrupo.
	 * @param idGrupo
	 * @return nombreGrupo
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	// public String dameNombreDeGrupo(byte idGrupo) throws SQLException;

	/**
	 * Lee los privilegios de un grupo por su idGrupo.
	 * @param idGrupo
	 * @return privilegios
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public long damePrivilegiosDeGrupo(byte idGrupo) throws SQLException {
		List<BinGrupoUsuario> lista = new ArrayList<BinGrupoUsuario>();
		try {
			return jdbcTemplate.queryForObject("SELECT g.privilegios FROM grupos_privilegios g  WHERE g.id_grupo=? ",
					Long.class, idGrupo);
			// lista = damePrivilegiosDeGrupo.execute(idGrupo);
			// if (lista.size()==1)
			// return lista.get(0);
			// else
			// throw new RuntimeException("obtuve mas de un registro o ningun registro,
			// fueron:"+ lista.size() + " para idGrupo: "+idGrupo);
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DamePrivilegiosDeGrupo extends MappingSqlQuery {

		protected DamePrivilegiosDeGrupo(DataSource ds) {
			super(ds, "SELECT g.privilegios" + " FROM grupos_privilegios g " + " WHERE g.id_grupo=? ");
			declareParameter(new SqlParameter(Types.TINYINT));
			compile();
		}

		protected BinGrupoUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinGrupoUsuario b = new BinGrupoUsuario();
			b.setPrivilegios(rs.getLong("privilegios"));
			return b;
		}

	}

	/**
	 * Lee la lista de usuarios asociados a un grupo.
	 * @param idGrupo
	 * @return List<BinUsuario>
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinUsuario> dameUsuariosDeGrupo(byte idGrupo) throws SQLException {
		List<BinUsuario> lista = new ArrayList<BinUsuario>();
		try {
			lista = dameUsuariosDeGrupo.execute(idGrupo);
			return lista;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameUsuariosDeGrupo extends MappingSqlQuery<BinUsuario> {

		protected DameUsuariosDeGrupo(DataSource ds) {
			super(ds, "SELECT u.username, u.nombreCompleto " + " FROM users u, trans_users_grupos_privilegios t "
					+ " WHERE u.username=t.username AND " + " t.id_grupo=?");
			declareParameter(new SqlParameter(Types.TINYINT));
			compile();
		}

		protected BinUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinUsuario b = new BinUsuario();
			b.setUsuario(rs.getString("username"));
			b.setNombreCompleto(rs.getString("nombreCompleto"));
			return b;
		}

	}

	/**
	 * Lee la lista de usuarios NO asociados a un grupo. Util para la GUI que asociará
	 * usuarios a un grupo.
	 * @param idGrupo
	 * @return List<BinUsuario>
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinUsuario> dameUsuariosFueraDeGrupo(byte idGrupo) throws SQLException {
		List<BinUsuario> lista = new ArrayList<BinUsuario>();
		try {
			lista = dameUsuariosFueraDeGrupo.execute(idGrupo);
			return lista;
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private class DameUsuariosFueraDeGrupo extends MappingSqlQuery<BinUsuario> {

		protected DameUsuariosFueraDeGrupo(DataSource ds) {
			super(ds, "SELECT u.username, u.nombreCompleto " + " FROM users u, trans_users_grupos_privilegios t "
					+ " WHERE u.username<>t.username AND " + " t.id_grupo=?");
			declareParameter(new SqlParameter(Types.TINYINT));
			compile();
		}

		protected BinUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinUsuario b = new BinUsuario();
			b.setUsuario(rs.getString("username"));
			b.setNombreCompleto(rs.getString("nombreCompleto"));
			return b;
		}

	}

	/**
	 * Crea un nuevo grupo con sus privilegios.
	 * @param binGrupo
	 * @param usuario
	 * @param ip
	 * @return idGrupo
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public byte insertaNuevoGrupo(BinGrupoUsuario binGrupo) throws SQLException {
		try {
			byte idGrupo = insertaNuevoGrupo.inserta(binGrupo);
			return idGrupo;
		}
		catch (Exception e) {
			logger.error("ERROR: insertaNuevoGrupo", e);
			throw e;
		}
	}

	private class InsertaNuevoGrupo extends SqlUpdate {

		public InsertaNuevoGrupo(DataSource ds) {
			super(ds, "INSERT INTO grupos_privilegios (id_grupo, nombre_grupo, privilegios) " + "VALUES (?," + // id_grupo
					"?," + // nombre_grupo
					"?)"); // privilegios

			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			declareParameter(new SqlParameter(Types.VARCHAR)); // nombre_grupo
			declareParameter(new SqlParameter(Types.BIGINT)); // privilegios
			compile();
		}

		protected final byte inserta(BinGrupoUsuario b) {
			Object[] params = new Object[] { b.getId_grupo(), b.getNombre_grupo(), b.getPrivilegios() };
			this.update(params);
			return b.getId_grupo();
		}

	}

	/**
	 * Actualiza el nombre y los privilegios de un grupo existente.
	 * @param binGrupo
	 * @param usuario
	 * @param ip
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void modificaGrupo(BinGrupoUsuario binGrupo) throws SQLException {
		try {
			modificaGrupo.actualiza(binGrupo);
		}
		catch (Exception e) {
			logger.error("ERROR: modificaGrupo", e);
			throw e;
		}
	}

	private class ModificaGrupo extends SqlUpdate {

		public ModificaGrupo(DataSource ds) {
			super(ds, "UPDATE grupos_privilegios SET " + "nombre_grupo=?, " + "privilegios=? ) " + "WHERE id_grupo=?");

			declareParameter(new SqlParameter(Types.VARCHAR)); // nombre_grupo
			declareParameter(new SqlParameter(Types.BIGINT)); // privilegios
			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			compile();
		}

		protected final void actualiza(BinGrupoUsuario b) {
			Object[] params = new Object[] { b.getNombre_grupo(), b.getPrivilegios(), b.getId_grupo() };
			this.update(params);
		}

	}

	/**
	 * Actualiza los privilegios de un grupo existente.
	 * @param binGrupo
	 * @param usuario
	 * @param ip
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void modificaPrivilegiosDeGrupo(BinGrupoUsuario binGrupo) throws SQLException {
		try {
			modificaPrivilegiosDeGrupo.actualiza(binGrupo);
		}
		catch (Exception e) {
			logger.error("ERROR: modificaPrivilegiosDeGrupo", e);
			throw e;
		}
	}

	private class ModificaPrivilegiosDeGrupo extends SqlUpdate {

		public ModificaPrivilegiosDeGrupo(DataSource ds) {
			super(ds, "UPDATE grupos_privilegios SET " + "privilegios=? ) " + "WHERE id_grupo=?");

			declareParameter(new SqlParameter(Types.BIGINT)); // privilegios
			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			compile();
		}

		protected final void actualiza(BinGrupoUsuario b) {
			Object[] params = new Object[] { b.getPrivilegios(), b.getId_grupo() };
			this.update(params);
		}

	}

	/**
	 * Elimina un grupo existente y desasocia sus usuarios por cascade delete (SQL)
	 * @param idGrupo
	 * @param usuario
	 * @param ip
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void borraGrupo(byte idGrupo) throws SQLException {
		try {
			borraGrupo.delete(idGrupo);
		}
		catch (Exception e) {
			logger.error("ERROR: borraGrupo", e);
			throw e;
		}
	}

	private class BorraGrupo extends SqlUpdate {

		public BorraGrupo(DataSource ds) {
			super(ds, "DELETE FROM grupos_privilegios WHERE id_grupo=?");
			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			compile();
		}

		protected final void delete(byte idGrupo) {
			this.update(idGrupo);
		}

	}

	/**
	 * Asocia una lista de usuarios a un grupo existente
	 * @param listaUsuarios
	 * @param idGrupo
	 * @param usuario
	 * @param ip
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void asociaUsuariosToGrupo(List<BinUsuario> listaUsuarios, byte idGrupo) throws SQLException {
		try {
			for (BinUsuario usuario : listaUsuarios)
				asociaUsuariosToGrupo.inserta(usuario.getUsuario(), idGrupo);
		}
		catch (Exception e) {
			logger.error("ERROR: asociaUsuariosToGrupo", e);
			throw e;
		}
	}

	private class AsociaUsuariosToGrupo extends SqlUpdate {

		public AsociaUsuariosToGrupo(DataSource ds) {
			super(ds, "INSERT IGNORE INTO trans_users_grupos_privilegios VALUES (?," + // username
					"?)"); // id_grupo
			declareParameter(new SqlParameter(Types.VARCHAR)); // username
			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			compile();
		}

		protected final void inserta(String strUsuario, byte idGrupo) {
			Object[] params = new Object[] { strUsuario, idGrupo };
			this.update(params);
		}

	}

	/**
	 * Desasocia una lista de usuarios de un grupo existente
	 * @param listaUsuarios
	 * @param idGrupo
	 * @param usuario
	 * @param ip
	 * @throws SQLException
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void desasociaUsuariosDeGrupo(List<BinUsuario> listaUsuarios, byte idGrupo) throws SQLException {
		try {
			for (BinUsuario usuario : listaUsuarios)
				desasociaUsuariosDeGrupo.delete(usuario.getUsuario(), idGrupo);
		}
		catch (Exception e) {
			logger.error("ERROR: insertaContenedorAndCopia", e);
			throw e;
		}
	}

	private class DesasociaUsuariosDeGrupo extends SqlUpdate {

		public DesasociaUsuariosDeGrupo(DataSource ds) {
			super(ds, "DELETE FROM trans_users_grupos_privilegios WHERE username=? AND id_grupo=? ");
			declareParameter(new SqlParameter(Types.VARCHAR)); // username
			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			compile();
		}

		protected final void delete(String strUsuario, byte idGrupo) {
			Object[] params = new Object[] { strUsuario, idGrupo };
			this.update(params);
		}

	}

}
