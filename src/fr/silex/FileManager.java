package fr.silex;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import javax.imageio.ImageIO;

import fr.silex.exception.BadFormatException;

/**
 * Help for loading files.
 */
public class FileManager {
	/**
	 * Lis un texte dans un certain format
	 * et le transforme en un dictionnaire de valeur exploitable.
	 * 
	 * exemple de format :
	 *   name=John
	 *   age=30
	 *   country=France
	 *   
	 * @param text
	 * @return
	 * @throws BadFormatException
	 */
	public static HashMap<String, String> getFileValue(String text) throws BadFormatException {
		HashMap<String, String> values = new HashMap<>();
		String[] lines = text.split("\n");

		for (String line : lines) {
			line = line.replace("\r", "");
			if (line.isBlank() || line.startsWith("//")) {
				continue;
			}
			String[] value = line.split("=");
			// Vérification du format de la ligne
			if (value.length < 2 || value.length > 2) {
				throw new BadFormatException(line + "\nThis line has an incorrect format.");
			}
			// Vérification que la variable n'a pas déjà été declaré.
			if (values.containsKey(value[0])) {
				throw new BadFormatException(line + "\nThis line declare a variable that already exist.");
			}
			values.put(value[0], value[1]);
		}

		return values;
	}

	/**
	 * Retourne le contenu du fichier indiqué.
	 * @param fileName
	 * @return content of the file.
	 */
	public static String loadFile(String fileName) {
		try {
			Path path = Path.of(fileName);
			return Files.readString(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retourne l'image du fichier.
	 */
	public static BufferedImage loadImage(String pathStr) {
		try {
			Path path = Path.of(pathStr);
			InputStream in = Files.newInputStream(path);
			BufferedImage img = ImageIO.read(in);
			return img;
		} catch(IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("L'image '"+pathStr+"' n'a pas pu être chargée.");
		}
	}

}
