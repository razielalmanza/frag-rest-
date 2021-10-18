package dgac.seguridad.entidades;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Java bean con los datos de un usuario en el contexto de CLAF.
 *
 * @author ger
 * @author @author Luis Felipe Maciel Mercado lfmm
 *
 */
public class BinUsuario {

	/* LISTA DE PRIVILEGIOS */
	@JsonIgnore
	public static final long PRIV_SIN_PRIVILEGIOS = 0L;

	@JsonIgnore
	public static final long PRIV_CONSULTA = 1L;

	// privilegios de CLAF
	// privilegios concernientes al registro del ingreso
	@JsonIgnore
	public static final long PRIV_CREA_ORDEN_INGRESO_CLAF = 1L << 1;

	@JsonIgnore
	public static final long PRIV_IMPRIME_Y_ASOCIA_CB = 1L << 2;

	@JsonIgnore
	public static final long PRIV_FINALIZA_INGRESO_CLAF = 1L << 3;

	// privilegios concernientes a los empujes
	@JsonIgnore
	public static final long PRIV_SOLICITA_MATERIALES_FILM = 1L << 4; // todos excepto
																		// bovedas, lcrd

	// @JsonIgnore public static final long PRIV_SOLICITA_MATERIALES = 1L<<32; //todos
	// excepto bovedas, lcrd
	@JsonIgnore
	public static final long PRIV_EMPUJA_MATERIALES = 1L << 5; // todos

	@JsonIgnore
	public static final long PRIV_RECIBE_MATERIALES = 1L << 6; // firma de recibido

	@JsonIgnore
	public static final long PRIV_DEFINE_TRAZA = 1L << 7;

	@JsonIgnore
	public static final long PRIV_ALTERA_TRAZA = 1L << 8;

	// privilegios concernientes a las tareas
	@JsonIgnore
	public static final long PRIV_ADMINISTRA_TAREAS = 1L << 9; // asignar, reasignar,
																// cancelar tarea

	// privilegios concernientes a prestamos (fase III)
	@JsonIgnore
	public static final long PRIV_SOLICITA_MATERIALES_DIGITALES = 1L << 10; // todos
																			// excepto
																			// bovedas,
																			// lcrd

	// privilegios concernientes a proyectos del LCRD (fase II)
	@JsonIgnore
	public static final long PRIV_REGISTRA_SOLICITUD_SERVICIOS_CLAF = 1L << 20; // todos

	@JsonIgnore
	public static final long PRIV_ADMINISTRA_SOLICITUD_SERVICIOS_CLAF = 1L << 21; // recepcionCoNTI

	@JsonIgnore
	public static final long PRIV_EDITA_PROYECTOS_LCRD = 1L << 22; // recepcionCoNTI,
																	// admin proyectos
																	// LCRD, operadorLCRD

	@JsonIgnore
	public static final long PRIV_EDITA_MATERIALES_FUENTE = 1L << 23; // recepcionCoNTI,
																		// admin proyectos
																		// LCRD,
																		// operadorLCRD

	@JsonIgnore
	public static final long PRIV_EDITA_SERVICIOS = 1L << 24; // recepcionCoNTI, admin
																// proyectos LCRD

	@JsonIgnore
	public static final long PRIV_REALIZA_ACTIVIDADES_INTERNAS = 1L << 25; // admin
																			// proyectos
																			// LCRD,
																			// operadorLCRD

	@JsonIgnore
	public static final long PRIV_EDITA_ACTIVIDADES_INTERNAS = 1L << 26; // admin
																			// proyectos
																			// LCRD,
																			// operadorLCRD

	@JsonIgnore
	public static final long PRIV_EDITA_COPIAS_GENERADAS = 1L << 27; // admin proyectos
																		// LCRD,
																		// operadorLCRD

	@JsonIgnore
	public static final long PRIV_FINALIZA_PROYECTOS_LCRD = 1L << 28; // recepcionCoNTI,
																		// admin proyectos
																		// LCRD

	@JsonIgnore
	public static final long PRIV_ADMINISTRA_LTOS = 1L << 29; // admin proyectos LCRD

	@JsonIgnore
	public static final long PRIV_ENTREGA_PROYECTOS_LCRD = 1L << 30; // recepcionCoNTI

	@JsonIgnore
	public static final long PRIV_RESGUARDA_PROYECTOS_LCRD = 1L << 31; // admin proyectos
																		// LCRD

	@JsonIgnore
	public static final long PRIV_ADMINISTRA_GRUPOS_PRIVILEGIOS = 1L << 63; // superadmin

