import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Demo {

	public static void main(String[] args) throws IOException {
		
		double[] data = {9, 7, 3, 5 };
		HaarWavelet1D oneD = new HaarWavelet1D(data);
		
		
		System.out.println("Processing One Dimensional Haar Wavelet Transformation");
		System.out.println("Input : ");
		oneD.show();
		oneD.standardDecomposition();
		System.out.println("Output : ");
		oneD.show();
		
		// 2D
		
		double[][] data2D = {{9, 7, 3, 5 },{2, 4, 8, 4 },{1, 7, 13, 4 },{19, 27, 53, 45 }};
		
		HaarWavelet2D twoD = new HaarWavelet2D(data2D);
		System.out.println("Processing Two Dimensional Haar Wavelet Transformation");
		System.out.println("Input : ");
		twoD.show();
		System.out.println("Output : ");
		twoD.standardDecomposition();
		twoD.show();
		/*
		// With an image :
		
		File inputImage = new File("mona.jpg");
		BufferedImage image = ImageIO.read(inputImage);
		
		double[][] imageData = ImageMatrixIO.imageToMat(image);
		HaarWavelet2D mat = new HaarWavelet2D(imageData);
		
		mat.standardDecomposition();
		mat.show();
		double[][] result = mat.getMatrix();
		
		ImageIO.write(ImageMatrixIO.matToImage(result), "png", new File("output.png"));*/
		
	}	

}
