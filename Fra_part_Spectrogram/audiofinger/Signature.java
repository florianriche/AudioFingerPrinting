package audiofinger;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author
 *
 */
public class Signature {
	public int firstOne(ArrayList<Boolean> vecteur, int seed) {
		Random R = new Random(seed);
		
		boolean found = false;
		int n = vecteur.size();
		int i = 0;
		while (i < n & i < 255 & !found) {
			int toTest = R.nextInt(n);
			found = vecteur.get(toTest) == true;
			i++;
		}
		return i;
	}
	/**
	 * Find the first '1' of the permutation
	 * @param vecteur
	 * @param seed
	 * @return
	 */
	public int firstOne(boolean[] vecteur, int seed) {
		Random R = new Random(seed);
		
		boolean found = false;
		int n = vecteur.length;
		int i = 0;
		while (i < n & i < 255 & !found) {
			int toTest = R.nextInt(n);
			found = vecteur[toTest] == true;
			i++;
		}
		return i;
	}

	/**
	 * Compute the fingerprint
	 * @param original
	 * @param p
	 * @return
	 */
	public byte[] fingerprint(boolean[] original, int p) {
		byte[] signature = new byte[p];
		
		//Find the first '1' and add it to the fingerprint
		for (int i = 0; i < signature.length; i++) {
			int indiceOriginal = firstOne(original, i);
			signature[i] = (byte) indiceOriginal;
		}
		return signature;
	}
	
	/**
	 * Compute the fingerprint
	 * @param original
	 * @param p
	 * @return
	 */
	public byte[] fingerprint(ArrayList<Boolean> original, int p) {
		byte[] signature = new byte[p];
		
		//Find the first '1' and add it to the fingerprint
		for (int i = 0; i < signature.length; i++) {
			int indiceOriginal = firstOne(original, i);
			signature[i] = (byte) indiceOriginal;
		}
		return signature;
	}

}//end of class
