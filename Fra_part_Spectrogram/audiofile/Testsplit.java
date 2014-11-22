package audiofile;

import java.io.IOException;

public class Testsplit {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		//TODO : Linux version 
		Ffmpeg ffmpeg = new Ffmpeg();
		ffmpeg.fromMp3ToSplit("artist.mp3", 30);
	}

}
