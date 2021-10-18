package dgac.utilidades;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dgac.fragmentos.utilerias.MinutosSegundos;
import dgac.fragmentos.dao.FragmentoDao;
import dgac.fragmentos.entidades.BinFragmento;
import dgac.fragmentos.entidades.BinSegmento;

/**
 * Se tienen diversos bins porque las formas de registro y edicion requieren de campos en
 * HTML y politicas hacia el registro en la base de datos muy diferentes. Particularmente
 * se tiene el BinAcervoFragmentos.java que se utiliza cuando desde la
 * formaDeAltaSimultanea.jsp - que se apoya en FormaDeAltaSimultaneaController - se
 * registra en el Acervo, en Fragmentos y en TransFragmentosAcervo con politicas de
 * distribucion de los datos capturados diferentes a lo que sucede cuando se emplea el
 * BinFragmentoSegmentos.java en formaAltaDeFragmento.jsp y formaEditarFragmento.jsp -
 * apoyadas en FormaDeAltaDeFragmentoController.java y EditarController.java
 * respectivamente.
 *
 * Esto ha recurrido a transformar bins en diferentes circunstancias y de aqui esta clase
 * con sus diferentes metodos.
 *
 * @author Gerardo Le Lastra
 */
public class TransformaBins {

	private static final Log log = LogFactory.getLog(TransformaBins.class);

	/*
	 * Apoyo a FragmentosDaoImpl en mapRow() de su clase interna LeeSegmentosParaEdicion
	 */
	public void interpretaYasignaTiempo(BinSegmento b, String segmentoInicio, String segmentoFin) {
		// significado de los prefijos: h-hora, m-minuto, s-segundo
		// significado de los sufijos: I-inicial, F-final
		byte hI, mI, sI, hF, mF, sF;
		int indxPrimerDosPtos = segmentoInicio.indexOf(':');
		int indxUltimoDosPtos = segmentoInicio.lastIndexOf(':');
		hI = Byte.parseByte(segmentoInicio.substring(0, indxPrimerDosPtos));
		mI = Byte.parseByte(segmentoInicio.substring(indxPrimerDosPtos + 1, indxUltimoDosPtos));
		sI = Byte.parseByte(segmentoInicio.substring(indxUltimoDosPtos + 1));

		indxPrimerDosPtos = segmentoFin.indexOf(':');
		indxUltimoDosPtos = segmentoFin.lastIndexOf(':');
		hF = Byte.parseByte(segmentoFin.substring(0, indxPrimerDosPtos));
		mF = Byte.parseByte(segmentoFin.substring(indxPrimerDosPtos + 1, indxUltimoDosPtos));
		sF = Byte.parseByte(segmentoFin.substring(indxUltimoDosPtos + 1));
		b.setTiempo(hI, mI, sI, hF, mF, sF);
	}

	public MinutosSegundos calculaDuracionDelPietaje(int pieDeInicio, int pieDeFin, String strFormato,
			String strColocacionEnCasoDeError) {
		MinutosSegundos ms = new MinutosSegundos();
		int iFormato = 0;
		try {
			iFormato = Integer.parseInt(strFormato);
			int numFotogramas = 0;
			int numeroDePies = pieDeFin - pieDeInicio;
			switch (iFormato) {
			case 35:
				numFotogramas = numeroDePies * 16;
				break;
			case 16:
				numFotogramas = numeroDePies * 40;
				break;
			default:
				log.error("formato de fragmento desconocido, ni 16 ni 35 mm");
				throw new RuntimeException("formato de fragmento desconocido, ni 16 ni 35 mm");
			}
			int segundos = numFotogramas / 24;
			int minutos = segundos / 60;
			segundos = segundos % 60;
			ms.minutos = minutos;
			ms.segundos = segundos;
		}
		catch (NumberFormatException e) {
			log.error("colocacion:" + strColocacionEnCasoDeError + " con pietaje erroneo. Se le asigna cero.");
		}
		return ms;
	}

	// private static final String[] nombresDeMeses =
	// {"enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre"};

