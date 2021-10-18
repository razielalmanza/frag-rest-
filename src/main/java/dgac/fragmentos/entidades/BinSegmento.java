package dgac.fragmentos.entidades;

/**
 * Bin para describir un segmento asociado a un fragmento. Los datos esenciales del
 * segmento se encuentran en transfragmentosacervo Sin embargo ahora los detalles de cada
 * segmento pueden estar en Acervo (BUDA) o en CLAF
 *
 * @author Raziel Almanza
 * @author Luis Bernabe
 */
public class BinSegmento {

	/*
	 * identificador interno del registro en la tabla transFragmentosAcervo
	 */
	private Long idRegTransitiva;

	/*
	 * Es el identificador del segmento en Acervo
	 */
	private Long idRegAcervo;

	/*
	 * idreg_copias_titulos es el identificador del segmento en CLAF
	 */
	private Long idreg_copias_titulos;

	/*
	 * Id del fregmento al que pertenece este segmento
	 */
	private long idRegFragmentos;

	private char codigoPiesOrTiempoOrMetros;

	private short numRolloOrVolumenDeInicioDelSegmento;

	private String segmentoInicio = "";

	private String segmentoFin = "";

	/* contribuyeAlTiempoTotalDelFragmento es 'S'=Si o 'N'=NO */
	private String contribuyeAlTiempoTotalDelFragmento = "S";

	private String colocacion = "";

	private String TituloOriginal = "";

	private String TituloEnEspaniol = "";

	private String Realizador = "";

	private String PaisDeRealizacion = "";

	private String AnioDeProduccion = "";

	private String Formato = "";

	private String Soporte = "";

	private String Emulsion = "";

	private String Color = "";

	private String Audio = "";

	private String Tama = "";

	private String Metraje = "";

	private String Danios = "";

	private String EstadoFisico = "";

	private String Origen = "";

	private String OrigenDos = "";

	private String MetrajeOriginal = "";

	private String Observaciones = "";

	private String PosibilidadDeExhibicion = "";

	private String PosibilidadDePrestamo = "";

	private String sinopsis_segmento;

	public short pietajeInicio;

	public short pietajeFin;

	public byte horaInicio;

	public byte minutoInicio;

	public byte segundoInicio;

	public byte horaFin;

	public byte minutoFin;

	public byte segundoFin;

	public short getPietajeInicio() {
		return pietajeInicio;
	}

	public void setPietajeInicio(short pietajeInicio) {
		this.pietajeInicio = pietajeInicio;
	}

	public short getPietajeFin() {
		return pietajeFin;
	}

	public void setPietajeFin(short pietajeFin) {
		this.pietajeFin = pietajeFin;
	}

	public void setPietaje(short inicio, short fin) {
		this.pietajeInicio = inicio;
		this.pietajeFin = fin;
		// this.segmentoInicio = ""+inicio + " pies";
		// this.segmentoFin = ""+fin + " pies";
	}

	public void setTiempo(byte horaInicio, byte minutoInicio, byte segundoInicio, byte horaFin, byte minutoFin,
			byte segundoFin) {
		this.horaInicio = horaInicio;
		this.minutoInicio = minutoInicio;
		this.segundoInicio = segundoInicio;
		this.horaFin = horaFin;
		this.minutoFin = minutoFin;
		this.segundoFin = segundoFin;
		this.segmentoInicio = horaInicio + ":" + minutoInicio + ":" + segundoInicio;
		this.segmentoFin = horaFin + ":" + minutoFin + ":" + segundoFin;
	}

	public byte getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(byte horaInicio) {
		this.horaInicio = horaInicio;
	}

	public byte getMinutoInicio() {
		return minutoInicio;
	}

	public void setMinutoInicio(byte minutoInicio) {
		this.minutoInicio = minutoInicio;
	}

	public byte getSegundoInicio() {
		return segundoInicio;
	}

	public void setSegundoInicio(byte segundoInicio) {
		this.segundoInicio = segundoInicio;
	}

	public byte getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(byte horaFin) {
		this.horaFin = horaFin;
	}

	public byte getMinutoFin() {
		return minutoFin;
	}

	public void setMinutoFin(byte minutoFin) {
		this.minutoFin = minutoFin;
	}

	public byte getSegundoFin() {
		return segundoFin;
	}

	public void setSegundoFin(byte segundoFin) {
		this.segundoFin = segundoFin;
	}

	public long getIdRegFragmentos() {
		return idRegFragmentos;
	}

	public void setIdRegFragmentos(long idRegFragmentos) {
		this.idRegFragmentos = idRegFragmentos;
	}

	public Long getIdreg_copias_titulos() {
		return idreg_copias_titulos;
	}

	public void setIdreg_copias_titulos(Long idreg_copias_titulos) {
		this.idreg_copias_titulos = idreg_copias_titulos;
	}

	public String getSinopsis_segmento() {
		return sinopsis_segmento;
	}

	public void setSinopsis_segmento(String sinopsis_segmento) {
		this.sinopsis_segmento = sinopsis_segmento;
	}

	public Long getIdRegTransitiva() {
		return idRegTransitiva;
	}

	public void setIdRegTransitiva(Long idRegTransitiva) {
		this.idRegTransitiva = idRegTransitiva;
	}

