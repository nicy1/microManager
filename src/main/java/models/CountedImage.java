package models;

public class CountedImage {

	private byte[] image;
	private String filePath;
	private int totNuclei;
	
	public CountedImage(byte[] image, int totNuclei) {
		this(image, totNuclei, null);
	}

	public CountedImage(byte[] image, int totNuclei, String filePath) {
		this.image = image;
		this.totNuclei = totNuclei;
		this.filePath =  filePath;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getTotNuclei() {
		return totNuclei;
	}

	public void setTotNuclei(int totNuclei) {
		this.totNuclei = totNuclei;
	}
}
