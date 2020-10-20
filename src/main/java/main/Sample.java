package main;

import ij.process.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Sample {
	private ImageProcessor imageProcessor;
	private String fileName;
	private String folder;

	public Sample(ImageProcessor imageProcessor, String folder, String fileName) {
		this.imageProcessor = imageProcessor;
		this.folder = folder;
		this.fileName = fileName;
	}


	public byte[] toBytes() {
		return this.imageProcessor.getMaskArray();
	}

	public File saveToFile() { 
		File f = null;
		try {
			f = new File(String.format("%s/%s", folder, fileName));
			ImageIO.write(imageProcessor.getBufferedImage(), "png", f);

		} catch (IOException e) {
		}
		return f;
	}

	public java.awt.Image getImage() {
		return imageProcessor.createImage();
	}


	public byte[] getImageBytes() throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageIO.write(imageProcessor.getBufferedImage(), "png", bs);
		return bs.toByteArray();
	}

	public String getFileName() {
		return fileName;
	}


	public ImageProcessor getImageProcessor() {
		return imageProcessor;
	}


	public static byte[] getImageBytes(BufferedImage image) throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageIO.write(image, "png", bs);
		return bs.toByteArray();
	}
}
