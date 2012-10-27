package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	
	private static Logger instance = null;
	private FileWriter errorLog = null;
	private FileWriter runLog = null;
	
	public enum MsgType {
		LOG, DEBUG
	};
	
	protected Logger() throws IOException {
		errorLog = new FileWriter(new File("error.log"), false);
		runLog = new FileWriter(new File("run.log"), false);
	}
	
	public static Logger getInstance() {
	
		if (instance == null) {
			try {
				instance = new Logger();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return instance;
	}
	
	public void write(String msg, MsgType type) {
		
		try {
			if (MsgType.DEBUG == type) {
				errorLog.write(msg + '\n');
				errorLog.flush();
			}
			else if(MsgType.LOG == type) {
				runLog.write(msg + '\n');
				runLog.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		errorLog.close();
	}
}