	public char getCodigoPiesOrTiempoOrMetros() {
		return codigoPiesOrTiempoOrMetros;
	}

	public void setCodigoPiesOrTiempoOrMetros(char codigoPiesOrTiempoOrMetros) {
		this.codigoPiesOrTiempoOrMetros = codigoPiesOrTiempoOrMetros;
	}

	public short getNumRolloOrVolumenDeInicioDelSegmento() {
		return numRolloOrVolumenDeInicioDelSegmento;
	}

	public void setNumRolloOrVolumenDeInicioDelSegmento(short numRolloOrVolumenDeInicioDelSegmento) {
		this.numRolloOrVolumenDeInicioDelSegmento = numRolloOrVolumenDeInicioDelSegmento;
	}

	public String getSegmentoInicio() {
		return segmentoInicio;
	}

	public void setSegmentoInicio(String segmentoInicio) {
		this.segmentoInicio = segmentoInicio;
	}

	public String getSegmentoFin() {
		return segmentoFin;
	}

	public void setSegmentoFin(String segmentoFin) {
		this.segmentoFin = segmentoFin;
	}

	public String getContribuyeAlTiempoTotalDelFragmento() {
		return contribuyeAlTiempoTotalDelFragmento;
	}

	public void setContribuyeAlTiempoTotalDelFragmento(String contribuyeAlTiempoTotalDelFragmento) {
		this.contribuyeAlTiempoTotalDelFragmento = contribuyeAlTiempoTotalDelFragmento;
	}

	public Long getIdRegAcervo() {
		return idRegAcervo;
	}

	public void setIdRegAcervo(Long idRegAcervo) {
		this.idRegAcervo = idRegAcervo;
	}

	public String getColocacion() {
		return colocacion;
	}

	public void setColocacion(String colocacion) {
		this.colocacion = colocacion;
	}

	public String getTituloOriginal() {
		return TituloOriginal;
	}

	public void setTituloOriginal(String tituloOriginal) {
		TituloOriginal = tituloOriginal;
	}

	public String getTituloEnEspaniol() {
		return TituloEnEspaniol;
	}

	public void setTituloEnEspaniol(String tituloEnEspaniol) {
		TituloEnEspaniol = tituloEnEspaniol;
	}

	public String getRealizador() {
		return Realizador;
	}

	public void setRealizador(String realizador) {
		Realizador = realizador;
	}

	public String getPaisDeRealizacion() {
		return PaisDeRealizacion;
	}

	public void setPaisDeRealizacion(String paisDeRealizacion) {
		PaisDeRealizacion = paisDeRealizacion;
	}

	public String getAnioDeProduccion() {
		return AnioDeProduccion;
	}

	public void setAnioDeProduccion(String anioDeProduccion) {
		AnioDeProduccion = anioDeProduccion;
	}

	public String getFormato() {
		return Formato;
	}

	public void setFormato(String formato) {
		Formato = formato;
	}

	public String getSoporte() {
		return Soporte;
	}

	public void setSoporte(String soporte) {
		Soporte = soporte;
	}

	public String getEmulsion() {
		return Emulsion;
	}

	public void setEmulsion(String emulsion) {
		Emulsion = emulsion;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		Color = color;
	}

	public String getAudio() {
		return Audio;
	}

	public void setAudio(String audio) {
		Audio = audio;
	}

	public String getTama() {
		return Tama;
	}

	public void setTama(String tama) {
		Tama = tama;
	}

	public String getMetraje() {
		return Metraje;
	}

	public void setMetraje(String metraje) {
		Metraje = metraje;
	}

	public String getDanios() {
		return Danios;
	}

	public void setDanios(String danios) {
		Danios = danios;
	}

	public String getEstadoFisico() {
		return EstadoFisico;
	}

	public void setEstadoFisico(String estadoFisico) {
		EstadoFisico = estadoFisico;
	}

	public String getOrigen() {
		return Origen;
	}

	public void setOrigen(String origen) {
		Origen = origen;
	}

	public String getOrigenDos() {
		return OrigenDos;
	}

	public void setOrigenDos(String origenDos) {
		OrigenDos = origenDos;
	}

	public String getMetrajeOriginal() {
		return MetrajeOriginal;
	}

	public void setMetrajeOriginal(String metrajeOriginal) {
		MetrajeOriginal = metrajeOriginal;
	}

	public String getObservaciones() {
		return Observaciones;
	}

	public void setObservaciones(String observaciones) {
		Observaciones = observaciones;
	}

	public String getPosibilidadDeExhibicion() {
		return PosibilidadDeExhibicion;
	}

	public void setPosibilidadDeExhibicion(String posibilidadDeExhibicion) {
		PosibilidadDeExhibicion = posibilidadDeExhibicion;
	}

	public String getPosibilidadDePrestamo() {
		return PosibilidadDePrestamo;
	}

	public void setPosibilidadDePrestamo(String posibilidadDePrestamo) {
		PosibilidadDePrestamo = posibilidadDePrestamo;
	}
	// public long getIdRegTransFragmentosAcervo() {
	// return idRegTransFragmentosAcervo;
	// }
	// public void setIdRegTransFragmentosAcervo(long idRegTransFragmentosAcervo) {
	// this.idRegTransFragmentosAcervo = idRegTransFragmentosAcervo;
	// }

}
