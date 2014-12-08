package database;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import process.Configuration;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.InvalidQueryException;

/**
 * 
 * @author Paco
 *
 */
public class AudioDatabase {

	public Cassandra cassandra = null;

	/**
	 * AudioDatabase Constructor
	 * 
	 * @param cassandra
	 */
	public AudioDatabase(Cassandra cassandra) {
		this.cassandra = cassandra;
	}

	/**
	 * Return the full byte signature of a row
	 * 
	 * @param id
	 * @return
	 */
	public byte[] selectFullSignatureofID(int id, int nbcol) {
		ArrayList<Byte> recompo = new ArrayList<Byte>();
		ResultSet res = cassandra
				.getResultRequest("SELECT * FROM fingerprints WHERE id = " + id
						+ ";");
		for (Row row : res) {

			for (int i = 0; i < nbcol; i++) {
				ByteBuffer bb = row.getBytes("fingerprint" + i);
				byte[] subfingerprint = new byte[bb.remaining()];
				bb.get(subfingerprint);
				for (byte b : subfingerprint) {
					recompo.add(b);
				}
				if (new Configuration().DEBUG_MODE) {
					System.out.println("ID_MUSIC= " + row.getInt("id_music"));
				}
			}
		}
		byte[] result = new byte[recompo.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = recompo.get(i);
		}
		return result;
	}

	/**
	 * 
	 * @param fingerprint
	 * @param nbcol
	 * @return
	 */
	public ArrayList<Integer> selectIdsmusicFromFingerprint(byte[] fingerprint,
			int nbcol) {
		ArrayList<Integer> idlist = new ArrayList<Integer>();
		for (int i = 0; i < nbcol; i++) {
			PreparedStatement ps = cassandra
					.prepareRequest("SELECT * FROM fingerprints WHERE fingerprint"
							+ i + " = ? ;");
			BoundStatement b = new BoundStatement(ps);
			b.setBytes(0, ByteBuffer.wrap(fingerprint));
			ResultSet res = cassandra.getResultRequest(b);
			for (Row row : res) {
				idlist.add(row.getInt("id_music"));
				if (new Configuration().DEBUG_MODE) {
					System.out.println("ID_MUSIC= " + row.getInt("id_music"));
				}
			}
		}
		return idlist;
	}

	/**
	 * Return the list of IDS/ID_musics linked a full fingerprint
	 * 
	 * @param Decomposition
	 * @return
	 */
	public ArrayList<HashMap<String, Integer>> selectIdsFromFingerprint(
			ArrayList<byte[]> Decomposition) {
		ArrayList<HashMap<String, Integer>> FullListOfIds = new ArrayList<HashMap<String, Integer>>();
		for (int i = 0; i < Decomposition.size(); i++) {
			ArrayList<HashMap<String, Integer>> tmp = selectIdsFromSubFingerprint(
					Decomposition.get(i), i);
			FullListOfIds.addAll(tmp);
		}
		return FullListOfIds;
	}

	/**
	 * Return the list of IDS/ID_musics linked to the subfingerprint in the
	 * specific column
	 * 
	 * @param fingerprint
	 * @param numcol
	 * @return
	 */
	public ArrayList<HashMap<String, Integer>> selectIdsFromSubFingerprint(
			byte[] fingerprint, int numcol) {
		ArrayList<HashMap<String, Integer>> idlist = new ArrayList<HashMap<String, Integer>>();
		PreparedStatement ps = cassandra
				.prepareRequest("SELECT * FROM fingerprints WHERE fingerprint"
						+ numcol + " = ? ;");
		BoundStatement b = new BoundStatement(ps);
		b.setBytes(0, ByteBuffer.wrap(fingerprint));
		ResultSet res = cassandra.getResultRequest(b);
		for (Row row : res) {
			HashMap<String, Integer> sublist = new HashMap<String, Integer>();
			sublist.put("ID", (row.getInt("id")));
			sublist.put("ID_MUSIC", (row.getInt("id_music")));
			if (new Configuration().DEBUG_MODE) {
				System.out.println("ID_MUSIC= " + row.getInt("id_music"));
			}
			idlist.add(sublist);

		}
		return idlist;
	}

	/**
	 * 
	 * @param idmusic
	 * @return
	 */
	public String selectSongFromMusics(int idmusic) {
		String output = "";
		ResultSet res = cassandra
				.getResultRequest("SELECT * FROM musics WHERE id_music = "
						+ idmusic);
		for (Row row : res) {
			output = row.getInt("id_music") + " " + row.getString("artist")
					+ " " + row.getString("title") + " " + row.getInt("length");
			if (new Configuration().DEBUG_MODE) {
				System.out.println("ID_MUSIC="+row.getInt("id_music")+" Artist="+row.getString("artist")+" Title="+row.getString("title")+" Length="+row.getInt("length"));
			}
		}
		return output;
	}

