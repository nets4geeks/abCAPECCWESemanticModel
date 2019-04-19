
package ab.parsers;

// https://github.com/FasterXML/jackson-docs/wiki/JacksonStreamingApi
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.util.*;
import java.util.Iterator;


import java.time.Duration;
import java.time.Instant;

import java.net.*;

import ab.data.*;

// to use
// https://www.dbpedia-spotlight.org/api

public class SpotlightParser{

   public String ANNOTATE_URL = "https://api.dbpedia-spotlight.org/en/annotate?";
   String JSON = "application/json";

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

   public SpotlightParser(){
      parserName = getClass().getName();
      DEBUG = true;
      mapper =  new ObjectMapper();
      jFactory = mapper.getJsonFactory();
   }


   public String readMyStream(InputStreamReader dtstream) throws java.io.IOException{

       BufferedReader in = new BufferedReader(dtstream);
       String inputLine;
       StringBuffer content = new StringBuffer();
       while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
       }
       in.close();

       return content.toString();
   }


   public JsonNode makeHTTPrequest(String request,String accept){
      try {
         URL url = new URL(request);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestProperty("ACCEPT",accept);
         connection.connect();

         String output = null;
         String error = null ;

         if (connection.getInputStream()!=null) output = readMyStream (new InputStreamReader(connection.getInputStream())); 
         if (connection.getErrorStream()!=null) error  = readMyStream (new InputStreamReader(connection.getErrorStream()));


         int status = connection.getResponseCode();
         if (status == HttpURLConnection.HTTP_OK) {
            if (output!=null) return mapper.readTree (output);
         } else {
            log("makeHTTPrequest failed. status code: "+status+ " request:" +request);
            if (output != null) log (output);
            if (error != null) log (error);
         }

         return null;


      } catch (Exception e) {
         e.printStackTrace();
         log("makeHTTPrequest failed "+request);
         return null;
      }
   }


   public DBpediaResource getAnnotationOfOrganization(String productName){
    //  String request = ANNOTATE_URL+"text="+URLEncoder.encode(productName)+"&types="+URLEncoder.encode("DBpedia:Organisation");
    //  String request = ANNOTATE_URL+"text="+URLEncoder.encode(productName)+"&types="+URLEncoder.encode("DBpedia:Company");
      String request = ANNOTATE_URL+"text="+URLEncoder.encode(productName);
      return getAnnotateBestResource(request);
   }


   public DBpediaResource getAnnotationOfSoftware(String productName){
      // "https://api.dbpedia-spotlight.org/en/annotate?text=Acrobat%20reader&types=DBpedia%3ASoftware";
      String request = ANNOTATE_URL+"text="+URLEncoder.encode(productName)+"&types="+URLEncoder.encode("DBpedia:Software");
      return getAnnotateBestResource(request);
   }

   public DBpediaResource getAnnotationOfDevice(String productName){
      String request = ANNOTATE_URL+"text="+URLEncoder.encode(productName)+"&types="+URLEncoder.encode("DBpedia:Device");
      return getAnnotateBestResource(request);
   }

   public DBpediaResource getGeneralAnnotation(String productName){
      // "https://api.dbpedia-spotlight.org/en/annotate?text=Acrobat%20reader;
      String request = ANNOTATE_URL+"text="+URLEncoder.encode(productName);
      return getAnnotateBestResource(request);
   }


   // if there are number of results it will choose the result, that has the highest similarityScore
   // return empty resource if error has happened
   // return empty resource (URI=null) if no results
   public DBpediaResource getAnnotateBestResource(String request){
       ArrayList<DBpediaResource> resources = getAnnotateResources(request);
       if (resources!=null){
          DBpediaResource max = new DBpediaResource();
          if (!resources.isEmpty() && resources.size() > 0) {

            if (resources.size()==1) return resources.get(0);
  
            max = resources.get(0);
            double f = max.getScore();
            for (int i=1; i< resources.size(); i++){
               DBpediaResource tmp = resources.get(i);
               if (tmp.getScore()>max.getScore()) max = tmp;
            }
          }
          return max;
       }
       return null;
 
   }

 
   // get list of annotated DBpedia resources by a request like that:
   // "https://api.dbpedia-spotlight.org/en/annotate?text=Acrobat%20reader
   // (see above)
   // return null if an error has happend
   // return empty list if no results
   public ArrayList<DBpediaResource> getAnnotateResources(String request){
      String accept = "application/json";
      JsonNode node = makeHTTPrequest(request,accept);
      if (node !=null){
         ArrayList<DBpediaResource> resources = new ArrayList<DBpediaResource>();
         Iterator<JsonNode> itr = node.path("Resources").elements();
         while (itr.hasNext()) {
            JsonNode temp = itr.next();
            DBpediaResource res = new DBpediaResource();
            res.URI = temp.path("@URI").textValue();
            res.surfaceForm = temp.path("@surfaceForm").textValue();
            res.simularityScore = temp.path("@similarityScore").textValue();
            res.types = temp.path("@types").textValue();
            resources.add(res); 
         }
         return resources;
      }
      return null;
   }


   public String testRequest(String request,String accept){
      return makeHTTPrequest(request,accept).toString();
   }


   protected String getms(long start, long stop){
      long diff = (stop-start)/1000000;
      return Long.toString(diff) + " ms";
   }


   // give a logfile name (or null if don't need)
   public boolean init(String _logFile){
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