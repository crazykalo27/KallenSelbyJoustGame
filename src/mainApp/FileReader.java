package mainApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
	//read file, return array list of gameObjects
	
	FileReader() {
		
	}
	
	public ArrayList<ArrayList<GameObject>> readFile(String fileName) {
		ArrayList<ArrayList<GameObject>> objs = new ArrayList<ArrayList<GameObject>>();
		File myFile = new File(fileName + ".txt");
		Scanner scan = new Scanner(myFile);
		
		while(scan.hasNext()) {
			if(scan.next() == '.') {
				
			} 
				
		}
			
		return myFile;
	}

}
