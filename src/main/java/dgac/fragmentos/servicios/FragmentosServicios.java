package dgac.fragmentos.servicios;

import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//OJO al futuro quedara asi:			import dgac.acervo.entidades.Registro;

import dgac.fragmentos.dao.FragmentoDao;
import dgac.fragmentos.entidades.BinFragmento;

@Service("fragService")
public class FragmentosServicios {

	private static final Log log = LogFactory.getLog(FragmentosServicios.class);

	private java.text.SimpleDateFormat fmtDateMesNum = new java.text.SimpleDateFormat("yyyy-MM-dd",
			new java.util.Locale("es", "MX"));

	@Autowired
	private FragmentoDao fragmentosDao;

	@Autowired
	private BovedasService bovedasService;

	/**
	 * Inserta un nuevo registro en la tabla de Fragmentos y un numero arbitrario de
	 * segmentos en la tabla transFragmentosAcervo (un segmento describe la ubicacion en
	 * el acervo de un trozo del fragmento). Note que la transaccion es rechazada si la
	 * colocacion en el 'acervo' de un segmento no existe.
	 * @param strUsuario nombre de quien registra
	 * @param binFragmento describe un fragmento y sus segmentos
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public void insertaRegistrosEnFragmentos(String strUsuario, BinFragmento binFragmentoSegmentos) {
		try {
			// Note que el siguiente metodo inserta un registro y luego asigna el valor
			// resultante del idRegFragmentos que es autoincrementable SQL.
			fragmentosDao.insertaRegistrosEnFragmentos(binFragmentoSegmentos);
			// Ahora inserta los segmentos asociados
			fragmentosDao.insertaArregloDeSegmentos(binFragmentoSegmentos, this.bovedasService);
			// deja rastro de la transaccion en tabla logDeNuevosFragmentos
			fragmentosDao.logueaTransaccion(strUsuario, "inserta fragmento:" + binFragmentoSegmentos.getIdReg(), null);
		}
		catch (RuntimeException e) {
			/*
			 * if(!e.getMessage().startsWith(FragmentosDaoImpl.strNoExiste)) { // si no es
			 * la excepcion que se arroja cuando una colocacion no existe en el acervo,
			 * metela a log. // nota: la excepcion que informa que no existe la colocacion
			 * en el acervo sirve para informar al usuario y permitirle corregirla
			 * log.error(e); }
			 */
			throw e;
		}
	}

	/**
	 * Servicio actualizaUnFragmento(String strUsuario, BinFragmentos actualizado) Hace un
	 * UPDATE con todos los campos del argumento BinFragmentos, excepto idReg que sirve
	 * para parametrizar el UPDATE hacia la tabla fragmentos.
	 *
	 * La implementacion debe de verificar que se afecta uno y solo un registro de la
	 * tabla, si no arroja un RuntimeException con un mensaje que indica el fallo.
	 * @param strUsuario el nombre del usuario que realiza la transaccion
	 * @param actualizado el BinFragmentos con los nuevos datos
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public void actualizaUnFragmento(String strUsuario, BinFragmento binFragmentoSegmentosActualizado) {
		try {
			// Recupera el registro original para el logger
			BinFragmento binFragmentoSegmentosOriginal = this
					.leeSegmentosDeAcervoDeUnFragmento(binFragmentoSegmentosActualizado.getIdReg());

			// Primero hazle update al registro en tabla de fragmentos.
			this.fragmentosDao.actualizaUnFragmento(binFragmentoSegmentosActualizado);
			// Ahora elimina todos los segmentos preexistentes del fragmento en la tabla
			// transFragmentosAcervo.
			this.fragmentosDao.eliminaSegmentosDeUnFragmento(binFragmentoSegmentosActualizado.getIdReg());
			// Finalmente inserta los nuevos segmentos asociados la tabla
			// transFragmentosAcervo (quizas segmentos
			// solo actualizados, o menos segmentos eliminados o nuevos segmentos).
			this.fragmentosDao.insertaArregloDeSegmentos(binFragmentoSegmentosActualizado, this.bovedasService);
			// deja rastro de la transaccion en tabla 'logOldFragmentos' y
			// 'logOldTransFragmentosAcervo'
			fragmentosDao.logueaTransaccion(strUsuario, "actualizaUnFragmento", binFragmentoSegmentosOriginal);
		}
		catch (RuntimeException e) {
			if (!e.getMessage().startsWith(FragmentoDao.strNoExiste)) {
				// si no es la excepcion que se arroja cuando una colocacion no existe en
				// el acervo, metela a log.
				// nota: la excepcion que informa que no existe la colocacion en el acervo
				// sirve para informar al usuario y permitirle corregirla
				log.error(e);
				e.printStackTrace();
			}
			throw e;
		}
	}

	/**
	 * Elimina de la tabla de fragmentos el registro identificado por idReg. y de la tabla
	 * transFragmentosAcervo todos aquellos asociados al idReg de fragmentos. La
	 * implementacion debe de verificar que se elimina uno y solo un registro de la tabla
	 * de fragmentos, si no arroja un RuntimeException con un mensaje que indica el fallo.
	 * @param strUsuario el nombre del usuario que realiza la transaccion
	 * @param idReg el BinFragmentos a eliminar
	 * @exception si no elimina uno y solo un registro de fragmentos
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public void eliminaUnFragmento(String strUsuario, long idRegFragmento) {
		try {
			// Recupera el registro original para el logger
			BinFragmento binFragmentoSegmentosOriginal = this.leeFragmentoConSegmentos(idRegFragmento);
			fragmentosDao.eliminaUnFragmentoConSusSegmentos(idRegFragmento);
			// deja rastro de la transaccion en tablas 'logOldFragmentos' y
			// 'logOldTransFragmentosAcervo'
			fragmentosDao.logueaTransaccion(strUsuario, "eliminaUnFragmento", binFragmentoSegmentosOriginal);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * Implantar Obtiene registrso que contiene los valores solicitados en los atributos
	 * del bin que no tienen el valor por omision.
	 * @param binFragmentos
	 * @return coleccion de BinFragmentos
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public java.util.Collection<BinFragmento> leeBuscaRegistrosPorQueryConjuntivo(
			dgac.fragmentos.entidades.BinFragmento binFragmentos) {
		StringBuffer strBuffQuery = null;
		try {
			/*
			 * Arma el query en terminos de valores no nulos restringido a campos
			 * presentados en la FormaDeBusquedaGenerica.jsp
			 */
			strBuffQuery = armaQueryDeBusquedaConjuntiva(binFragmentos, "fragmentos");
			// ejecuta query resultante
			return fragmentosDao.buscaRegistrosPorQueryConjuntivo(strBuffQuery.toString());
		}
		catch (RuntimeException e) {
			// log.error("para el QUERY:"+strBuffQuery.toString()+"\n",e);
			throw e;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * armaQueryDeBusquedaConjuntiva es una utileria utilizada por
	 * leeBuscaRegistrosPorQueryConjuntivo.
	 */
	private StringBuffer armaQueryDeBusquedaConjuntiva(dgac.fragmentos.entidades.BinFragmento r, String strTabla) {
		String strContenido = null;
		boolean meteConjuncion = false;
		StringBuffer strBuffQuery = new StringBuffer("SELECT * FROM " + strTabla + " WHERE ");

		if (!r.getTituloDelFragmento().trim().equals("") && !r.getTituloDelFragmento().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getTituloDelFragmento().trim());
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " tituloDelFragmento LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getColeccion().trim().equals("") && !r.getColeccion().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getColeccion().trim());
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " coleccion LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getRealizador().trim().equals("") && !r.getRealizador().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getRealizador().trim());
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " realizador LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getUbicacionGeografica().trim().equals("") && !r.getUbicacionGeografica().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getUbicacionGeografica().trim());
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " ubicacionGeografica LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		char codigoFechaOrEpoca = r.getCodigoFechaOrEpoca();
		try {
			if (codigoFechaOrEpoca == BinFragmento.EPOCA) {
				strBuffQuery.append((meteConjuncion ? " AND (NOT (" : " ( NOT (") + // la
																					// expresion
																					// es
																					// la
																					// negacion
																					// de
																					// un
																					// minimax-test
																					// para
																					// ver
																					// si
																					// son
																					// agenos
				// "(fechaRangoMin <= '"+fmtDateMesNum.format(r.getFechaRangoMin()) +"'
				// AND '"+fmtDateMesNum.format(r.getFechaRangoMin()) + "' <=
				// fechaRangoMax) OR" +
				// "(fechaRangoMin <= '"+fmtDateMesNum.format(r.getFechaRangoMax()) +"'
				// AND '"+fmtDateMesNum.format(r.getFechaRangoMax()) + "' <=
				// fechaRangoMax) OR "+
				// "(fechaRangoMin >= '"+fmtDateMesNum.format(r.getFechaRangoMin()) +"'
				// AND '"+fmtDateMesNum.format(r.getFechaRangoMax()) + "' >=
				// fechaRangoMax)" +
						"(fechaRangoMax < '" + fmtDateMesNum.format(r.getStrFechaRangoMin()) + "' OR '"
						+ fmtDateMesNum.format(r.getStrFechaRangoMax()) + "' < fechaRangoMin)" + ") )");
			}
			else if (codigoFechaOrEpoca == BinFragmento.FECHA_PRECISA) {
				strBuffQuery.append((meteConjuncion ? " AND " : "") + " fechaRangoMin = '"
						+ fmtDateMesNum.format(r.getStrFechaRangoMax()) + "'");

			}

		}
		catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!r.getDescriptoresImplicitos().trim().equals("") && !r.getDescriptoresImplicitos().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getDescriptoresImplicitos().trim());
			strBuffQuery
					.append((meteConjuncion ? " AND " : "") + " descriptoresImplicitos LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getDescriptoresExplicitos().trim().equals("") && !r.getDescriptoresExplicitos().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getDescriptoresExplicitos().trim());
			strBuffQuery
					.append((meteConjuncion ? " AND " : "") + " descriptoresExplicitos LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getObservaciones().trim().equals("") && !r.getObservaciones().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getObservaciones().trim());
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " observaciones LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getCualquierCampoDeTexto().trim().equals("") && !r.getCualquierCampoDeTexto().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getCualquierCampoDeTexto().trim());
			strBuffQuery.append((meteConjuncion ? " OR (" : "(") + " tituloDelFragmento LIKE '" + strContenido + "'"
					+ " OR coleccion LIKE '" + strContenido + "'" + " OR realizador LIKE '" + strContenido + "'"
					+ " OR ubicacionGeografica LIKE '" + strContenido + "'" + " OR descriptoresImplicitos LIKE '"
					+ strContenido + "'" + " OR descriptoresExplicitos LIKE '" + strContenido + "'" + " OR idReg = '"
					+ quitaAsteriscos(r.getCualquierCampoDeTexto()).trim() + "'" + " OR observaciones LIKE '"
					+ strContenido + "')");
			meteConjuncion = true;
		}
		strBuffQuery.append(" AND activo=1"); // lfmm 14-3-2014 elimiacion virtual

		return strBuffQuery;
	}

	private String procesaAsteriscos(String str) {
		StringBuffer stBuf = new StringBuffer();
		StringTokenizer st = new StringTokenizer(str, "*", /* returnDelims */true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.equals("*"))
				stBuf.append("%");
			else
				stBuf.append(token);
		}
		return stBuf.toString();
	}

	private String quitaAsteriscos(String consulta) {
		return consulta.replace('*', ' ');
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Dado el idReg de un fragmento, recupera sus datos y entregalos en instancia de
	 * BinFragmentos.
	 * @param idReg
	 * @return instancia de BinFragmentos con sus datos.
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public BinFragmento leeRegistroPorId(long idReg) {
		try {
			return fragmentosDao.leeRegistroPorId(idReg);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param idReg identifica un fragmento.
	 * @return entrega instancia de BinFragmentos con los datos del fragmento idReg y con
	 * los datos de los segmentos asociados al fragmento en el campo
	 * java.util.ArrayList<BinSegmentosDeAcervo> alSegmentos de BinFragmentos.
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public BinFragmento leeSegmentosDeAcervoDeUnFragmento(long idReg) {
		try {
			return fragmentosDao.leeSegmentosDeAcervoDeUnFragmento(idReg);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * Lee el fragmento identificado por idRegFragmento y sus segmentos para modificarlos
	 * en edicion de fragmentos.
	 * @param idRegFragmento
	 * @return instancia de BinFragmentos
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public BinFragmento leeFragmentoConSegmentos(long idRegFragmento) {
		try {
			// return fragmentosDao.leeSegmentosDeAcervoDeUnFragmento(idRegFragmento);
			return fragmentosDao.leeSegmentosDeAcervoDeUnFragmento(idRegFragmento);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * Obtiene el numero de registros en la tabla de fragmentos.
	 * @return
	 */
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public long leeTotalDeRegistrosFragmentos() {
		try {
			return fragmentosDao.leeTotalDeRegistrosFragmentos();
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
			rollbackFor = { Exception.class })
	public Collection colocacionesExistentes(String colocacion) {
		try {
			return fragmentosDao.colocacionesExistentesDeSegmentos(colocacion, this.bovedasService);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

}
