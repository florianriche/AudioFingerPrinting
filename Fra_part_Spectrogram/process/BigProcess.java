package process;

import hadoop.MapReduceHaar;
import hadoop.MapReduceSpectrogram;

import java.io.IOException;
import java.util.ArrayList;

import audiofile.Ffmpeg;
import audiofinger.Signature;
import audiofinger.Utils;

public class BigProcess {

	public long durationProcess = 0; // execution time of the process 

	/**
	 * 
	 * @param filename
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void computeTheProcess(String filename) throws InterruptedException, IOException{
		long startTime = System.nanoTime();
		
		//Initialize configuration
		Configuration  conf = new Configuration();
		
		//Split the audio file
		Ffmpeg ffmpeg = new Ffmpeg();	
		ffmpeg.fromMp3ToSplit(filename, conf.SEC_SPLIT, conf.OUTPUT_FOLDER);	
		
		ArrayList<String> audiolist = new Utils().readFile(conf.OUTPUT_FOLDER+"audio.txt");
		
		//Compute spectrograms
		ArrayList<MapReduceSpectrogram> threadlist1 = new ArrayList<MapReduceSpectrogram>();
		for(int i=0;i<audiolist.size();i++){
			String t = audiolist.get(i);
			MapReduceSpectrogram mapreduceaudio = new MapReduceSpectrogram(conf.OUTPUT_FOLDER+t, conf.POINTS_NB_FFT, conf.FRAME_SIZE, conf.OUTPUT_FOLDER+t.replace(".wav", ""));
			threadlist1.add(mapreduceaudio);
			mapreduceaudio.start();
		}
		for(int i=0;i<threadlist1.size();i++){
			threadlist1.get(i).join();//wait for spectrogram threads
		}
		
		
		//Compute haar wavelets and selection by threshold
		ArrayList<MapReduceHaar> threadlist2 = new ArrayList<MapReduceHaar>();
		for(int j=0;j<audiolist.size();j++){
			String name = audiolist.get(j).replace(".wav","");	
			MapReduceHaar mapreducehaar = new MapReduceHaar(name,conf.THRESHOLD,conf.OUTPUT_FOLDER);
			threadlist2.add(mapreducehaar);
			mapreducehaar.start();
		}	
		for(int j=0;j<threadlist2.size();j++){
			threadlist2.get(j).join();//wait for haar wavelet threads
		}
		
		//Compute the signature
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
			
			byte[]  fingerprint = new Signature().fingerprint(booleansSignature, 50);
			
			/*
			Cassandra cassandra = new Cassandra();
			cassandra.connectToKeyspace("127.0.0.1", "plop");
			//AudioDatabase db = new AudioDatabase(cassandra);
			ByteBuffer trucc = ByteBuffer.wrap(fingerprint);
			PreparedStatement ps = cassandra.session.prepare("INSERT INTO signaturebis (id,sign) VALUES (?,?)");
			BoundStatement b = new BoundStatement(ps);
			b.setInt(0, 3+k);
			b.setBytes(1, trucc);
			cassandra.session.execute(b);		
			cassandra.closeConnection();
			*/
		}	
		
		
		long endTime = System.nanoTime();
		setDurationProcess((endTime - startTime) / 1000000);
		System.out.println("End of big process !");
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
