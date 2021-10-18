package dgac.seguridad.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dgac.seguridad.entidades.BinGrupoUsuario;
import dgac.seguridad.entidades.BinUsuario;
import dgac.seguridad.grupos.servicios.GruposService;
import dgac.seguridad.grupos.servicios.UsuariosService;

@RestController
public class UsuariosGruposPrivilegiosController {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private UsuariosService usuariosService;

	@Autowired
	private GruposService gruposService;

	@RequestMapping(value = "/listaGrupos", method = RequestMethod.GET)
	public @ResponseBody List<BinGrupoUsuario> listaGrupos(HttpServletRequest request, HttpServletResponse response) {
		List<BinGrupoUsuario> listaGrupos = new ArrayList<BinGrupoUsuario>();
		try {
			listaGrupos = gruposService.leeTodosLosGrupos();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listaGrupos;
	}

	@RequestMapping(value = "/listaUsuarios", method = RequestMethod.GET)
	public @ResponseBody List<BinUsuario> listaUsuarios(HttpServletRequest request, HttpServletResponse response) {
		List<BinUsuario> listaUsuarios = new ArrayList<BinUsuario>();
		try {
			listaUsuarios = usuariosService.leeTodosLosUsuarios();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listaUsuarios;
	}

}
