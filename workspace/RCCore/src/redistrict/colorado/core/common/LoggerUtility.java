/**
 * Copyright 2018. Charles Coughlin. All Rights Reserved.
 * MIT License.
 * Configures a java.util.logging Logger to use our log directory.
 */
package redistrict.colorado.core.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


/**
 * This class is a singleton that features a method to configure the root logger
 * and, presumably, all its progeny. The logger is configured to write to both
 * a common log file and the console.
 */
public class LoggerUtility {
	static final int MAX_BYTES  = 100000; // Max bytes in a log file
	static final int MAX_FILES  = 3;     // Max log files before overwriting
	private static LoggerUtility instance = null;


	/**
	 * Constructor is private per Singleton pattern.
	 */
	private LoggerUtility() {}

	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static LoggerUtility getInstance() {
		if( instance==null) {
			synchronized(LoggerUtility.class) {
				instance = new LoggerUtility();
			}
		}
		return instance;
	}
	
	/**
	 * @param root core name for the log files
	 */
	public void configureRootLogger(String rootName) {
		Logger root = Logger.getLogger("");
		Handler[] handlers = root.getHandlers(); 
	    for (Handler h : handlers) {
	        h.setLevel(Level.INFO);   // Display info and worse on console
	        h.setFormatter(new RCFormatter());
	        if( h instanceof FileHandler ) root.removeHandler(h);
	    }

		FileHandler fh;  
		
	    try {  
	        // Configure the logger with handler and formatter 
	    	Path pattern = Paths.get(PathConstants.LOG_DIR.toString(),rootName+".log");
	        fh = new FileHandler(pattern.toString(),MAX_BYTES,MAX_FILES,true); 
	        fh.setLevel(Level.INFO); // Display info and worse in the log file
	        root.addHandler(fh);
	        fh.setFormatter(new RCFormatter());

	    } 
	    catch (SecurityException e) {  
	        e.printStackTrace();  
	    } 
	    catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}
	
	/**
	 * Configure the loggers to output INFO to std out, do not send to a log file.
	 * @param root core name for the log files
	 */
	public void configureTestLogger(String rootName) {
		Logger root = Logger.getLogger("");
		Handler[] handlers = root.getHandlers(); 
	    for (Handler h : handlers) {
	        h.setLevel(Level.INFO);   // Display info and worse on console
	        h.setFormatter(new RCFormatter());
	        if( h instanceof FileHandler ) root.removeHandler(h);
	    }  
	}
	public final class RCFormatter extends Formatter {
		private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS ";
		private final String LINE_SEPARATOR = System.getProperty("line.separator");
		private final SimpleDateFormat dateFormatter;

		
		public RCFormatter() {
			dateFormatter = new SimpleDateFormat(DATE_PATTERN);
		}
	    

	    @Override
	    public String format(LogRecord record) {
	        StringBuilder sb = new StringBuilder();

	        sb.append(dateFormatter.format((new Date(record.getMillis()))))
	            .append(String.format("%-6s",record.getLevel().getLocalizedName()))
	            .append(": ")
	            .append(formatMessage(record))
	            .append(LINE_SEPARATOR);

	        if (record.getThrown() != null) {
	            try {
	                StringWriter sw = new StringWriter();
	                PrintWriter pw = new PrintWriter(sw);
	                record.getThrown().printStackTrace(pw);
	                pw.close();
	                sb.append(sw.toString());
	            } catch (Exception ex) {}
	        }

	        return sb.toString();
	    }
	}
}
