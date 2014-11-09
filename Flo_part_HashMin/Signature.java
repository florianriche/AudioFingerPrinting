package audioWavePrint;

import java.sql.Time;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

public class Signature {

	/** Trouve le premier 1 de la permutation */
	public static int firstOne(boolean[] vecteur, int seed) {
		/**
		 * Fonction aléatoire initalisé selon une seed différente pour chaque
		 * position on a donc p fonctions aléatoires
		 */
		Random R = new Random(seed);
		/**
		 * HashMap utilisé pour éviter de tester deux fois le même chiffre.
		 * Est-ce obligatoire?
		 */

		// HashMap<Integer, Boolean> Tested = new HashMap<Integer,Boolean>();
		boolean found = false;
		int n = vecteur.length;
		int i = 0;
		while (i < n & i < 255 & !found) {
			int toTest = R.nextInt(n);
			// while(Tested.containsKey(toTest)){
			// toTest = R.nextInt(n);
			// }
			// Tested.put(toTest, null);
			found = vecteur[toTest] == true;
			i++;
		}

		return i;
	}

	/** Crée la signature */
	public static byte[] fingerprint(boolean[] original, int p) {
		byte[] signature = new byte[p];

		/** Trouve le premier 1 et l'ajoute à la signature. */
		for (int i = 0; i < signature.length; i++) {
			int indiceOriginal = firstOne(original, i);
			signature[i] = (byte) indiceOriginal;
		}
		return signature;
	}

	public static void main(String[] args) {
		/** Création d'une matrice de test réglages peuvent être modifiés */
		double[][] spectro = CreationMatriceTest.MatriceTest(40, 50);
		spectro = CreationMatriceTest.RemplissageMatriceRandom(spectro, 0);
		System.out.println("Aperçu de la matrice de test");
		CreationMatriceTest.prettyMatrice(spectro);
		/**
		 * Commence le décommpte de la méthode
		 */
		// Long debut = System.currentTimeMillis();
		Long debut = System.nanoTime();

		/**
		 * Conversion de la matrice en bits
		 */
		boolean[] sparseVector = ConversionBits.conversiontoBits(spectro);
		System.out.println("Nombre de bits dans le vecteur : "
				+ sparseVector.length);
		// for(int i =0;i<ar.length;i=i+2){
		// System.out.print(ar[i]);
		// System.out.print(ar[i+1]);
		// System.out.println();
		// }

		/**
		 * Création de la signature de la matrice ( p : 50 ici)
		 */
		byte[] Signature = fingerprint(sparseVector, 50);

		/**
		 * Affiche la signature et des informations
		 */
		Long Fin = System.nanoTime();
		// Long Fin = System.currentTimeMillis();

		System.out.println("Ordre de Temps : 10^"
				+ ("" + (Fin - debut)).length() + " nanosecondes");
		System.out.println("Signature : ");
		for (int i = 0; i < Signature.length; i++) {
			System.out.print(Signature[i] + ",");
		}
	}
}
