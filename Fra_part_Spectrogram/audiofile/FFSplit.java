package audiofile;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Paco
 *
 */
public class FFSplit extends Thread{

	public String ffmpegpath = "ffmpeg\\bin"; 
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
		String command = ffmpegpath+"\\ffmpeg.exe -ss "+iter+" -t "+(iter+frame)+" -i "+file+" "+((iter+frame)/frame)+file;
		Process p;
		try {
			p = Runtime.getRuntime().exec(command,null,new File(ffmpegpath));
			p.waitFor(); 
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} 
        System.out.println(command);
	}

}//end of class
