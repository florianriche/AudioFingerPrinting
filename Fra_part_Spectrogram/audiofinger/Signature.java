package audiofinger;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author
 *
 */
public class Signature {
		
	/**
	 * 
	 * @param subFingerprint
	 * @param l
	 * @return
	 */
	public ArrayList<byte[]> Decomposition(byte[] subFingerprint, int l) {
		int p = subFingerprint.length;
		ArrayList<byte[]> Decomposition = new ArrayList<byte[]>();
		// Si plus de tables que de byte ( situation qui ne devrait pas
		// arriver), on remplit juste avec des tableaux de 1
		if (p < l) {
			for (byte b : subFingerprint) {
				byte[] to_add = { b };
				Decomposition.add(to_add);
			}
		}
		// Si plus de bytes que de tables :
		else {
			int taille = p / l;
			int reste = p % l;
			// System.out.println(reste);
			int compteur = 0;
			int debut = 0;
			byte[] to_add = null;
			// Si il y a un reste, on met 1 byte de plus sur les premiers
			if (reste > 0) {
				debut = (taille + 1) * reste;
				to_add = new byte[taille + 1];
				for (int i = 0; i < (taille + 1) * reste; i++) {
					to_add[compteur] = subFingerprint[i];
					compteur++;
					if (compteur % (taille + 1) == 0) {
						compteur = 0;
						Decomposition.add(to_add);
						to_add = new byte[taille + 1];
					}
				}
			}
			compteur = 0;
			to_add = new byte[taille];
			for (int i = debut; i < p; i++) {
				to_add[compteur] = subFingerprint[i];
				compteur++;
				if (compteur % (taille) == 0) {
					compteur = 0;
					Decomposition.add(to_add);
					to_add = new byte[taille];
				}
			}
		}

		return Decomposition;
	}
	
	/**
	 * 
	 * @param vecteur
	 * @param seed
	 * @return
	 */
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
