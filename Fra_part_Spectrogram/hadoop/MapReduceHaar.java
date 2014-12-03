package hadoop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import audiofinger.Utils;

/**
 * 
 * @author Paco
 *
 */
public class MapReduceHaar extends Thread{

	public String name = "";
	public double threshold = 0;
	public String folder = "";
	
	/**
	 * MapReduceHaar Constructor 
	 * @param name
	 * @param threshold
	 */
	public MapReduceHaar(String name, double threshold, String folder) {
		this.name = name;
		this.threshold = threshold;
		this.folder = folder;
	}

	/**
	 * MapReduceHaar Runnable
	 */
	public void run(){
		try {
			mapReduceFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Compute HaarWavelets and filter with threshold
	 * @throws Exception
	 */
	public synchronized void mapReduceFile() throws Exception{	
		HaarWaveletMapReduce haarhadoop = new HaarWaveletMapReduce();
		
		//process rows
		haarhadoop.doMapReduce(folder+name+"/part-r-00000", folder+"outhaarrow"+name);
		//transpose
		BufferedWriter rowhaarwrite = new BufferedWriter(new FileWriter((new File(folder+"resrowhaar"+name))));
		ArrayList<String> truc = new Utils().readFile(folder+"outhaarrow"+name+"/part-r-00000");
		for (String tru : truc) {
			String[] splits = tru.split("\t");
			rowhaarwrite.write(splits[1] + "\t" + splits[0] + "\t" + splits[2] + "\n");

		}
		rowhaarwrite.close();
		
		// process column
		haarhadoop.doMapReduce(folder+"resrowhaar"+name, folder+"outhaarcolumn"+name);
		// transpose
		BufferedWriter columnhaarwrite = new BufferedWriter(new FileWriter((new File(folder+"outHaarFinal"+name))));
		ArrayList<String> trouc = new Utils().readFile(folder+"outhaarcolumn"+name+"/part-r-00000");
		for (String tru : trouc) {
			String[] splits = tru.split("\t");
			columnhaarwrite.write(splits[1] + "\t" + splits[0] + "\t" + splits[2] + "\n");

		}
		columnhaarwrite.close();			
		
		//Filter threshold
		ArrayList<String> Newoutput = new Utils().readFile(folder+"outHaarFinal"+name);
		ArrayList<Float> values = new ArrayList<>();
		for (String output : Newoutput) {
			String[] outSplits = output.split("\t");
			values.add(Math.abs(Float.parseFloat(outSplits[2])));
		}
		
		//sort values and get the threholdvalue% most important
		Collections.sort(values, Collections.reverseOrder());
		int ThresholdIndex = (int) (values.size() * threshold);
		float ThreSholdValue = values.get(ThresholdIndex);

		BufferedWriter finalwrite = new BufferedWriter(new FileWriter(new File(folder+"outHaarThresholdFinal"+name)));				
		for (String output : Newoutput) {
			String[] outSplits = output.split("\t");
			float current_value = Float.parseFloat(outSplits[2]);
			current_value = (Math.abs(current_value) < ThreSholdValue) ? 0 : current_value;
			finalwrite.write(outSplits[0] + "\t" + outSplits[1] + "\t"
					+ current_value+"\n");
		}
		finalwrite.close();
	}

}//end of class