	/*
	 * utilizada desde FragmentosDaoImpl en su metodo
	 * leeFragmentoConSegmentosParaEdicion()
	 */
	/**
	 * Se invoca desde FragmentosDaoImpl.leeSegmentosDeAcervoDeUnFragmento() al consultar
	 * el fragmento y sus segmentos. Note que
	 * @param alBinSegmento
	 * @param fragmentosDao se pasa como referencia para obtener el formato de cada
	 * colocacion en el segmento si es film y no video. NOTE que no esta resuelto como
	 * manejar una colocacion inexistente.
	 */
	public MinutosSegundos calculaDuracionComoContribucionDeSegmentosEnConsulta(BinFragmento binFragmentoConSegmentos,
			FragmentoDao fragmentosDao) {
		ArrayList<BinSegmento> alBinSegmento = binFragmentoConSegmentos.getAlSegmentos();
		int totalMinutos = 0;
		int totalSegundos = 0;
		for (Iterator<BinSegmento> iterator = alBinSegmento.iterator(); iterator.hasNext();) {
			BinSegmento binFragmento = (BinSegmento) iterator.next();
			if (binFragmento.getContribuyeAlTiempoTotalDelFragmento().charAt(0) == 'S') {
				switch (binFragmento.getCodigoPiesOrTiempoOrMetros()) {
				case BinFragmento.PIETAJE:
					String segPies = binFragmento.getSegmentoInicio();
					int piesInicio = Integer.parseInt(segPies);// .substring(0,segPies.indexOf("
																// pies")));
					segPies = binFragmento.getSegmentoFin();
					int piesFin = Integer.parseInt(segPies);// .substring(0,segPies.indexOf("
															// pies")));
					MinutosSegundos ms = this.calculaDuracionDelPietaje(piesInicio, piesFin,
							fragmentosDao.getFormato(binFragmento.getColocacion()), binFragmento.getColocacion());
					totalMinutos += ms.minutos;
					totalSegundos += ms.segundos;
					break;
				case BinFragmento.TIEMPO:
					// significado de los prefijos: h-hora, m-minuto, s-segundo
					// significado de los sufijos: I-inicial, F-final
					byte hI, mI, sI, hF, mF, sF;
					String segmentoInicio = binFragmento.getSegmentoInicio();
					String segmentoFin = binFragmento.getSegmentoFin();
					int indxPrimerDosPtos = segmentoInicio.indexOf(':');
					int indxUltimoDosPtos = segmentoInicio.lastIndexOf(':');
					hI = Byte.parseByte(segmentoInicio.substring(0, indxPrimerDosPtos));
					mI = Byte.parseByte(segmentoInicio.substring(indxPrimerDosPtos + 1, indxUltimoDosPtos));
					sI = Byte.parseByte(segmentoInicio.substring(indxUltimoDosPtos + 1));

					indxPrimerDosPtos = segmentoFin.indexOf(':');
					indxUltimoDosPtos = segmentoFin.lastIndexOf(':');
					hF = Byte.parseByte(segmentoFin.substring(0, indxPrimerDosPtos));
					mF = Byte.parseByte(segmentoFin.substring(indxPrimerDosPtos + 1, indxUltimoDosPtos));
					sF = Byte.parseByte(segmentoFin.substring(indxUltimoDosPtos + 1));
					int segundoDeInicio = ((hI * 60) + mI) * 60 + sI;
					int segundoDeFin = ((hF * 60) + mF) * 60 + sF;
					totalSegundos += segundoDeFin - segundoDeInicio;
					break;
				default:
					log.error(
							"TransformaBins.calculaDuracionComoContribucionDeSegmentosEnConsulta binFragmento.getCodigoPiesOrTiempoOrMetros desconocido:"
									+ binFragmento.getCodigoPiesOrTiempoOrMetros());
					// no arrojamos excepcion ya que hay muchos errores en la DB migrada
					// de ISIS (con codigo I = indefinido).
				}

			}
			else {
				log.error(
						"TransformaBins.calculaDuracionComoContribucionDeSegmentosEnConsulta binFragmento.getContribuyeAlTiempoTotalDelFragmento desconocido:"
								+ binFragmento.getContribuyeAlTiempoTotalDelFragmento());
			}

		}

		MinutosSegundos minSeg = new MinutosSegundos();
		minSeg.minutos = totalMinutos + (totalSegundos / 60);
		minSeg.segundos = totalSegundos % 60;
		if (minSeg.minutos > 32767) {
			log.error("fragmento con tiempo > 32767 minutos, Titulo del fragmento:"
					+ binFragmentoConSegmentos.getTituloDelFragmento());
			minSeg.minutos = 32767;
		}
		return minSeg;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Se invoca desde FragmentosDaoImpl.insertaRegistrosEnFragmentosAndSegmentos().
	 * @param BinFragmento
	 * @param fragmentosDao se pasa como referencia para obtener el formato de cada
	 * colocacion en el segmento si es film y no video. NOTE que no esta resuelto como
	 * manejar una colocacion inexistente.
	 */
	public MinutosSegundos calculaDuracionComoContribucionDeSegmentos(BinFragmento binFragmentos,
			FragmentoDao fragmentosDao) {
		ArrayList<BinSegmento> alSegmentos = binFragmentos.getAlSegmentos();
		int totalMinutos = 0;
		int totalSegundos = 0;
		for (Iterator<BinSegmento> iterator = alSegmentos.iterator(); iterator.hasNext();) {
			BinSegmento binSegmentoDeAcervo = (BinSegmento) iterator.next();
			if (binSegmentoDeAcervo.getContribuyeAlTiempoTotalDelFragmento().charAt(0) == 'S') {
				switch (binSegmentoDeAcervo.getCodigoPiesOrTiempoOrMetros()) {
				case BinFragmento.PIETAJE:
					MinutosSegundos ms = this.calculaDuracionDelPietaje(binSegmentoDeAcervo.getPietajeInicio(),
							binSegmentoDeAcervo.getPietajeFin(),
							fragmentosDao.getFormato(binSegmentoDeAcervo.getColocacion()),
							binSegmentoDeAcervo.getColocacion());
					totalMinutos += ms.minutos;
					totalSegundos += ms.segundos;
					break;
				case BinFragmento.TIEMPO:
					int segundoDeInicio = ((binSegmentoDeAcervo.getHoraInicio() * 60)
							+ binSegmentoDeAcervo.getMinutoInicio()) * 60 + binSegmentoDeAcervo.getSegundoInicio();
					int segundoDeFin = ((binSegmentoDeAcervo.getHoraFin() * 60) + binSegmentoDeAcervo.getMinutoFin())
							* 60 + binSegmentoDeAcervo.getSegundoFin();
					totalSegundos += segundoDeFin - segundoDeInicio;
					break;
				default:
					log.error(
							"TransformaBins.calculaDuracionComoContribucionDeSegmentos binFragmento.getCodigoPiesOrTiempoOrMetros desconocido:"
									+ binSegmentoDeAcervo.getCodigoPiesOrTiempoOrMetros());
					throw new RuntimeException(
							"TransformaBins.calculaDuracionComoContribucionDeSegmentos binFragmento.getCodigoPiesOrTiempoOrMetros desconocido:"
									+ binSegmentoDeAcervo.getCodigoPiesOrTiempoOrMetros());
				}

			}
			else {
				log.error(
						"TransformaBins.calculaDuracionComoContribucionDeSegmentos binFragmento.getContribuyeAlTiempoTotalDelFragmento desconocido:"
								+ binSegmentoDeAcervo.getContribuyeAlTiempoTotalDelFragmento());
			}

		}
		MinutosSegundos minSeg = new MinutosSegundos();
		minSeg.minutos = totalMinutos + (totalSegundos / 60);
		minSeg.segundos = totalSegundos % 60;
		if (minSeg.minutos > 32767) {
			log.error("fragmento con tiempo > 32767 minutos, Titulo del fragmento:"
					+ binFragmentos.getTituloDelFragmento());
			minSeg.minutos = 32767;
		}
		return minSeg;
	}

}
