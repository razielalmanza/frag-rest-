package dgac.seguridad.entidades;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BinAreaDgac {

	@JsonIgnore
	public static final String AREA_RECEPCION_INGRESOS = "LU100A01-01 4"; // Recepcion de
																			// Ingresos
																			// CLAF");

	@JsonIgnore
	public static final String AREA_TALLER = "LU101A01-01 2"; // Taller de Rescate y
																// Restauración");

	@JsonIgnore
	public static final String AREA_VIDEOTALLER = "LU102A01-01 0"; // Video taller");

	@JsonIgnore
	public static final String AREA_LAB_QUIMICO = "LU103A01-01 8"; // Lab. Fotoquímico");

	@JsonIgnore
	public static final String AREA_LCRD = "LU104A01-01 6"; // LCRD");

	@JsonIgnore
	public static final String AREA_BOVEDAS = "LU105A01-01 3"; // Bóvedas");

	@JsonIgnore
	public static final String AREA_SUB_RESCATE_Y_RESTAURACION = "LU106A01-01 1"; // Subdirección
																					// de
																					// Rescate
																					// y
																					// Restauración");

	@JsonIgnore
	public static final String AREA_SUB_ACERVOS = "LU107A01-01 9"; // Subdirección de
																	// Acervo");

	@JsonIgnore
	public static final String AREA_UNIDAD_DE_ACCESO_INTERINSTITUCIONAL = "LU108A01-01 7"; // Acceso
																							// Interinstitucional");

	@JsonIgnore
	public static final String AREA_CONTI = "LU109A01-01 5"; // Coordinación de Nuevas
																// Tecnologías");

	@JsonIgnore
	public static final String AREA_CENTRO_DOCUMENTACION = "LU110A01-01 3"; // Centro de
																			// Documentación");

	@JsonIgnore
	public static final String AREA_CATALOGACION = "LU111A01-01 1"; // Catalogación");

	@JsonIgnore
	public static final String AREA_BANCO_DE_IMAGEN = "LU112A01-01 9"; // Banco de
																		// Imagen");

	@JsonIgnore
	public static final String AREA_PRODUCCION = "LU113A01-01 7"; // Producción");

	@JsonIgnore
	public static final String AREA_DIFUSION = "LU114A01-01 5"; // Difusión");

	@JsonIgnore
	public static final String AREA_EXHIBICION = "LU115A01-01 2"; // Exhibición");

	@JsonIgnore
	public static final String AREA_ARPPFU = "LU116A01-01 0"; // ARPPFU");

	@JsonIgnore
	public static final String AREA_EXTERNO = "LU117A01-01 8"; // Usuario Externo");

	@JsonIgnore
	public static final String AREA_SALIDA_DEFINITIVA = "LU118A01-01 6"; // UBICACION_DEPOSITANTE_SALIDA_DEFINITIVA

	@JsonIgnore
	public static final String AREA_BAJA = "LU119A01-01 4"; // UBICACION_BAJA

	@JsonIgnore
	public static final String AREA_DIRECCION = "LU124A01-01 4"; // Direccion

	@JsonIgnore
	public static final String AREA_RESTAURACION_DIGITAL = "LU125A01-01 1"; // Restauracion
																			// digital

	@JsonIgnore
	public static final String AREA_HH_UNIDAD_ADMINISTRATIVA = "LU126A01-01 9"; // Unidad
																				// administrativa

	@JsonIgnore
	public static final String AREA_DTEC_AITEM = "LU129A01-01 3"; // DTEC AITE

	@JsonIgnore
	public static final String AREA_ENLACE = "LU130A01-01 1";

	/* IDs de cada area */
	@JsonIgnore
	public static final byte ID_TODAS = -1;

	@JsonIgnore
	public static final byte ID_RECEPCION_INGRESOS = 0;

	@JsonIgnore
	public static final byte ID_TALLER = 1;

	@JsonIgnore
	public static final byte ID_VIDEOTALLER = 2;

	@JsonIgnore
	public static final byte ID_LAB_QUIMICO = 3;

	@JsonIgnore
	public static final byte ID_LCRD = 4;

	@JsonIgnore
	public static final byte ID_BOVEDAS = 5;

	@JsonIgnore
	public static final byte ID_SUB_RESCATE_Y_RESTAURACION = 6;

	@JsonIgnore
	public static final byte ID_SUB_ACERVOS = 7;

	@JsonIgnore
	public static final byte ID_UNIDAD_DE_ACCESO_INTERINSTITUCIONAL = 8;

	@JsonIgnore
	public static final byte ID_CONTI = 9;

	@JsonIgnore
	public static final byte ID_CENTRO_DOCUMENTACION = 10;

	@JsonIgnore
	public static final byte ID_CATALOGACION = 11;

	@JsonIgnore
	public static final byte ID_BANCO_DE_IMAGEN = 12;

	@JsonIgnore
	public static final byte ID_PRODUCCION = 13;

	@JsonIgnore
	public static final byte ID_DIFUSION = 14;

	@JsonIgnore
	public static final byte ID_EXHIBICION = 15;

	@JsonIgnore
	public static final byte ID_ARPPFU = 16;

	@JsonIgnore
	public static final byte ID_EXTERNO = 17;

	@JsonIgnore
	public static final byte ID_SALIDA_DEFINITIVA = 18;

	@JsonIgnore
	public static final byte ID_BAJA = 19;

	@JsonIgnore
	public static final byte ID_DIRECCION = 24;

	@JsonIgnore
	public static final byte ID_RESTAURACION_DIGITAL = 25;

	@JsonIgnore
	public static final byte ID_HH_UNIDAD_ADMINISTRATIVA = 26;

	@JsonIgnore
	public static final byte ID_DTEC_AITEM = 29;

	/* catálogo de areas dgac */
	@JsonIgnore
	public static Map<String, String> getMapAreasDgac() {
		Map<String, String> tipos = new HashMap<String, String>();
		// tipos.put(AREA_RECEPCION_INGRESOS,"Recepcion de Ingresos CLAF");
		tipos.put(AREA_TALLER, "Taller de Rescate y Restauración");
		tipos.put(AREA_VIDEOTALLER, "Video taller");
		tipos.put(AREA_LAB_QUIMICO, "Lab. Fotoquímico");
		tipos.put(AREA_LCRD, "LCRD");
		tipos.put(AREA_BOVEDAS, "Bóvedas");
		tipos.put(AREA_SUB_RESCATE_Y_RESTAURACION, "Subdirección de Rescate y Restauración");
		tipos.put(AREA_SUB_ACERVOS, "Subdirección de Acervo");
		tipos.put(AREA_UNIDAD_DE_ACCESO_INTERINSTITUCIONAL, "Acceso Interinstitucional");
		tipos.put(AREA_CONTI, "Coordinación de Nuevas Tecnologías");
		tipos.put(AREA_CENTRO_DOCUMENTACION, "Centro de Documentación");
		tipos.put(AREA_CATALOGACION, "Catalogación");
		tipos.put(AREA_BANCO_DE_IMAGEN, "Banco de Imagen");
		tipos.put(AREA_PRODUCCION, "Producción");
		tipos.put(AREA_DIFUSION, "Difusión");
		tipos.put(AREA_EXHIBICION, "Exhibición");
		tipos.put(AREA_ARPPFU, "ARPPFU");
		tipos.put(AREA_EXTERNO, "Usuario Externo");
		tipos.put(AREA_SALIDA_DEFINITIVA, "Salida definitiva");
		tipos.put(AREA_BAJA, "Baja");
		tipos.put(AREA_DIRECCION, "Dirección");
		tipos.put(AREA_RESTAURACION_DIGITAL, "Restauracion digital");
		tipos.put(AREA_HH_UNIDAD_ADMINISTRATIVA, "Unidad Administrativa");
		tipos.put(AREA_DTEC_AITEM, "AITEM");
		return tipos;
	} // getMapAreasDgac

	/* catalogo de ids de area y su cb */
	@JsonIgnore
	public static Map<Byte, String> getMapIdAreasDgac() {
		Map<Byte, String> tipos = new HashMap<Byte, String>();
		// tipos.put(ID_RECEPCION_INGRESOS,AREA_RECEPCION_INGRESOS);
		tipos.put(ID_TALLER, AREA_TALLER);
		tipos.put(ID_VIDEOTALLER, AREA_VIDEOTALLER);
		tipos.put(ID_LAB_QUIMICO, AREA_LAB_QUIMICO);
		tipos.put(ID_LCRD, AREA_LCRD);
		tipos.put(ID_BOVEDAS, AREA_BOVEDAS);
		tipos.put(ID_SUB_RESCATE_Y_RESTAURACION, AREA_SUB_RESCATE_Y_RESTAURACION);
		tipos.put(ID_SUB_ACERVOS, AREA_SUB_ACERVOS);
		tipos.put(ID_UNIDAD_DE_ACCESO_INTERINSTITUCIONAL, AREA_UNIDAD_DE_ACCESO_INTERINSTITUCIONAL);
		tipos.put(ID_CONTI, AREA_CONTI);
		tipos.put(ID_CENTRO_DOCUMENTACION, AREA_CENTRO_DOCUMENTACION);
		tipos.put(ID_CATALOGACION, AREA_CATALOGACION);
		tipos.put(ID_BANCO_DE_IMAGEN, AREA_BANCO_DE_IMAGEN);
		tipos.put(ID_PRODUCCION, AREA_PRODUCCION);
		tipos.put(ID_DIFUSION, AREA_DIFUSION);
		tipos.put(ID_EXHIBICION, AREA_EXHIBICION);
		tipos.put(ID_ARPPFU, AREA_ARPPFU);
		tipos.put(ID_EXTERNO, AREA_EXTERNO);
		tipos.put(ID_SALIDA_DEFINITIVA, AREA_SALIDA_DEFINITIVA);
		tipos.put(ID_BAJA, AREA_BAJA);

		tipos.put(ID_DIRECCION, AREA_DIRECCION);
		tipos.put(ID_RESTAURACION_DIGITAL, AREA_RESTAURACION_DIGITAL);
		tipos.put(ID_HH_UNIDAD_ADMINISTRATIVA, AREA_HH_UNIDAD_ADMINISTRATIVA);
		tipos.put(ID_DTEC_AITEM, AREA_DTEC_AITEM);
		return tipos;
	} // getMapAreasDgac

	/* CONSTRUCTOR */
	public BinAreaDgac() {
	}

	private byte id_area_dgac = -1; // en la DB es null

	private String nombre_area = "";

	private String cb_ubicacion = "";

	public byte getId_area_dgac() {
		return id_area_dgac;
	}

	public void setId_area_dgac(byte id_area_dgac) {
		this.id_area_dgac = id_area_dgac;
	}

	public String getNombre_area() {
		return nombre_area;
	}

	public void setNombre_area(String nombre_area) {
		this.nombre_area = nombre_area;
	}

	public String getCb_ubicacion() {
		return cb_ubicacion;
	}

	public void setCb_ubicacion(String cb_ubicacion) {
		this.cb_ubicacion = cb_ubicacion;
	}

}
