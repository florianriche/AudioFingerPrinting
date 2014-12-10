package process;

import java.io.IOException;
import java.util.ArrayList;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		ArrayList<BigProcess> threadlist = new ArrayList<BigProcess>();
		String[] musics = {"noise.mp3","Metronomy-TheBay.mp3"};
		//for(int i=0;i<musics.length;i++){
		for(int i=1;i<2;i++){
			BigProcess audioprocess = new BigProcess(i+1,musics[i]);
			threadlist.add(audioprocess);
			audioprocess.start();	
		}
		for(int j=0;j<threadlist.size();j++){
			threadlist.get(j).join();
		}
		//System.out.println("Duration of Audio Process : "+audioprocess.getDurationProcess()+"ms");
	}

}
