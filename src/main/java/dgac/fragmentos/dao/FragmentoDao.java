package dgac.fragmentos.dao;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import dgac.fragmentos.utilerias.MinutosSegundos;
import dgac.fragmentos.entidades.BinFragmento;
import dgac.fragmentos.entidades.BinSegmento;
import dgac.utilidades.TransformaBins;
import dgac.fragmentos.servicios.BovedasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FragmentoDao extends JdbcDaoSupport {

	private static final Log log = LogFactory.getLog(FragmentoDao.class);

	public static final String strNoExiste = "No existe en el acervo la colocaci�n:";

	private LogDeNuevosFragmentos logDeNuevosFragmentos;

	private LogDeViejosFragmentos logDeViejosFragmentos;

	private LogDeViejosSegmentos logDeViejosSegmentos;

	private LeeFormatoDeColocacion leeFormatoDeColocacion;

	private InsertaRegistrosEnFragmentos insertaRegistrosEnFragmentos;

	private InsertaRegistrosEnTransFragmentosAcervo insertaRegistrosEnTransFragmentosAcervo;

	private ActualizaSegmentoEnTransFragmentosAcervo actualizaSegmentoEnTransFragmentosAcervo;

	private LeeRegistroPorId leeRegistroPorId;

	private LeeSegmentos leeSegmentos;

	private ObtieneSegmentoDeClaf obtieneSegmentoDeClaf;

	private ObtieneSegmentoDeAcervo obtieneSegmentoDeAcervo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	private TransformaBins transformaBins;

	private EliminaUnFragmento eliminaUnFragmento;

	private EliminaSegmentosDeUnFragmento eliminaSegmentosDeUnFragmento;

	private ActualizaUnFragmento actualizaUnFragmento;

	@PostConstruct
	protected void initDao() {
		setDataSource(dataSource);
		this.transformaBins = new TransformaBins();
		this.jdbcTemplate = super.getJdbcTemplate();

		this.logDeNuevosFragmentos = new LogDeNuevosFragmentos(getDataSource());
		this.logDeViejosFragmentos = new LogDeViejosFragmentos(getDataSource());
		this.logDeViejosSegmentos = new LogDeViejosSegmentos(getDataSource());

		this.leeFormatoDeColocacion = new LeeFormatoDeColocacion(super.getDataSource());// <--
																						// pendiente
																						// revisar
																						// que
																						// onda
																						// con
																						// reads
																						// transaccionales
																						// de
																						// este
		this.insertaRegistrosEnFragmentos = new InsertaRegistrosEnFragmentos(super.getDataSource());
		this.insertaRegistrosEnTransFragmentosAcervo = new InsertaRegistrosEnTransFragmentosAcervo(
				super.getDataSource());
		this.actualizaSegmentoEnTransFragmentosAcervo = new ActualizaSegmentoEnTransFragmentosAcervo(
				super.getDataSource());
		this.leeRegistroPorId = new LeeRegistroPorId(super.getDataSource());
		this.leeSegmentos = new LeeSegmentos(super.getDataSource());
		this.obtieneSegmentoDeClaf = new ObtieneSegmentoDeClaf(super.getDataSource());
		this.obtieneSegmentoDeAcervo = new ObtieneSegmentoDeAcervo(super.getDataSource());
		this.eliminaUnFragmento = new EliminaUnFragmento(super.getDataSource());
		this.eliminaSegmentosDeUnFragmento = new EliminaSegmentosDeUnFragmento(super.getDataSource());
		this.actualizaUnFragmento = new ActualizaUnFragmento(super.getDataSource());
	}

	/**
	 * inserta en la tabla logDeFragmentos una descripcion de la transaccion y quien la
	 * hizo. Los primeros dos argumentos siempre presentes. Los demas argumentos dependen
	 * del valor del segundo argumento "operacion".
	 * @param usuario
	 * @param operacion indica el nombre de la transaccion en ejecucion
	 * @param BinFragmento
	 * @param idRegFragmento este argumento solo presente al eliminar un fragmento
	 */
	public void logueaTransaccion(String usuario, String operacion, BinFragmento binFragmentoSegmentosOriginal) {
		Object[] params = null;
		try {
			if (operacion.startsWith("altaSimultanea") || operacion.startsWith("inserta")) {
				params = new Object[] { new java.util.Date(), usuario, operacion };
				if (logDeNuevosFragmentos.update(params) != 1) {
					throw new RuntimeException(
							"Imposible insertar en logDeNuevosFragmentos para operacion:" + operacion);
				}
			}
			else if (operacion.equals("actualizaUnFragmento") || operacion.equals("eliminaUnFragmento")) {
				// nota los nombres de la operacion se restringen a 9 caracteres:
				// actualiaz o eliminaUn
				java.util.Date ahora = new java.util.Date();
				insertaEnLogDeViejosFragmentos(ahora, usuario, operacion.substring(0, 9),
						binFragmentoSegmentosOriginal);
				insertaArregloDeViejosSegmentos(ahora, usuario, operacion.substring(0, 9),
						binFragmentoSegmentosOriginal);
			}
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
		catch (Exception e) {
			log.error(e);
			throw new RuntimeException("**ERROR:" + e.getMessage() + "\n**CAUSA:" + e.getCause());
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inserta imagen preexistente de Fragmento en la tabla logOldFragmentos
	 * @param binFragmentoSegmentos describe un fragmento con el tipo
	 * BinFragmentoSegmentos
	 */
	public void insertaEnLogDeViejosFragmentos(java.util.Date fechaHora, String usuario, String operacion,
			BinFragmento binFragmentoSegmentos) {
		try {
			MinutosSegundos minSeg = this.transformaBins
					.calculaDuracionComoContribucionDeSegmentos(binFragmentoSegmentos, this);

			Object[] params = new Object[] { fechaHora, usuario, operacion, binFragmentoSegmentos.getIdReg(),
					binFragmentoSegmentos.getTituloDelFragmento(), binFragmentoSegmentos.getColeccion(),
					binFragmentoSegmentos.getRealizador(), binFragmentoSegmentos.getUbicacionGeografica(),
					// nota, los siguientes tres campos los obtuve copiando "a ojos
					// cerrados" lo que hace this.actualizaUnFragmento(), debe funcionar.
					binFragmentoSegmentos.getCodigoFechaOrEpoca() == 'E' ? "" + BinFragmento.EPOCA
							: "" + BinFragmento.FECHA_PRECISA,
					binFragmentoSegmentos.getFechaMinParaLog(), binFragmentoSegmentos.getFechaMaxParaLog(),
					minSeg.minutos, minSeg.segundos, binFragmentoSegmentos.getDescriptoresImplicitos(),
					binFragmentoSegmentos.getDescriptoresExplicitos(), binFragmentoSegmentos.getObservaciones() };
			this.logDeViejosFragmentos.update(params);

		}
		catch (DataAccessException e) {
			log.error(e);
			throw e;
		}
	}

	private class LogDeViejosFragmentos extends SqlUpdate {

		protected LogDeViejosFragmentos(DataSource ds) {
			super(ds, "INSERT INTO logOldFragmentos VALUES (?,?,?," + // <--corresponden a
																		// los campos
																		// fechaHora,
																		// usuario y
																		// operacion con
																		// parametros
					"?,?,?,?,?, ?,?,?,?,?, ?,?,?)");
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaHora
			declareParameter(new SqlParameter(Types.VARCHAR));// usuario
			declareParameter(new SqlParameter(Types.VARCHAR));// nombre de la transaccion,
																// i.e. la operacion
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.VARCHAR));// tituloDelFragmento
			declareParameter(new SqlParameter(Types.VARCHAR));// coleccion
			declareParameter(new SqlParameter(Types.VARCHAR));// realizador
			declareParameter(new SqlParameter(Types.VARCHAR));// ubicacionGeografica
			declareParameter(new SqlParameter(Types.VARCHAR));// codigoFechaOrEpoca
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaRangoMin
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaRangoMax
			declareParameter(new SqlParameter(Types.SMALLINT));// duracionMinutos
			declareParameter(new SqlParameter(Types.TINYINT));// duracionSegundos
			declareParameter(new SqlParameter(Types.VARCHAR));// descriptoresImplicitos
			declareParameter(new SqlParameter(Types.CLOB));// descriptoresExplicitos////lfmm
															// TEXT tambi�n se cambi� a
															// TEXT la tabla de
															// logOldFragmentos
			declareParameter(new SqlParameter(Types.VARCHAR));// observaciones
			compile();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inserta el arreglo en tabla de log insertaEnOldTransFragmentosAcervo.
	 * @param binFragmentoSegmentos
	 */
	public void insertaArregloDeViejosSegmentos(java.util.Date fechaHora, String usuario, String operacion,
			BinFragmento binFragmentoSegmentos) {
		long idRegFragmento = binFragmentoSegmentos.getIdReg();

		for (Iterator it = binFragmentoSegmentos.getAlSegmentos().iterator(); it.hasNext();) {
			BinSegmento binSegmentoDeAcervo = (BinSegmento) it.next();
			/*
			 * BinTransitiva binTransitiva = new BinTransitiva();
			 * binTransitiva.setIdReg(binSegmentoDeAcervo.getIdRegTransitiva());
			 * binTransitiva.setIdRegAcervo(binSegmentoDeAcervo.getIdRegAcervo());
			 * binTransitiva.setIdRegFragmentos(idRegFragmento); char codigo =
			 * binSegmentoDeAcervo.getCodigoPiesOrTiempoOrMetros(); switch(codigo) { case
			 * BinFragmentos.PIETAJE:
			 * binTransitiva.setSegmentoInicio(""+binSegmentoDeAcervo.getPietajeInicio());
			 * //binSegmento.getSegmentoInicio()==getPietajeInicio+" pies"
			 * binTransitiva.setSegmentoFin(""+binSegmentoDeAcervo.getPietajeFin());//
			 * binSegmento.getSegmentoFin()==getPietajeFin+" pies" break; case
			 * BinFragmentos.TIEMPO:
			 * binTransitiva.setSegmentoInicio(""+binSegmentoDeAcervo.getSegmentoInicio())
			 * ;//aqui viene codificado como hh:mm:ss
			 * binTransitiva.setSegmentoFin(""+binSegmentoDeAcervo.getSegmentoFin());//
			 * aqui viene codificado como hh:mm:ss break;
			 *
			 * default: log.
			 * error("insertaArregloDeViejosSegmentos no tiene implantado el manejo del codigo de tiempo/metros:"
			 * +codigo+
			 * "para fragmento llamado "+binFragmentoSegmentos.getTituloDelFragmento());
			 * throw new
			 * RuntimeException("insertaArregloDeViejosSegmentos no tiene implantado el manejo del codigo de tiempo/metros:"
			 * +codigo+
			 * "para fragmento llamado "+binFragmentoSegmentos.getTituloDelFragmento()); }
			 *
			 * binTransitiva.setCodigoPiesOrTiempoOrMetros(""+binSegmentoDeAcervo.
			 * getCodigoPiesOrTiempoOrMetros());
			 * binTransitiva.setNumRolloOrVolumenDeInicioDelSegmento(binSegmentoDeAcervo.
			 * getNumRolloOrVolumenDeInicioDelSegmento());
			 * binTransitiva.setContribuyeAlTiempoTotalDelFragmento(binSegmentoDeAcervo.
			 * getContribuyeAlTiempoTotalDelFragmento());
			 */
			this.insertaEnOldTransFragmentosAcervo(fechaHora, usuario, operacion, binSegmentoDeAcervo);
		}
	}

	/**
	 * Inserta un registro en tabla de log insertaEnOldTransFragmentosAcervo que guarda
	 * imagenes de la tabla transitiva transFragmentosAcervo.
	 * @param binTransitiva describe un fragmento con el tipo BinTransitiva
	 */
	public void insertaEnOldTransFragmentosAcervo(java.util.Date fechaHora, String usuario, String operacion,
			BinSegmento binTransitiva) {
		try {
			Object[] params = new Object[] { fechaHora, usuario, operacion, binTransitiva.getIdRegTransitiva(),
					binTransitiva.getIdRegFragmentos(), binTransitiva.getIdRegAcervo(),
					binTransitiva.getCodigoPiesOrTiempoOrMetros(),
					binTransitiva.getNumRolloOrVolumenDeInicioDelSegmento(), binTransitiva.getSegmentoInicio(),
					binTransitiva.getSegmentoFin(), binTransitiva.getContribuyeAlTiempoTotalDelFragmento() };
			this.logDeViejosSegmentos.update(params);// logDeViejosSegmentos
														// insertaRegistrosEnTransFragmentosAcervo
		}
		catch (DataAccessException e) {
			log.error(e);
			throw e;
		}
	}

	private class LogDeViejosSegmentos extends SqlUpdate {

		protected LogDeViejosSegmentos(DataSource ds) {
			super(ds, "INSERT INTO logOldTransFragmentosAcervo VALUES (?,?,?," + // <--corresponden
																					// a
																					// los
																					// campos
																					// fechaHora,
																					// usuario
																					// y
																					// operacion
																					// con
																					// parametros
					"?,?,?,?,?, ?,?,?)");
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaHora
			declareParameter(new SqlParameter(Types.VARCHAR));// usuario
			declareParameter(new SqlParameter(Types.VARCHAR));// operacion
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.BIGINT));// idRegFragmentos
			declareParameter(new SqlParameter(Types.BIGINT));// idRegAcervo
			declareParameter(new SqlParameter(Types.CHAR));// codigoPiesOrTiempoOrMetros
			declareParameter(new SqlParameter(Types.SMALLINT));// numRolloOrVolumenDeInicioDelSegmento
			declareParameter(new SqlParameter(Types.VARCHAR));// segmentoInicio
			declareParameter(new SqlParameter(Types.VARCHAR));// segmentoFin
			declareParameter(new SqlParameter(Types.VARCHAR));// contribuyeAlTiempoTotalDelFragmento
			compile();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class LogDeNuevosFragmentos extends SqlUpdate {

		protected LogDeNuevosFragmentos(DataSource ds) {
			super(ds, "INSERT INTO logDeNuevosFragmentos VALUES (?,?,?)");// <--corresponden
																			// a los
																			// campos
																			// fechaHora,
																			// usuario y
																			// operacion
																			// con
																			// parametros
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaHora
			declareParameter(new SqlParameter(Types.VARCHAR));// usuario
			declareParameter(new SqlParameter(Types.VARCHAR));// operacion
			compile();
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * metodo que NO es de la interfaz y se utiliza para sumar las contribuciones de
	 * segmentos Pendiente de implantar
	 */
	public String getFormato(String colocacion) {
		java.util.List formatos = this.leeFormatoDeColocacion.execute(colocacion);
		if (formatos != null && formatos.size() > 0) {
			return (String) formatos.get(0);
		}
		else {
			return "";
		}
	}

	/**
	 * clase que lee el acervo los registros de una colocacion dada y regresa la coleccion
	 * de formatos. Note que quien sabe como se comporte ante concurrencia si cambian
	 * valores del formato dado que esta conexion es diferente de la del sistema de
	 * bovedas i.e. acervo
	 *
	 * @author ger
	 *
	 */
	private class LeeFormatoDeColocacion extends MappingSqlQuery {

		protected LeeFormatoDeColocacion(DataSource ds) {
			// super(ds,"SELECT formato FROM acervo WHERE colocacion like ?"); //lfmm
			// 14-3-2014 elimiacion virtual
			super(ds, "SELECT formato FROM acervo  WHERE colocacion like ? AND activo=1");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			String formato = rs.getString(1).trim();
			return formato;
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inserta un nuevo registro en la tabla de Fragmentos rescata el valor auto
	 * incrementable asignado a su llave idReg en tabla de fragmentos y lo asigna a la
	 * propiedad del binFragmentoSegmentos de nombre idRegFragmento.
	 *
	 * NOTE QUE HAY UN METODO DEL MISMO NOMBRE PERO CON ARGUMENTO DIFERENTE.
	 * @param binFragmentoSegmentos describe un fragmento y sus segmentos
	 */
	public void insertaRegistrosEnFragmentos(BinFragmento binFragmentoSegmentos) {
		try {
			MinutosSegundos minSeg = this.transformaBins
					.calculaDuracionComoContribucionDeSegmentos(binFragmentoSegmentos, this);

			java.util.Date fchMin;
			java.util.Date fchMax;
			if (binFragmentoSegmentos.getCodigoFechaOrEpoca() == 'E') {
				fchMin = binFragmentoSegmentos.getStrFechaRangoMin();
				fchMax = binFragmentoSegmentos.getStrFechaRangoMax();
			}
			else { // note que asignamos la misma fecha a ambos: el inicio y el fin del
					// rango de tiempo
				fchMin = fchMax = binFragmentoSegmentos.getStrFechaRangoMin();
			}
			System.out.println(binFragmentoSegmentos.toString());

			Object[] params = new Object[] { new Long(0L), binFragmentoSegmentos.getTituloDelFragmento(),
					binFragmentoSegmentos.getColeccion(), binFragmentoSegmentos.getRealizador(),
					binFragmentoSegmentos.getUbicacionGeografica(),
					binFragmentoSegmentos.getCodigoFechaOrEpoca() == 'E' ? "" + BinFragmento.EPOCA
							: "" + BinFragmento.FECHA_PRECISA,
					fchMin, fchMax, minSeg.minutos, minSeg.segundos,
					// NOTE QUE EL USUARIO NO SE ENTERA DE SU ERROR DE ESCRIBIR UN
					// ARTICULO EN LOS SIGUIENTES TRES CAMPOB
					binFragmentoSegmentos.getDescriptoresImplicitos().substring(0,
							Math.min(binFragmentoSegmentos.getDescriptoresImplicitos().trim().length(), 8000)),
					binFragmentoSegmentos.getDescriptoresExplicitos().substring(0,
							Math.min(binFragmentoSegmentos.getDescriptoresExplicitos().trim().length(), 8000)), // cambio
																												// a
																												// 8000
																												// cuando
																												// Angel
																												// Mtz.
																												// requer�a
																												// insertar
																												// textos
																												// m�s
																												// largos
					binFragmentoSegmentos.getObservaciones().substring(0,
							Math.min(binFragmentoSegmentos.getObservaciones().trim().length(), 8000)) };
			System.out.println(binFragmentoSegmentos.toString());

			this.insertaRegistrosEnFragmentos.update(params);
			long idRegAutogenerado = this.jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
			binFragmentoSegmentos.setIdReg(idRegAutogenerado);
		}
		catch (DataAccessException e) {
			log.error(e);
			throw e;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inserta el arreglo de segmentos asociado en su primer argumento como
	 * binFragmentoSegmentos.getAlBinSegmento().
	 * @param binFragmentoSegmentos
	 * @param acervoService, pasamos la referencia porque el dao requiere una consulta al
	 * acervo
	 */
	public void insertaArregloDeSegmentos(BinFragmento binFragmentoSegmentos, BovedasService acervoService) {
		long idRegFragmento = binFragmentoSegmentos.getIdReg();
		for (Iterator it = binFragmentoSegmentos.getAlSegmentos().iterator(); it.hasNext();) {
			BinSegmento binSegmentoDeAcervo = (BinSegmento) it.next();

			binSegmentoDeAcervo.setIdRegFragmentos(idRegFragmento);

			char codigo = binSegmentoDeAcervo.getCodigoPiesOrTiempoOrMetros();
			if (codigo == BinFragmento.PIETAJE) {
				binSegmentoDeAcervo.setSegmentoInicio("" + binSegmentoDeAcervo.getPietajeInicio());// binSegmento.getSegmentoInicio()==getPietajeInicio+"
																									// pies"
				binSegmentoDeAcervo.setSegmentoFin("" + binSegmentoDeAcervo.getPietajeFin());// binSegmento.getSegmentoFin()==getPietajeFin+"
																								// pies"
			}
			else if (codigo == BinFragmento.TIEMPO) {

				binSegmentoDeAcervo.setSegmentoInicio("" + binSegmentoDeAcervo.getSegmentoInicio());// aqui
																									// viene
																									// codificado
																									// como
																									// hh:mm:ss
				binSegmentoDeAcervo.setSegmentoFin("" + binSegmentoDeAcervo.getSegmentoFin());// aqui
																								// viene
																								// codificado
																								// como
																								// hh:mm:ss
			}
			else {
				log.error("insertaRegistrosEnFragmentos no tiene implantado el manejo del codigo de tiempo/metros:"
						+ codigo + "para fragmento llamado " + binFragmentoSegmentos.getTituloDelFragmento());
				throw new RuntimeException(
						"insertaRegistrosEnFragmentos no tiene implantado el manejo del codigo de tiempo/metros:"
								+ codigo + "para fragmento llamado " + binFragmentoSegmentos.getTituloDelFragmento());
			}
			log.info(binSegmentoDeAcervo.getIdRegTransitiva());

			if (binSegmentoDeAcervo.getIdRegTransitiva() == null) {
				log.info("Es nuevo segmento");
				this.insertaRegistrosEnTransFragmentosAcervo(binSegmentoDeAcervo);
			}
			else {
				log.info("Es edición");
				this.actualizaSegmentoEnTransFragmentosAcervo(binSegmentoDeAcervo);
			}
		}
	}

	/**
	 * Como varios rollos de pelicula se pueden almacenar en una misma lata, puede ser que
	 * el query entregue mas de un idReg pero todos estos registros llevar'an LA MISMA
	 * COLOCACION pues se refieren a una misma lata, por lo tanto, agarramos cualquier
	 * idReg!
	 * @param colocacion
	 * @param acervoService se utiliza para consultar el Acervo
	 * @return el idReg en acervo de la colocacion dada
	 * @exception arroja excepcion si no encuentra registro alguno con la colocacion dada
	 */

	/**
	 * Verifica que exista al menos una colocacion del segmento, se utiliza para editar un
	 * segemento o agregar uno nuevo
	 * @param colocacion la colocacion del segmento a verificar
	 * @param bovedaService se utiliza para consultar el acervo
	 * @return una lista con todas las coincidencias de colocaciones.
	 */
	public Collection colocacionesExistentesDeSegmentos(String colocacion, BovedasService bovedaService) {
		return (bovedaService.colocacionesExistentes(colocacion));

	}

	/*
	 * las extensiones a SqlUpdate de Spring son para implantar UPDATE, INSERT y DELETE de
	 * SQL
	 */
	private class InsertaRegistrosEnFragmentos extends SqlUpdate {

		/*
		 * inserta con los otros dos campos costoUnitario y metrajeDeLata con su default
		 * que es cero
		 */
		protected InsertaRegistrosEnFragmentos(DataSource ds) {
			super(ds, "INSERT INTO fragmentos (idReg, tituloDelFragmento, "
					+ "coleccion, realizador, ubicacionGeografica, "
					+ "codigoFechaOrEpoca, fechaRangoMin, fechaRangoMax, " + "duracionMinutos, duracionSegundos, "
					+ "descriptoresImplicitos, descriptoresExplicitos, observaciones) VALUES (?,?,?, "
					+ "?,?,?,?,?, ?,?,?,?,?)");
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.VARCHAR));// tituloDelFragmento
			declareParameter(new SqlParameter(Types.VARCHAR));// coleccion
			declareParameter(new SqlParameter(Types.VARCHAR));// realizador
			declareParameter(new SqlParameter(Types.VARCHAR));// ubicacionGeografica
			declareParameter(new SqlParameter(Types.VARCHAR));// codigoFechaOrEpoca
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaRangoMin
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaRangoMax
			declareParameter(new SqlParameter(Types.SMALLINT));// duracionMinutos
			declareParameter(new SqlParameter(Types.TINYINT));// duracionSegundos
			declareParameter(new SqlParameter(Types.VARCHAR));// descriptoresImplicitos
			declareParameter(new SqlParameter(Types.CLOB));// descriptoresExplicitos////lfmm
															// TEXT
			declareParameter(new SqlParameter(Types.VARCHAR));// observaciones
			compile();
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inserta en tabla transitiva transFragmentosAcervo.
	 * @param binTransitiva describe un fragmento con el tipo BinTransitiva
	 */
	public void insertaRegistrosEnTransFragmentosAcervo(BinSegmento binTransitiva) {
		try {
			Object[] params = new Object[] { new Long(0L), // cero provoca el
															// autoincremento en MySQL
					binTransitiva.getIdRegFragmentos(), binTransitiva.getIdRegAcervo(),
					binTransitiva.getCodigoPiesOrTiempoOrMetros(),
					binTransitiva.getNumRolloOrVolumenDeInicioDelSegmento(), binTransitiva.getSegmentoInicio(),
					binTransitiva.getSegmentoFin(), binTransitiva.getContribuyeAlTiempoTotalDelFragmento(),
					binTransitiva.getSinopsis_segmento(), binTransitiva.getIdreg_copias_titulos() };
			this.insertaRegistrosEnTransFragmentosAcervo.update(params);
		}
		catch (DataAccessException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * las extensiones a SqlUpdate de Spring son para implantar UPDATE, INSERT y DELETE de
	 * SQL
	 */
	private class InsertaRegistrosEnTransFragmentosAcervo extends SqlUpdate {

		protected InsertaRegistrosEnTransFragmentosAcervo(DataSource ds) {
			super(ds,
					"INSERT INTO transFragmentosAcervo (idReg, idRegFragmentos, idRegAcervo, codigoPiesOrTiempoOrMetros, "
							+ "numRolloOrVolumenDeInicioDelSegmento, segmentoInicio, segmentoFin, contribuyeAlTiempoTotalDelFragmento,"
							+ "sinopsis_segmento,idreg_copias_titulos,activo)" + " VALUES (?,?,?, ?, ?,?,?,?,?,?,1)");
			declareParameter(new SqlParameter(Types.BIGINT));// idReg
			declareParameter(new SqlParameter(Types.BIGINT));// idRegFragmentos
			declareParameter(new SqlParameter(Types.BIGINT));// idRegAcervo
			declareParameter(new SqlParameter(Types.CHAR));// codigoPiesOrTiempoOrMetros
			declareParameter(new SqlParameter(Types.SMALLINT));// numRolloOrVolumenDeInicioDelSegmento
			declareParameter(new SqlParameter(Types.VARCHAR));// segmentoInicio
			declareParameter(new SqlParameter(Types.VARCHAR));// segmentoFin
			declareParameter(new SqlParameter(Types.VARCHAR));// contribuyeAlTiempoTotalDelFragmento
			declareParameter(new SqlParameter(Types.VARCHAR));// sinopsis
			declareParameter(new SqlParameter(Types.BIGINT));// idreg_copias_titulos
			compile();
		}

	}

	/**
	 * Inserta en tabla transitiva transFragmentosAcervo.
	 * @param binTransitiva describe un fragmento con el tipo BinTransitiva
	 */
	public void actualizaSegmentoEnTransFragmentosAcervo(BinSegmento binSegmento) {
		try {
			Object[] params = new Object[] {
					// binSegmento.getIdRegFragmentos(),
					binSegmento.getIdRegAcervo(), binSegmento.getCodigoPiesOrTiempoOrMetros(),
					binSegmento.getNumRolloOrVolumenDeInicioDelSegmento(), binSegmento.getSegmentoInicio(),
					binSegmento.getSegmentoFin(), binSegmento.getContribuyeAlTiempoTotalDelFragmento(),
					binSegmento.getSinopsis_segmento(), binSegmento.getIdreg_copias_titulos(),
					binSegmento.getIdRegTransitiva() };
			this.actualizaSegmentoEnTransFragmentosAcervo.update(params);
		}
		catch (DataAccessException e) {
			log.error(e);
			throw e;
		}
	}

	private class ActualizaSegmentoEnTransFragmentosAcervo extends SqlUpdate {

		protected ActualizaSegmentoEnTransFragmentosAcervo(DataSource ds) {
			/*
			 * super(ds,
			 * "INSERT INTO transFragmentosAcervo (idReg, idRegFragmentos, idRegAcervo, codigoPiesOrTiempoOrMetros, "
			 * +
			 * "numRolloOrVolumenDeInicioDelSegmento, segmentoInicio, segmentoFin, contribuyeAlTiempoTotalDelFragmento,"
			 * + "sinopsis_segmento,idreg_copias_titulos,activo)" +
			 * " VALUES (?,?,?, ?, ?,?,?,?,?,?,1)");
			 */
			super(ds, "UPDATE transFragmentosAcervo SET idRegAcervo = ? , codigoPiesOrTiempoOrMetros = ? ,"
					+ "numRolloOrVolumenDeInicioDelSegmento = ? , segmentoInicio = ? , segmentoFin = ?,"
					+ "contribuyeAlTiempoTotalDelFragmento = ? , sinopsis_segmento = ? , idreg_copias_titulos = ?, activo = 1 "
					+ "  WHERE idReg = ?");
			// declareParameter(new SqlParameter(Types.BIGINT));//idRegFragmentos
			declareParameter(new SqlParameter(Types.BIGINT));// idRegAcervo
			declareParameter(new SqlParameter(Types.CHAR));// codigoPiesOrTiempoOrMetros
			declareParameter(new SqlParameter(Types.SMALLINT));// numRolloOrVolumenDeInicioDelSegmento
			declareParameter(new SqlParameter(Types.VARCHAR));// segmentoInicio
			declareParameter(new SqlParameter(Types.VARCHAR));// segmentoFin
			declareParameter(new SqlParameter(Types.VARCHAR));// contribuyeAlTiempoTotalDelFragmento
			declareParameter(new SqlParameter(Types.VARCHAR));// sinopsis_segmento
			declareParameter(new SqlParameter(Types.BIGINT));// idreg_copias_titulos
			declareParameter(new SqlParameter(Types.BIGINT));// idRegTransitiva
			compile();
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta el query argumento que es un SELECT armado al vuelo que restringe los
	 * campos de acuerdo a la especificacion del usuario.
	 * @param strQuery
	 * @return coleccion de BinFragmentos (posiblemente vacia).
	 */
	public Collection<BinFragmento> buscaRegistrosPorQueryConjuntivo(String strQuery) {
		try {
			final ArrayList<BinFragmento> alResultado = new ArrayList<BinFragmento>();
			jdbcTemplate.query(strQuery, new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					BinFragmento b = new BinFragmento();
					b.setIdReg(rs.getLong("idReg"));
					b.setTituloDelFragmento(rs.getString("tituloDelFragmento"));
					b.setColeccion(rs.getString("coleccion"));
					b.setRealizador(rs.getString("realizador"));
					b.setUbicacionGeografica(rs.getString("ubicacionGeografica"));
					b.setCodigoFechaOrEpoca(rs.getString("codigoFechaOrEpoca").charAt(0));
					b.setStrFechaRangoMin(rs.getTimestamp("fechaRangoMin"));
					b.setStrFechaRangoMax(rs.getTimestamp("fechaRangoMax"));
					b.setDuracionMinutos(rs.getShort("duracionMinutos"));
					b.setDuracionSegundos(rs.getByte("duracionSegundos"));
					b.setDescriptoresImplicitos(rs.getString("descriptoresImplicitos"));
					b.setDescriptoresExplicitos(rs.getString("descriptoresExplicitos"));
					b.setObservaciones(rs.getString("observaciones"));
					b.setHistoriaIsis(rs.getString("historiaIsis"));// <-- tiende a
																	// desaparecer
					alResultado.add(b);
				}
			});
			return alResultado;
		}
		catch (DataAccessException e) {
			log.error(e);
			throw e;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Dado el idReg de un fragmento, recupera sus datos y entregalos en instancia de
	 * BinFragmentos.
	 * @param idReg
	 * @return instancia de BinFragmentos con sus datos.
	 */

	public BinFragmento leeRegistroPorId(long idReg) {
		List<Object> lista = null;
		try {
			lista = (List<Object>) leeRegistroPorId.execute(idReg);
			if (lista.size() == 1) {
				return (BinFragmento) lista.get(0);
			}
			else {
				throw new RuntimeException(
						"Error: Se encontraron :" + lista.size() + " registros para el idReg:" + idReg);
			}
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	private class LeeRegistroPorId extends MappingSqlQuery {

		java.text.SimpleDateFormat fmtDateMesNum = new java.text.SimpleDateFormat("dd/MM/yyyy",
				new java.util.Locale("es", "MX"));

		protected LeeRegistroPorId(DataSource ds) {
			// super(ds,"SELECT * FROM fragmentos WHERE IDREG = ?");
			super(ds, "SELECT * FROM fragmentos WHERE IDREG = ? AND ACTIVO=1"); // lfmm
																				// 14-3-2014
																				// elimiacion
																				// virtual
			declareParameter(new SqlParameter(Types.BIGINT));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinFragmento b = new BinFragmento();
			b.setIdReg(rs.getInt("idReg"));
			b.setTituloDelFragmento(rs.getString("tituloDelFragmento"));
			b.setColeccion(rs.getString("coleccion"));
			b.setRealizador(rs.getString("realizador"));
			b.setUbicacionGeografica(rs.getString("ubicacionGeografica"));
			b.setColocacion(rs.getString("colocacion"));
			// System.out.println(rs.getString("pietajeInicio").getClass().getSimpleName());
			// System.out.println(rs.getString("pietajeFin"));
			b.setPietajeInicio(rs.getShort("pietajeInicio"));
			b.setPietajeFin(rs.getShort("pietajeFin"));
			// obtenemos fecha o epoca y la formateamos para jsp de una vez
			char chEpocaOrFecha = rs.getString("codigoFechaOrEpoca").charAt(0);
			b.setCodigoFechaOrEpoca(chEpocaOrFecha);
			java.util.Date fechaRangoMin = rs.getTimestamp("fechaRangoMin");
			b.setStrFechaRangoMin(fechaRangoMin);
			b.setFechaMinParaLog(fechaRangoMin);
			if (chEpocaOrFecha == BinFragmento.FECHA_PRECISA) {
				b.setStrFechaRangoMax(fechaRangoMin); // min y max son la misma en este
														// caso, la asignamos por lo que
														// sea
				b.setFechaMaxParaLog(fechaRangoMin);
				b.setFechaOrEpocaFormateada(fmtDateMesNum.format(fechaRangoMin));
			}
			else {
				b.setStrFechaRangoMax(rs.getTimestamp("fechaRangoMax"));
				b.setFechaMaxParaLog(rs.getTimestamp("fechaRangoMax"));
				b.setFechaOrEpocaFormateada(fmtDateMesNum.format(fechaRangoMin) + " a "
						+ fmtDateMesNum.format(rs.getTimestamp("fechaRangoMax")));
			}
			b.setDuracionMinutos(rs.getShort("duracionMinutos"));
			b.setDuracionSegundos(rs.getByte("duracionSegundos"));
			b.setDescriptoresImplicitos(rs.getString("descriptoresImplicitos"));
			b.setDescriptoresExplicitos(rs.getString("descriptoresExplicitos"));
			b.setObservaciones(rs.getString("observaciones"));
			b.setHistoriaIsis(rs.getString("historiaIsis"));// <-- tiende a desaparecer
			return b;
		}

	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Funcion similar a leeFragmentoConSegmentosParaEdicion excepto porque jala mas
	 * campos y entrega bins de otro tipo.
	 * @param idReg identifica un fragmento.
	 * @return entrega instancia de BinFragmentos con los datos del fragmento idReg y con
	 * los datos de los segmentos asociados al fragmento en el campo
	 * java.util.ArrayList<BinSegmentosDeAcervo> alSegmentos de BinFragmentos.
	 */
	public BinFragmento leeSegmentosDeAcervoDeUnFragmento(long idReg) {
		BinFragmento binFragmentoConSegmentos = this.leeRegistroPorId(idReg);
		java.util.List lSegmentosA = this.leeSegmentos.execute(idReg);
		java.util.ArrayList<BinSegmento> lSegmentos = new java.util.ArrayList<BinSegmento>(lSegmentosA);
		java.util.ArrayList<BinSegmento> lSegmentosConDatos = new java.util.ArrayList<BinSegmento>();
		log.info(lSegmentosA.size());

		// Se itera la lista de segmentos, para ver si tiene segmentos de CLAF
		for (BinSegmento b : lSegmentos) {
			// Si tiene datos para obtener de claf, los obtenemos y agregamos a la lista
			// que regesaremos
			if (b.getIdreg_copias_titulos() > 0) {
				log.info(b.getIdreg_copias_titulos());
				log.info("Es de CLAF");
				// Pedimos datos de CLAF
				BinSegmento bs = (BinSegmento) this.obtieneSegmentoDeClaf.execute(b.getIdreg_copias_titulos()).get(0);
				// Se recuperan datos del segmento original
				bs.setIdRegTransitiva(b.getIdRegTransitiva());
				bs.setIdRegFragmentos(b.getIdRegFragmentos());
				bs.setHoraInicio(b.getHoraInicio());
				bs.setHoraFin(b.getHoraFin());
				bs.setMinutoInicio(b.getMinutoInicio());
				bs.setMinutoFin(b.getMinutoFin());
				bs.setSegundoInicio(b.getSegundoInicio());
				bs.setSegundoFin(b.getSegundoFin());
				bs.setPietajeFin(b.getPietajeFin());
				bs.setPietajeInicio(b.getPietajeInicio());
				bs.setNumRolloOrVolumenDeInicioDelSegmento(b.getNumRolloOrVolumenDeInicioDelSegmento());
				bs.setCodigoPiesOrTiempoOrMetros(b.getCodigoPiesOrTiempoOrMetros());
				bs.setSinopsis_segmento(b.getSinopsis_segmento());
				bs.setSegmentoInicio(b.getSegmentoInicio());
				bs.setSegmentoFin(b.getSegmentoFin());
				// Se añade a la lista que regresará de segmentos y se borra el objeto
				// original.
				lSegmentosConDatos.add(bs);
			}
			else {
				BinSegmento bs = (BinSegmento) this.obtieneSegmentoDeAcervo.execute(b.getIdRegAcervo()).get(0);
				// Se recuperan datos del segmento original
				log.info(b.getIdRegAcervo());
				log.info("Es de BUDA ");
				bs.setIdRegTransitiva(b.getIdRegTransitiva());
				bs.setIdRegFragmentos(b.getIdRegFragmentos());
				bs.setIdRegAcervo(b.getIdRegAcervo());
				bs.setHoraInicio(b.getHoraInicio());
				bs.setHoraFin(b.getHoraFin());
				bs.setMinutoInicio(b.getMinutoInicio());
				bs.setMinutoFin(b.getMinutoFin());
				bs.setSegundoInicio(b.getSegundoInicio());
				bs.setSegundoFin(b.getSegundoFin());
				bs.setPietajeFin(b.getPietajeFin());
				bs.setPietajeInicio(b.getPietajeInicio());
				bs.setNumRolloOrVolumenDeInicioDelSegmento(b.getNumRolloOrVolumenDeInicioDelSegmento());
				bs.setCodigoPiesOrTiempoOrMetros(b.getCodigoPiesOrTiempoOrMetros());
				bs.setSinopsis_segmento(b.getSinopsis_segmento());
				bs.setSegmentoInicio(b.getSegmentoInicio());
				bs.setSegmentoFin(b.getSegmentoFin());
				// Si no es de claf solo se agrega a la nueva lista
				lSegmentosConDatos.add(bs);
			}
		}

		binFragmentoConSegmentos.setAlSegmentos(lSegmentosConDatos);
		MinutosSegundos minSeg = this.transformaBins
				.calculaDuracionComoContribucionDeSegmentosEnConsulta(binFragmentoConSegmentos, this);
		binFragmentoConSegmentos.setDuracionMinutos((short) minSeg.minutos);
		binFragmentoConSegmentos.setDuracionSegundos((byte) minSeg.segundos);
		return binFragmentoConSegmentos;
	}

	/**
	 * Dado un transFragmentosAcervo.idRegFragmentos hace el SELECT JOIN por
	 * transFragmentosAcervo.idRegAcervo = Acervo.idReg para obtener las ubicaciones de
	 * los segmentos en transFragmentosAcervo y los atributos fisicos de los segmentos en
	 * el Acervo.
	 */
	private class LeeSegmentos extends org.springframework.jdbc.object.MappingSqlQuery {

		protected LeeSegmentos(DataSource ds) {
			/*
			 * ###########################################################################
			 * ###########################################################################
			 * ############
			 */
			super(ds, "SELECT t.idReg AS idRegTransitiva, t.idRegAcervo AS idRegAcervo,"
					+ "t.codigoPiesOrTiempoOrMetros AS codigoPiesOrTiempoOrMetros, t.numRolloOrVolumenDeInicioDelSegmento AS numRolloOrVol,"
					+ "t.segmentoInicio AS segmentoInicio, t.segmentoFin AS segmentoFin, t.contribuyeAlTiempoTotalDelFragmento AS contribuyeAlTiempoTotalDelFragmento,"
					+ " t.idRegFragmentos AS idRegFragmentos,t.idreg_copias_titulos AS idreg_copias_titulos,t.sinopsis_segmento AS sinopsis_segmento,"
					+ "t.segmentoInicio AS segmentoInicio,t.segmentoFin AS segmentoFin, t.idRegAcervo AS idRegAcervo " +

					" FROM transFragmentosAcervo t WHERE t.idRegFragmentos = ? " + "AND t.activo=1");
			/*
			 * ###########################################################################
			 * ###########################################################################
			 * ############
			 */
			declareParameter(new SqlParameter(Types.BIGINT));
			compile();
		}

		@Override
		protected BinSegmento mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinSegmento b = new BinSegmento();
			b.setIdRegTransitiva(rs.getLong(1));
			b.setIdRegAcervo(rs.getLong(2));
			b.setCodigoPiesOrTiempoOrMetros(rs.getString(3).charAt(0));
			b.setNumRolloOrVolumenDeInicioDelSegmento(rs.getShort(4));
			b.setSegmentoInicio(rs.getString(5));
			b.setSegmentoFin(rs.getString(6));
			b.setContribuyeAlTiempoTotalDelFragmento(rs.getString(7));
			b.setIdRegFragmentos(rs.getLong(8));
			b.setIdreg_copias_titulos(rs.getLong(9));
			b.setSinopsis_segmento(rs.getString("sinopsis_segmento"));
			b.setSegmentoInicio(rs.getString("segmentoInicio"));
			b.setSegmentoFin(rs.getString("segmentoFin"));
			String segmentoInicio = rs.getString("segmentoInicio");
			String segmentoFin = rs.getString("segmentoFin");
			b.setIdRegAcervo(rs.getLong(13));
			switch (b.getCodigoPiesOrTiempoOrMetros()) {
			case 'P':
				System.out.println(segmentoInicio);
				b.setPietaje(Short.parseShort(segmentoInicio), Short.parseShort(segmentoFin));
				break;
			case 'T':
				FragmentoDao.this.transformaBins.interpretaYasignaTiempo(b, segmentoInicio, segmentoFin);
				break;
			case 'M':
				// break;
			default:
				log.error("*** CODIGO DE PiesOrTiempoOrMetros AUN NO IMPLANTADO O DESCONOCIDO");
			}
			return b;
		}

	}

	private class ObtieneSegmentoDeClaf extends org.springframework.jdbc.object.MappingSqlQuery {

		protected ObtieneSegmentoDeClaf(DataSource ds) {
			super(ds, "SELECT " + "co.idreg_copias_titulos AS idReg_copias_titulos,"
					+ "ca.sinopsis AS sinopsis_segmento,"
					+ "ca.titulo_original AS TituloOriginal, ca.titulo_en_espa AS TituloEnEspaniol,"
					+ "ca.realizador AS Realizador, ca.pais AS PaisDeRealizacion, ca.anio AS AnioDeProduccion,"
					+ "co.formato AS Formato, co.soporte AS Soporte, co.emulsion AS Emulsion, co.color AS Color,"
					+ "co.audio AS Audio," + "co.origen AS Origen, co.observaciones_copia_titulo AS Observaciones,"
					+ "ca.se_puede_exhibir AS PosibilidadExhibir "
					+ "FROM cat_titulos ca, copias_titulos co WHERE co.idreg_copias_titulos = ? "
					+ "AND ca.idreg_cat_titulos  = co.idreg_cat_titulos ");
			// +"AND co.activo=1");
			declareParameter(new SqlParameter(Types.BIGINT)); // idReg_copias_titulo

			compile();
		}

		@Override
		protected BinSegmento mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinSegmento b = new BinSegmento();
			b.setIdreg_copias_titulos(rs.getLong("idReg_copias_titulos"));
			if (rs.getString("TituloOriginal") != null)
				b.setTituloOriginal(rs.getString("TituloOriginal"));
			if (rs.getString("TituloEnEspaniol") != null)
				b.setTituloEnEspaniol(rs.getString("TituloEnEspaniol"));
			if (rs.getString("Realizador") != null)
				b.setRealizador(rs.getString("Realizador"));
			if (rs.getString("PaisDeRealizacion") != null)
				b.setPaisDeRealizacion(rs.getString("PaisDeRealizacion"));
			if (rs.getString("AnioDeProduccion") != null)
				b.setAnioDeProduccion(rs.getString("AnioDeProduccion"));
			if (rs.getString("Formato") != null)
				b.setFormato(rs.getString("Formato"));
			if (rs.getString("Soporte") != null)
				b.setSoporte(rs.getString("Soporte"));
			if (rs.getString("Emulsion") != null)
				b.setEmulsion(rs.getString("Emulsion"));
			if (rs.getString("Color") != null)
				b.setColor(rs.getString("Color"));
			if (rs.getString("Audio") != null)
				b.setAudio(rs.getString("Audio"));
			if (rs.getString("PosibilidadExhibir") != null)
				b.setPosibilidadDeExhibicion(rs.getString("PosibilidadExhibir"));
			if (rs.getString("sinopsis_segmento") != null)
				b.setSinopsis_segmento(rs.getString("sinopsis_segmento"));
			if (rs.getString("Origen") != null)
				b.setOrigen(rs.getString("Origen"));
			if (rs.getString("Observaciones") != null)
				b.setObservaciones(rs.getString("Observaciones"));
			return b;
		}

	}

	private class ObtieneSegmentoDeAcervo extends org.springframework.jdbc.object.MappingSqlQuery {

		protected ObtieneSegmentoDeAcervo(DataSource ds) {
			super(ds,
					"SELECT a.colocacion AS colocacion, a.Titulo_Original AS TituloOriginal, a.Titulo_En_Espa AS TituloEnEspaniol,"
							+ "a.Realizador AS Realizador, a.Pais_De_Realizacion AS PaisDeRealizacion, a.A AS AnioDeProduccion,"
							+ "a.Formato AS Formato, a.Soporte AS Soporte, a.Emulsion AS Emulsion, a.Color AS Color,"
							+ "a.Audio AS Audio, a.Tama AS Tama, a.Metraje AS Metraje, a.Da AS Danios, "
							+ "a.Origen AS Origen, a.Observaciones AS Observaciones"
							+ " FROM acervo a WHERE a.idReg = ? ");
			declareParameter(new SqlParameter(Types.BIGINT)); // idReg_copias_titulo

			compile();
		}

		@Override
		protected BinSegmento mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinSegmento b = new BinSegmento();
			if (rs.getString("colocacion") != null)
				b.setColocacion(rs.getString("colocacion"));
			if (rs.getString("TituloOriginal") != null)
				b.setTituloOriginal(rs.getString("TituloOriginal"));
			if (rs.getString("TituloEnEspaniol") != null)
				b.setTituloEnEspaniol(rs.getString("TituloEnEspaniol"));
			if (rs.getString("Realizador") != null)
				b.setRealizador(rs.getString("Realizador"));
			if (rs.getString("PaisDeRealizacion") != null)
				b.setPaisDeRealizacion(rs.getString("PaisDeRealizacion"));
			if (rs.getString("AnioDeProduccion") != null)
				b.setAnioDeProduccion(rs.getString("AnioDeProduccion"));
			if (rs.getString("Formato") != null)
				b.setFormato(rs.getString("Formato"));
			if (rs.getString("Soporte") != null)
				b.setSoporte(rs.getString("Soporte"));
			if (rs.getString("Emulsion") != null)
				b.setEmulsion(rs.getString("Emulsion"));
			if (rs.getString("Color") != null)
				b.setColor(rs.getString("Color"));
			if (rs.getString("Audio") != null)
				b.setAudio(rs.getString("Audio"));
			if (rs.getString("Tama") != null)
				b.setTama(rs.getString("Tama"));
			if (rs.getString("Metraje") != null)
				b.setMetraje(rs.getString("Metraje"));
			if (rs.getString("Danios") != null)
				b.setDanios(rs.getString("Danios"));
			if (rs.getString("Origen") != null)
				b.setOrigen(rs.getString("Origen"));
			if (rs.getString("Observaciones") != null)
				b.setObservaciones(rs.getString("Observaciones"));
			return b;
		}

	}

	/////////////////////// NO SE USA DE MOMENTO, SIRVE?
	/////////////////////// ///////////////////////////////////////////////////////////////////

	private class LeeRegistrosFragmentos extends org.springframework.jdbc.object.MappingSqlQuery {

		protected LeeRegistrosFragmentos(DataSource ds) {
			super(ds, "SELECT idReg, tituloDelFragmento, " + "coleccion, realizador, ubicacionGeografica, "
					+ "codigoFechaOrEpoca, fechaRangoMin, fechaRangoMax, " +
					// "colocacion, " +//<-- temporal hasta dar por migrados los
					// fragmentos
					"duracionMinutos, duracionSegundos, "
					+ "descriptoresImplicitos, descriptoresExplicitos, observaciones, " + "historiaIsis" + // <--
																											// tiende
																											// a
																											// desaparecer
					// ", pietajeInicio, pietajeFin, codigoInicioFinSegmento" +//<--
					// temporal hasta dar por migrados los fragmentos
					// "FROM fragmentos"); //lfmm 14-3-2014 elimiacion virtual
					"FROM fragmentos WHERE activo=1");
		}

		@Override
		protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			BinFragmento b = new BinFragmento();
			b.setIdReg(rs.getInt("idReg"));
			b.setTituloDelFragmento(rs.getString("tituloDelFragmento"));
			b.setColeccion(rs.getString("coleccion"));
			b.setRealizador(rs.getString("realizador"));
			b.setUbicacionGeografica(rs.getString("ubicacionGeografica"));
			b.setCodigoFechaOrEpoca(rs.getString("codigoFechaOrEpoca").charAt(0));
			b.setStrFechaRangoMin(rs.getTimestamp("fechaRangoMin"));
			b.setStrFechaRangoMax(rs.getTimestamp("fechaRangoMax"));
			b.setDuracionMinutos(rs.getShort("duracionMinutos"));
			b.setDuracionSegundos(rs.getByte("duracionSegundos"));
			b.setDescriptoresImplicitos(rs.getString("descriptoresImplicitos"));
			b.setDescriptoresExplicitos(rs.getString("descriptoresExplicitos"));
			b.setObservaciones(rs.getString("observaciones"));
			b.setHistoriaIsis(rs.getString("historiaIsis"));// <-- tiende a desaparecer
			return b;
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Funcion similar a leeSegmentosDeAcervoDeUnFragmento excepto porque jala menos
	 * campos y entrega bins de otro tipo. Lee el fragmento identificado por
	 * idRegFragmento y sus segmentos para modificarlos en edicion de fragmentos.
	 * @param idRegFragmento
	 * @return instancia de BinFragmentos
	 */
	public dgac.fragmentos.utilerias.MinutosSegundos getDuracionyMinutos(long idRegFragmento) {
		BinFragmento binFragmentoConSegmentos = this.leeRegistroPorId(idRegFragmento);
		java.util.List lSegmentos = this.leeSegmentos.execute(idRegFragmento);
		binFragmentoConSegmentos.setAlSegmentos(new java.util.ArrayList<BinSegmento>(lSegmentos));
		dgac.fragmentos.utilerias.MinutosSegundos minSeg = this.transformaBins
				.calculaDuracionComoContribucionDeSegmentosEnConsulta(binFragmentoConSegmentos, this);
		binFragmentoConSegmentos.setDuracionMinutos((short) minSeg.minutos);
		binFragmentoConSegmentos.setDuracionSegundos((byte) minSeg.segundos);
		return minSeg;
	}
	/*
	 * public BinFragmentoSegmentos leeFragmentoConSegmentosParaEdicion(long
	 * idRegFragmento) { try { BinFragmentos binFragmentos =
	 * leeRegistroPorId(idRegFragmento); BinFragmentoSegmentos binFragmentoSegmentos =
	 * this.transformaBins.transformaBinFragmentosAlBinFragmentoSegmentos(binFragmentos);
	 * java.util.List lSegmentos = this.leeSegmentosParaEdicion.execute(idRegFragmento);
	 * binFragmentoSegmentos.setAlBinSegmento(new java.util.ArrayList
	 * <BinSegmento>(lSegmentos)); dgac.fragmentos.utilerias.MinutosSegundos
	 * getDuracionyMinutos = getDuracionyMinutos( idRegFragmento);
	 * binFragmentoSegmentos.setDuracionMinutos((short)getDuracionyMinutos.minutos);
	 * binFragmentoSegmentos.setDuracionSegundos((byte)getDuracionyMinutos.segundos);
	 * return binFragmentoSegmentos; } catch (RuntimeException e) { log.error(e); throw e;
	 * } }
	 */

	/**
	 * Dado un transFragmentosAcervo.idRegFragmentos hace el SELECT JOIN por
	 * transFragmentosAcervo.idRegAcervo = Acervo.idReg para obtener las ubicaciones de
	 * los segmentos en transFragmentosAcervo y los atributos fisicos de los segmentos en
	 * el Acervo.
	 */
	/*
	 * private class LeeSegmentosParaEdicion extends
	 * org.springframework.jdbc.object.MappingSqlQuery{
	 *
	 * protected LeeSegmentosParaEdicion(DataSource ds) { super(ds,
	 * "SELECT t.idReg AS idRegTransitiva, " +
	 * "t.codigoPiesOrTiempoOrMetros AS codigoPiesOrTiempoOrMetros, t.numRolloOrVolumenDeInicioDelSegmento AS numRolloOrVol,"
	 * +
	 * "t.segmentoInicio AS segmentoInicio, t.segmentoFin AS segmentoFin, t.contribuyeAlTiempoTotalDelFragmento AS contribuyeAlTiempoTotalDelFragmento, t.idRegFragmentos AS idRegFragmentos,"
	 * + "a.idReg AS idRegAcervo, a.colocacion AS colocacion " + //
	 * " FROM transFragmentosAcervo t, acervo a WHERE t.idRegFragmentos = ? AND t.idRegAcervo = a.idReg"
	 * ); //lfmm 14-3-2014 elimiacion virtual
	 * " FROM transFragmentosAcervo t, acervo a WHERE t.idRegFragmentos = ? AND t.idRegAcervo = a.idReg AND a.activo=1 AND t.activo=1"
	 * ); declareParameter(new SqlParameter(Types.BIGINT)); compile(); }
	 *
	 * @Override protected BinSegmento mapRow(ResultSet rs, int rowNum) throws
	 * SQLException { BinSegmento b = new BinSegmento();
	 * b.setIdRegTransFragmentosAcervo(rs.getLong(1));
	 * b.setCodigoPiesOrTiempoOrMetros(rs.getString(2));
	 * b.setNumRolloOrVolumenDeInicioDelSegmento(rs.getShort(3)); String segmentoInicio =
	 * rs.getString(4); String segmentoFin = rs.getString(5);
	 * switch(b.getCodigoPiesOrTiempoOrMetros().charAt(0)) { case 'P':
	 * b.setPietaje(Short.parseShort(segmentoInicio), Short.parseShort(segmentoFin));
	 * break; case 'T':
	 * FragmentosDaoImpl.this.transformaBins.interpretaYasignaTiempo(b,segmentoInicio,
	 * segmentoFin); break; case 'M': // break; default:
	 * log.error("*** CODIGO DE PiesOrTiempoOrMetros AUN NO IMPLANTADO O DESCONOCIDO:"+b.
	 * getCodigoPiesOrTiempoOrMetros()+
	 * "*** para el idReg de transFragmentosAcervo:"+b.getIdRegTransFragmentosAcervo()); }
	 * b.setContribuyeAlTiempoTotalDelFragmento(rs.getString(6));
	 * b.setIdRegFragmentos(rs.getLong(7)); b.setIdRegAcervo(rs.getLong(8));
	 * if(rs.getString("colocacion")!= null) b.setColocacion(rs.getString("colocacion"));
	 * else b.setColocacion("desconocida"); return b; } }
	 */

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el total de registros de Fragmentos
	 * @return el numero de registros en la tabla de fragmentos
	 */
	public long leeTotalDeRegistrosFragmentos() {
		// return jdbcTemplate.queryForLong("SELECT COUNT(*) FROM fragmentos"); //lfmm
		// 14-3-2014 elimiacion virtual
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM fragmentos WHERE activo=1", Long.class);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Elimina el fragmento identificado por idReg y sus segmentos asociados en la tabla
	 * transFragmentosAcervo.
	 * @param idReg
	 * @exception si no lo logra
	 */
	public void eliminaUnFragmentoConSusSegmentos(long idRegFragmento) {
		// primero eliminamos los segmentos del fragmento
		int numAfectados = this.eliminaSegmentosDeUnFragmento.update(idRegFragmento);
		/*
		 * Como puede ser que resultante de la migracion desde ISIS no haya segmentos
		 * asociados a un fragmento, no criticamos si se eliminaron uno o mas fragmentos,
		 * solamente hacemos un log.info.
		 */
		log.info("se eleiminaron " + numAfectados
				+ " registros de segmentos en transFragmentosAcervo para el fragmento:" + idRegFragmento);

		// ahora eliminamos el fragmento
		numAfectados = this.eliminaUnFragmento.update(idRegFragmento);
		if (numAfectados != 1) {
			throw new RuntimeException("Imposible eliminar registro:" + idRegFragmento);
		}
	}

	private class EliminaUnFragmento extends SqlUpdate {

		protected EliminaUnFragmento(DataSource ds) {
			// super(ds, "DELETE FROM FRAGMENTOS WHERE idReg = ?"); //lfmm 14-3-2014
			// elimiacion virtual
			super(ds, "UPDATE FRAGMENTOS SET ACTIVO=0 WHERE idReg = ?");
			declareParameter(new SqlParameter(Types.BIGINT));
			compile();
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Elimina todos los segmentos de la tabla transFragmentosAcervo asociados a
	 * idRegFragmento.
	 * @param idRegFragmento NO ARROJA EXCEPCION ALGUNA PORQUE LA MIGRACION DESDE ISIS NO
	 * GARANTIZA fragmentos con al menos un segmento asociado.
	 */
	public void eliminaSegmentosDeUnFragmento(long idRegFragmento) {
		this.eliminaSegmentosDeUnFragmento.update(idRegFragmento);
	}

	private class EliminaSegmentosDeUnFragmento extends SqlUpdate {

		protected EliminaSegmentosDeUnFragmento(DataSource ds) {
			// super(ds, "DELETE FROM transFragmentosAcervo WHERE idRegFragmentos = ?");
			// //lfmm 14-3-2014 elimiacion virtual
			super(ds, "UPDATE transFragmentosAcervo SET ACTIVO=0 WHERE idRegFragmentos = ?");
			declareParameter(new SqlParameter(Types.BIGINT));
			compile();
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Hace update del fragmento en la tabla de fragmentos
	 * @param fragmentoSegmentosActualizado
	 * @exception si no es exitoso el UPDATE arroja RuntimeException con el mensaje de
	 * error.
	 */
	public void actualizaUnFragmento(BinFragmento binFragmentoSegmentosActualizado) {
		MinutosSegundos minSeg = this.transformaBins
				.calculaDuracionComoContribucionDeSegmentos(binFragmentoSegmentosActualizado, this);

		java.util.Date fchMin;
		java.util.Date fchMax;
		if (binFragmentoSegmentosActualizado.getCodigoFechaOrEpoca() == 'E') {
			fchMin = binFragmentoSegmentosActualizado.getStrFechaRangoMin();
			fchMax = binFragmentoSegmentosActualizado.getStrFechaRangoMax();
		}
		else { // note que asignamos la misma fecha a ambos: el inicio y el fin del rango
				// de tiempo
			fchMin = fchMax = binFragmentoSegmentosActualizado.getStrFechaRangoMax();
		}
		Object[] params = new Object[] { binFragmentoSegmentosActualizado.getTituloDelFragmento(),
				binFragmentoSegmentosActualizado.getColeccion(), binFragmentoSegmentosActualizado.getRealizador(),
				binFragmentoSegmentosActualizado.getUbicacionGeografica(),
				binFragmentoSegmentosActualizado.getCodigoFechaOrEpoca() == 'E' ? "" + BinFragmento.EPOCA
						: "" + BinFragmento.FECHA_PRECISA,
				fchMin, fchMax, minSeg.minutos, minSeg.segundos,
				// NOTE QUE EL USUARIO NO SE ENTERA DE SU ERROR DE ESCRIBIR UN ARTICULO
				// muy grande EN LOS SIGUIENTES TRES CAMPOB
				binFragmentoSegmentosActualizado.getDescriptoresImplicitos().substring(0,
						Math.min(binFragmentoSegmentosActualizado.getDescriptoresImplicitos().trim().length(), 8000)),
				binFragmentoSegmentosActualizado.getDescriptoresExplicitos().substring(0,
						Math.min(binFragmentoSegmentosActualizado.getDescriptoresExplicitos().trim().length(), 8000)), // cambio
																														// a
																														// 4000
																														// cuando
																														// Angel
																														// Mtz.
																														// requer�a
																														// insertar
																														// textos
																														// m�s
																														// largos
				binFragmentoSegmentosActualizado.getObservaciones().substring(0,
						Math.min(binFragmentoSegmentosActualizado.getObservaciones().trim().length(), 8000)),
				new Long(binFragmentoSegmentosActualizado.getIdReg()), };
		this.actualizaUnFragmento.update(params);
	}

	private class ActualizaUnFragmento extends SqlUpdate {

		protected ActualizaUnFragmento(DataSource ds) {
			super(ds,
					"UPDATE fragmentos SET " + " tituloDelFragmento = ?, " + " coleccion = ?, " + " realizador = ?, "
							+ " ubicacionGeografica = ?, " + " codigoFechaOrEpoca = ?, " + " fechaRangoMin = ?, "
							+ " fechaRangoMax = ?, " +
							// " colocacion = ? " + TIENDE A DESAPARECER
							" duracionMinutos = ?, " + " duracionSegundos = ?, " + " descriptoresImplicitos = ?, "
							+ " descriptoresExplicitos = ?, " + " observaciones = ? " +
							// " historiaIsis = ? " + TIENDE A DESAPARECER
							// " pietajeInicio = ? " + TIENDE A DESAPARECER
							// " pietajeFin = ? " + TIENDE A DESAPARECER
							// " codigoInicioFinSegmento = ? " + TIENDE A DESAPARECER
							" WHERE idReg = ?");
			declareParameter(new SqlParameter(Types.VARCHAR));// tituloDelFragmento
			declareParameter(new SqlParameter(Types.VARCHAR));// coleccion
			declareParameter(new SqlParameter(Types.VARCHAR));// realizador
			declareParameter(new SqlParameter(Types.VARCHAR));// ubicacionGeografica
			declareParameter(new SqlParameter(Types.VARCHAR));// codigoFechaOrEpoca
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaRangoMin
			declareParameter(new SqlParameter(Types.TIMESTAMP));// fechaRangoMax
			declareParameter(new SqlParameter(Types.SMALLINT));// duracionMinutos
			declareParameter(new SqlParameter(Types.TINYINT));// duracionSegundos
			declareParameter(new SqlParameter(Types.VARCHAR));// descriptoresImplicitos
			declareParameter(new SqlParameter(Types.CLOB));// descriptoresExplicitos////lfmm
															// TEXT
			declareParameter(new SqlParameter(Types.VARCHAR));// observaciones
			declareParameter(new SqlParameter(Types.BIGINT));// es idReg de fragmentos
			compile();
		}

	}

}
