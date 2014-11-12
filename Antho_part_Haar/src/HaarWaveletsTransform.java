
public class HaarWaveletsTransform {
	double[][] matrixImage;
	double[][] haarTransform;
	
	public HaarWaveletsTransform(double[][] matImg){
		matrixImage = matImg;
	}
	
	public double[][] processStandardDecomposition(){
		int width = matrixImage[0].length;
		int height = matrixImage.length;
		
		for (int i = 0 ; i < height ; i++){
			decompositionRow(i);
		}
		
		/*for (int i = 0 ; i < width ; i++){
			decompositionColumn(i);
		}*/
		
		return matrixImage;
	}
	
	public double[] decompositionRow(int row){
		
		double h = (double) matrixImage[row].length;
		// Normalisation
		for (int i = 0 ; i < matrixImage[row].length ; i++){
			
			matrixImage[row][i] = matrixImage[row][i] / Math.sqrt(h);
			System.out.println(matrixImage[row][i]);
		}
		
		int steps = matrixImage[row].length;
		while (steps > 1){
			System.out.println(steps);
			decompositionStepRow(row,steps);
			steps = steps / 2;
		}
		
		return matrixImage[row];
	}
	
	public double[] decompositionStepRow(int row,int steps){
		double[] newRow = new double[matrixImage[row].length];
		
		for (int i = 1 ; i < steps/2 ; i++){
			newRow[i] = (matrixImage[row][2*i] + matrixImage[row][2*i - 1]) / Math.sqrt(2);
			newRow[(steps/2) + i] = (matrixImage[row][2*i] - matrixImage[row][2*i - 1]) / Math.sqrt(2);
		}
		return null;
	}

}
