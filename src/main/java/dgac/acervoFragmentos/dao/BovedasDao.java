package dgac.acervoFragmentos.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dgac.fragmentos.utileriaAcervo.Registro;

@Configuration
public class BovedasDao extends JdbcDaoSupport {

	@JsonIgnore
	public static final String TABLA_ACERVO = "Acervo";

	@JsonIgnore
	public static final String TABLA_LOG_MOVIMIENTOS = "log" + TABLA_ACERVO;

	@JsonIgnore
	public static final String TABLA_BOVEDAS = "bovedaPropone";

	@JsonIgnore
	public static final String TABLA_LOG_PROPUESTAS = "logBovedaPropone";

	private static final Log log = LogFactory.getLog(BovedasDao.class);

	private String[] nombresDeCampoEnAcervo = null;

	private String[] nombresDeCampoEnBovedaPropone = null;

	private String updateStmtDeAcervo = null;

	private String updateStmtDePropuesta = null;

	public BovedasDao() {
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	// que trabajan sobre la tabla Acervo
	private MapRowDeRegistros mapRowDeRegistros;

	private AltaDeNuevoRegistro altaDeNuevoRegistro;

	private LeeRegistroPorId leeRegistroPorId;

	private ActualizaUnRegistro actualizaUnRegistro;

	private BajaDeUnRegistro bajaDeUnRegistro;

	private LogDeMovimientos logDeMovimientos;

	// que trabajan sobre la tabla bovedaAcervo
	// private MapRowDePropuestas mapRowDePropuestas;
	private AltaDeNuevaPropuesta altaDeNuevaPropuesta;

	// private LeePropuestaDeEdicionPorId leePropuestaDeEdicionPorId;
	private ActualizaUnaPropuesta actualizaUnaPropuesta;

	// private BajaDeUnaPropuesta bajaDeUnaPropuesta;
	private LogDePropuestas logDePropuestas;

	@PostConstruct
	protected void initDao() {
		setDataSource(dataSource);
		this.jdbcTemplate = super.getJdbcTemplate();
		this.mapRowDeRegistros = new MapRowDeRegistros();
		this.altaDeNuevoRegistro = new AltaDeNuevoRegistro(getDataSource());
		this.leeRegistroPorId = new LeeRegistroPorId(getDataSource(), mapRowDeRegistros);
		this.bajaDeUnRegistro = new BajaDeUnRegistro(getDataSource());
		// note que no hacemos this.actualizaUnRegistro = new
		// ActualizaUnRegistro(getDataSource());
		// ver metodo actualizaUnRegistro()
		this.logDeMovimientos = new LogDeMovimientos(getDataSource());

		// que trabajan sobre la tabla bovedaAcervo
		// this.mapRowDePropuestas = new MapRowDePropuestas();
		this.altaDeNuevaPropuesta = new AltaDeNuevaPropuesta(getDataSource());
		// this.leePropuestaDeEdicionPorId = new
		// LeePropuestaDeEdicionPorId(getDataSource(), mapRowDePropuestas);
		// this.bajaDeUnaPropuesta = new BajaDeUnaPropuesta(getDataSource());
		// note que no hacemos this.actualizaUnaPropuesta = new
		// AtualizaUnPropuesta(getDataSource());
		// ver metodo actualizaUnPropuesta()
		this.logDePropuestas = new LogDePropuestas(getDataSource());
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	public long leeTotalDeRegistrosAcervo() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ACERVO", Long.class);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	public Collection buscaRegistrosPorQueryConjuntivo(String strQuery) {
		// log.error("El query fue:"+strQuery);
		final ArrayList alResultado = new ArrayList();
		jdbcTemplate.query(strQuery, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				alResultado.add(mapRowDeRegistros.mapRow(rs, /* no uso row num */0));
			}
		});
		return alResultado;
	}

	public Collection colocacionesExistentes(String colocacion) {
		try {
			/*
			 * Arma el query en terminos de valores no nulos restringido a campos
			 * presentados en la FormaDeBusquedaGenerica.jsp
			 */
			// StringBuffer strBuffQuery = new StringBuffer("SELECT idReg,
			// fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA,
			// REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR,
			// AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN,
			// FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA,
			// COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION,
			// NOMBRE_CAPTURA, RESTRICCIONES FROM Acervo WHERE ");
			StringBuffer strBuffQuery = new StringBuffer("SELECT * FROM Acervo WHERE ");

			strBuffQuery.append("colocacion LIKE '%" + colocacion + "%'");
			final ArrayList alResultado = new ArrayList();
			jdbcTemplate.query(strBuffQuery.toString(), new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					alResultado.add(mapRowDeRegistros.mapRow(rs, 0));
				}
			});
			return alResultado;

		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	/* clase que se comparte por queries que trabajan con ACERVO */
	private class MapRowDeRegistros {

		public Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Registro r = new Registro();
			int i = 1;
			r.setIdReg(rs.getLong(i++));
			r.setFechaHoraDeInsercion(rs.getLong(i++));
			r.setNuevaColocacionBoveda(rs.getString(i++));

			r.setTituloOriginal(rs.getString(i++));
			r.setTituloEnEspaniol(rs.getString(i++));
			r.setRealizador(rs.getString(i++));
			r.setPaisDeRealizacion(rs.getString(i++));
			r.setAnioDeProduccion(rs.getString(i++));
			r.setFormato(rs.getString(i++));
			r.setSoporte(rs.getString(i++));
			r.setEmulsion(rs.getString(i++));
			r.setColor(rs.getString(i++));
			r.setAudio(rs.getString(i++));
			r.setTama(rs.getString(i++));
			r.setMetraje(rs.getString(i++));
			r.setDa(rs.getString(i++));
			// lfmm-12-04-2013 r.setEstadoFisico(rs.getString(i++));
			r.setNorma(rs.getString(i++));
			r.setCuadro(rs.getString(i++));
			r.setDuracion(rs.getString(i++));
			// lfmm-12-04-2013 r.setRegion(rs.getString(i++));
			// lfmm-12-04-2013 r.setPantalla(rs.getString(i++));
			// lfmm-12-04-2013 r.setFuente(rs.getString(i++));
			r.setOrigen(rs.getString(i++));
			r.setFechaDeGrabacion(rs.getString(i++));
			r.setFechaDeRevision(rs.getString(i++));
			r.setFechaDeIngreso(rs.getString(i++));
			// lfmm-12-04-2013 r.setFechaDeAcidez(rs.getString(i++));
			// lfmm-12-04-2013 r.setFechaDeResultado(rs.getString(i++));
			r.setFechaDeCaptura(rs.getString(i++));
			// lfmm-12-04-2013 r.setFechaDeBaja(rs.getString(i++));
			// lfmm-12-04-2013 r.setOrigenDos(rs.getString(i++));
			r.setColocacion(rs.getString(i++));
			// lfmm-12-04-2013 r.setDistribucion(rs.getString(i++));
			// lfmm-12-04-2013 r.setProgramacion(rs.getString(i++));
			// lfmm-12-04-2013 r.setBiblioteca(rs.getString(i++));
			// inconsistente con version MAC r.setVideoClub(rs.getString(i++));
			// lfmm-12-04-2013 r.setMetrajeOriginal(rs.getString(i++));
			r.setIncomImg(rs.getString(i++));
			r.setSt(rs.getString(i++));
			// lfmm-12-04-2013 r.setOtros(rs.getString(i++));
			r.setReparaciones(rs.getString(i++));
			r.setObservaciones(rs.getString(i++));
			// lfmm-12-04-2013 r.setNotasHistoricas(rs.getString(i++));
			// lfmm-12-04-2013 r.setPrestamo(rs.getString(i++));
			// lfmm-12-04-2013 r.setInstitucionPrestamo(rs.getString(i++));
			// lfmm-12-04-2013 r.setConductoPrestamo(rs.getString(i++));
			// lfmm-12-04-2013 r.setFechaDeSalida(rs.getString(i++));
			// lfmm-12-04-2013 r.setFechaDeRetorno(rs.getString(i++));
			// lfmm-12-04-2013 r.setNoCedulaRecibo(rs.getString(i++));
			r.setNombreRevision(rs.getString(i++));
			r.setNombreCaptura(rs.getString(i++));
			// lfmm-12-04-2013 r.setPosibilidadDeExhibicion(rs.getString(i++));
			// lfmm-12-04-2013 r.setPosibilidadDePrestamo(rs.getString(i++));
			r.setRestricciones(rs.getString(i++));
			// lfmm-12-04-2013 r.setExtras(rs.getString(i++));
			// lfmm-12-04-2013 r.setStatusAcervo(rs.getString(i++));
			// lfmm-12-04-2013 r.setStatusDistribucion(rs.getString(i++));
			// lfmm-12-04-2013 r.setStatusProgramacion(rs.getString(i++));
			// lfmm-12-04-2013 r.setStatusBiblioteca(rs.getString(i++));
			return r;
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * afecta su argumento r.setIdReg(valorAutogenerado) porque LoggerDeTransacciones lo
	 * requiere en afterReturning()
	 */
	public void altaDeNuevoRegistro(String userName, Registro r) {

		Object[] params = new Object[] { // recuerda que uno similar a este en
											// this.obtenParametrosDelLoggerDeTransaccion()
											// apoya a this.logueaTransaccion()
				new Long(0), // valor que proboca el incremento por default en MySql
				r.getFechaHoraDeInsercion(), r.getNuevaColocacionBoveda(),

				r.getTituloOriginal(), r.getTituloEnEspaniol(), r.getRealizador(), r.getPaisDeRealizacion(),
				r.getAnioDeProduccion(), r.getFormato(), r.getSoporte(), r.getEmulsion(), r.getColor(), r.getAudio(),
				r.getTama(), r.getMetraje(), r.getDa(),
				// lfmm-12-04-2013 r.getEstadoFisico(),
				r.getNorma(), r.getCuadro(), r.getDuracion(),
				// lfmm-12-04-2013 r.getRegion(),
				// r.getPantalla(),
				// r.getFuente(),
				r.getOrigen(), r.getFechaDeGrabacion(), r.getFechaDeRevision(), r.getFechaDeIngreso(),
				// lfmm-12-04-2013 r.getFechaDeAcidez(),
				// r.getFechaDeResultado(),
				r.getFechaDeCaptura(),
				// lfmm-12-04-2013 r.getFechaDeBaja(),
				// r.getOrigenDos(),
				r.getColocacion(),
				// lfmm-12-04-2013 r.getDistribucion(),
				// r.getProgramacion(),
				// r.getBiblioteca(),
				//// inconsistente con version MAC r.getVideoClub(),
				// r.getMetrajeOriginal(),
				r.getIncomImg(), r.getSt(),
				// lfmm-12-04-2013 r.getOtros(),
				r.getReparaciones(), r.getObservaciones(),
				// lfmm-12-04-2013 r.getNotasHistoricas(),
				// r.getPrestamo(),
				// r.getInstitucionPrestamo(),
				// r.getConductoPrestamo(),
				// r.getFechaDeSalida(),
				// r.getFechaDeRetorno(),
				// r.getNoCedulaRecibo(),
				r.getNombreRevision(), r.getNombreCaptura(),
				// lfmm-12-04-2013 r.getPosibilidadDeExhibicion(),
				// r.getPosibilidadDePrestamo(),
				r.getRestricciones(),
				// lfmm-12-04-2013 r.getExtras(),
				// r.getStatusAcervo(),
				// r.getStatusDistribucion(),
				// r.getStatusProgramacion(),
				// r.getStatusBiblioteca()
		};
		// log.error("por hacer update de serviceDao altaDeNuevoRegistro");
		if (altaDeNuevoRegistro.update(params) != 1) {
			throw new RuntimeException("Imposible insertar registro:" + r.getIdReg());
		}
		long idRegAutogenerado = this.jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
		r.setIdReg(idRegAutogenerado);
	}

	private class AltaDeNuevoRegistro extends SqlUpdate {

		protected AltaDeNuevoRegistro(DataSource ds) {
			// lfmm-12-04-2013 super(ds, "INSERT INTO "+ TABLA_ACERVO +
			// " VALUES ("+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?)");
			super(ds, "INSERT INTO " + TABLA_ACERVO
					+ "(idReg, fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA, REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR, AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN, FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA, COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION, NOMBRE_CAPTURA, RESTRICCIONES)"
					+ " VALUES (" + "?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?)");
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.BIGINT));// fechaHoraInsercion
			declareParameter(new SqlParameter(Types.VARCHAR));// colocacionBoveda
			// lfmm-12-04-2013 for(int i = 1; i < 57; i++)
			for (int i = 1; i < 30; i++)
				declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public void logueaTransaccion(String usuario, String operacion, Registro registro) {
		Object[] params = null;
		try {
			if (operacion.equals("actualizaUnRegistro") || operacion.equals("actualizaDataBovedaEnAcervo")
					|| operacion.equals("autorizaPropuesta")) {
				String strPre = "pre_" + operacion;
				// recupera la imagen del registro antes de su actualizacion
				Registro registroOriginal = leeRegistroPorId(registro.getIdReg());
				params = this.obtenParametrosDelLoggerDeTransaccion(registroOriginal, usuario, strPre);
				if (logDeMovimientos.update(params) != 1) {
					throw new RuntimeException("Imposible insertar en logDeMovimientos1:" + registro.getIdReg()
							+ ",usuario:" + usuario + ",operacion:" + operacion);
				}
			}
			params = this.obtenParametrosDelLoggerDeTransaccion(registro, usuario, operacion);
			if (logDeMovimientos.update(params) != 1) {
				throw new RuntimeException("Imposible insertar en logDeMovimientos2:" + registro.getIdReg()
						+ ",usuario:" + usuario + ",operacion:" + operacion);
			}
			if (operacion.equals("autorizaPropuesta")) {
				// loguea la propuesta original que se va a eliminar
				// TODO

			}
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
		catch (Exception e) {
			log.error(e);
		}
	}

	private Object[] obtenParametrosDelLoggerDeTransaccion(Registro r, String usuario, String operacion) {
		// log.error("por armar params de obtenParametrosDelLoggerDeTransaccion");
		Object[] params = new Object[] { // recuerda que uno similar a este apoya a
											// this.altaDeNuevoRegistro()
				new Long(System.currentTimeMillis()), usuario, operacion, new Long(r.getIdReg()),
				new Long(r.getFechaHoraDeInsercion()), r.getNuevaColocacionBoveda(),

				r.getTituloOriginal(), r.getTituloEnEspaniol(), r.getRealizador(), r.getPaisDeRealizacion(),
				r.getAnioDeProduccion(), r.getFormato(), r.getSoporte(), r.getEmulsion(), r.getColor(), r.getAudio(),
				r.getTama(), r.getMetraje(), r.getDa(),
				// lfmm-12-04-2013 r.getEstadoFisico(),
				r.getNorma(), r.getCuadro(), r.getDuracion(),
				// lfmm-12-04-2013 r.getRegion(),
				// r.getPantalla(),
				// r.getFuente(),
				r.getOrigen(), r.getFechaDeGrabacion(), r.getFechaDeRevision(), r.getFechaDeIngreso(),
				// lfmm-12-04-2013 r.getFechaDeAcidez(),
				// r.getFechaDeResultado(),
				r.getFechaDeCaptura(),
				// lfmm-12-04-2013 r.getFechaDeBaja(),
				// r.getOrigenDos(),
				r.getColocacion(),
				// lfmm-12-04-2013 r.getDistribucion(),
				// r.getProgramacion(),
				// r.getBiblioteca(),
				//// inconsistente con version MAC r.getVideoClub(),
				// r.getMetrajeOriginal(),
				r.getIncomImg(), r.getSt(),
				// lfmm-12-04-2013 r.getOtros(),
				r.getReparaciones(), r.getObservaciones(),
				// lfmm-12-04-2013 r.getNotasHistoricas(),
				// r.getPrestamo(),
				// r.getInstitucionPrestamo(),
				// r.getConductoPrestamo(),
				// r.getFechaDeSalida(),
				// r.getFechaDeRetorno(),
				// r.getNoCedulaRecibo(),
				r.getNombreRevision(), r.getNombreCaptura(),
				// lfmm-12-04-2013 r.getPosibilidadDeExhibicion(),
				// r.getPosibilidadDePrestamo(),
				r.getRestricciones(),
				// lfmm-12-04-2013 r.getExtras(),
				// r.getStatusAcervo(),
				// r.getStatusDistribucion(),
				// r.getStatusProgramacion(),
				// r.getStatusBiblioteca()
		};
		return params;
	}

	private class LogDeMovimientos extends SqlUpdate {

		protected LogDeMovimientos(DataSource ds) {
			// lfmm-12-04-2013
			// super(ds, "INSERT INTO " + TABLA_LOG_MOVIMIENTOS + " VALUES
			// (?,?,?,"+//<--corresponden a los campos fechaHora, usuario y operacion
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?,?,"+
			// "?,?,?,?,?,?,?,?,?)");
			super(ds, "INSERT INTO " + TABLA_LOG_MOVIMIENTOS
					+ "(fechaHora, usuario, operacion, idReg, fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA, REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR, AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN, FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA, COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION, NOMBRE_CAPTURA, RESTRICCIONES)"
					+ " VALUES (?,?,?," + // <--corresponden a los campos fechaHora,
											// usuario y operacion
					"?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?)");
			declareParameter(new SqlParameter(Types.BIGINT));// fechaHora
			declareParameter(new SqlParameter(Types.VARCHAR));// usuario
			declareParameter(new SqlParameter(Types.VARCHAR));// operacion
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.BIGINT));// fechaHoraDeInsercion
			declareParameter(new SqlParameter(Types.VARCHAR));// colocacionBoveda
			// lfmm-12-04-2013 for(int i = 1; i < 57; i++)
			for (int i = 1; i < 30; i++)
				declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public Registro leeRegistroPorId(long idReg) {
		List<Object> lista = null;
		try {
			lista = (List<Object>) leeRegistroPorId.execute(idReg);
			if (lista.size() == 1) {
				return (Registro) lista.get(0);
			}
		}
		catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	private class LeeRegistroPorId extends MappingSqlQuery {

		private MapRowDeRegistros mapRowDeApoyoParaRegistros;

		protected LeeRegistroPorId(DataSource ds, MapRowDeRegistros mapRowDeApoyoParaRegistros) {
			super(ds,
					"SELECT idReg, fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA, REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR, AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN, FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA, COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION, NOMBRE_CAPTURA, RESTRICCIONES FROM "
							+ TABLA_ACERVO + " WHERE IDREG = ?");
			declareParameter(new SqlParameter(Types.BIGINT));
			compile();
			this.mapRowDeApoyoParaRegistros = mapRowDeApoyoParaRegistros;
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			if (BovedasDao.this.nombresDeCampoEnAcervo == null) {
				// inicializa los nombres de campo que se necesitan para
				// actualizaUnRegistro()
				java.sql.ResultSetMetaData meta = rs.getMetaData();
				BovedasDao.this.nombresDeCampoEnAcervo = new String[meta.getColumnCount()];
				for (int i = 0; i < BovedasDao.this.nombresDeCampoEnAcervo.length; i++) {
					BovedasDao.this.nombresDeCampoEnAcervo[i] = meta.getColumnName(i + 1);
				}
			}
			return mapRowDeApoyoParaRegistros.mapRow(rs, rownum);
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	private void obtenNombresDeCamposAndCreaClaseActualizaUnRegistro() {
		// si nombres de campo aun no se inicializa, lee el registro de idReg menor y
		// aprovecha el viaje para obtener los nombres de campo
		// y formar el QUERY DE UPDATE
		String query = "SELECT MIN(IDREG) FROM " + TABLA_ACERVO;
		while (this.nombresDeCampoEnAcervo == null) {
			long menor = jdbcTemplate.queryForObject(query, Long.class);
			// durante la primera ejecucion del mapRow de la clase en que
			// leeRegistroPorId(menor) se apoya,
			// utilizando los metadatos del query, se hace la inicializacion del arreglo
			// this.nombresDeCampoEnAcervo
			this.leeRegistroPorId(menor);
		}
		// ya teniendo los nombres de campo, forma el query de update
		StringBuffer buff = new StringBuffer("UPDATE " + TABLA_ACERVO + " SET ");
		for (int i = 2; i < this.nombresDeCampoEnAcervo.length - 1; i++) {// note que
																			// omitimos
																			// indices [0]
																			// y [1] que
																			// corresponden
																			// a idReg y
																			// fechaHoraInsercion
			buff.append(this.nombresDeCampoEnAcervo[i] + " = ?, ");
		}
		buff.append(this.nombresDeCampoEnAcervo[this.nombresDeCampoEnAcervo.length - 1] + " = ? ");// <--
																									// excepcional
																									// porque
																									// no
																									// lleva
																									// coma
		buff.append("WHERE idReg = ? ");
		this.updateStmtDeAcervo = buff.toString();
		// finalmente, estamos listos y creamos la clase
		this.actualizaUnRegistro = new ActualizaUnRegistro(getDataSource());
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public boolean actualizaUnRegistro(Registro r) {
		if (this.actualizaUnRegistro == null) {
			this.obtenNombresDeCamposAndCreaClaseActualizaUnRegistro();
		}
		Object[] params = new Object[] { r.getNuevaColocacionBoveda(),

				r.getTituloOriginal(), r.getTituloEnEspaniol(), r.getRealizador(), r.getPaisDeRealizacion(),
				r.getAnioDeProduccion(), r.getFormato(), r.getSoporte(), r.getEmulsion(), r.getColor(), r.getAudio(),
				r.getTama(), r.getMetraje(), r.getDa(),
				// lfmm-12-04-2013 r.getEstadoFisico(),
				r.getNorma(), r.getCuadro(), r.getDuracion(),
				// lfmm-12-04-2013 r.getRegion(),
				// r.getPantalla(),
				// r.getFuente(),
				r.getOrigen(), r.getFechaDeGrabacion(), r.getFechaDeRevision(), r.getFechaDeIngreso(),
				// lfmm-12-04-2013 r.getFechaDeAcidez(),
				// r.getFechaDeResultado(),
				r.getFechaDeCaptura(),
				// lfmm-12-04-2013 r.getFechaDeBaja(),
				// r.getOrigenDos(),
				r.getColocacion(),
				// lfmm-12-04-2013 r.getDistribucion(),
				// r.getProgramacion(),
				// r.getBiblioteca(),
				//// inconsistente con version MAC r.getVideoClub(),
				// r.getMetrajeOriginal(),
				r.getIncomImg(), r.getSt(),
				// lfmm-12-04-2013 r.getOtros(),
				r.getReparaciones(), r.getObservaciones(),
				// lfmm-12-04-2013 r.getNotasHistoricas(),
				// r.getPrestamo(),
				// r.getInstitucionPrestamo(),
				// r.getConductoPrestamo(),
				// r.getFechaDeSalida(),
				// r.getFechaDeRetorno(),
				// r.getNoCedulaRecibo(),
				r.getNombreRevision(), r.getNombreCaptura(),
				// lfmm-12-04-2013 r.getPosibilidadDeExhibicion(),
				// r.getPosibilidadDePrestamo(),
				r.getRestricciones(),
				// lfmm-12-04-2013 r.getExtras(),
				// r.getStatusAcervo(),
				// r.getStatusDistribucion(),
				// r.getStatusProgramacion(),
				// r.getStatusBiblioteca(),
				r.getIdReg() };
		if (actualizaUnRegistro.update(params) != 1) {
			throw new RuntimeException("Imposible actualizar registro:" + r.getIdReg());
		}
		return true;
	}

	private class ActualizaUnRegistro extends SqlUpdate {

		protected ActualizaUnRegistro(DataSource ds) {
			super(ds, BovedasDao.this.updateStmtDeAcervo);
			for (int i = 2; i < BovedasDao.this.nombresDeCampoEnAcervo.length; i++)// note
																					// que
																					// omitimos
																					// el
																					// de
																					// indices
																					// [0]
																					// y
																					// [1]
																					// que
																					// corresponden
																					// a
																					// idReg
																					// y
																					// fechaHoraInsercion
				declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BIGINT));// es idReg y omitimos
																// fechaHoraInsercion
			compile();
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * La baja de un registro implica: eliminar cualquier referencia al idReg desde la
	 * tabla "bovedaPropone" (lo hacemos porque el cascade delete por alguna razon de
	 * desconocimiento de MySql, no funciona, quizas el tipo de tabla no es inoddb o que
	 * se yo) y eliminar el registro de la tabla "Acervo". NOTA el motivo de la baja queda
	 * en el logAcervo como sufijo del campo operacion de baja (operaciones con prefijo
	 * 'b:').
	 */
	public boolean bajaDeUnRegistro(String usuario, Registro r) {
		/*
		 * Para probar si funcionan las transacciones, reemplazar el codigo comentado por
		 * la linea siguiente. Cuando el registro a borrar no tiene propuestas de cambios
		 * en tabla bovedaPropone, se espera que haga rollback por arrojar la excepcion.
		 * El rollback debe de eliminar de logAcervo el registro generado por
		 * bovedasDaoImpl.logueaTransaccion(usuario, "b:"+motivo, registro); que antes se
		 * invoc� desde el servicio de BovedasServiceFace.bajaDeUnRegistro(), mismo
		 * servicio que inmediatemente despues invoca a this.bajaDeUnRegistro().
		 * if(bajaDeUnaPropuesta.update(r.getIdReg()) != 1){ throw new
		 * RuntimeException("Imposible eliminar propuesta:"+r.getIdReg()); }
		 */
		// bajaDeUnaPropuesta.update(r.getIdReg());
		if (bajaDeUnRegistro.update(r.getIdReg()) != 1) {
			throw new RuntimeException("Imposible eliminar registro:" + r.getIdReg());
		}
		return true;
	}

	private class BajaDeUnRegistro extends SqlUpdate {

		protected BajaDeUnRegistro(DataSource ds) {
			super(ds, "DELETE FROM " + TABLA_ACERVO + " WHERE idReg = ?");
			declareParameter(new SqlParameter(Types.BIGINT));
			compile();
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private class AltaDeNuevaPropuesta extends SqlUpdate {

		protected AltaDeNuevaPropuesta(DataSource ds) {
			super(ds, "INSERT INTO " + TABLA_BOVEDAS
					+ "(idReg, fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA, REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR, AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN, FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA, COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION, NOMBRE_CAPTURA, RESTRICCIONES)"
					+ " VALUES (" +
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?)");
					"?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?)");
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.BIGINT));// fechaHoraInsercion
			declareParameter(new SqlParameter(Types.VARCHAR));// colocacionBoveda
			// lfmm-12-04-2013 for(int i = 1; i < 57; i++)
			for (int i = 1; i < 30; i++)
				declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

	}

	private class LogDePropuestas extends SqlUpdate {

		protected LogDePropuestas(DataSource ds) {
			// lfmm-12-04-2013 super(ds, "INSERT INTO " + TABLA_LOG_PROPUESTAS + " VALUES
			// (?,?,?,"+//<--corresponden a los campos fechaHora, usuario y operacion
			super(ds, "INSERT INTO " + TABLA_LOG_PROPUESTAS
					+ "(fechaHora, usuario, operacion, idReg, fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA, REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR, AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN, FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA, COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION, NOMBRE_CAPTURA, RESTRICCIONES)"
					+ " VALUES (?,?,?," + // <--corresponden a los campos fechaHora,
											// usuario y operacion
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?,?,"+
					// "?,?,?,?,?,?,?,?,?)");
					"?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?," + "?,?)");
			declareParameter(new SqlParameter(Types.BIGINT));// fechaHora
			declareParameter(new SqlParameter(Types.VARCHAR));// usuario
			declareParameter(new SqlParameter(Types.VARCHAR));// operacion
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.BIGINT));// fechaHoraDeInsercion
			declareParameter(new SqlParameter(Types.VARCHAR));// colocacionBoveda
			// lfmm-12-04-2013 for(int i = 1; i < 57; i++)
			for (int i = 1; i < 30; i++)
				declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

	}

	private class ActualizaUnaPropuesta extends SqlUpdate {

		protected ActualizaUnaPropuesta(DataSource ds) {
			super(ds, BovedasDao.this.updateStmtDePropuesta);
			for (int i = 2; i < BovedasDao.this.nombresDeCampoEnBovedaPropone.length; i++)// note
																							// que
																							// omitimos
																							// el
																							// de
																							// indices
																							// [0]
																							// y
																							// [1]
																							// que
																							// corresponden
																							// a
																							// idReg
																							// y
																							// fechaHoraInsercion
				declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BIGINT));// es idReg y omitimos
																// fechaHoraInsercion
			compile();
		}

	}

	//////////////////////////////////// propiedades Spring beans
	//////////////////////////////////// /////////////////////////////

}
