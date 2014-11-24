package process;

import hadoop.MapReduceAudio;

import java.io.IOException;
import java.util.ArrayList;

import audiofile.Ffmpeg;
import audiofinger.Utils;

public class Process {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Ffmpeg ffmpeg = new Ffmpeg();
		ffmpeg.fromMp3ToSplit("noise.mp3", 1);
		
		ArrayList<String> plop = new Utils().readFile("audio.txt");
		MapReduceAudio mapreduceaudio = null;
		for(int i=0;i<plop.size();i++){
			String t = plop.get(i);
			mapreduceaudio = new MapReduceAudio("ffmpeg/bin/"+t, 4096, 485,""+t.replace(".wav", ""));
			mapreduceaudio.start();
		}
		mapreduceaudio.join();
		
	}//end of main

}//end of class
