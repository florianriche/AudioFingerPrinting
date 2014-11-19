package database;

import java.util.ArrayList;

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
	 * @param cassandra
	 */
	public AudioDatabase(Cassandra cassandra) {
		this.cassandra = cassandra;
	}
	
	/**
	 * 
	 * @param fingerprint
	 * @param nbcol
	 * @return
	 */
	public ArrayList<Integer> selectIdsmusicFromFingerprint(String fingerprint, int nbcol){
		ArrayList<Integer> idlist = new ArrayList<Integer>();
		fingerprint = "'"+fingerprint+"'";
		for(int i=0;i<nbcol;i++){
			ResultSet res = cassandra.getResultRequest("SELECT id_music FROM fingerprints WHERE fingerprint"+i+" = "+fingerprint+";");
			for (Row row : res) {
				idlist.add(row.getInt("id_music"));
				System.out.println(row.getInt("id_music"));
			}
		}
		return idlist;
	}
	
	/**
	 * 
	 * @param idmusic
	 * @return
	 */
	public String selectSongFromMusics(int idmusic){
		String output = "";
		ResultSet res = cassandra.getResultRequest("SELECT * FROM musics WHERE id_music = "+idmusic);
		for (Row row : res) {
			output = row.getInt("id_music")+" "+row.getString("artist")+" "+row.getString("title")+" "+row.getInt("length");
			System.out.println(row.getInt("id_music")+" "+row.getString("artist")+" "+row.getString("title")+" "+row.getInt("length"));
		}
		return output;
	}
		
	/**
	 * Insert one fingerprint for a music into the fingerprints tables
	 * @param id
	 * @param idmusic
	 * @param fingerprint
	 * @param numfingerprint
	 */
	public void insertOneFingerprint(int id, int idmusic, String fingerprint, int numfingerprint){
		try{
			cassandra.executeRequest("SELECT fingerprint"+numfingerprint+" FROM fingerprints;");
		}
		catch(InvalidQueryException e){
			System.out.println("The column of fingerprint is not available");
			return;
		}
		fingerprint = "'"+fingerprint+"'";
		cassandra.executeRequest("INSERT INTO fingerprints (ID, ID_music, fingerprint"+numfingerprint+") VALUES ("+id+","+idmusic+","+fingerprint+");");
		if(new Configuration().DEBUG_MODE){
			System.out.println("Fingerprint added : "+id+","+idmusic+","+fingerprint);
		}
	}
	
	/**
	 * Insert a row into musics table
	 * @param idmusic
	 * @param title
	 * @param artist
	 * @param length
	 */
	public void insertMusics(int idmusic, String title, String artist, int length){
		title = "'"+title+"'";
		artist = "'"+artist+"'";
		cassandra.executeRequest("INSERT INTO musics (ID_music, title, artist, length) VALUES ("+idmusic+","+title+","+artist+","+length+");");
		if(new Configuration().DEBUG_MODE){
			System.out.println("Music added : "+idmusic+","+title+","+artist+","+length);
		}
	}
	
	/**
	 * Add a column to a table
	 * @param table
	 * @param name
	 * @param type
	 */
	public void addColumn(String table, String name, String type, boolean index){
		cassandra.executeRequest("ALTER TABLE "+table+" ADD "+name+" "+type+";");
		if(new Configuration().DEBUG_MODE){
			System.out.println("The table "+table+" has a new column "+name+" of type "+type);
		}
		if(index){
			createIndexColumn(table, name);
		}
	}
	
	/**
	 * Create the index for a column (useful for 'select where' research)
	 * @param table
	 * @param column
	 */
	public void createIndexColumn(String table, String column){
		cassandra.executeRequest("CREATE INDEX ON "+table+" ("+column+");");
		if(new Configuration().DEBUG_MODE){
			System.out.println("Index created for "+column+" on "+table);
		}
	}
	
	/**
	 * Create both tables
	 * @param nbfingerprints
	 */
	public void createTables(int nbfingerprints){
		createTableMusics();
		createTableFingerprints(nbfingerprints);
	}

	/**
	 * Create the musics table
	 */
	public void createTableMusics(){
		if(existTable("musics")){
			dropTable("musics");
		}
		cassandra.executeRequest("CREATE TABLE musics (ID_music int, title text, artist text, length int,PRIMARY KEY (ID_music));");
		if(new Configuration().DEBUG_MODE){
			System.out.println("musics table created !");
		}
	}
	
	/**
	 * Create the fingerprints table
	 * @param nb
	 */
	public void createTableFingerprints(int nb){
		if(nb==0){
			if(new Configuration().DEBUG_MODE){
				System.out.println("You must create at least one column, so nb must be >= 1");
			}	
			return;
		}
		if(existTable("fingerprints")){
			dropTable("fingerprints");
		}
		String finger = "";
		ArrayList<String> fingerlist = new ArrayList<String>();
		for(int i=0;i<nb;i++){
			finger += " fingerprint"+i+" text,";
			fingerlist.add("fingerprint"+i);
		}
		cassandra.executeRequest("CREATE TABLE fingerprints (ID int, ID_music int,"+finger+"PRIMARY KEY (ID));");
		if(new Configuration().DEBUG_MODE){
			System.out.println("fingerprints table created !");
		}
		for(int j=0;j<fingerlist.size();j++){
			createIndexColumn("fingerprints",fingerlist.get(j));
		}
	}
	
	/**
	 * Drop a table
	 * @param table
	 */
	public void dropTable(String table){
		cassandra.executeRequest("DROP TABLE "+table+";");
		if(new Configuration().DEBUG_MODE){
			System.out.println("table "+table+" is removed !");
		}
	}
	
	/**
	 * Verify the existance of the table
	 * @param table
	 * @return
	 */
	public boolean existTable(String table){
		boolean res = false;
		try{
			cassandra.executeRequest("SELECT * FROM "+table+";");
			res = true;
		}
		catch(InvalidQueryException e){
			res = false;
		}
		return res;
	}

}//end of class
