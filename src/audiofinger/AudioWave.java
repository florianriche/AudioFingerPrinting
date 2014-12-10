package audiofinger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * 
 * @author Paco
 *
 */
public class AudioWave {
	
	public AudioInputStream audioInputStream = null;
	public AudioFormat audioFormat = null;
	
	public float sampleRate = 0; // sample rate
	public float lengthSound = 0; //length in seconds of the file
	public int eqPoints = 0; //number of equidistant points in time
	public float intervalTime = 0; //time interval at each equidistant point
	
	public String filename = "";
	
	/**
	 * Calculate data signal
	 * @param pathfile
	 * @return
	 * @throws IOException
	 */
	public int[] dataSignal(String pathfile) throws IOException{
		
		File file = new File(pathfile);		
		
		//Load the Audio Input Stream from the file        
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} 
		catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}

		//Get Audio Format information
		audioFormat = audioInputStream.getFormat();
		
		//Handle opening the line
		SourceDataLine line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
		} 
		catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Start playing the sound
		line.start();

		//Write the sound to an array of bytes
		ByteArrayOutputStream abData = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead = 0;

		while((bytesRead = audioInputStream.read(buffer)) != -1) {
		    abData.write(buffer, 0, bytesRead);
		}

		byte[] soundBytes = abData.toByteArray();

		//close the line      
		line.drain();
		line.close();
		
		//set variables
		setFilename(file.getName());
		setSampleRate(audioFormat.getSampleRate());
		setLengthSound(audioInputStream.getFrameLength() / audioFormat.getFrameRate());
		setEqPoints((int) (lengthSound * sampleRate)*2);
		setIntervalTime((lengthSound / eqPoints));
        
		return byteToInt(soundBytes);
	}
	
	/**
	 * 
	 * @param tab
	 * @return
	 */
	public int[] byteToInt(byte[] tab){
		int[] x = new int[tab.length];
		for(int i=0;i<tab.length;i++){
			x[i] = tab[i];
		}
		return x;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public float getLengthSound() {
		return lengthSound;
	}

	public void setLengthSound(float lengthSound) {
		this.lengthSound = lengthSound;
	}

	public int getEqPoints() {
		return eqPoints;
	}

	public void setEqPoints(int eqPoints) {
		this.eqPoints = eqPoints;
	}

	public float getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(float intervalTime) {
		this.intervalTime = intervalTime;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	

	
	
}//end of class