	/**
	 * Select only couples ID/ID_MUSICS whose ID_MUSIC is a potential match (>
	 * threshold)
	 * 
	 * @param Decomposition
	 * @param votes_threshold
	 * @return
	 */
	public ArrayList<HashMap<String, Integer>> getCandidates(
			ArrayList<byte[]> Decomposition, int votes_threshold) {
		
		ArrayList<HashMap<String, Integer>> AllCandidates = selectIdsFromFingerprint(Decomposition);
		HashMap<Integer, Integer> id_musics_counters = new HashMap<Integer, Integer>();
		
		// Comptage générale
		for (HashMap<String, Integer> candidate : AllCandidates) {
			int key = candidate.get("ID_MUSIC");
			if (id_musics_counters.containsKey(key)) {
				int oldValue = id_musics_counters.get(key);
				id_musics_counters.put(candidate.get("ID_MUSIC"), oldValue + 1);
			} else {
				id_musics_counters.put(candidate.get("ID_MUSIC"), 1);
			}
		}
		
		// Filtrage selon le threshold
		HashMap<Integer, Integer> filteredCounts = new HashMap<Integer, Integer>();
		for (int keys : id_musics_counters.keySet()) {
			if (id_musics_counters.get(keys) >= votes_threshold) {
				filteredCounts.put(keys, id_musics_counters.get(keys));
			}
		}
		
		// On ne laisse que les couples des chansons candidates :
		for (HashMap<String, Integer> candidate : AllCandidates) {
			if (!id_musics_counters.containsKey(candidate.get("ID_MUSIC"))) {
				AllCandidates.remove(candidate);
			}
		}
		return AllCandidates;
	}

	/**
	 * Return the ID_MUSIC of the best candidate
	 * 
	 * @param fullSignature
	 * @param l
	 * @param votes_threshold
	 * @return
	 */
	public int getIdFromBestCandidate(byte[] fullSignature, int l,
			int votes_threshold) {
		ArrayList<byte[]> decomposition = Utils.Decomposition(fullSignature, l);

		ArrayList<HashMap<String, Integer>> candidates = getCandidates(
				decomposition, votes_threshold);

		if(candidates.size()==0){
			System.out.println("No matching candidate found");
			return -1;
		}else{
		ArrayList<byte[]> candidatesSignature = new ArrayList<byte[]>();
		HashMap<Integer, Integer> idmusics = new HashMap<Integer, Integer>();

		int i = 0;
		for (HashMap<String, Integer> candidate : candidates) {
			byte[] tmpSig = selectFullSignatureofID(candidate.get("ID"), l);
			candidatesSignature.add(tmpSig);
			idmusics.put(i, candidate.get("ID_MUSIC"));
			i++;
		}
		int bestCandidate = FindBestCandidate(fullSignature,
				candidatesSignature);
		
		return idmusics.get(bestCandidate);}
	}

	/**
	 * Launch the retrieval process and return the best matchin row from the
	 * music database
	 * 
	 * @param fullSignature
	 * @param l
	 * @param votes_threshold
	 * @return
	 */
	public String getMatchingSong(byte[] fullSignature, int l,
			int votes_threshold) {
		if (l<votes_threshold) {
			if (new Configuration().DEBUG_MODE) {
				System.out
						.println("You must create at least one column, so nb must be >= 1");
			}
			return "";
		}
		int bestCandiate = getIdFromBestCandidate(fullSignature, l,
				votes_threshold);
		if (bestCandiate==-1){
			System.out.println("No matching candidate found");
			return "";
		}
		else{
		String bestSong = selectSongFromMusics(bestCandiate);
		return bestSong;}
	}

	/**
	 * Find the byte array with the best proximity from a number of candidates
	 * 
	 * @param Reference
	 * @param ListToCompare
	 * @return
	 */
	public int FindBestCandidate(byte Reference[],
			ArrayList<byte[]> ListToCompare) {
		int currentmax = 0;
		int current = 0;
		int imax = 0;
		for (int i = 0; i < ListToCompare.size(); i++) {
			current = Utils.Proximite(Reference, ListToCompare.get(i));
			if (current > currentmax) {
				imax = i;
				currentmax = current;
			}
		}
		return imax;
	}

	/**
	 * 
	 * @param numfingerprint
	 */
	public void addFingerprintColumn(int numfingerprint) {
		addColumn("fingerprints", "fingerprint" + numfingerprint, "blob", true);
	}

	/**
	 * Insert a full Fingerprint into the fingerprint database
	 * 
	 * @param id
	 * @param idmusic
	 * @param Decomposition
	 */
	public void insertFingerprint(int id, int idmusic,
			ArrayList<byte[]> Decomposition) {
		for (int l = 0; l < Decomposition.size(); l++) {
			insertOneSubFingerprint(id, idmusic, Decomposition.get(l), l);
		}
	}

