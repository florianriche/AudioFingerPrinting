package audiofinger;

/**
 * 
 * @author
 *
 */
public class ByteConversion {
	
	/**
	 * Conversion wavelets
	 * @param wavelets
	 * @return
	 */
	public boolean[] conversiontoBits(double[][] wavelets) {
		int n = wavelets.length;
		int m = wavelets[0].length;
		boolean[] array = new boolean[2 * n * m];
		
		//for all the elements of the matrix		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				//wavelets fingerprint and storage into byte array
				if (wavelets[i][j] == 0) {
					array[2 * ((i * m) + j)] = false;
					array[2 * ((i * m) + j) + 1] = false;
				}
				if (wavelets[i][j] > 0) {
					array[2 * ((i * m) + j)] = true;
					array[2 * ((i * m) + j) + 1] = false;
				}
				if (wavelets[i][j] < 0) {
					array[2 * ((i * m) + j)] = false;
					array[2 * ((i * m) + j) + 1] = true;
				}
			}
		}
		return array;
	}

}//end of class
