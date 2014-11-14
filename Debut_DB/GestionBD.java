package audioWavePrint;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GestionBD {

	// Remplissage des tables
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

	public static boolean existe(Connection connection, String nomTable)
			throws SQLException {
		boolean existe;
		DatabaseMetaData dmd = connection.getMetaData();
		ResultSet tables = dmd.getTables(connection.getCatalog(), null,
				nomTable, null);
		existe = tables.next();
		tables.close();
		return existe;
	}

	public static void CreationTables(int l) throws SQLException {
		Connection C = ConnectionManager.Connect();
		if (existe(C,"SIGNATURES")) {
			Statement statebis = C.createStatement();
			String querybis = "DROP TABLE \"SIGNATURES\"";
			System.out.println(querybis);

			statebis.execute(querybis);
		}
		Statement state = C.createStatement();
		String fingerprints_columns = "";
		for (int i = 0; i < l; i++) {
			fingerprints_columns = fingerprints_columns
					+ "Signature_Fingerprint" + i
					+ " bytea NOT NULL,";
		}
		String query = "CREATE TABLE \"SIGNATURES\" " + '('
				+ "ID SERIAL,"
				+ " Signature_IDMovie integer NOT NULL,"
				+ fingerprints_columns + " PRIMARY KEY (ID) )";
		System.out.println(query);
		//
		state.execute(query);

	}

	public static void RemplissageDesTables(ArrayList<byte[]> Décomposition,
			int l , int LeMovie) throws SQLException {
		Connection C = ConnectionManager.Connect();

		String valuestoInsert="(";
		String selectedfields = "(Signature_IDMovie";
		for(int i =  0 ; i < l ; i ++){
			valuestoInsert =valuestoInsert+ "? ,";
			selectedfields = selectedfields + ",Signature_Fingerprint" + i;
		}
		valuestoInsert = valuestoInsert + "?)";
		selectedfields = selectedfields +")";
		System.out.println(valuestoInsert);
//		String query = 
		PreparedStatement ps = C.prepareStatement("INSERT INTO \"SIGNATURES\" "+selectedfields+" VALUES "+valuestoInsert);
		System.out.println(ps);
//		ps.setInt(1, 0);
		ps.setInt(1, LeMovie);

		for(int i =  0 ; i < l ; i ++){
			System.out.println((i+2));
			ps.setBytes((i+2), Décomposition.get(i));
		}
		System.out.println(ps);
		
		ps.executeUpdate();
		ps.close();
		
		
	}

	// Processus pour retrouver la musique correspondante
	public static ArrayList<Integer> moviesToCompare(byte[] Signature, int l,
			int seuilVotes) throws SQLException {
		ArrayList<byte[]> Decompo = Decomposition(Signature, l);
		HashMap<Integer, Integer> MovieScore = new HashMap<Integer, Integer>();
		ArrayList<Integer> CurrentMovies = new ArrayList<Integer>();
		for (int i = 0; i < l; i++) {
			CurrentMovies = findMovieInTable(Decompo.get(i), i);
			for (int movie : CurrentMovies) {
				if (MovieScore.containsKey(movie)) {
					MovieScore.put(movie, MovieScore.get(movie) + 1);
				} else {
					MovieScore.put(movie, 1);
				}
			}
		}

		ArrayList<Integer> FinalMovies = new ArrayList<Integer>();
		for (Integer i : MovieScore.keySet()) {
			if (MovieScore.get(i) >= seuilVotes)
				FinalMovies.add(i);
		}
		return FinalMovies;
	}

	public static ArrayList<Integer> findMovieInTable(byte[] subSignature,
			int numero_Signature) throws SQLException {
		Connection C = ConnectionManager.Connect();
		ArrayList<Integer> retrievedMusics = new ArrayList<Integer>();
		PreparedStatement ps = C.prepareStatement("SELECT signature_idmovie"
				+ " FROM \"SIGNATURES\" "
				+ "WHERE Signature_Fingerprint" + numero_Signature+" = ?");
		ps.setBytes(1, subSignature);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			retrievedMusics.add(rs.getInt(1));
		}
		System.out.println(retrievedMusics);
		return retrievedMusics;
	}

	public static byte[] RetrieveSignatureInTable(int music, int numero_Signature) throws SQLException {
		Connection C = ConnectionManager.Connect();
		 byte[] subFingerprint = null;
		PreparedStatement ps = C.prepareStatement("SELECT Signature_Fingerprint" + numero_Signature+""
				+ " FROM \"SIGNATURES\" "
				+ "WHERE signature_idmovie = ?");
		ps.setInt(1	, music);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
		  subFingerprint = rs.getBytes(1);
		  for(int i  = 0 ; i< subFingerprint.length;i++){
				System.out.print(subFingerprint[i]);
			}
		  System.out.println();
		 		}
		rs.close();
		ps.close();
		return subFingerprint;
	}

	public byte[] RetrieveFullSignature(int movie, int l) throws SQLException {
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

	public int RetrievalProcess(byte[] Signature, int l, int seuilVotes) throws SQLException {
		ArrayList<Integer> movies = moviesToCompare(Signature, l, seuilVotes);
		ArrayList<byte[]> listSignatures = new ArrayList<byte[]>();
		for (int movie : movies) {
			listSignatures.add(RetrieveFullSignature(movie, l));
		}
		return FindMatch(Signature, listSignatures);
	}

	public static void main(String[] args) throws SQLException {
//		for(int n  = 0  ; n< 5;n++){
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
		//Creation table entraine aussi la destruction
//		CreationTables(8);
		Random R= new Random();
		 RemplissageDesTables(deco, 8 ,5);
System.out.println("yolo");
	byte[] a = RetrieveSignatureInTable(5,2);
	byte[] b = RetrieveSignatureInTable(5,3);

	ArrayList<Integer> A = findMovieInTable(a, 2);
	ArrayList<Integer> B = findMovieInTable(b, 3);
System.out.println(A);
System.out.println(B);
	
	}
//	}
}
