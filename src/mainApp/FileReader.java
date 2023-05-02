package mainApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
	// read file, return array list of gameObjects

	public static final String AIR_STRING = ".";
	public static final String PLATFORM_STRING = "o";
	public static final String HERO_STRING = "h";

	FileReader() {

	}

	public static void main(String[] args) {
		FileReader bruh = new FileReader();
		
		ArrayList<ArrayList<String>> yes = bruh.readFile("firstfile");
		bruh.printLists(yes);
		
		ArrayList<ArrayList<GameObject>> yes1 = bruh.convertToObjects(yes);
		
		yes = bruh.convertToStrings(yes1);
		
		System.out.println("--------------------------");
		bruh.printLists(yes);
	}

	// creates a 2d arrayList of every character in the file in its correct place
	public ArrayList<ArrayList<String>> readFile(String fileName) {
		ArrayList<ArrayList<String>> chars = new ArrayList<ArrayList<String>>();

		File file = new File(fileName + ".txt");
		Scanner scan;
		try {
			scan = new Scanner(file);

			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] fromLine = line.split("");

				ArrayList<String> temp = new ArrayList<>();
				for (int i = 0; i < fromLine.length; i++) {
					temp.add(fromLine[i]);
				}
				chars.add(temp);

			}

			scan.close();
			return chars;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// prints out what the readFile is reading from the given file
	public void printLists(ArrayList<ArrayList<String>> temp) {
		for (ArrayList<String> a : temp) {
			for (String s : a) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}

	public ArrayList<ArrayList<GameObject>> convertToObjects(ArrayList<ArrayList<String>> cum) {
		ArrayList<ArrayList<GameObject>> ans = new ArrayList<ArrayList<GameObject>>();

		for (ArrayList<String> a : cum) {
			ArrayList<GameObject> line = new ArrayList<GameObject>();
			for (String s : a) {
				if (s.equals(FileReader.AIR_STRING)) {
					line.add(null);
				} else if (s.equals(FileReader.PLATFORM_STRING)) {
					line.add(new Platform());
				} else if (s.equals(FileReader.HERO_STRING)) {
					line.add(new Hero(0, 0, 0, 0));
				}
			}
			ans.add(line);
		}

		return ans;
	}

	public ArrayList<ArrayList<String>> convertToStrings(ArrayList<ArrayList<GameObject>> Ahh) {
		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();

		for (ArrayList<GameObject> a : Ahh) {
			ArrayList<String> line = new ArrayList<String>();
			for (GameObject s : a) {
				if (s == null) {
					line.add(FileReader.AIR_STRING);
				} else if (s.getClass().equals(new Hero(0, 0, 0, 0).getClass())) {
					line.add(FileReader.HERO_STRING);
				} else if (s.getClass().equals(new Platform().getClass())) {
					line.add(FileReader.PLATFORM_STRING);
				}
			}
			ans.add(line);
		}
		
		return ans;
	}

}
