package audiofinger;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author
 *
 */
public class HaarWavelet2D {
	
	public double[][] matrix;
	public double maxVal;
	
	/**
	 * 
	 * @param data
	 */
	public HaarWavelet2D(double[][] data){
		matrix = data;
	}
	
	public HaarWavelet2D(){
		
	}
	
	/**
	 * 
	 */
	public void show(){
		for (int i = 0 ; i < matrix.length ; i++){
			for (int j = 0 ; j < matrix[i].length ; j++){
				System.out.print(matrix[i][j] + ", ");
			}
			System.out.println("");
		}
	}
	
	/**
	 * 
	 */
	public void transpose(){
		double[][] transposed = new double[matrix[0].length][matrix.length];
		for (int i = 0 ; i < matrix.length ; i++){
			for (int j = 0 ; j < matrix[0].length ; j++){
				transposed[j][i] = matrix[i][j];
			}
		}
		
		matrix = transposed;
	}
	
	/**
	 * 
	 */
	public void standardDecomposition(){
		processRows();
		transpose();
		processRows();
		transpose();
	}
	
	/**
	 * 
	 * @param threshold
	 */
	public void topTWavelets(double threshold){
		
		ArrayList<Double> ListValues = new ArrayList<Double>();
		
		for(int i = 0 ;i<matrix.length ; i++){
			for(int j = 0 ; j <matrix[0].length ; j++){
				ListValues.add(Math.abs(matrix[i][j]));
			}
		}
		
		Collections.sort(ListValues,Collections.reverseOrder());
		
		int ThresholdIndex = (int)(ListValues.size()*threshold);
		System.out.println(ThresholdIndex);
		double ThreSholdValue = ListValues.get(ThresholdIndex);
		
		for(int i = 0 ;i<matrix.length ; i++){
			for(int j = 0 ; j < matrix[0].length ; j++){
				matrix[i][j] = (Math.abs(matrix[i][j])<ThreSholdValue)?0:matrix[i][j];
			}
		}
	}
	
	/**
	 * 
	 */
	public void processRows(){
		int size = matrix.length;
		for (int i = 0 ; i < size ; i++){
			matrix[i] = decompositionRow(matrix[i]);
		}
	}
	
	/**
	 * 
	 * @param row
	 * @return
	 */
	public double[] decompositionRow(double[] row){
		int steps = row.length;
		while (steps > 1){
			row = decompositionStep(steps, row);
			steps = steps / 2;
		}
		return row;
	}
	
	/**
	 * 
	 * @param step
	 * @param row
	 * @return
	 */
	public double[] decompositionStep(int step, double[] row){
		double[] newRow = new double[row.length];
		System.arraycopy( row, 0, newRow, 0, row.length );
		
		for (int i=0 ; i < step/2 ; i++){
			newRow[i] = (row[2*i + 1] + row[2*i])/ 2;
			newRow[step/2 + i] = (row[(2*i)] - row[(2*i + 1)])/2;	
		}
		
		return newRow;
	}
	
	/**
	 * 
	 * @return
	 */
	public double[][] getMatrix(){
		return matrix;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getMax(){
		return maxVal;
	}
}//end of class