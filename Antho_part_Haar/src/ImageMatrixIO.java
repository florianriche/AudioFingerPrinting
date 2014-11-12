import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class ImageMatrixIO {
	public static BufferedImage matToImage(double[][] mat){
		int w = mat[0].length;
		int h = mat.length;
		
		BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		
		for( int i = 0 ; i < w ; i++){
			for (int j = 0 ; j < h ; j++){
				int value = (int) mat[i][j];
				int color = value << 16 | value << 8 | value;
				img.setRGB(i, j, color);
			}
		}
		return img;
	}
	
	public static double[][] imageToMat(BufferedImage img){
		// Initializing the matrix
		double[][] mat = new double[img.getHeight()][img.getWidth()];
		
		// Filling the matrix
		for (int i = 0 ; i < img.getWidth() ; i++){
			for (int j = 0 ; j < img.getHeight() ; j++){
				Color grayColor = new Color(img.getRGB(i,j));	
				mat[i][j] = (double) grayColor.getRed();
			}
		}
		
		return mat;
	}
	
	/*public static void main(String[] args) throws IOException{
		
		double mat[][]={{250.,0.0,0.0},{0.,0.,0.},{0.,0.,250.}};
		
		BufferedImage img = matToImage(mat);
		
		File outputfile = new File("output.png");
		ImageIO.write(img, "png", outputfile);
		
	}*/

}
