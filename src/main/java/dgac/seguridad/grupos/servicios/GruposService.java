package dgac.seguridad.grupos.servicios;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dgac.seguridad.entidades.BinGrupoUsuario;
import dgac.seguridad.entidades.BinUsuario;
import dgac.seguridad.grupos.dao.GruposDao;
import dgac.seguridad.grupos.dao.log.LogGrupos;
import dgac.utilidades.org.jam.ChangeInfo;
import dgac.utilidades.org.jam.Diff4J;

@Service("gruposService")
public class GruposService {

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Lee todos los grupos que existen en la DB sin leer sus privilegios.
	 * @return List<BinGrupoUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinGrupoUsuario> leeTodosLosGrupos() {
		try {
			return gruposDao.dameTodosLosGrupos();
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeTodosLosGrupos");
		}
	}

	/**
	 * Lee el nombre y los privilegios de un grupo por su idGrupo.
	 * @param idGrupo
	 * @return nombreGrupo
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public BinGrupoUsuario leeDatosDeGrupo(byte idGrupo) {
		try {
			return gruposDao.dameDatosDeGrupo(idGrupo);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeDatosDeGrupo");
		}
	}

	/**
	 * Lee el nombre de un grupo por su idGrupo.
	 * @param idGrupo
	 * @return nombreGrupo
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	// public String leeNombreDeGrupo(byte idGrupo){
	// try{
	// return gruposDao.dameNombreDeGrupo(idGrupo);
	// } catch (Exception e) {
	// throw new RuntimeException("ERROR: leeNombredeGrupo");
	// }
	// }

	/**
	 * Lee los privilegios de un grupo por su idGrupo.
	 * @param idGrupo
	 * @return privilegios
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public long leePrivilegiosDeGrupo(byte idGrupo) {
		try {
			return gruposDao.damePrivilegiosDeGrupo(idGrupo);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leePrivilegiosDeGrupo");
		}
	}

	/**
	 * Lee la lista de usuarios asociados a un grupo.
	 * @param idGrupo
	 * @return List<BinUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinUsuario> leeUsuariosDeGrupo(byte idGrupo) {
		try {
			return gruposDao.dameUsuariosDeGrupo(idGrupo);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeUsuariosDeGrupo");
		}
	}

	/**
	 * Lee la lista de usuarios NO asociados a un grupo. Util para la GUI que asociará
	 * usuarios a un grupo.
	 * @param idGrupo
	 * @return List<BinUsuario>
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public List<BinUsuario> leeUsuariosFueraDeGrupo(byte idGrupo) {
		try {
			return gruposDao.dameUsuariosFueraDeGrupo(idGrupo);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: leeUsuariosFueraDeGrupo");
		}
	}

	/**
	 * Crea un nuevo grupo con sus privilegios.
	 * @param binGrupo
	 * @param usuario
	 * @param ip
	 * @return idGrupo
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public byte creaNuevoGrupo(BinGrupoUsuario binGrupo, String usuario, String ip) {
		try {
			byte idGrupo = gruposDao.insertaNuevoGrupo(binGrupo);
			logGrupos.registraEnLogGrupos(idGrupo, usuario, "creaNuevoGrupo", "", "", ip);
			return idGrupo;
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: creaNuevoGrupo");
		}
	}

	/**
	 * Actualiza el nombre y los privilegios de un grupo existente.
	 * @param binGrupo
	 * @param usuario
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void actualizaGrupo(BinGrupoUsuario binGrupo, String usuario, String ip) {
		try {
			byte idGrupo = binGrupo.getId_grupo();
			BinGrupoUsuario regAntesActualizar = gruposDao.dameDatosDeGrupo(idGrupo);
			Diff4J beanComparer = new Diff4J();
			Collection<ChangeInfo> cambios = beanComparer.diff(regAntesActualizar, binGrupo);
			String nombreGrupoAnterior = "";
			String privilegiosAnteriores = "";
			for (ChangeInfo cambio : cambios) {
				if (cambio.getFieldName().equals("nombre_grupo"))
					nombreGrupoAnterior = cambio.getFrom().toString();
				if (cambio.getFieldName().equals("nombre_grupo"))
					privilegiosAnteriores = cambio.getFrom().toString();
			}
			gruposDao.modificaGrupo(binGrupo);
			logGrupos.registraEnLogGrupos(idGrupo, usuario, "actualizaGrupo", nombreGrupoAnterior,
					privilegiosAnteriores, ip);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: actualizaGrupo");
		}
	}

	/**
	 * Actualiza los privilegios de un grupo existente.
	 * @param binGrupo
	 * @param usuario
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void actualizaPrivilegiosDeGrupo(BinGrupoUsuario binGrupo, String usuario, String ip) {
		try {
			byte idGrupo = binGrupo.getId_grupo();
			BinGrupoUsuario regAntesActualizar = new BinGrupoUsuario();
			regAntesActualizar.setId_grupo(idGrupo);
			regAntesActualizar.setPrivilegios(gruposDao.damePrivilegiosDeGrupo(idGrupo));
			Diff4J beanComparer = new Diff4J();
			Collection<ChangeInfo> cambios = beanComparer.diff(regAntesActualizar, binGrupo);
			String privilegiosAnteriores = "";
			for (ChangeInfo cambio : cambios) {
				if (cambio.getFieldName().equals("nombre_grupo"))
					privilegiosAnteriores = cambio.getFrom().toString();
			}
			gruposDao.modificaGrupo(binGrupo);
			logGrupos.registraEnLogGrupos(idGrupo, usuario, "actualizaPrivilegiosDeGrupo", "", privilegiosAnteriores,
					ip);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: actualizaPrivilegiosDeGrupo");
		}
	}

	/**
	 * Elimina un grupo existente y desasocia sus usuarios por cascade delete (SQL)
	 * @param idGrupo
	 * @param usuario
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void eliminaGrupo(byte idGrupo, String usuario, String ip) {
		try {
			gruposDao.borraGrupo(idGrupo);
			logGrupos.registraEnLogGrupos(idGrupo, usuario, "eliminaGrupo", "", "", ip);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: eliminaGrupo");
		}
	}

	/**
	 * Asocia una lista de usuarios a un grupo existente
	 * @param listaUsuarios
	 * @param idGrupo
	 * @param usuario
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void asociaUsuariosToGrupo(List<BinUsuario> listaUsuarios, byte idGrupo, String usuario, String ip) {
		try {
			gruposDao.asociaUsuariosToGrupo(listaUsuarios, idGrupo);
			logGrupos.registraEnLogGrupos(idGrupo, usuario, "asociaUsuariosToGrupo", "", "", ip);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: asociaUsuariosToGrupo");
		}
	}

	/**
	 * Desasocia una lista de usuarios de un grupo existente
	 * @param listaUsuarios
	 * @param idGrupo
	 * @param usuario
	 * @param ip
	 * @author Luis Felipe Maciel Mercado lfmm
	 */
	public void desasociaUsuariosDeGrupo(List<BinUsuario> listaUsuarios, byte idGrupo, String usuario, String ip) {
		try {
			gruposDao.desasociaUsuariosDeGrupo(listaUsuarios, idGrupo);
			logGrupos.registraEnLogGrupos(idGrupo, usuario, "desasociaUsuariosDeGrupo", "", "", ip);
		}
		catch (Exception e) {
			throw new RuntimeException("ERROR: desasociaUsuariosDeGrupo");
		}
	}

	@Autowired
	private GruposDao gruposDao;

	@Autowired
	private LogGrupos logGrupos;

}
