package process;

import hadoop.MapReduceHaar;
import hadoop.MapReduceSpectrogram;

import java.io.IOException;
import java.util.ArrayList;

import database.AudioDatabase;
import database.Cassandra;

import audiofile.Ffmpeg;
import audiofinger.Signature;
import audiofinger.Utils;

/**
 * 
 * @author Paco
 *
 */
public class BigProcess extends Thread{

	public long durationProcess = 0; // execution time of the process 
	public int id_music = 0;
	public String filename = "";

	/**
	 * BigProcess Constructor
	 * @param id_music
	 * @param file
	 */
	public BigProcess(int id_music, String filename) {
		this.id_music = id_music;
		this.filename = filename;
	}

	/**
	 * BigProcess Runnable
	 */
	public void run(){
		try {
			computeTheProcess();
		} 
		catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Compute the AudioFingerPrinting process
	 * @param filename
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public synchronized void computeTheProcess() throws InterruptedException, IOException{
		System.out.println(">>>>>>>>>>>>>>>>>> Begin AudioFingerPrinting : "+filename+" <<<<<<<<<<<<<<<<<<");
		
		long startTime = System.nanoTime();
		
		//Initialize configuration
		Configuration  conf = new Configuration();
		
		//Split the audio file
		System.out.println(">>>>>>>>>>>>>>>>>> Split the file <<<<<<<<<<<<<<<<<<");
		Ffmpeg ffmpeg = new Ffmpeg();	
		ffmpeg.fromMp3ToSplit(filename, conf.FILE_SECONDS_SPLIT, conf.OUTPUT_FOLDER);	
		
		ArrayList<String> audiolist = new Utils().readFile(conf.OUTPUT_FOLDER+filename.replace(".mp3", "")+"audio.txt");
		
		//Compute spectrograms
		System.out.println(">>>>>>>>>>>>>>>>>> Compute Spectrograms <<<<<<<<<<<<<<<<<<");
		ArrayList<MapReduceSpectrogram> threadlist1 = new ArrayList<MapReduceSpectrogram>();
		for(int i=0;i<audiolist.size();i++){
			String t = audiolist.get(i);
			MapReduceSpectrogram mapreduceaudio = new MapReduceSpectrogram(conf.OUTPUT_FOLDER+t, conf.FFT_POINTS_NB, conf.STFT_FRAME_SIZE, conf.OUTPUT_FOLDER+t.replace(".wav", ""));
			threadlist1.add(mapreduceaudio);
			mapreduceaudio.start();
		}
		for(int i=0;i<threadlist1.size();i++){
			threadlist1.get(i).join();//wait for spectrogram threads
		}
		
		
		//Compute haar wavelets and selection by threshold
		System.out.println(">>>>>>>>>>>>>>>>>> Compute Haar Wavelets <<<<<<<<<<<<<<<<<<");
		ArrayList<MapReduceHaar> threadlist2 = new ArrayList<MapReduceHaar>();
		for(int j=0;j<audiolist.size();j++){
			String name = audiolist.get(j).replace(".wav","");	
			MapReduceHaar mapreducehaar = new MapReduceHaar(name,conf.HAAR_THRESHOLD,conf.OUTPUT_FOLDER);
			threadlist2.add(mapreducehaar);
			mapreducehaar.start();
		}	
		for(int j=0;j<threadlist2.size();j++){
			threadlist2.get(j).join();//wait for haar wavelet threads
		}
		
		//Compute the signature and put it in database
		System.out.println(">>>>>>>>>>>>>>>>>> Compute Signature and Insertion into Database <<<<<<<<<<<<<<<<<<");
		//initialize database
		Cassandra cassandra = new Cassandra();
		cassandra.connectToKeyspace(conf.CASSANDRA_IP, conf.CASSANDRA_KEYSPACE);
		AudioDatabase db = new AudioDatabase(cassandra);		
		//signature
		int overlap = 0;
		for(int k=0;k<audiolist.size();k++){
			String name = audiolist.get(k).replace(".wav","");		

			ArrayList<Boolean> booleansSignature = new ArrayList<>();
			ArrayList<String> thresholdoutputs = new Utils().readFile(conf.OUTPUT_FOLDER+"outHaarThresholdFinal"+name);
			
			for(String thresholdOutput : thresholdoutputs){
				String[] outThresholdSplits = thresholdOutput.split("\t");
				if(Float.parseFloat(outThresholdSplits[2])>0){
					booleansSignature.add(true);
					booleansSignature.add(false);
				}
				else if (Float.parseFloat(outThresholdSplits[2])<0) {
					booleansSignature.add(false);
					booleansSignature.add(true);

				}
				else{
					booleansSignature.add(false);
					booleansSignature.add(false);		
				}
			}
			//fingerprint and decomposition into subfingerprints
			byte[]  fingerprint = new Signature().fingerprint(booleansSignature, conf.SIGNATURE_SIZE);			
			ArrayList<byte[]> subfingerprint = new Signature().Decomposition(fingerprint, conf.SIGNATURE_SUB_SIZE);
			//insert fingerprint into database
			db.insertFingerprint(id_music, id_music, subfingerprint, overlap);
			overlap += subfingerprint.size();
		}	
		cassandra.closeConnection();
		
		long endTime = System.nanoTime();
		setDurationProcess((endTime - startTime) / 1000000);
		System.out.println(">>>>>>>>>>>>>>>>>> End of AudioFingerPrinting : "+filename+" <<<<<<<<<<<<<<<<<<");
	}

	/**
	 * 
	 * @return
	 */
	public long getDurationProcess() {
		return durationProcess;
	}

	/**
	 * 
	 * @param durationProcess
	 */
	public void setDurationProcess(long durationProcess) {
		this.durationProcess = durationProcess;
	}
	
}//end of class
