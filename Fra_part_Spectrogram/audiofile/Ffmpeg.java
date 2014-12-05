package audiofile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import process.Configuration;

import audiofinger.Utils;

/**
 * 
 * @author Paco
 *
 */
public class Ffmpeg{
		
	/**
	 * Convert mp3 into multiple wave chuncks
	 * @param input
	 * @param frame
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void fromMp3ToSplit(String input, int frame,String folder) throws IOException, InterruptedException{
		String output = input.replace("mp3","wav");
		convertMp3(input, output);
		float len = getSoundLength(folder+output);
		System.out.println(len+" seconds");
		int nb = (int)(len/frame);
		new Utils().writeFile(folder+"audio.txt", "", false);
		for(int i=1;i<=nb;i++){
			new Utils().writeFile(folder+"audio.txt", ""+i+output, true);
		}
		splitWavFile(output, len, frame);
	}
	
	/**
	 * Split the Wave file into chuncks
	 * @param file
	 * @param length
	 * @param frame
	 * @throws InterruptedException
	 */
	public void splitWavFile(String file, float length, int frame) throws InterruptedException{
		ArrayList<FFSplit> threadlist = new ArrayList<FFSplit>();
		for(int i=0;i<length;i+=frame){
			FFSplit ffsplit = new FFSplit(frame,i, file);
			threadlist.add(ffsplit);
			ffsplit.start();
		}
		for(int j=0;j<threadlist.size();j+=frame){
			threadlist.get(j).join();
		}
		if(new Configuration().DEBUG_MODE){
			 System.out.println("End of split");
		}
	}
	
	/**
	 * Convert mp3 file into wav
	 * @param file
	 * @param output
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void convertMp3(String file, String output) throws IOException, InterruptedException{
		SystemUtils system = new SystemUtils();
		String command = "";
		if(system.isWindows()){
			command = new Configuration().OUTPUT_FOLDER.replace("/", "")+"\\ffmpeg.exe"+" -i "+file+" "+output;
		}
		else if(system.isLinux() || system.isMac()){
			command = new Configuration().OUTPUT_FOLDER+"ffmpeg"+" -i "+file+" "+output;
		}
		else{
			System.out.println("Command is not available for your OS");
			return;
		}
		Process p = Runtime.getRuntime().exec(command,null,new File(new Configuration().OUTPUT_FOLDER.replace("/", ""))); 
        p.waitFor(); 
        if(new Configuration().DEBUG_MODE){
        	System.out.println(file+" converted in "+output);
		}
	}
	
	/**
	 * Get total length of song (in seconds)
	 * @param filename
	 * @return
	 */
	public float getSoundLength(String filename){
		AudioInputStream audioInputStream = null;
		AudioFormat audioFormat = null;
		try {
			 audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
			 audioFormat = audioInputStream.getFormat();
		} 
		catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		return audioInputStream.getFrameLength() / audioFormat.getFrameRate();
	}

}//end of class
