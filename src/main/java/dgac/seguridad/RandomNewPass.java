package dgac.seguridad;

import java.util.Random;

public class RandomNewPass {

	private static final String AB = "safe-holder";

	private static Random rnd = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

}
