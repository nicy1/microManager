package test;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import com.message.protobuf.Messages.MessageClient;



public class CreateDataset {
	private int counter;
	// file .csv writer
	private FileWriter fw;
	
	public CreateDataset() {
		counter = 0;
		try { FileWriteLine(true, 0, 0, 0, 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void FileWriteLine(boolean header, int reqS, float ratio, long t2resp, int reqType) throws InterruptedException {
		String line;
		if(header){
		   try { 
			   line = "No.,Timestamp,Source,Destination,Protocol,Length,TimeToResp,Ratio,ActionType";
			   fw = new FileWriter(new File("telepathology.csv")); 
			   fw.append(line);
			   fw.append('\n');
		   } catch (IOException e) { 
			   e.printStackTrace(); 
		   }	   
		}
		else {
			try {
				String no = String.valueOf(counter++);
				String src = "127.0.0.1";
				String dst = "127.0.0.1";
				Date date = new Date();
				String millis = String.valueOf(System.currentTimeMillis());
				String length = String.valueOf(reqS); 
				String actiontype = String.valueOf(reqType);
				String r = String.valueOf(ratio);
				String t2r = String.valueOf(t2resp);
				line = no+","+millis+","+src+","+dst+",TCP,"+length+","+t2r+","+r+","+actiontype;
				
				fw.append(line);
				fw.append('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}    
	}
	
}