	@JsonIgnore
	public static final long PRIV_ADMINISTRA_PROYECTOS_LCRD = PRIV_EDITA_PROYECTOS_LCRD | PRIV_EDITA_MATERIALES_FUENTE
			| PRIV_EDITA_SERVICIOS | PRIV_REALIZA_ACTIVIDADES_INTERNAS | PRIV_EDITA_ACTIVIDADES_INTERNAS
			| PRIV_EDITA_COPIAS_GENERADAS | PRIV_FINALIZA_PROYECTOS_LCRD | PRIV_ADMINISTRA_LTOS
			| PRIV_RESGUARDA_PROYECTOS_LCRD; // admin proyectos LCRD

	// registro solicitudes servicios {tecnicamente cualquier usuario de la DGAC -->
	// priv_consulta}
	// admin proyectos {consulta, proyectos, servicios, actividades, copias,
	// material_fuente, cat_titulos, ingresos, contenedores}
	// operador_lcrd {consulta, actividades, copias, material_fuente?, cat_titulos}
	// conti {consulta}

	@JsonIgnore
	public static final long PRIV_ADMIN = Long.MAX_VALUE; // 9223372036854775807 signed

	// @JsonIgnore public static final long PRIV_ADMIN =
	// Long.parseUnsignedLong("18446744073709551615");//unsigned

	private String usuario = "";

	private String rol = "";

	private String password = "";

	private short iAbilitado = 0;

	private String nombreCompleto = "";

	private String observaciones = "";

	private String email = "";

	private String ruta_ftp = "";

	private boolean recibe_mail_ingresos_claf = false;

	private long privilegios = 0;

	private String userRole = ""; // para angular

	private List<BinAreaDgac> areasDgac = null;

	private List<BinGrupoUsuario> grupos = null;

	/* CONSTRUCTOR */
	public BinUsuario() {
	}

	/* CONSTRUCTOR */
	public BinUsuario(String username) {
		this.usuario = username;
	}

	/* CONSTRUCTOR */
	public BinUsuario(String username, String rol, short iActivo, String nombreCompleto, String observaciones,
			String strEmail, String rutaFtp, boolean recibeMailIngresosClaf) {
		this.usuario = username;
		this.rol = rol;
		this.iAbilitado = iActivo;
		this.nombreCompleto = nombreCompleto;
		this.observaciones = observaciones;
		this.email = strEmail;
		this.ruta_ftp = rutaFtp;
		this.recibe_mail_ingresos_claf = recibeMailIngresosClaf;
		// this.password = password;//no entregamos el password del usuario
	}

	public boolean tienePrivilegio(long privilegio) {
		return (privilegio & this.privilegios) != 0;
	}

	public static boolean tienePrivilegio(long privilegio, long privilegios) {
		return (privilegio & privilegios) != 0;
	}

	public boolean perteneceAlGrupo(byte idGrupo) {
		for (BinGrupoUsuario grupo : this.grupos) {
			if (grupo.getId_grupo() == idGrupo)
				return true;
		}
		return false;
	}

	public boolean perteneceAlArea(String cbArea) {
		for (BinAreaDgac a : this.areasDgac) {
			if (a.getCb_ubicacion().equals(cbArea))
				return true;
		}
		return false;
	}

	public static boolean perteneceAlArea(List<BinAreaDgac> lAreas, String cbArea) {
		for (BinAreaDgac a : lAreas) {
			if (a.getCb_ubicacion().equals(cbArea))
				return true;
		}
		return false;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public short getiAbilitado() {
		return iAbilitado;
	}

	public void setiAbilitado(short iAbilitado) {
		this.iAbilitado = iAbilitado;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getPrivilegios() {
		return privilegios;
	}

	public void setPrivilegios(long privilegios) {
		this.privilegios = privilegios;
	}

	public List<BinAreaDgac> getAreasDgac() {
		return areasDgac;
	}

	public void setAreasDgac(List<BinAreaDgac> areasDgac) {
		this.areasDgac = areasDgac;
	}

	public List<BinGrupoUsuario> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<BinGrupoUsuario> grupos) {
		this.grupos = grupos;
	}

	public String getRuta_ftp() {
		return ruta_ftp;
	}

	public void setRuta_ftp(String ruta_ftp) {
		this.ruta_ftp = ruta_ftp;
	}

	public boolean getRecibe_mail_ingresos_claf() {
		return recibe_mail_ingresos_claf;
	}

	public void setRecibe_mail_ingresos_claf(boolean recibe_mail_ingresos_claf) {
		this.recibe_mail_ingresos_claf = recibe_mail_ingresos_claf;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

}
