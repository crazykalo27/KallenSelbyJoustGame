package mainApp;
/**
 * Class: FileReader
 * @author Team 303
 * <br>Purpose: Read input files and turn them into arrays of GameObjects
 * <br>Restrictions: input files need to be in the proper format
 * <br>For example: 
 * <pre>
 *    FileReader r = new FileReader();
 * </pre>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class: FileReader
 * @author Team 303
 * <br>Purpose: Contains methods for loading levels from text files.
 * <br>Restrictions: Must be instantiated before any methods can be called.
 * <br>For example: 
 * <pre>
 *    FileReader fileReader = new FileReader();
 * </pre>
 */
public class FileReader {
	private static final String AIR_STRING = ".";
	private static final String PLATFORM_STRING = "o";
	private static final String HERO_STRING = "h";
	private static final String BADDIE_STRING = "b";
	private static final String ENEMY_STRING = "e";
	private static final String TRACKER_STRING = "t";
	private static final String LAVA_STRING = "l";
	private static final String ICE_STRING = "i";
	private static final String SLIME_STRING = "s";
	private static final String COOL_STRING = "c";
	private static final int COORDINATE_SCALE = 50;
	
	private static final String RANDOM_ENEMY_FILE = "BlueKoopa";
	private static final String TRACKER_ENEMY_FILE = "WaddleD";
	private static final String HERO_FILE = "DigDug";
	private static final String LEFT_RIGHT_ENEMY_FILE = "PacMan";

	
	private ArrayList<Enemy> bad = new ArrayList<Enemy>();
	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<GameObject> player = new ArrayList<GameObject>();
	FileReader() {

	}
	
	// think ab JFileChooser for promting for a fiel
	// Print writer to write to a file

	/*
	public static void main(String[] args) {
		FileReader bruh = new FileReader();

		ArrayList<ArrayList<String>> yes = bruh.readFile("firstfile");
		bruh.printLists(yes);

		ArrayList<GameObject> yes1 = null;

		try {
			yes1 = bruh.convertToObjects(yes);
		} catch (InvalidLevelFormatException e) {
			e.printStackTrace();
		}

		//Not sure if we need this?
		try {
			yes = bruh.convertToStrings(yes1);
		} catch (InvalidLevelFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println("--------------------------");
		bruh.printLists(yes);
	}
	*/
	
	public ArrayList<GameObject> getObjectsFromFile(String filename) {
		ArrayList<ArrayList<String>> levelStringArray = this.readFile(filename);
		ArrayList<GameObject> objects;
		try {
			objects = this.convertStringsToObjects(levelStringArray);
			return objects;
		} catch (InvalidLevelFormatException e) {
			e.printStackTrace();
			return new ArrayList<GameObject>();
		}
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
			e.printStackTrace();
			return new ArrayList<ArrayList<String>>();
		}
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

	/*
	// converts 2d array of strings to 2d array of gameobjects corresponding to the
	// strings
	public ArrayList<ArrayList<GameObject>> convertToObjects(ArrayList<ArrayList<String>> change)
			throws InvalidLevelFormatException {
		ArrayList<ArrayList<GameObject>> ans = new ArrayList<ArrayList<GameObject>>();

		for(int i = 0; i < change.size(); i++) {
			ArrayList<GameObject> line = new ArrayList<GameObject>();
			for(int j = 0; j < change.get(i).size(); j++) {
				if (change.get(i).get(j).equals(FileReader.AIR_STRING)) {
					line.add(null);
				} else if (change.get(i).get(j).equals(FileReader.PLATFORM_STRING)) {
					line.add(new Platform(i, j));
				} else if (change.get(i).get(j).equals(FileReader.HERO_STRING)) {
					line.add(new Hero(i, j, 0, 0));
				} else {
					throw new InvalidLevelFormatException("Text file to load a level is not in the proper format");
				}
			}
			ans.add(line);
		}


		return ans;
	}
	*/
	
