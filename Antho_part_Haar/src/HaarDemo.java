import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class HaarDemo {

	public static void main(String[] args) throws IOException{
		
		File inputImage = new File("mona.jpg");
		BufferedImage image = ImageIO.read(inputImage);
		
		double[][] matrix = ImageMatrixIO.imageToMat(image);
		
		HaarWaveletsTransform dwht = new HaarWaveletsTransform(matrix);
		double[][] result = dwht.processStandardDecomposition();
		
		ImageIO.write(ImageMatrixIO.matToImage(result), "png", new File("output.png"));
		
	}
}
