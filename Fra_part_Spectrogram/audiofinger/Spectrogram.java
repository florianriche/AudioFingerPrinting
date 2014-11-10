package audiofinger;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.imageio.ImageIO;

public class Spectrogram {

	public LinkedHashMap<Integer,Double> zones = new LinkedHashMap<Integer,Double>();
	public LinkedHashMap<Double,Integer> unique = new LinkedHashMap<Double,Integer>();
	public LinkedHashMap<Integer, Color> colorzones = new LinkedHashMap<Integer,Color>();
	public ArrayList<LinkedHashMap> spec = new ArrayList<LinkedHashMap>();
	
	public int accuracy = 1; //1, 2, 4 or 8
	public boolean writePicture = false;
	//public double[][] spectrogram = new double[32768][2000];
	
	/**
	 * 
	 * @param stft
	 * @param filename
	 * @throws IOException
	 */
	public void generateSpectrogram(STFT stft, String filename) throws IOException{
		Set cles = stft.getTimeFreqMagn().keySet();
		Iterator it = cles.iterator();
		int iter = 0;	
		while(it.hasNext()){
			Object cle = it.next();
			Object[] freq = stft.getTimeFreqMagn().get(cle).keySet().toArray();
			Object[] magn = stft.getTimeFreqMagn().get(cle).values().toArray();
			fillZones(stft,freq,magn);
			Object[] valzones = zones.values().toArray();			
			fillUnique(zones);
			fillColors(valzones, zones, unique);
			iter++;
			spec.add(colorzones);
		}//end of while
		
		System.out.println("End color list");	
		System.out.println("Size list color: "+spec.get(0).size());

		if(writePicture){ 
			generatePicture(spec, filename);
		}
		
	}
	
	/**
	 * 
	 * @param spec
	 * @param filename
	 * @throws IOException
	 */
	public void generatePicture(ArrayList<LinkedHashMap> spec, String filename) throws IOException{
		BufferedImage img = new BufferedImage(spec.size(), spec.get(0).size(),BufferedImage.TYPE_INT_RGB);
		for(int u=0;u<spec.size();u++){
			for(int uu=0;uu<spec.get(0).size();uu++){
				Color truc = (Color)spec.get(u).get(uu);		
				img.setRGB(u, uu, truc.getRGB());
			}
		}
		BufferedImage img2 = flipHorizontal(img); 
		ImageIO.write(img2, "PNG", new File(filename+".png"));
	}
	
	/**
	 * 
	 * @param stft
	 * @param freq
	 * @param magn
	 */
	public void fillZones(STFT stft, Object[] freq, Object[] magn){
		int nbzones = (stft.getFreqMax()-stft.getFreqMin())/10;
		for(int i=0;i<nbzones*accuracy;i++){
			int zonebegin = i*(100/accuracy)+stft.getFreqMin();
			int zoneend = i*(100/accuracy)+stft.getFreqMin()+(100/accuracy);
			double res = 0;
			double res2 = 0;
			for(int j=0;j<freq.length;j++){
				if((double)freq[j]>=zonebegin && (double)freq[j]<=zoneend){
					res += (double)magn[j];
					res2++;
				}
			}
			if(res2==0){res2=1;}//avoid infinite value
			zones.put(i,res/res2);

		}
	}
	
	/**
	 * 
	 * @param zones
	 */
	public void fillUnique(LinkedHashMap zones){
		ArrayList<Double> order = new ArrayList<Double>(zones.values());
		Set<Double> un = new HashSet<Double>(order);
		order.clear();
		order.addAll(un);
		Collections.sort(order,Collections.reverseOrder());
		int degrade = (int) 255 / order.size();
		for(int p=0;p<order.size();p++){
			if(p==0){
				unique.put(order.get(p), p);
			}
			else{
				unique.put(order.get(p), p+degrade);
			}	
		}
	}
	
	/**
	 * 
	 * @param valzones
	 * @param zones
	 * @param unique
	 */
	public void fillColors(Object[] valzones, LinkedHashMap zones, LinkedHashMap unique){
		for(int t=0;t<zones.size()-1;t++){
			Color c = new Color(255-(int)unique.get((double)valzones[t]),255-(int)unique.get((double)valzones[t]),255-(int)unique.get((double)valzones[t]));
			colorzones.put(t, c);
		}
	}
	
	/**
	 * Flip horizontaly the picture
	 * @param src
	 * @return
	 */
	public BufferedImage flipHorizontal(BufferedImage src){
	    AffineTransform tx=AffineTransform.getScaleInstance(1.0,-1.0);  //scaling
	    tx.translate(0,-src.getHeight());  //translating
	    AffineTransformOp tr=new AffineTransformOp(tx,null);  //transforming	   
	    return tr.filter(src, null);  //filtering
	}

	/**
	 * 
	 * @param writePicture
	 */
	public void setWritePicture(boolean writePicture) {
		this.writePicture = writePicture;
	}

	/**
	 * Set Accuracy of the spectrogram picture
	 * @param accuracy
	 */
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
	
	
	
	
}//end of class
