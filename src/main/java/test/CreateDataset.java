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
		try { FileWriteLine(null, true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void FileWriteLine(MessageClient mc, boolean header) throws InterruptedException {
		String line;
		if(header){
		   try { 
			   line = "No.,Timestamp,Source,Destination,Protocol,Length,ActionType";
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
				Thread.sleep(500);
				String millis = String.valueOf(date.getTime());
				String length = String.valueOf(mc.toString().length()); 
				String actiontype = String.valueOf(mc.getRequest().getRequestTypeValue());
				line = no+","+millis+","+src+","+dst+",TCP,"+length+","+actiontype;
				
				fw.append(line);
				fw.append('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}    
	}
	
}