package database;

import java.util.ArrayList;

import audioWavePrint.ConversionBits;
import audioWavePrint.CreationMatriceTest;
import audioWavePrint.Signature;

public class Utils {

	/**
	 * Decompose the Fingeprint into a list of l subfingerprints
	 * @param subFingerprint
	 * @param l
	 * @return
	 */
	public static ArrayList<byte[]> Decomposition(byte[] Fingerprint, int l) {
		int p = Fingerprint.length;
		ArrayList<byte[]> Decomposition = new ArrayList<byte[]>();
		// Si plus de tables que de byte ( situation qui ne devrait pas
		// arriver), on remplit juste avec des tableaux de 1
		if (p < l) {
			for (byte b : Fingerprint) {
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
					to_add[compteur] = Fingerprint[i];
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
				to_add[compteur] = Fingerprint[i];
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
	 * Compute the number of exact match between two byte arrays
	 * @param Reference
	 * @param toCompare
	 * @return
	 */
	public static int Proximite(byte Reference[], byte toCompare[]) {
		if (Reference.length != toCompare.length) {
			System.err.println("Erreur : Fingerprint sizes are inconsistents");
			return 0;
		}
		int score = 0;
		for (int i = 0; i < Reference.length; i++) {
			if (Reference[i] == toCompare[i])
				score = score + 1;
		}
		return score;
	}
	

	
}
