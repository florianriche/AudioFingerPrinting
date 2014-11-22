package audiofinger;

import java.io.File;
import java.io.IOException;

import org.tc33.jheatchart.HeatChart;

public class Hello {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// Create some dummy data.
		double[][] data = new double[][]{{3,2,3,4,5,6},
		                                 {2,3,4,5,6,7},
		                                 {3,4,5,6,7,6},
		                                 {4,5,6,7,6,5}};

		// Step 1: Create our heat map chart using our data.
		HeatChart map = new HeatChart(data);

		// Step 2: Customise the chart.
		map.setTitle("This is my heat chart title");
		map.setXAxisLabel("X Axis");
		map.setYAxisLabel("Y Axis");

		// Step 3: Output the chart to a file.
		map.saveToFile(new File("java-heat-chart.png"));
	}

}