	//TODO: Stop bottom and right side of level getting cut off
	public ArrayList<GameObject> convertStringsToObjects(ArrayList<ArrayList<String>> change)
			throws InvalidLevelFormatException {
		ArrayList<GameObject> ans = new ArrayList<GameObject>();
		platforms.clear();
		bad.clear();
		player.clear();
		int loc = -1;
		int locb = -1;
		int heroLoc = -1;
		for(int i = 0; i < change.size(); i++) {
			for(int j = 0; j < change.get(i).size(); j++) {
				int y = i * COORDINATE_SCALE + COORDINATE_SCALE/2;
				int x = j * COORDINATE_SCALE + COORDINATE_SCALE/2;
				if (change.get(i).get(j).equals(FileReader.AIR_STRING)) {
					continue;
				} else if (change.get(i).get(j).equals(FileReader.PLATFORM_STRING)) {
					Platform temp = new Platform(x,y,0);
					ans.add(temp);
					platforms.add(temp);
					
				} else if (change.get(i).get(j).equals(FileReader.HERO_STRING)) {
					Hero temp = new Hero(x,y,1,FileReader.HERO_FILE);
					ans.add(temp);
					heroLoc = ans.indexOf(temp);
					player.add(temp);
				}else if (change.get(i).get(j).equals(FileReader.BADDIE_STRING)) {
					LeftRightEnemy temp = new LeftRightEnemy(x,y,4,FileReader.LEFT_RIGHT_ENEMY_FILE);
					ans.add(temp);
					bad.add(temp);
				}
				else if (change.get(i).get(j).equals(FileReader.ENEMY_STRING)) {
					RandomMoveEnemy temp = new RandomMoveEnemy(x,y,2,FileReader.RANDOM_ENEMY_FILE);
					ans.add(temp);
					bad.add(temp);
				}else if (change.get(i).get(j).equals(FileReader.TRACKER_STRING)) {
					RandomMoveEnemy placeHolder = new RandomMoveEnemy(x,y,2,FileReader.RANDOM_ENEMY_FILE);
					ans.add(placeHolder);
					bad.add(placeHolder);
					loc = ans.indexOf(placeHolder);
					locb = bad.indexOf(placeHolder);
					
				}else if (change.get(i).get(j).equals(FileReader.LAVA_STRING)) {
					Platform temp = new Platform(x,y,1);
					ans.add(temp);
					platforms.add(temp);
					
				}else if (change.get(i).get(j).equals(FileReader.ICE_STRING)) {
					Platform temp = new Platform(x,y,2);
					ans.add(temp);
					platforms.add(temp);
				}else if (change.get(i).get(j).equals(FileReader.SLIME_STRING)) {
					Platform temp = new Platform(x,y,3);
					ans.add(temp);
					platforms.add(temp);
				}else if (change.get(i).get(j).equals(FileReader.COOL_STRING)) {
					Platform temp = new Platform(x,y,4);
					ans.add(temp);
					platforms.add(temp);
				}
					else {
					
					throw new InvalidLevelFormatException("Text file to load a level is not in the proper format");
				}
			}
		}
		if(heroLoc != -1 && loc != -1) {
			RandomMoveEnemy placeHolder = (RandomMoveEnemy) ans.get(loc);
			double x = placeHolder.getXCent();
			double y = placeHolder.getYCent();
			Tracker temp = new Tracker(x,y,2.0, (Hero)ans.get(heroLoc), FileReader.TRACKER_ENEMY_FILE);
			ans.set(loc, temp);
			bad.set(locb, temp);
			
		}
		return ans;
	}

	public ArrayList<Enemy> getBad() {
		return bad;
	}

	public void setBad(ArrayList<Enemy> bad) {
		this.bad = bad;
	}

	public ArrayList<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(ArrayList<Platform> platforms) {
		this.platforms = platforms;
	}

	public ArrayList<GameObject> getPlayer() {
		return player;
	}

	public void setPlayer(ArrayList<GameObject> player) {
		this.player = player;
	}

	// converts 2d array of gameobjects to a 2d array of strings corresponding to
	// the gameobjects
	public ArrayList<ArrayList<String>> convertToStrings(ArrayList<ArrayList<GameObject>> change)
			throws InvalidLevelFormatException {
		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();

		for (ArrayList<GameObject> a : change) {
			ArrayList<String> line = new ArrayList<String>();
			for (GameObject s : a) {
				if (s == null) {
					line.add(FileReader.AIR_STRING);
				} else if (s.getClass().equals(new Hero(0, 0, 5, FileReader.HERO_FILE).getClass())) {
					line.add(FileReader.HERO_STRING);
				} else if (s.getClass().equals(new Platform(0,0,0).getClass()) && !((Platform) s).isLava()) {
					line.add(FileReader.PLATFORM_STRING);
				} else if (s.getClass().equals(new LeftRightEnemy(0,0,5, FileReader.LEFT_RIGHT_ENEMY_FILE).getClass())) {
					line.add(FileReader.BADDIE_STRING);
				}
				else if (s.getClass().equals(new RandomMoveEnemy(0,0,5, FileReader.RANDOM_ENEMY_FILE).getClass())) {
					line.add(FileReader.ENEMY_STRING);
				}
				else if (s.getClass().equals(new Tracker(0,0,5,new Hero(0,0,5, FileReader.HERO_FILE),FileReader.TRACKER_ENEMY_FILE).getClass())) {
					line.add(FileReader.TRACKER_STRING);
				}else if (s.getClass().equals(new Platform(0,0,1).getClass()) && ((Platform) s).isLava()) {
					line.add(FileReader.LAVA_STRING);
				}else if (s.getClass().equals(new Platform(0,0,2).getClass()) && ((Platform) s).isIce()) {
					line.add(FileReader.ICE_STRING);
				}else if (s.getClass().equals(new Platform(0,0,3).getClass()) && ((Platform) s).isSlime()) {
					line.add(FileReader.SLIME_STRING);
				}else if (s.getClass().equals(new Platform(0,0,4).getClass()) && ((Platform) s).isCool()) {
					line.add(FileReader.COOL_STRING);
				}
					else {
					throw new InvalidLevelFormatException("File of gameobjects is not correctly set up");

				}
			}
			ans.add(line);
		}

		return ans;
	}

}
