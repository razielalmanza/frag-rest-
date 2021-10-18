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

import dgac.seguridad.entidades.BinUsuario;

@Configuration
public class LogUsuarios extends JdbcDaoSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private DataSource dataSource;

	// @Autowired private JdbcTemplate jdbcTemplate;
	private RegistraEnLogUsuarios registraEnLogUsuarios;

	@PostConstruct
	protected void initDao() {
		try {
			setDataSource(dataSource);
			// this.jdbcTemplate = super.getJdbcTemplate();
			this.registraEnLogUsuarios = new RegistraEnLogUsuarios(getDataSource());
		}
		catch (Exception e) {
			logger.error("falla initDao", e);
		}
	}

	/**
	 * @param binUsuario
	 * @param operacion
	 * @param cambiosEnRegistro
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void registraEnLogUsuarios(BinUsuario binUsuario, String operacion, String cambiosEnRegistro, String ip) {
		try {
			Calendar calendar = Calendar.getInstance();
			Timestamp fechaHora = new java.sql.Timestamp(calendar.getTime().getTime());
			this.registraEnLogUsuarios.inserta(fechaHora, binUsuario, operacion, cambiosEnRegistro, ip);
		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}
	}

	private class RegistraEnLogUsuarios extends SqlUpdate {

		public RegistraEnLogUsuarios(DataSource ds) {
			super(ds, "INSERT INTO log_dgac.log_usuarios VALUES (?," + // PK log_usuarios
																		// autoincrementable
					"?," + // fecha_hora
					"?," + // username
					"?," + // operacion
					"?," + // cambios en Registro
					"?)"); // IP
			declareParameter(new SqlParameter(Types.BIGINT)); // PK log_usuarios
																// autoincrementable
			declareParameter(new SqlParameter(Types.TIMESTAMP)); // fecha_hora
			declareParameter(new SqlParameter(Types.VARCHAR)); // username
			declareParameter(new SqlParameter(Types.VARCHAR)); // operacion
			declareParameter(new SqlParameter(Types.CLOB)); // cambios en Registro
			declareParameter(new SqlParameter(Types.VARCHAR)); // IP
			compile();
		}

		protected void inserta(Timestamp fechaHora, BinUsuario binUsuario, String operacion, String cambiosEnRegistro,
				String ip) {
			Object[] params = new Object[] { new Long(0), fechaHora, binUsuario.getUsuario(), operacion,
					cambiosEnRegistro, ip };
			try {
				this.update(params);
			}
			catch (Exception e) {
				logger.error(e);
			}
		}

	}

}