	/**
	 * Insert one fingerprint for a music into the fingerprints tables
	 * 
	 * @param id
	 * @param idmusic
	 * @param fingerprint
	 * @param numfingerprint
	 */
	public void insertOneSubFingerprint(int id, int idmusic,
			byte[] fingerprint, int numfingerprint) {
		try {
			cassandra.executeRequest("SELECT fingerprint" + numfingerprint
					+ " FROM fingerprints;");
		} catch (InvalidQueryException e) {
			// System.out.println("The column of fingerprint is not available");
			addColumn("fingerprints", "fingerprint" + numfingerprint, "blob",
					true);
			insertOneSubFingerprint(id, idmusic, fingerprint, numfingerprint);
			return;
		}
		PreparedStatement ps = cassandra
				.prepareRequest("INSERT INTO fingerprints (id,id_music,fingerprint"
						+ numfingerprint + ") VALUES (?,?,?)");
		BoundStatement b = new BoundStatement(ps);
		b.setInt(0, id);
		b.setInt(1, idmusic);
		b.setBytes(2, ByteBuffer.wrap(fingerprint));
		cassandra.executeRequest(b);
		if (new Configuration().DEBUG_MODE) {
			System.out.println("Fingerprint added : "+id+","+idmusic+","+fingerprint);
		}
	}

	/**
	 * Insert a row into musics table
	 * 
	 * @param idmusic
	 * @param title
	 * @param artist
	 * @param length
	 */
	public void insertMusics(int idmusic, String title, String artist,
			int length) {
		title = "'" + title + "'";
		artist = "'" + artist + "'";
		cassandra
				.executeRequest("INSERT INTO musics (ID_music, title, artist, length) VALUES ("
						+ idmusic
						+ ","
						+ title
						+ ","
						+ artist
						+ ","
						+ length
						+ ");");
		if (new Configuration().DEBUG_MODE) {
			System.out.println("Music added : "+idmusic+","+title+","+artist+","+length);
		}
	}

	/**
	 * Add a column to a table
	 * 
	 * @param table
	 * @param name
	 * @param type
	 */
	public void addColumn(String table, String name, String type, boolean index) {
		cassandra.executeRequest("ALTER TABLE " + table + " ADD " + name + " "
				+ type + ";");
		if (new Configuration().DEBUG_MODE) {
			System.out.println("The table " + table + " has a new column "
					+ name + " of type " + type);
		}
		if (index) {
			createIndexColumn(table, name);
		}
	}

	/**
	 * Create the index for a column (useful for 'select where' research)
	 * 
	 * @param table
	 * @param column
	 */
	public void createIndexColumn(String table, String column) {
		cassandra.executeRequest("CREATE INDEX ON " + table + " (" + column
				+ ");");
		if (new Configuration().DEBUG_MODE) {
			System.out.println("Index created for " + column + " on " + table);
		}
	}

	/**
	 * Create both tables
	 * 
	 * @param nbfingerprints
	 */
	public void createTables(int nbfingerprints) {
		createTableMusics();
		createTableFingerprints(nbfingerprints);
	}

	/**
	 * Create the musics table
	 */
	public void createTableMusics() {
		if (existTable("musics")) {
			dropTable("musics");
		}
		cassandra
				.executeRequest("CREATE TABLE musics (ID_music int, title text, artist text, length int,PRIMARY KEY (ID_music));");
		if (new Configuration().DEBUG_MODE) {
			System.out.println("musics table created !");
		}
	}

	/**
	 * Create the fingerprints table
	 * 
	 * @param nb
	 */
	public void createTableFingerprints(int nb) {
		if (nb == 0) {
			if (new Configuration().DEBUG_MODE) {
				System.out
						.println("You must create at least one column, so nb must be >= 1");
			}
			return;
		}
		if (existTable("fingerprints")) {
			dropTable("fingerprints");
		}
		String finger = "";
		ArrayList<String> fingerlist = new ArrayList<String>();
		for (int i = 0; i < nb; i++) {
			finger += " fingerprint" + i + " blob,";
			fingerlist.add("fingerprint" + i);
		}
		cassandra
				.executeRequest("CREATE TABLE fingerprints (ID int, ID_music int,"
						+ finger + "PRIMARY KEY (ID));");
		if (new Configuration().DEBUG_MODE) {
			System.out.println("fingerprints table created !");
		}
		for (int j = 0; j < fingerlist.size(); j++) {
			createIndexColumn("fingerprints", fingerlist.get(j));
		}
	}

	/**
	 * Drop a table
	 * 
	 * @param table
	 */
	public void dropTable(String table) {
		cassandra.executeRequest("DROP TABLE " + table + ";");
		if (new Configuration().DEBUG_MODE) {
			System.out.println("table " + table + " is removed !");
		}
	}

	/**
	 * Verify the existance of the table
	 * 
	 * @param table
	 * @return
	 */
	public boolean existTable(String table) {
		boolean res = false;
		try {
			cassandra.executeRequest("SELECT * FROM " + table + ";");
			res = true;
		} catch (InvalidQueryException e) {
			res = false;
		}
		return res;
	}

}// end of class
