package test;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class ConfReader {

	private Ini ini;
	
	public ConfReader() throws InvalidFileFormatException, IOException {
		ini = new Ini(new File("config.ini"));
	}
	
	public String getIpDb() {
		return ini.get("url", "database");
	}
	
	public String getIpApi() {
		return ini.get("url", "api");
	}
	
	public String getApiSecret() {
		return ini.get("secret", "api");
	}
	
	public String getPluginSecret() {
		return ini.get("secret", "telepathology");
	}
}
