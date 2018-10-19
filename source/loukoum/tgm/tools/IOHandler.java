package loukoum.tgm.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IOHandler {
	private static final File MAIN_DIRECTORY = new File(System.getProperty("user.home"), "tgm");

	public static Object loadObject(String name) {
		Object obj = null;
		File f = new File(MAIN_DIRECTORY, name + ".tgm");
		try (FileInputStream fileIn = new FileInputStream(f); ObjectInputStream in = new ObjectInputStream(fileIn);) {
			obj = in.readObject();
			System.out.println("Loaded file " + name);
		} catch (IOException i) {
			System.out.println("Could not find file " + name + ". Error: " + i.getMessage());
		} catch (ClassNotFoundException c) {
			System.out.println("Could not load class " + name + ". Error: " + c.getMessage());
		}
		return obj;
	}

	public static void saveObject(Object file, String saveName) {
		File f = new File(MAIN_DIRECTORY, saveName + ".tgm");
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		try (FileOutputStream fileOut = new FileOutputStream(f);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
			out.writeObject(file);
			System.out.println("Saved file " + saveName);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not save file " + saveName);
		}
	}

	public static void saveTxt(String file, String saveName) {
		File f = new File(MAIN_DIRECTORY, saveName);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f));) {
			bw.write(file);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
