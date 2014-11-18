
public class HaarWavelet1D {

	double[] matrix;
	int size;
	
	public HaarWavelet1D(double[] data){
		matrix = data;
		size = matrix.length;
	}

	public void show(){
		for (int i = 0; i < size; i++) {
            System.out.print(matrix[i] + ", ");
        }
        System.out.println("");
	}
	
	public void standardDecomposition(){
		int steps = size;
		double[] row = matrix;
		
		while (steps > 1){
			row = decompositionStep(steps, row);
			steps = steps / 2;
		}
		
		matrix = row;
	}
	
	public double[] decompositionStep(int step,double[] row){
		double[] newRow = new double[row.length];
		System.arraycopy( row, 0, newRow, 0, row.length );
		
		for (int i=0 ; i < step/2 ; i++){
			newRow[i] = (row[2*i + 1] + row[2*i])/ 2;
			newRow[step/2 + i] = (row[(2*i)] - row[(2*i + 1)])/2;	
		}
		
		return newRow;
	}
}
