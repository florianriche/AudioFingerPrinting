package process;

/**
 * 
 * @author Paco
 *
 */
public class Configuration {

	/**
	 * Recommend values
	 * 
	 * HAAR_THRESHOLD = 0.05 (5%)
	 * FFT_POINTS_NB = 4096
	 * STFT_FRAME_SIZE = 485 (11ms)
	 * FILE_SECONDS_SPLIT = 2 
	 * SIGNATURE_SIZE = ?
	 * SIGNATURE_SUB_SIZE = ?
	 */
	
	final public String OUTPUT_FOLDER = "Audiodoop/"; //Folder where all files are written
	final public boolean DEBUG_MODE = true; //Debug mode
	
	final public double HAAR_THRESHOLD = 0.05; //Threshold value for the selection after haar (has to be inferior to 1)
	final public int FFT_POINTS_NB = 4096; //Number of points for each FFT (has to be power of 2) 
	final public int STFT_OVERLAP = 485; //Overlap for the frame of the STFT
	final public int FILE_SECONDS_SPLIT = 2; //Duration of each split wav file (in seconds)
	final public int SIGNATURE_SIZE = 50; //Length of each fingerprint
	final public int SIGNATURE_SUB_SIZE = 5; //Number of sub fingerprints
	
	final public String CASSANDRA_KEYSPACE = "audiofingerprint"; //Database name (keyspace)
	final public String CASSANDRA_IP = "127.0.0.1"; //Database IP

}//end of class
