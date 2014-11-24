package audiofile;

public class SystemUtils {

	/**
	 * Detect the host OS
	 * @return the OS family name
	 */
	public String detectOS(){
		String det = System.getProperty("os.name");
		if(det.contains("Windows")){
			return "Windows";
		}
		if(det.contains("Linux")){
			return "Linux";
		}
		else{
			return det;
		}
	}

}//end of class
