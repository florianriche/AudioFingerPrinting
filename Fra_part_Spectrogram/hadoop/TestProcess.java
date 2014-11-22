package hadoop;

import java.io.IOException;

import audiofinger.AudioWave;
import audiofinger.Utils;

public class TestProcess {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		AudioWave sound = new AudioWave();
		int[] x = sound.dataSignal("ffmpeg/bin/out440.wav");
		
		System.out.println(""+sound.getFilename());
		float sample_rate = sound.getSampleRate();
		System.out.println("Sample rate = "+sample_rate);
		float T = sound.getLengthSound();
		System.out.println("T = "+T+ " (length of sampled sound in seconds)");
		int n = sound.getEqPoints();
		System.out.println("n = "+n+" (number of equidistant points)");
		float h = sound.getIntervalTime();
		System.out.println("h = "+h+" (length of each time interval in seconds)");
		int overlap = 485;
		int iter = 0;
		new Utils().writeFile("MapWav.txt", "", false);
		for(int i=0;i<=n;i+=overlap){
			new Utils().writeFile("MapWav.txt", iter+","+i, true);
			iter++;
		}
		SpectrogramMapReduce dodoop = new SpectrogramMapReduce();
		SpectrogramMapReduce.sig=x;
		SpectrogramMapReduce.framesize=4096;
		SpectrogramMapReduce.samplerate=sample_rate;
		SpectrogramMapReduce.h=h;
		SpectrogramMapReduce.endtime=T;
		dodoop.doMapReduce("MapWav.txt","OUTPUT");
	}//end of main

}//end of class
