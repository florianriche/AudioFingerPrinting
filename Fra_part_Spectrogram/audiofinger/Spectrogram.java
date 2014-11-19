package audiofinger;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.imageio.ImageIO;

import org.tc33.jheatchart.HeatChart;

/**
 * 
 * @author Paco
 *
 */
public class Spectrogram {

	public LinkedHashMap<Integer,Double> zones = new LinkedHashMap<Integer,Double>();
	
	public int accuracy = 1; //1, 2, or 4
	public boolean writePicture = false; //if true, generate the picture of the spectrogram
	public long durationSpectrogram = 0; // execution time of the spectrogram
	
	public double[][] spectrogram; //the spectrogram table
	
	public void generateSpectrogram(STFT stft, String filename) throws IOException{
		//store start time to calculate execution time
		long startTime = System.nanoTime();
		
		//instanciate new spectrogram
		int nbzones = (stft.getFreqMax()-stft.getFreqMin())/10;
		Set cles = stft.getTimeFreqMagn().keySet();
		spectrogram = new double[nbzones*accuracy][cles.size()];
		
		Iterator it = cles.iterator();
		int iter = 0;			
		while(it.hasNext()){
			Object cle = it.next();
			Object[] freq = stft.getTimeFreqMagn().get(cle).keySet().toArray();//get frequencies
			Object[] magn = stft.getTimeFreqMagn().get(cle).values().toArray();//get magnitudes
			calculateZones(stft,freq,magn);//determine frequency zones
			Object[] valzones = zones.values().toArray();			
			for(int v=0;v<valzones.length;v++){
				spectrogram[v][iter] = (double)valzones[v];//fill the table
			}
			iter++;
		}//end of while
		
		long endTime = System.nanoTime();
		setDurationSpectrogram((endTime - startTime) / 1000000);
		System.out.println("End generation spectrogram !");
		
		if(writePicture){
			generatePicture(filename,spectrogram);//Heatmap
		}
	}
	
	/**
	 * Generate the spectrogram picture (cropped)
	 * @param filename
	 * @throws IOException
	 */
	public void generatePicture(String filename, double[][] spec) throws IOException{
		HeatChart map = new HeatChart(spec);
		map.setHighValueColour(Color.RED);
		map.setColourScale(1);
		BufferedImage img = (BufferedImage) map.getChartImage();
		BufferedImage img2 = flipHorizontal(img);
		img2 = cropImage(img2,new Rectangle(img2.getWidth(),img2.getHeight()/4),0,img2.getHeight()-(img2.getHeight()/4));
		ImageIO.write(img2, "PNG", new File(filename+".png"));
		System.out.println("End generation picture !");
	}
	
	/**
	 * Crop a picture
	 * @param src
	 * @param rect
	 * @param x
	 * @param y
	 * @return
	 */
	public BufferedImage cropImage(BufferedImage src, Rectangle rect, int x, int y) {
		BufferedImage dest = src.getSubimage(x, y, rect.width, rect.height);
	    return dest; 
	}
	
	/**
	 * Determine the number of frequency zones and calculate the magnitude average for each
	 * @param stft
	 * @param freq
	 * @param magn
	 */
	public void calculateZones(STFT stft, Object[] freq, Object[] magn){
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
	 * Flip horizontaly a picture
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

	/**
	 * 
	 * @return
	 */
	public long getDurationSpectrogram() {
		return durationSpectrogram;
	}

	/**
	 * 
	 * @param durationSpectrogram
	 */
	public void setDurationSpectrogram(long durationSpectrogram) {
		this.durationSpectrogram = durationSpectrogram;
	}

	/**
	 * 
	 * @return
	 */
	public double[][] getSpectrogram() {
		return spectrogram;
	}

}//end of class
