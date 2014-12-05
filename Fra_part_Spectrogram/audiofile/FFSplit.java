package audiofile;

import java.io.File;
import java.io.IOException;

import process.Configuration;

/**
 * 
 * @author Paco
 *
 */
public class FFSplit extends Thread{

	public int frame = 0;
	public int iter = 0;
	public String file = "";
	
	/**
	 * FFSplit Constructor
	 * @param frame
	 * @param iter
	 * @param file
	 */
	public FFSplit(int frame, int iter, String file) {
		this.frame = frame;
		this.iter = iter;
		this.file = file;
	}

	/**
	 * FFsplit Runnable
	 */
	public void run(){
		SystemUtils system = new SystemUtils();
		String command = "";
		if(system.isWindows()){
			command = new Configuration().OUTPUT_FOLDER.replace("/", "")+"\\ffmpeg.exe -ss "+iter+" -t "+(iter+frame)+" -i "+file+" "+((iter+frame)/frame)+file;
		}
		else if(system.isLinux() || system.isMac()){
			command = new Configuration().OUTPUT_FOLDER+"ffmpeg -ss "+iter+" -t "+(iter+frame)+" -i "+file+" "+((iter+frame)/frame)+file;
		}
		else{
			System.out.println("Command is not available for your OS");
			return;
		}
		Process p;
		try {
			p = Runtime.getRuntime().exec(command,null,new File(new Configuration().OUTPUT_FOLDER.replace("/", "")));
			p.waitFor(); 
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} 
		
		if(new Configuration().DEBUG_MODE){
			 System.out.println(command);
		}
       
	}

}//end of class
