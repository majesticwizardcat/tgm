package loukoum.tgm.tools;

import java.util.Random;

import static loukoum.tgm.tools.IOHandler.*;

public class Tools {

	private static final Random RGEN = new Random();

	public static float random() {
		return RGEN.nextFloat();
	}

	public static float random(float max) {
		return max * random();
	}

	public static float random(float min, float max) {
		return min + random(max - min);
	}

	public static void saveObjectFile(Object obj, String filename) {
		saveObject(obj, filename);
	}

	public static Object loadObjectFile(String location) {
		return loadObject(location);
	}

}
