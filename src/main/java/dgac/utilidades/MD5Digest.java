package dgac.utilidades;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5Digest implements PasswordEncoder {

	public MD5Digest() {
	}

	public static String convierteToMd5(String cadenaOriginal) throws NoSuchAlgorithmException {
		// if (args.length != 1) {
		// System.err.println("String to MD5 digest should be first and only parameter");
		// return;
		// }
		// String cadenaOriginal = args[0];
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(cadenaOriginal.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
		// System.out.println("original:" + cadenaOriginal);
		// System.out.println("digested(hex):" + sb.toString());
	}

	@Override
	public String encode(CharSequence rawPassword) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(((String) rawPassword).getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (this.encode(rawPassword).equals(encodedPassword))
			return true;
		else
			return false;
	}

}
