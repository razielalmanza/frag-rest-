package dgac.fragmentos.servicios;

import java.util.List;

import dgac.fragmentos.entidades.*;
import dgac.fragmentos.servicios.ServiciosFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dgac.fragmentos.dao.*;

@Service("serviciosFrag")
public class Servicios implements ServiciosFace {

	@Autowired
	private FragmentoDao fragmentosDaoImpl;

	public long leeTotalDeRegistrosFragmentos() {
		try {
			return fragmentosDaoImpl.leeTotalDeRegistrosFragmentos();
		}
		catch (RuntimeException e) {
			// log.error(e);
			throw e;
		}
	}

}