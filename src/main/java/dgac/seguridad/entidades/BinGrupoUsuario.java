package dgac.seguridad.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BinGrupoUsuario {

	@JsonIgnore
	public static final byte GPO_RECEPCION_INGRESOS = 0;

	@JsonIgnore
	public static final byte GPO_TALLER = 1;

	@JsonIgnore
	public static final byte GPO_VIDEOTALLER = 2;

	@JsonIgnore
	public static final byte GPO_LAB_QUIMICO = 3;

	@JsonIgnore
	public static final byte GPO_LCRD = 4;

	@JsonIgnore
	public static final byte GPO_BOVEDAS = 5;

	@JsonIgnore
	public static final byte GPO_SUB_RESCATE_Y_RESTAURACION = 6;

	@JsonIgnore
	public static final byte GPO_SUB_ACERVOS = 7;

	@JsonIgnore
	public static final byte GPO_UNIDAD_DE_ACCESO_INTERINSTITUCIONAL = 8;

	@JsonIgnore
	public static final byte GPO_CONTI = 9;

	@JsonIgnore
	public static final byte GPO_CENTRO_DOCUMENTACION = 10;

	@JsonIgnore
	public static final byte GPO_CATALOGACION = 11;

	@JsonIgnore
	public static final byte GPO_BANCO_DE_IMAGEN = 12;

	@JsonIgnore
	public static final byte GPO_PRODUCCION = 13;

	@JsonIgnore
	public static final byte GPO_DIFUSION = 14;

	@JsonIgnore
	public static final byte GPO_EXHIBICION = 15;

	@JsonIgnore
	public static final byte GPO_ARPPFU = 16;

	@JsonIgnore
	public static final byte GPO_CONSULTA = 17;

	@JsonIgnore
	public static final byte GPO_ADMIN = 18;

	@JsonIgnore
	public static final byte GPO_ADMIN_BOVEDAS = 19;

	@JsonIgnore
	public static final byte GPO_ADMIN_CONTI = 20;

	@JsonIgnore
	public static final byte GPO_ADMIN_TALLER = 21;

	@JsonIgnore
	public static final byte GPO_ORDEN_INGRESO_CLAF = 22;// subdirectores RyR y Acervos

	@JsonIgnore
	public static final byte GPO_SUPERADMIN = 23;

	@JsonIgnore
	public static final byte GPO_DIRECCION = 24;

	@JsonIgnore
	public static final byte GPO_RESTAURACION_DIGITAL = 25;

	@JsonIgnore
	public static final byte GPO_HH_UNIDAD_ADMINISTRATIVA = 26;

	@JsonIgnore
	public static final byte GPO_ADMIN_CATALOGACION = 27;

	// @JsonIgnore public static final byte GPO_ADMIN_VIDEOTALLER = 28;
	@JsonIgnore
	public static final byte GPO_DTEC_AITEM = 29;

	@JsonIgnore
	public static final byte GPO_ENLACE = 30;

	/* CONSTRUCTOR */
	public BinGrupoUsuario() {
	}

	/* CONSTRUCTOR */
	public BinGrupoUsuario(String grupo) {
		this.nombre_grupo = grupo;
	}

	private byte id_grupo = -1; // en la DB es null

	private String nombre_grupo = "";

	private long privilegios = 0;

	public byte getId_grupo() {
		return id_grupo;
	}

	public void setId_grupo(byte id_grupo) {
		this.id_grupo = id_grupo;
	}

	public String getNombre_grupo() {
		return nombre_grupo;
	}

	public void setNombre_grupo(String nombre_grupo) {
		this.nombre_grupo = nombre_grupo;
	}

	public long getPrivilegios() {
		return privilegios;
	}

	public void setPrivilegios(long privilegios) {
		this.privilegios = privilegios;
	}

}
