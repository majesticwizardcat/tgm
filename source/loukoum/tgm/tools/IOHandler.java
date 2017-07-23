package loukoum.tgm.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IOHandler {

	public static Object loadObject(String name) {

		Object obj = null;

		try {
			FileInputStream fileIn = new FileInputStream(
					System.getProperty("user.home") + "/" + "tgm/" + name
							+ ".tgm");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			obj = in.readObject();
			in.close();
			fileIn.close();
			System.out.println("Loaded fie " + name);
		} catch (IOException i) {
			System.out.println("Could not find file " + name);
		} catch (ClassNotFoundException c) {
			System.out.println("Could not load class " + name);
		}

		return obj;

	}

	public static void saveObject(Object file, String saveName) {
		try {

			File f = new File(System.getProperty("user.home") + "/" + "tgm/" + saveName + ".tgm");
			
			if (!f.exists()) {
				f.createNewFile();
				System.out.println("Created save folder at: " + f.getAbsolutePath());
			}

			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(file);
			out.close();
			fileOut.close();
			System.out.println("Saved file " + saveName);

		} catch (IOException e) {
			System.out.println("Could not save file " + saveName);
		}	
	}

	public static void saveTxt(String file, String saveName) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					System.getProperty("user.home") + "/" + "tgm/" + saveName)));
			bw.write(file);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
