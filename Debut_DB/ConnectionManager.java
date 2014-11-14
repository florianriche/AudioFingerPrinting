package audioWavePrint;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {
	
	//CTRL + SHIFT + O pour générer les imports
	  public static Connection Connect(){ 
		  Connection conn = null;
	    try {
	      Class.forName("org.postgresql.Driver");
	      System.out.println("Driver O.K.");

	      String url = "jdbc:postgresql://localhost:5432/AudioFingerprinting";
	      String user = "postgres";
	      String passwd = "a";

	      conn = DriverManager.getConnection(url, user, passwd);
	      System.out.println("Connexion effective !");         

	    } catch (Exception e) {
	      e.printStackTrace();
	    }     
	    return conn;

	  }
	

}
