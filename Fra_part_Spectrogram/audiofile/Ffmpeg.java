package audiofile;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import audiofinger.Utils;

/**
 * 
 * @author Paco
 *
 */
public class Ffmpeg{
	
	public String ffmpegpath = "ffmpeg\\bin"; 
	
	/**
	 * Convert mp3 into multiple wave chuncks
	 * @param input
	 * @param frame
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void fromMp3ToSplit(String input, int frame) throws IOException, InterruptedException{
		String output = input.replace("mp3","wav");
		convertMp3(input, output);
		float len = getSoundLength("ffmpeg/bin/"+output);
		System.out.println(len+" seconds");
		int nb = (int)(len/frame);
		new Utils().writeFile("audio.txt", "", false);
		for(int i=1;i<=nb;i++){
			new Utils().writeFile("audio.txt", ""+i+output, true);
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
		FFSplit ffsplit = null;
		for(int i=0;i<length;i+=frame){
			ffsplit = new FFSplit(frame,i, file);
			ffsplit.start();
		}
		ffsplit.join();
	}
	
	/**
	 * Convert mp3 file into wav
	 * @param file
	 * @param output
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void convertMp3(String file, String output) throws IOException, InterruptedException{
		String command = ffmpegpath+"\\ffmpeg.exe"+" -i "+file+" "+output;
		Process p = Runtime.getRuntime().exec(command,null,new File(ffmpegpath)); 
        p.waitFor(); 
        System.out.println(file+" converted in "+output);
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
