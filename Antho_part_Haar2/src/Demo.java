import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;



public class Demo {

	public static void main(String[] args) throws IOException {
		
		// With an image :
		
		File inputImage = new File(args[0]);
		BufferedImage image = ImageIO.read(inputImage);
		
		double[][] imageData = ImageMatrixIO.imageToMat(image);
		HaarWavelet2D mat = new HaarWavelet2D(imageData);
		
		mat.standardDecomposition();

		mat.topTWavelets(Double.parseDouble(args[1]));
		
		ImageIO.write(ImageMatrixIO.matToImage(mat.getMatrix(),Double.parseDouble(args[1]),mat.getMax()), "png", new File(inputImage.getName().replaceFirst("[.][^.]+$", "") + "_haar.png"));
		
		boolean[] vect = ByteConversion.conversiontoBits(mat.getMatrix());
		
		byte[] sig = Signature.fingerprint(vect, 50);
		
		System.out.println("Signature : ");
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < sig.length; i++) {
			stringBuilder.append(sig[i] + ",");
		}
		
		stringBuilder.deleteCharAt(stringBuilder.length() -1 );
		
		String content = stringBuilder.toString();
		
		String fileNameWithOutExt = inputImage.getName().replaceFirst("[.][^.]+$", "");
		File outputfile = new File(fileNameWithOutExt+".txt");
		 
		// if file doesnt exists, then create it
		if (!outputfile.exists()) {
			outputfile.createNewFile();
		}

		FileWriter fw = new FileWriter(outputfile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		
	}	

}
