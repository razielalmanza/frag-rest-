package dgac.seguridad.entidades;

/**
 * Java bean para manejo de cambio y recuperacion de contraseña.
 *
 * @author Manuel Comi Xolot mcx
 *
 */
public class BinOpcionesContrasenia {

	public BinOpcionesContrasenia() {
	}

	private String username = "";

	private String actualPassword = "";

	private String newPassword = "";

	private String email = "";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getActualPassword() {
		return actualPassword;
	}

	public void setActualPassword(String actualPassword) {
		this.actualPassword = actualPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
