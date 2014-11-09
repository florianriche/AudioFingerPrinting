package audioWavePrint;

public class CreationMatriceTest {

	/** Méthodes pour tester les fonctions */

	public static double[][] MatriceTest(int nbColonne, int nbLignes) {
		return new double[nbColonne][nbLignes];
	}

	/** Création d'une matrice aléatoire ou fixe : */

	public static double[][] RemplissageMatriceRandom(double[][] spectogramme,
			int coefficient) {
		for (int i = 0; i < spectogramme.length; i++) {
			for (int j = 0; j < spectogramme[i].length; j++) {
				spectogramme[i][j] = Math.rint(2 * (Math.random() - 0.5));
				/**
				 * Décommenter la ligne dessous pour vérifier que la signature
				 * est bien toujours la même pour une matrice donnée
				 */
				// spectogramme[i][j] = (i==j)?1:0;
				spectogramme[i][j] = (i > j) ? 1 : -1;

			}
		}
		return spectogramme;

	}

	/** Sert juste à afficher la matrice */
	public static void prettyMatrice(double[][] matrice) {
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice[i].length; j++) {
				System.out.print(matrice[i][j] + "	");
			}
			System.out.println();
		}
	}
}
