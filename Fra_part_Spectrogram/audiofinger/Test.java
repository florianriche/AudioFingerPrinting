package audiofinger;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.tc33.jheatchart.HeatChart;

/**
 * 
 * @author Paco
 * http://jvalentino2.tripod.com/dft/index.html
 */
public class Test {
	
	/**
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException{

		STFT stft = new STFT();
		Spectrogram spectrogram = new Spectrogram();

		//framesize = 2048 bytes
		// overlap = 485 bytes (equals to 11ms at 44100 samples per sec)
		 
		//stft.setWriteFilesStft(true);
		stft.setFreqMin(200);
		stft.performStft("ffmpeg/bin/outtest.wav", 4096, 485);
		
		System.out.println("Duration of STFT : "+stft.getDurationStft()+"ms");
		System.out.println("Number of lists generated: "+stft.getTimeFreqMagn().keySet().size());
		
		//spectrogram.tableSpectrogram(stft);
		spectrogram.setAccuracy(2);
		spectrogram.setWritePicture(true);
		
		
		for(String lol : stft.getTimeFreqMagn().keySet()){
			System.out.println(lol);
			System.out.println(stft.getTimeFreqMagn().get(lol).keySet());
			System.out.println(stft.getTimeFreqMagn().get(lol).values());
		}
		spectrogram.generateSpectrogram(stft, "spectrogramTest");
		
	}//end of main
		
}//end of class


/*
* To perform fft, x.length has to be a number power of 2
* So the length of the sample has to be calculed as follow
* tt = (65536-44100)/44100
* 44100 Hz is because of the wav format
* 65536 is 2^16
* tt = 0,48607709750566893424036281179138
* t = 1 + tt
* Roughly 1.5 sec
* You can use ffmpeg to do that:
* ffmpeg -ss 0 -t 0:01.48607709 -i input.mp3 output.wav
*/
