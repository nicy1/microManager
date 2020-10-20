package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.micromanager.utils.ImageUtils;

import dagg.DaggerSingletonComponent;
import features.FocusStageControl;
import features.MoreImageTesting;
import features.XYStageControl;
import main.Client;
import main.Main;
import main.Sample;
import mmcorej.CMMCore;
import models.Microscope;
import utils.ILogger;


public class MultipleRequests {

	public static void main(String[] args) throws Exception {
		int threadCount = 15;
		
		CMMCore core = Main.config();

        List<ControlMicroscope> tasks = new ArrayList<ControlMicroscope>();
        for(int i=0; i<threadCount; i++) {
        	tasks.add(new ControlMicroscope(core));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<Integer>> futures = executorService.invokeAll(tasks);
        List<Integer> resultList = new ArrayList<Integer>(futures.size());

        // Check for exceptions
        for (Future<Integer> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get());
        }
        // Validate the dimensions
        if (threadCount == futures.size()) {
        	System.out.println("Ok");
        }
        
	}
	
	private int randInt(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
	
	static class ControlMicroscope implements Callable<Integer> {

		private CMMCore core;
		long timeBefore;
		
        public ControlMicroscope(CMMCore core) {
            this.core = core;
            this.timeBefore = System.currentTimeMillis();
        }

        @Override
        public Integer call() throws Exception {
            //Thread.sleep(randInt(0,2000));
            System.out.println("WOW");
            //core.setXYPosition(0, 0);
            //FocusStageControl.changeStage(this.core, 0);
            
            Sample sample = null;
    		if(core!=null) {
    			//core.setExposure(50);
    			if (core.getBytesPerPixel() == 1) {
    				// 8-bit grayscale pixels
    				//byte[] image = ImageAcquisition.getBytePhoto(core);
    				byte[] image = Microscope.getBytePhoto(core);
    				long timeAfter = System.currentTimeMillis();
    			    long elapsed = timeAfter - timeBefore;
    				System.out.println("elapsed:" + elapsed);
    				//sample = new Sample(ImageUtils.makeProcessor(core, image), Main.folder, Main.fileName);
    				//return image;
    			} else if (core.getBytesPerPixel() == 2){
    				// 16-bit grayscale pixels
    				System.out.println(" 16 bit");
    				short[] image = Microscope.getShortPhoto(core);
    				sample = new Sample(ImageUtils.makeProcessor(core, image), Main.folder, Main.fileName);

    			} else {
    				System.out.println("Dont' know how to handle images with " +
    						core.getBytesPerPixel() + " byte pixels.");             
    			}		
    			//sample.saveToFile();
    		}	
            
            return Client.OK_TYPE;
        }

    }

}
