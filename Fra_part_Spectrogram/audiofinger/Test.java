package audiofinger;

import java.io.IOException;

/**
 * 
 * @author Paco
 *
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

		//framesize = 4096 bytes
		// overlap = 485 bytes (equals to 11ms at 44100 samples per sec)
		 
		//stft.setWriteFilesStft(true);
		stft.performStft("ffmpeg/bin/out440.wav", 4096, 485);

		System.out.println("Duration of STFT : "+stft.getDurationStft()+"ms");
		
		spectrogram.setAccuracy(4);
		spectrogram.setWritePicture(true);
		spectrogram.generateSpectrogram(stft, "test");
		
		System.out.println("Duration of Spectrogram : "+spectrogram.getDurationSpectrogram()+"ms");
		
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
