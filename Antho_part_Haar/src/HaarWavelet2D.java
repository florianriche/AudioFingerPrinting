
public class HaarWavelet2D {
	double[][] matrix;
	
	public HaarWavelet2D(double[][] data){
		matrix = data;
	}
	
	public void show(){
		for (int i = 0 ; i < matrix.length ; i++){
			for (int j = 0 ; j < matrix[i].length ; j++){
				System.out.print(matrix[i][j] + ", ");
			}
			System.out.println("");
		}
	}
	
	public void transpose(){
		double[][] transposed = new double[matrix[0].length][matrix.length];
		for (int i = 0 ; i < matrix.length ; i++){
			for (int j = 0 ; j < matrix[0].length ; j++){
				transposed[j][i] = matrix[i][j];
			}
		}
		
		matrix = transposed;
	}
	
	public void standardDecomposition(){
		processRows();
		transpose();
		processRows();
		transpose();
	}
	
	public void processRows(){
		int size = matrix.length;
		for (int i = 0 ; i < size ; i++){
			matrix[i] = decompositionRow(matrix[i]);
		}
	}
	
	public double[] decompositionRow(double[] row){
		int steps = row.length;
		while (steps > 1){
			row = decompositionStep(steps, row);
			steps = steps / 2;
		}
		return row;
	}
	
	public double[] decompositionStep(int step, double[] row){
		double[] newRow = new double[row.length];
		System.arraycopy( row, 0, newRow, 0, row.length );
		
		for (int i=0 ; i < step/2 ; i++){
			newRow[i] = (row[2*i + 1] + row[2*i])/ 2;
			newRow[step/2 + i] = (row[(2*i)] - row[(2*i + 1)])/2;	
		}
		
		return newRow;
	}
	
	public double[][] getMatrix(){
		return matrix;
	}
}
