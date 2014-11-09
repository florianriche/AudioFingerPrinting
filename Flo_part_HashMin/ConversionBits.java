package audioWavePrint;

public class ConversionBits {

	public static boolean[] conversiontoBits(double[][] wavelets) {
		/**
		 * Préparation du vecteur de stockage (2* le nombre de pixels dans la
		 * matrice car codés en 10/01/00
		 */
		int n = wavelets.length;
		int m = wavelets[0].length;
		boolean[] array = new boolean[2 * n * m];
		/** Parcours de la matrice */
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				/** Signature des wavelets et stockage dans le tableau de bytes */
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

}
