package dgac.fragmentos.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dgac.fragmentos.entidades.BinFragmento;
import dgac.fragmentos.servicios.FragmentosServicios;

@RestController
@RequestMapping("/fragmentos")
public class FragController {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private FragmentosServicios fragService;

	@RequestMapping(value = "/buscaFragmento", method = RequestMethod.POST)
	public ResponseEntity<?> buscaFragemento(HttpServletRequest request, HttpServletResponse response,
			@RequestBody BinFragmento consulta) throws Exception {
		try {
			BinFragmento binFragmentos = consulta;
			return new ResponseEntity<Collection>(fragService.leeBuscaRegistrosPorQueryConjuntivo(binFragmentos),
					HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR: buscaFragmentos", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/getById", method = RequestMethod.GET)
	public ResponseEntity<?> leeRegistroPorId(@RequestParam(required = true) long id) {
		BinFragmento resultado;
		try {
			resultado = fragService.leeRegistroPorId(id);
			return new ResponseEntity<BinFragmento>(resultado, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR: leeRegistroPorId", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/fragmentoConSegmentos", method = RequestMethod.GET)
	public ResponseEntity<?> leeRegistroPorIdConSegmentos(@RequestParam(required = true) long id) {
		BinFragmento resultado;
		try {
			resultado = fragService.leeSegmentosDeAcervoDeUnFragmento(id);
			return new ResponseEntity<BinFragmento>(resultado, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR: leeRegistroPorIdConSegmentos", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/actualizaFragmento", method = RequestMethod.PATCH)
	public ResponseEntity<?> actualizaFragemento(HttpServletRequest request, HttpServletResponse response,
			@RequestBody BinFragmento binFragmentoSegmentosActualizado) throws Exception {
		try {
			String usuario = request.getUserPrincipal().getName();
			fragService.actualizaUnFragmento(usuario, binFragmentoSegmentosActualizado);
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR actualizaFragmento", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/insertaFragmento", method = RequestMethod.PUT)
	public ResponseEntity<?> insertaFragemento(HttpServletRequest request, HttpServletResponse response,
			@RequestBody BinFragmento binFragmentoSegmentosInsertar) throws Exception {
		try {
			String usuario = request.getUserPrincipal().getName();
			fragService.insertaRegistrosEnFragmentos(usuario, binFragmentoSegmentosInsertar);
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR insertando un fragmento.", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@RequestMapping(value = "/colocacionesExistentes", method = RequestMethod.GET)
	public ResponseEntity<?> colocacionesExistentes(@RequestParam(required = true) String colocacion) throws Exception {
		try {
			return new ResponseEntity<Collection>(fragService.colocacionesExistentes(colocacion), HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR: colocacionesExistentes", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/eliminaFragmento", method = RequestMethod.DELETE)
	public ResponseEntity<?> eliminaFragmentoConSegmentos(HttpServletRequest request,
			@RequestParam(required = true) long idReg) {
		try {
			String usuario = request.getUserPrincipal().getName();
			fragService.eliminaUnFragmento(usuario, idReg);
			return new ResponseEntity<String>("Eliminado correctamente", HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR: Al eliminar", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public ResponseEntity<?> totalDeRegistrosFragmentos() {
		Long resultado;
		try {
			resultado = fragService.leeTotalDeRegistrosFragmentos();
			return new ResponseEntity<Long>(resultado, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("ERROR: leeTotalDeResgistroFragmentos", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
