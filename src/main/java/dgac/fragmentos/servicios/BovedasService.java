package dgac.fragmentos.servicios;

import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dgac.acervoFragmentos.dao.BovedasDao;

import dgac.fragmentos.utileriaAcervo.Registro;
/* Todos los servicios o son transaccionales o apoyan transacciones, por lo tanto,
 * deben de arrojar excepcion si la hay de manera que pueda haber rollback.
 */

@Service("bovedasService")
public class BovedasService {

	private static final Log log = LogFactory.getLog(BovedasService.class);

	// ArrayList<Registro> testList = new ArrayList<Registro>();

	@Autowired
	private BovedasDao bovedasDaoImpl;

	public BovedasService() {
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	public long leeTotalDeRegistrosAcervo() {
		try {
			return bovedasDaoImpl.leeTotalDeRegistrosAcervo();
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * en registro, aquellos campos con valores no triviales representan los campos y sus
	 * valores con los que hay que efectuar la busqueda
	 */
	public Collection leeBuscaRegistrosPorQueryConjuntivo(Registro r) {
		try {
			/*
			 * Arma el query en terminos de valores no nulos restringido a campos
			 * presentados en la FormaDeBusquedaGenerica.jsp
			 */
			StringBuffer strBuffQuery = armaQueryDeBusquedaConjuntiva(r, BovedasDao.TABLA_ACERVO);
			// ejecuta query resultante
			return bovedasDaoImpl.buscaRegistrosPorQueryConjuntivo(strBuffQuery.toString());
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	public Collection colocacionesExistentes(String colocacion) {
		try {
			return bovedasDaoImpl.colocacionesExistentes(colocacion);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public void altaDeNuevoRegistro(String userName, Registro registro) {
		try {
			// loggerINI
			registro.setFechaHoraDeInsercion(System.currentTimeMillis());
			registro.setNombreCaptura(userName);
			registro.setNuevaColocacionBoveda("###");
			// loggerFIN

			// log.error("entrando a serviceImpl altaDeNuevoRegistro");
			/*
			 * bovedasDaoImpl.altaDeNuevoRegistro afecta su argumento
			 * registro.setIdReg(valor)
			 */
			bovedasDaoImpl.altaDeNuevoRegistro(userName, registro);

			// loggerINI
			bovedasDaoImpl.logueaTransaccion(userName, "altaDeNuevoRegistro", registro);
			// loggerFIN
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
		// log.error("regresando de serviceImpl altaDeNuevoRegistro");
	}
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Registro leeRegistroPorId(long idReg) {
		try {
			return bovedasDaoImpl.leeRegistroPorId(idReg);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean actualizaUnRegistro(String usuario, Registro modificado) {
		try {
			// loggerINI
			bovedasDaoImpl.logueaTransaccion(usuario, "actualizaUnRegistro", modificado);
			// loggerFIN
			return bovedasDaoImpl.actualizaUnRegistro(modificado);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * La baja de un registro implica eliminar cualquier referencia al idReg desde la
	 * tabla "bovedaPropone". y eliminar el registro de la tabla "Acervo".
	 *
	 * NOTESE QUE LA operacion o nombre del metodo para el log de movimientos (2o
	 * argumento de logueaTransaccion) en este caso no es "bajaDeUnRegistro" sino
	 * "b:"+motivo en donde el sufijo 'motivo' distingue la razon de la baja.
	 */
	public boolean bajaDeUnRegistro(String usuario, Registro registro, String motivo) {
		try {
			// loggerINI
			bovedasDaoImpl.logueaTransaccion(usuario, "b:" + motivo, registro);
			// loggerFIN
			return bovedasDaoImpl.bajaDeUnRegistro(usuario, registro);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	/**
	 * Actualiza los campos que controla ROLE_BOVEDA en la tabla "Acervo". ESTO ES UN
	 * DISE�O PROVISIONAL EN TANTO NO SE DESARROLLE EL MODULO PARA BOVEDAS. Trabaja sobre
	 * la tabla del acervo, aunque la manda ROLE_BOVEDA
	 */
	public boolean actualizaDataBovedaEnAcervo(String strUsuario, Registro regDeAcervo) {
		try {
			// loggerINI
			bovedasDaoImpl.logueaTransaccion(strUsuario, "actualizaDataBovedaEnAcervo", regDeAcervo);
			// loggerFIN
			return bovedasDaoImpl.actualizaUnRegistro(regDeAcervo);
		}
		catch (RuntimeException e) {
			log.error(e);
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * armaQueryDeBusquedaConjuntiva es una utileria utilizada por
	 * leeBuscaRegistrosPorQueryConjuntivo y leeBuscaPropuestasPorQueryConjuntivo
	 */
	private StringBuffer armaQueryDeBusquedaConjuntiva(Registro r, String strTabla) {
		String strContenido = null;
		boolean meteConjuncion = false;
		// lfmm-12-04-2013 StringBuffer strBuffQuery = new StringBuffer("SELECT * FROM " +
		// strTabla +" WHERE ");
		StringBuffer strBuffQuery = new StringBuffer(
				"SELECT idReg, fechaHoraInsercion, nuevaColocacionBoveda, TITULO_ORIGINAL, TITULO_EN_ESPA, REALIZADOR, PAIS_DE_REALIZACION, A, FORMATO, SOPORTE, EMULSION, COLOR, AUDIO, TAMA, METRAJE, DA, NORMA, CUADRO, DURACION, ORIGEN, FECHA_DE_GRABACION, FECHA_DE_REVISION, FECHA_DE_INGRESO, FECHA_DE_CAPTURA, COLOCACION, INCOM_IMG, ST, REPARACIONES, OBSERVACIONES, NOMBRE_REVISION, NOMBRE_CAPTURA, RESTRICCIONES FROM "
						+ strTabla + " WHERE ");

		if (!r.getTituloOriginal().trim().equals("") && !r.getTituloOriginal().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getTituloOriginal().trim());
			// strBuffQuery.append("("+(meteConjuncion?" AND ":"") + "TITULO_ORIGINAL LIKE
			// '%"+strContenido+
			// "%' OR TITULO_EN_ESPA LIKE '%"+strContenido+"%')");
			strBuffQuery.append("(" + (meteConjuncion ? " AND " : "") + "TITULO_ORIGINAL LIKE '" + strContenido
					+ "' OR TITULO_EN_ESPA LIKE '" + strContenido + "')");
			meteConjuncion = true;
		}

		if (!r.getRealizador().trim().equals("") && !r.getRealizador().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getRealizador().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " REALIZADOR LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " REALIZADOR LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getPaisDeRealizacion().trim().equals("") && !r.getPaisDeRealizacion().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getPaisDeRealizacion().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " PAIS_DE_REALIZACION
			// LIKE '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " PAIS_DE_REALIZACION LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getAnioDeProduccion().trim().equals("") && !r.getAnioDeProduccion().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getAnioDeProduccion().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " A LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " A LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getFormato().trim().equals("") && !r.getFormato().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getFormato().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " FORMATO LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " FORMATO LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getSoporte().trim().equals("") && !r.getSoporte().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getSoporte().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " SOPORTE LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " SOPORTE LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getEmulsion().trim().equals("") && !r.getEmulsion().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getEmulsion().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " EMULSION LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " EMULSION LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getColor().trim().equals("") && !r.getColor().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getColor().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " COLOR LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " COLOR LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getAudio().trim().equals("") && !r.getAudio().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getAudio().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " AUDIO LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " AUDIO LIKE '" + strContenido + "'");
			meteConjuncion = true;
		}

		if (!r.getColocacion().trim().equals("") && !r.getColocacion().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getColocacion().trim());
			// strBuffQuery.append((meteConjuncion?" AND ":"") + " COLOCACION LIKE
			// '%"+strContenido+"%'");
			strBuffQuery.append((meteConjuncion ? " AND " : "") + " COLOCACION LIKE '" + strContenido + "'");
			if (!meteConjuncion) {
				// la busqueda es exclusiva por colocacion, la ordenamos
				strBuffQuery.append(" ORDER BY COLOCACION");
			}
			meteConjuncion = true;
		}

		if (!r.getLosOtrosCampos().trim().equals("") && !r.getLosOtrosCampos().trim().equals("*")) {
			strContenido = procesaAsteriscos(r.getLosOtrosCampos().trim());
			if (meteConjuncion) {
				strBuffQuery.append(" AND (");
			}
			strBuffQuery.append("TAMA LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR METRAJE LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR DA LIKE '" + strContenido + "'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR ESTADO_FISICO LIKE '"+strContenido
			// +"'");
			strBuffQuery.append(" OR NORMA LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR CUADRO LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR DURACION LIKE '" + strContenido + "'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR PANTALLA LIKE '"+strContenido
			// +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR FUENTE LIKE '"+strContenido +"'");
			strBuffQuery.append(" OR ORIGEN LIKE '" + strContenido + "'");
			// strBuffQuery.append(" OR FECHA_DE_GRABACION LIKE '"+strContenido +"'");
			strBuffQuery.append(" OR FECHA_DE_REVISION LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR FECHA_DE_INGRESO LIKE '" + strContenido + "'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR FECHA_DE_ACIDEZ LIKE
			// '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR FECHA_DE_RESULTADO LIKE
			// '"+strContenido +"'");
			// strBuffQuery.append(" OR FECHA_DE_CAPTURA LIKE '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR FECHA_DE_BAJA LIKE '"+strContenido
			// +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR ORIGEN500 LIKE '"+strContenido
			// +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR DISTRIBUCION LIKE '"+strContenido
			// +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR PROGRAMACION LIKE '"+strContenido
			// +"'");
			// strBuffQuery.append(" OR BIBLIOTECA LIKE '"+strContenido +"'");
			// lfmm-04-03-2014 strBuffQuery.append(" OR METRAJE_ORIGINAL LIKE
			// '"+strContenido +"'");
			// strBuffQuery.append(" OR INCOM_IMG LIKE '"+strContenido +"'");
			// strBuffQuery.append(" OR ST LIKE '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR OTROS LIKE '"+strContenido +"'");
			strBuffQuery.append(" OR REPARACIONES LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR OBSERVACIONES LIKE '" + strContenido + "'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR NOTAS_HISTORICAS LIKE
			// '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR PRESTAMO LIKE '"+strContenido
			// +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR INSTITUCION_PRESTAMO LIKE
			// '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR FECHA_DE_SALIDA LIKE
			// '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR FECHA_DE_RETORNO LIKE
			// '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR NO_CEDULA_RECIBO LIKE
			// '"+strContenido +"'");
			strBuffQuery.append(" OR NOMBRE_REVISION LIKE '" + strContenido + "'");
			strBuffQuery.append(" OR NOMBRE_CAPTURA LIKE '" + strContenido + "'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR POSIBILIDAD_DE_EXHIBICION LIKE
			// '"+strContenido +"'");
			// lfmm-12-04-2013 strBuffQuery.append(" OR POSIBILIDAD_DE_PRESTAMO LIKE
			// '"+strContenido +"'");
			strBuffQuery.append(" OR RESTRICCIONES LIKE '" + strContenido + "'");
			// strBuffQuery.append(" OR EXTRAS LIKE '"+strContenido +"'");
			// strBuffQuery.append(" OR STATUS_ACERVO LIKE '"+strContenido +"'");
			// strBuffQuery.append(" OR STATUS_DISTRIBUCION LIKE '"+strContenido +"'");
			// strBuffQuery.append(" OR STATUS_PROGRAMACION LIKE '"+strContenido +"'");
			// strBuffQuery.append(" OR STATUS_BIBLIOTECA LIKE '"+strContenido +"'");
			if (meteConjuncion) {
				strBuffQuery.append(")");
			}
			meteConjuncion = true;
		}

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

}
