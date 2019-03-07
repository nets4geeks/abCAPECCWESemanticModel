
package ab.parsers;

// https://github.com/FasterXML/jackson-docs/wiki/JacksonStreamingApi
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

import java.time.Duration;
import java.time.Instant;

import ab.data.*;



public class JacksonParser{

   // to enable/disable log messages set this by hand as true/false
   public boolean DEBUG;
   // own name
   protected String parserName;
   // source file
   protected String dataFile;
   // log file
   // to enable/disable the writting logs
   private boolean WRITELOG = false;
   private String logFile;
   private FileWriter logWriter;
   // jackson factory
   protected ObjectMapper mapper;
   protected JsonFactory jFactory;
   protected JsonParser jParser;

   public JacksonParser (){
      parserName = getClass().getName();
      DEBUG = true;
      mapper =  new ObjectMapper();
      jFactory = mapper.getJsonFactory();
   }


   protected String getms(long start, long stop){
      long diff = (stop-start)/1000000;
      return Long.toString(diff) + " ms";
   }


   // give absolute path to data file 
   // and log file name (or null if don't need)
   public boolean init(String _dataFile, String _logFile){
      logFile = _logFile;
      if (logFile !=null){
         try {
            logWriter = new FileWriter(_logFile); 
            WRITELOG=true;
            log("starting logging to "+logFile+" ...");   
         } catch (Exception e){
            e.printStackTrace();
            log("failed to init log file "+logFile);
            return false;
         }
      }
 

      dataFile = _dataFile;
      try {
         jParser = jFactory.createJsonParser(new File(dataFile));
      } catch (Exception e) {
         e.printStackTrace();
         log("failed to read JSON file "+dataFile);
         return false;
      }
      return true;
   }

   public void free(){
      if (WRITELOG){
        try {
           log("... closing log "+logFile);
           logWriter.close();
        } catch (Exception e){
           e.printStackTrace();
        }
      }
   }

   protected void log(String msg){
     String logMsg = "["+parserName+"]: "+msg;
     if (DEBUG) {
        System.out.println(logMsg);
     }
     if (WRITELOG){
        try {
           logWriter.write(logMsg+"\n");
        } catch (Exception e){
           e.printStackTrace();
        }
     }
   }


   public String getParserName(){
      return parserName;
   }

}