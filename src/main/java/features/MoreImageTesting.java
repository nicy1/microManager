package features;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.micromanager.utils.ImageUtils;

import main.Main;
import main.Sample;
import mmcorej.CMMCore;
import models.IMicroscope;
import models.Microscope;
import rx.Observable;
import utils.ILogger;

public class MoreImageTesting implements IMicroscope{

	private List<String> names;
	private final CMMCore hardware;
	private final ILogger logger;
	private String directory;
	private int count;

	public MoreImageTesting(CMMCore hardware, ILogger logger) {
		this.hardware = hardware;
		this.logger = logger;
		directory = "images/samples";
		names = new ArrayList<String>();
		count = 0;
		initialize();
	}

	public byte[] getPhoto(CMMCore core) throws Exception {
		byte[] img;		
		if(count < names.size()) {
			img = getImageFromFile(names.get(count));
			System.out.println("File " + names.get(count));
			count++;
		} else {			
			img = Microscope.getBytePhoto(core);
			Sample sample = new Sample(ImageUtils.makeProcessor(core, img), Main.folder, Main.fileName);
			img = sample.getImageBytes();			
			count = 0;
		}
		return img;
	}

	public byte[] getImageFromFile(String filename) throws IOException {
		Path path = Paths.get(directory + "/" + filename);
		return Files.readAllBytes(path);
	}

	private void initialize() {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				names.add(file.getName());
				System.out.println("Adding file " + file.getName());
			}
		}
	}

	@Override
	public void startLiveMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Observable<models.Sample> sampleSubscribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<models.Sample> videoSubscribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopLiveMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listDeviceProperties(String deviceName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggleLiveMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLiveModeOn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sampleUnSubscribe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void videoUnSubscribe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void snapImage() {
		// TODO Auto-generated method stub
		
	}

}
