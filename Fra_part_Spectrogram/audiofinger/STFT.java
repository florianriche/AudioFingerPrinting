package audiofinger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * 
 * @author Paco
 *
 */
public class STFT {

	public LinkedHashMap<Double,Double> freqMagn= new LinkedHashMap<Double,Double>(); // List1 :  param1=frequency, param2=magnitude
	public LinkedHashMap<String, LinkedHashMap<Double, Double>> timeFreqMagn= new LinkedHashMap<String,LinkedHashMap<Double, Double>>(); // List2 : param1=time, param2=list1
	
	public boolean writeFilesStft = false; //if true, write all the list freq-magn on files
	public long durationStft = 0; // execution time of the stft
	
	//frequencies filter
	public int freqMin = 300;
	public int freqMax = 2000;
	
	/**
	 * Do the stft of the sound with special framesize and overlap
	 * @param pathfile
	 * @param framesize
	 * @param overlap
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void performStft(String pathfile,int framesize, int overlap) throws IOException, InterruptedException{
		
		//store start time to calculate execution time
		long startTime = System.nanoTime();
		
		timeFreqMagn.clear();
		
		//get sound data
		AudioWave sound = new AudioWave();
		int[] x = sound.dataSignal(pathfile);
		
		System.out.println(""+sound.getFilename());
		
		//get sample rate
		float sample_rate = sound.getSampleRate();
		System.out.println("Sample rate = "+sample_rate);

		//get length in seconds of the sample
		float T = sound.getLengthSound();
		System.out.println("T = "+T+ " (length of sampled sound in seconds)");

		//get the number of equidistant points in time
		int n = sound.getEqPoints();
		System.out.println("n = "+n+" (number of equidistant points)");

		//get the time interval at each equidistant point
		float h = sound.getIntervalTime();
		System.out.println("h = "+h+" (length of each time interval in seconds)");
				
		int sizeframe = framesize;
		
		for(int i=0;i<=n;i+=overlap){
			writeFreqMagn(x, sizeframe, i, sample_rate, h, T);
		}
		
		//store end time to calculate execution time
		long endTime = System.nanoTime();
		setDurationStft((endTime - startTime) / 1000000);
		System.out.println("End STFT !");
	}
	
	/**
	 * Do fft and fill the hashmap with special framesize and offset
	 * @param sig
	 * @param size
	 * @param offset
	 * @param samplerate
	 * @param h
	 * @param endtime
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void writeFreqMagn(int[] sig, int size, int offset, float samplerate,float h, float endtime) throws IOException, InterruptedException{
			
		float timebegin = (offset-size)*h;
		if(timebegin<0){return;}
		
		float timeend = offset*h;	
		if(timeend>endtime){return;}
		
		System.out.println("timebegin: "+timebegin+" // timeend: "+timeend);
		if(writeFilesStft){
			new Utils().writeFile(timebegin+"-"+timeend+".txt","",false);
		}

		//limit array to the given offset
		int test[] = new int[size];
		int o = 0;
		for(int y=offset;y<offset+size;y++){ 
			test[o] = sig[y];
			o++;
		}
		/**
		 * Non optimisé
		 */
		
		//do the fft
		ArrayList<Double> res = calculateFFT(test,test.length);
		
		//copy of the fft results
		ArrayList<Double> copyres = new ArrayList<Double>(res);
		
		//order by highest magnitude
		Collections.sort(res,Collections.reverseOrder());

//		freqMagn.clear();
		freqMagn= new LinkedHashMap<Double,Double>(); // List1 :  param1=frequency, param2=magnitude

		//calculate freq and magnitude
		for(int i=0;i<res.size();i++){
			//double amplitude = 20*Math.log(2 * res[i]/n);
//			double magnitude = copyres.get(i);
			double magnitude = res.get(i);

			double frequency = (copyres.indexOf(res.get(i)) *(sig.length/res.size())* samplerate)/(sig.length);
//			double frequency = (copyres.indexOf(copyres.get(i)) * samplerate)/res.size();
//			double frequency = i * h / T * sample_rate;
			if(frequency>freqMin && frequency<freqMax){
				if(writeFilesStft){
					new Utils().writeFile(timebegin+"-"+timeend+".txt","frequency = "+frequency+", magnitude = "+magnitude,true);
				}
				freqMagn.put(frequency, magnitude);
			}			
		}
		timeFreqMagn.put(timebegin+"-"+timeend, freqMagn);
		System.out.println("End FFT !");
	}
	
	/**
	 * Calculate the fft and get magnitudes of signal 
	 * @param signal
	 * @param numberofpoints
	 * @return
	 */
	public ArrayList<Double> calculateFFT(int[] signal,int numberofpoints)
    {           
        final int mNumberOfFFTPoints = numberofpoints;
        Complex[] y;
        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
        ArrayList<Double> absSignal = new ArrayList<Double>();
        
        //fill complex signal
        for(int i = 0; i < mNumberOfFFTPoints; i++){
        	complexSignal[i] = new Complex(signal[i],0);
        }

        //do the fft
        y = new FFT().fft(complexSignal);

        //calculate magnitude and add to result  
        for(int i = 0; i < mNumberOfFFTPoints/2; i++){
            absSignal.add(i,Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2)));
        }
        return absSignal;
    }

	/**
	 * 
	 * @return
	 */
	public LinkedHashMap<String, LinkedHashMap<Double, Double>> getTimeFreqMagn() {
		return timeFreqMagn;
	}

	/**
	 * 
	 * @param writeFilesStft
	 */
	public void setWriteFilesStft(boolean writeFilesStft) {
		this.writeFilesStft = writeFilesStft;
	}

	/**
	 * 
	 * @return
	 */
	public long getDurationStft() {
		return durationStft;
	}

	/**
	 * 
	 * @param durationStft
	 */
	public void setDurationStft(long durationStft) {
		this.durationStft = durationStft;
	}

	/**
	 * 
	 * @return
	 */
	public int getFreqMin() {
		return freqMin;
	}

	/**
	 * 
	 * @param freqMin
	 */
	public void setFreqMin(int freqMin) {
		this.freqMin = freqMin;
	}

	/**
	 * 
	 * @return
	 */
	public int getFreqMax() {
		return freqMax;
	}

	/**
	 * 
	 * @param freqMax
	 */
	public void setFreqMax(int freqMax) {
		this.freqMax = freqMax;
	}

	
	
	

}//end of class
