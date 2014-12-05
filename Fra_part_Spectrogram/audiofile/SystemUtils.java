package audiofile;

/**
 * 
 * @author Paco
 *
 */
public class SystemUtils {

	/**
	 * Detect Mac OS
	 * @return
	 */
	public boolean isMac(){
		boolean res = false;
		if(detectOS().equals("Mac")){
			res = true;
		}
		else{
			res = false;
		}
		return res;
	}
	
	/**
	 * Detect Linux OS
	 * @return
	 */
	public boolean isLinux(){
		boolean res = false;
		if(detectOS().equals("Linux")){
			res = true;
		}
		else{
			res = false;
		}
		return res;
	}
	
	/**
	 * Detect Windows OS
	 * @return
	 */
	public boolean isWindows(){
		boolean res = false;
		if(detectOS().equals("Windows")){
			res = true;
		}
		else{
			res = false;
		}
		return res;
	}
	
	/**
	 * Detect the host OS
	 * @return the OS family name
	 */
	public String detectOS(){
		String det = System.getProperty("os.name").toLowerCase();
		if(det.contains("windows")){
			return "Windows";
		}
		else if(det.contains("linux")){
			return "Linux";
		}
		else if(det.contains("mac")){
			return "Mac";
		}
		else{
			return det;
		}
	}

}//end of class
