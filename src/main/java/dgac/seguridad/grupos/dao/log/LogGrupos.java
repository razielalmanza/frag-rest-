package dgac.seguridad.grupos.dao.log;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.SqlUpdate;

@Configuration
public class LogGrupos extends JdbcDaoSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private DataSource dataSource;

	// @Autowired private JdbcTemplate jdbcTemplate;
	private RegistraEnLogGrupos registraEnLogGrupos;

	@PostConstruct
	protected void initDao() {
		try {
			setDataSource(dataSource);
			// this.jdbcTemplate = super.getJdbcTemplate();
			this.registraEnLogGrupos = new RegistraEnLogGrupos(getDataSource());
		}
		catch (Exception e) {
			logger.error("falla initDao", e);
		}
	}

	/**
	 * @param idGrupo
	 * @param usuario
	 * @param operacion
	 * @param nombreGrupoAnterior
	 * @param privilegiosAnteriores
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void registraEnLogGrupos(byte idGrupo, String usuario, String operacion, String nombreGrupoAnterior,
			String privilegiosAnteriores, String ip) {
		try {
			Calendar calendar = Calendar.getInstance();
			Timestamp fechaHora = new java.sql.Timestamp(calendar.getTime().getTime());
			this.registraEnLogGrupos.inserta(fechaHora, usuario, operacion, idGrupo, nombreGrupoAnterior,
					privilegiosAnteriores, ip);
		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}
	}

	private class RegistraEnLogGrupos extends SqlUpdate {

		public RegistraEnLogGrupos(DataSource ds) {
			super(ds, "INSERT INTO log_dgac.log_grupos VALUES (?," + // PK log_grupos
																		// autoincrementable
					"?," + // fecha_hora
					"?," + // username
					"?," + // operacion
					"?," + // id_grupo
					"?," + // nombre_grupo_anterior
					"?," + // privilegios_anteriores
					"?)"); // IP
			declareParameter(new SqlParameter(Types.BIGINT)); // PK log_usuarios
																// autoincrementable
			declareParameter(new SqlParameter(Types.TIMESTAMP)); // fecha_hora
			declareParameter(new SqlParameter(Types.VARCHAR)); // username
			declareParameter(new SqlParameter(Types.VARCHAR)); // operacion
			declareParameter(new SqlParameter(Types.TINYINT)); // id_grupo
			declareParameter(new SqlParameter(Types.VARCHAR)); // nombre_grupo_anterior
			declareParameter(new SqlParameter(Types.VARCHAR)); // privilegios_anteriores
			declareParameter(new SqlParameter(Types.VARCHAR)); // IP
			compile();
		}

		protected void inserta(Timestamp fechaHora, String usuario, String operacion, byte idGrupo,
				String nombreGrupoAnterior, String privilegiosAnteriores, String ip) {
			Object[] params = new Object[] { new Long(0), fechaHora, usuario, operacion, idGrupo, nombreGrupoAnterior,
					privilegiosAnteriores, ip };
			try {
				this.update(params);
			}
			catch (Exception e) {
				logger.error(e);
			}
		}

	}

}
