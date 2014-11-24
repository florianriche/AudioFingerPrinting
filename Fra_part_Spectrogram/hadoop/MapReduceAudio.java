package hadoop;

import audiofinger.AudioWave;
import audiofinger.Utils;

/**
 * 
 * @author Paco
 *
 */
public class MapReduceAudio extends Thread{

	public String audiofile = "";
	public int framesize = 0;
	public int overlap = 0;
	public String outputfolder = "";
	
	/**
	 * MapReduceAudio Constructor
	 * @param audiofile
	 * @param framesize
	 * @param overlap
	 * @param outputfolder
	 */
	public MapReduceAudio(String audiofile, int framesize, int overlap, String outputfolder) {
		this.audiofile = audiofile;
		this.framesize = framesize;
		this.overlap = overlap;
		this.outputfolder = outputfolder;
	}

	/**
	 * MapReduceAudio Runnable
	 */
	public void run(){
		try {
			mapReduceFile(audiofile, framesize, overlap, outputfolder);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Process Spectrogam with MapReduce for a wav file
	 * @param audiofile
	 * @param framesize
	 * @param overlap
	 * @throws Exception
	 */
	public synchronized void mapReduceFile(String audiofile, int framesize, int overlap, String outputfolder) throws Exception{
		AudioWave sound = new AudioWave();
		int[] x = sound.dataSignal(audiofile);
		
		System.out.println(""+sound.getFilename());
		
		float sample_rate = sound.getSampleRate();
		System.out.println("Sample rate = "+sample_rate);
		
		float T = sound.getLengthSound();
		System.out.println("T = "+T+ " (length of sampled sound in seconds)");
		
		int n = sound.getEqPoints();
		System.out.println("n = "+n+" (number of equidistant points)");
		
		float h = sound.getIntervalTime();
		System.out.println("h = "+h+" (length of each time interval in seconds)");
		
		int iter = 0;
		new Utils().writeFile(outputfolder+".txt", "", false);
		for(int i=0;i<=n;i+=overlap){
			new Utils().writeFile(outputfolder+".txt", iter+","+i, true);
			iter++;
		}
		
		//hadoop
		SpectrogramMapReduce dodoop = new SpectrogramMapReduce();
		SpectrogramMapReduce.sig=x;
		SpectrogramMapReduce.framesize=framesize;
		SpectrogramMapReduce.samplerate=sample_rate;
		SpectrogramMapReduce.h=h;
		SpectrogramMapReduce.endtime=T;
		dodoop.doMapReduce(outputfolder+".txt",outputfolder);
	}
	
}//end of class
