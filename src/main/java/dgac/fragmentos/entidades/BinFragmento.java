package dgac.fragmentos.entidades;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BinFragmento {

	// SIMBOLIZA CODIGO (de codigoInicioFinSegmento o de codigoFechaOrEpoca) INDEFINIDO
	@JsonIgnore
	public static final char INDEFINIDO = 'I';

	// DOS CONSTANTES SON LOS POSIBLES VALORES DEL CAMPO codigoInicioFinSegmento
	@JsonIgnore
	public static final char PIETAJE = 'P';

	@JsonIgnore
	public static final char TIEMPO = 'T';

	// LDOS CONSTANTES SON LOS POSIBLES VALORES DEL CAMPOT codigoFechaOrEpoca
	@JsonIgnore
	public static final char EPOCA = 'E';

	@JsonIgnore
	public static final char FECHA_PRECISA = 'F';

	private long idReg;

	private String tituloDelFragmento;

	private String coleccion;

	private String realizador;

	private String ubicacionGeografica;

	// "codigoFechaOrEpoca" es 'F'== fecha precisa <=> fechaRangoMin==fechaRangoMax.
	private java.util.Date strFechaRangoMin;

	private java.util.Date strFechaRangoMax;

	private short duracionMinutos;

	private byte duracionSegundos;

	private String descriptoresImplicitos;

	private String descriptoresExplicitos;

	private String observaciones;

	// "historiaIsis" contiene datos del registro en ISIS, tiende a desaparecer.
	private String historiaIsis;

	/*
	 * "codigoFechaOrEpoca" en DB toma valores 'E' si tiene epoca, 'F' si tiene fecha
	 * precisa y 'I' cuando al migrar de DB Isis no puedo interpretarse
	 */
	private char codigoFechaOrEpoca = INDEFINIDO;

	private int numRolloOrVolumenDeInicioDelSegmento = 1;

	private short pietajeInicio;

	private short pietajeFin;

	private String tiempoInicio;

	private String tiempoFin;

	private String colocacion;

	private String cualquierCampoDeTexto = "";

	/*
	 * "codigoInicioFinSegmento" toma valores: 'P' si se usa pietaje, en pietajeInicio y
	 * pietajeFin 'T' si se usa tiempo en tiempoInicio y tiempoFin "" cuando al migrar de
	 * DB Isis no puedo interpretarse.
	 */
	private char codigoInicioFinSegmento = INDEFINIDO;

	/*
	 * "idParaNavegacion" no es parte de un registro de fragmentos, se utiliza para
	 * navegar en las busquedas.
	 */
	private String idParaNavegacion;

	/*
	 * "fechaOrEpocaFormateada" no es parte de un registro de fragmentos, se utiliza al
	 * presentar detalles de fragmentos. En fragmentosDaoImpl.leeRegistroPorId() se
	 * formatea la fecha o la epoca y se asigna al campo "fechaOrEpocaFormateada" que se
	 * muestra en presentaDetallesDeFragmentos.jsp
	 */
	private String fechaOrEpocaFormateada;

	/*
	 * alSegmentos es un ArrayList<BinSegmentosDeAcervo> que se utiliza al consultar los
	 * segmentos asociados a un fragmento. Es null al ver los resultados de una busqueda
	 * de fragmentos, pero al consultar los detalles de un fragmento al accesar el
	 * hipervinculo "ver segmentos" en la pagina detalles.html, se direcciona a la pagina
	 * segmentos.html en donde este ArrayList ya se llena con los datos que dan los
	 * segmentos de un fragmento como resultado de la consulta a las tablas
	 * transFragmentosAcervo y Acervo.
	 */
	private java.util.ArrayList<BinSegmento> alSegmentos = null;

	/*
	 * "idRegFragmento" identificador interno del fragmento, solamente se utiliza en
	 * edicion de fragmentos para el update del registro
	 */
	// private long idRegFragmento;

	/*
	 * Dos campos redundantes que marcan la fecha o epoca. Para apoyar el log de
	 * transacciones ya que las transformaciones entre bins se han quedado
	 * complicado-churriguerescas!
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es-MX",
			timezone = "America/Mexico_City")
	private java.util.Date fechaMinParaLog;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es-MX",
			timezone = "America/Mexico_City")
	private java.util.Date fechaMaxParaLog;

	public java.util.Date getFechaMinParaLog() {
		return fechaMinParaLog;
	}

	public void setFechaMinParaLog(java.util.Date fechaMinParaLog) {
		this.fechaMinParaLog = fechaMinParaLog;
	}

	public java.util.Date getFechaMaxParaLog() {
		return fechaMaxParaLog;
	}

	public void setFechaMaxParaLog(java.util.Date fechaMaxParaLog) {
		this.fechaMaxParaLog = fechaMaxParaLog;
	}

	public java.util.ArrayList<BinSegmento> getAlSegmentos() {
		return alSegmentos;
	}

	public void setAlSegmentos(java.util.ArrayList<BinSegmento> alSegmentos) {
		this.alSegmentos = alSegmentos;
	}

	public String getIdParaNavegacion() {
		return idParaNavegacion;
	}

	public void setIdParaNavegacion(String idParaNavegacion) {
		this.idParaNavegacion = idParaNavegacion;
	}

	public String getFechaOrEpocaFormateada() {
		return fechaOrEpocaFormateada;
	}

	public void setFechaOrEpocaFormateada(String fechaOrEpocaFormateada) {
		this.fechaOrEpocaFormateada = fechaOrEpocaFormateada;
	}

	public long getIdReg() {
		return idReg;
	}

	public void setIdReg(long idReg) {
		this.idReg = idReg;
	}

	public String getTituloDelFragmento() {
		return tituloDelFragmento;
	}

	public void setTituloDelFragmento(String tituloDelFragmento) {
		this.tituloDelFragmento = tituloDelFragmento;
	}

	public String getColeccion() {
		return coleccion;
	}

	public void setColeccion(String coleccion) {
		this.coleccion = coleccion;
	}

	public String getRealizador() {
		return realizador;
	}

	public void setRealizador(String realizador) {
		this.realizador = realizador;
	}

	public String getUbicacionGeografica() {
		return ubicacionGeografica;
	}

	public void setUbicacionGeografica(String ubicacionGeografica) {
		this.ubicacionGeografica = ubicacionGeografica;
	}

	public java.util.Date getStrFechaRangoMin() {
		return strFechaRangoMin;
	}

	public void setStrFechaRangoMin(java.util.Date fechaRangoMin) {
		this.strFechaRangoMin = fechaRangoMin;
	}

	public java.util.Date getStrFechaRangoMax() {
		return strFechaRangoMax;
	}

	public void setStrFechaRangoMax(java.util.Date fechaRangoMax) {
		this.strFechaRangoMax = fechaRangoMax;
	}

	public short getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(short minutosDuracion) {
		this.duracionMinutos = minutosDuracion;
	}

	public byte getDuracionSegundos() {
		return duracionSegundos;
	}

	public void setDuracionSegundos(byte segundosDuracion) {
		this.duracionSegundos = segundosDuracion;
	}

	public String getDescriptoresImplicitos() {
		return descriptoresImplicitos;
	}

	public void setDescriptoresImplicitos(String descriptoresImplicitos) {
		this.descriptoresImplicitos = descriptoresImplicitos;
	}

	public String getDescriptoresExplicitos() {
		return descriptoresExplicitos;
	}

	public void setDescriptoresExplicitos(String descriptoresExplicitos) {
		this.descriptoresExplicitos = descriptoresExplicitos;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public char getCodigoFechaOrEpoca() {
		return codigoFechaOrEpoca;
	}

	public void setCodigoFechaOrEpoca(char codigoFechaOrEpoca) {
		this.codigoFechaOrEpoca = codigoFechaOrEpoca;
	}

	public void setPietajeInicio(short pietajeInicio) {
		this.pietajeInicio = pietajeInicio;
	}

	public void setPietajeFin(short pietajeFin) {
		this.pietajeFin = pietajeFin;
	}

	public short getPietajeInicio() {
		return pietajeInicio;
	}

	public short getPietajeFin() {
		return pietajeFin;
	}

	public String getTiempoInicio() {
		return tiempoInicio;
	}

	public void setTiempoInicio(String tiempoInicio) {
		this.tiempoInicio = tiempoInicio;
	}

	public String getTiempoFin() {
		return tiempoFin;
	}

	public void setTiempoFin(String tiempoFin) {
		this.tiempoFin = tiempoFin;
	}

	public char getCodigoInicioFinSegmento() {
		return codigoInicioFinSegmento;
	}

	public void setCodigoInicioFinSegmento(char tipoDeCodificacion) {
		this.codigoInicioFinSegmento = tipoDeCodificacion;
	}

	public String getHistoriaIsis() {
		return historiaIsis;
	}

	public void setHistoriaIsis(String historiaIsis) {
		this.historiaIsis = historiaIsis;
	}

	public int getNumRolloOrVolumenDeInicioDelSegmento() {
		return numRolloOrVolumenDeInicioDelSegmento;
	}

	public void setNumRolloOrVolumenDeInicioDelSegmento(int numRolloOrVolumenDeInicioDelSegmento) {
		this.numRolloOrVolumenDeInicioDelSegmento = numRolloOrVolumenDeInicioDelSegmento;
	}

	public String getColocacion() {
		return colocacion;
	}

	public void setColocacion(String colocacion) {
		this.colocacion = colocacion;
	}

	public String getCualquierCampoDeTexto() {
		return cualquierCampoDeTexto;
	}

	public void setCualquierCampoDeTexto(String cualquierCampoDeTexto) {
		this.cualquierCampoDeTexto = cualquierCampoDeTexto;
	}

}
