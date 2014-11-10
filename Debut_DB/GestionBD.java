package audioWavePrint;

import java.util.ArrayList;
import java.util.HashMap;

public class GestionBD {

	//Remplissage des tables
	public static ArrayList<byte[]> Decomposition(byte[] subFingerprint, int l) {
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

	public static void RemplissageDesTables(ArrayList<byte[]> Décomposition,
			int l) {
		// TODO
	}

	
	
	//Processus pour retrouver la musique correspondante
	public static ArrayList<Integer> moviesToCompare(byte[] Signature, int l,
			int seuilVotes) {
		ArrayList<byte[]> Decompo = Decomposition(Signature, l);
		HashMap<Integer,Integer> MovieScore = new HashMap<Integer,Integer>();
		ArrayList<Integer> CurrentMovies = new ArrayList<Integer>();
		for(int i = 0 ;i< l ; i ++){
			CurrentMovies=findMovieInTable(Decompo.get(i), i);
			for(int movie:CurrentMovies){
				if(MovieScore.containsKey(movie)){
					MovieScore.put(movie, MovieScore.get(movie)+1);}
				else{
					MovieScore.put(movie, 1);
				}
			}
		}
		
		ArrayList<Integer> FinalMovies = new ArrayList<Integer>();
		for(Integer i: MovieScore.keySet()){
			if(MovieScore.get(i)>=seuilVotes)FinalMovies.add(i);
		}
		return FinalMovies;
	}
	
	public static ArrayList<Integer> findMovieInTable(byte[] subSignature, int numero_table) {
		return null;
	}

	public byte[] RetrieveSignatureInTable(int movie, int numero_table) {
		// TODO
		return null;
	}

	public byte[] RetrieveFullSignature(int movie, int l) {
		ArrayList<Byte> listSignature = new ArrayList<Byte>();
		byte[] tmp = null;
		for (int i = 0; i < l; i++) {
			tmp = RetrieveSignatureInTable(movie, i);
			for (byte b : tmp) {
				listSignature.add(b);
			}
		}
		byte[] FullSignature = new byte[listSignature.size()];
		for (int i = 0; i < listSignature.size(); i++) {
			FullSignature[i] = listSignature.get(i);
		}
		return FullSignature;
	}

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

	public static int FindMatch(byte Reference[],
			ArrayList<byte[]> ListToCompare) {
		int currentmax = 0;
		int current = 0;
		int imax = 0;
		for (int i = 0; i < ListToCompare.size(); i++) {
			current = Proximite(Reference, ListToCompare.get(i));
			if (current > currentmax) {
				imax = i;
				currentmax = current;
			}
		}
		return imax;
	}

	public int RetrievalProcess(byte[] Signature, int l, int seuilVotes) {
		ArrayList<Integer> movies = moviesToCompare(Signature, l, seuilVotes);
		ArrayList<byte[]> listSignatures = new ArrayList<byte[]>();
		for (int movie : movies) {
			listSignatures.add(RetrieveFullSignature(movie, l));
		}
		return FindMatch(Signature, listSignatures);
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
		byte[] signature = Signature.fingerprint(sparseVector, 50);

		/**
		 * Affiche la signature et des informations
		 */
		Long Fin = System.nanoTime();
		// Long Fin = System.currentTimeMillis();

		System.out.println("Ordre de Temps : 10^"
				+ ("" + (Fin - debut)).length() + " nanosecondes");
		System.out.println("Signature : ");
		for (int i = 0; i < signature.length; i++) {
			System.out.print(signature[i] + "");
		}

		ArrayList<byte[]> deco = Decomposition(signature, 8);

		/**
		 * Ecriture de la décomposition :
		 */
		System.out.println();

		for (byte[] b : deco) {
			System.out.print(b.length + " : ");
			for (byte bibi : b) {
				System.out.print(bibi);
			}
			System.out.println();
		}

		/**
		 * Ecriture dans les DB
		 */

	}
}
