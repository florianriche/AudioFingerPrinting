package database;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.utils.Bytes;

public class Testdb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cassandra cassandra = new Cassandra();
		cassandra.connectToKeyspace("127.0.0.1", "plop");
		AudioDatabase db = new AudioDatabase(cassandra);
		
		//db.createTables(5);
		//db.insertMusics(1, "tructitle", "batman", 148);
		//db.insertOneFingerprint(1, 1, "AAAAAAAADGDHEjGHEJGZ64573", 2);
		//db.insertOneFingerprint(4, 4, "AAAAAAAADGDHEjGHEJGZ64573", 1);
		//db.selectIdsmusicFromFingerprint("AAAAAAAADGDHEjGHEJGZ64573",5);
		//db.selectSongFromMusics(1);
		/*byte[] truc = {1,2,3,45,66,79,-127};
		ByteBuffer trucc = ByteBuffer.wrap(truc);
		PreparedStatement ps = cassandra.session.prepare("INSERT INTO signaturebis (id,sign) VALUES (2,?)");
		BoundStatement b = new BoundStatement(ps);
		b.setBytes(0, trucc);
		cassandra.session.execute(b);*/
		PreparedStatement ps = cassandra.session.prepare("SELECT * FROM signaturebis");
		BoundStatement b = new BoundStatement(ps);
		ResultSet res = cassandra.session.execute(b);
		for(Row row:res){
			System.out.println(Bytes.toHexString(row.getBytes("sign")));
			System.out.println(Bytes.getArray(row.getBytes("sign")));
			byte[] tr = Bytes.getArray(row.getBytes("sign"));
			for(int i=0;i<tr.length;i++){
				System.out.print(tr[i]);
			}System.out.println();
		}
		
		
		cassandra.closeConnection();
	}

}
