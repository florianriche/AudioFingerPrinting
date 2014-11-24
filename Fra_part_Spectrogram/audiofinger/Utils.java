package audiofinger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Paco
 *
 */
public class Utils {
	
	/**
	 * Write in a file
	 * @param filepath
	 * @param content
	 * @param overwrite (if false rewrite the whole document, if true begin to write at the end of the document)
	 */
	public void writeFile(String filepath,String content,boolean overwrite){
		try {
 
			File file = new File(filepath);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),overwrite);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			if(overwrite){
				bw.newLine();
			}	
			bw.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read a file
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public ArrayList<String> readFile(String filepath) throws IOException{
		ArrayList<String> res = new ArrayList<String>();
	    BufferedReader br = new BufferedReader(new FileReader(filepath));
	    try {
	        String line = br.readLine();
	        while (line != null) {
	           res.add(line);
	           line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    return res;
	}

}//end of class
