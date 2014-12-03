package process;

import java.io.IOException;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		BigProcess audioprocess = new BigProcess();
		audioprocess.computeTheProcess("noise.mp3");
		System.out.println("Duration of Audio Process : "+audioprocess.getDurationProcess()+"ms");
	}

}
