package process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import audiofinger.Utils;

public class Testx {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ArrayList<String> file = new Utils().readFile("5noise/part-r-00000");
		LinkedHashMap<Integer,Integer> sec = new LinkedHashMap<Integer,Integer>();
		for(int i=0;i<file.size();i++){
			try{
				sec.put(i, Integer.parseInt(file.get(i).split("\t")[1]));
			}
			catch(Exception e){}
		}
		
		new SortHashMap().specialSortedByValue(sec,file);	

	}

}
