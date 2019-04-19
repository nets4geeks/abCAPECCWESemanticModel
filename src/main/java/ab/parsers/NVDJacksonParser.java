
package ab.parsers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.util.Iterator;

import ab.data.*;
import ab.owl.*;

public class NVDJacksonParser extends JacksonParser{
 
   protected int noCWE = 0;
   protected int noProduct = 0;
   protected int noDescription = 0;
   protected int noCVSSv3 = 0;
   protected int noCVSSv2 = 0;

   public boolean getCWEs(JsonNode node,NVDVulnerability vuln){
      JsonNode node1 = node.path("cve").path("problemtype").path("problemtype_data"); 
      Iterator<JsonNode> itr = node1.elements();
      while (itr.hasNext()) {
         JsonNode temp = itr.next().path("description");
         Iterator<JsonNode> itr1 = temp.elements();
         while (itr1.hasNext()) {
            JsonNode temp1 = itr1.next();
            vuln.CWEs.add(temp1.path("value").textValue());
         }
      }
      if (vuln.CWEs.size() == 0) return false;
      return true;
   }

   // todo: versions of products
   public boolean getProducts(JsonNode node,NVDVulnerability vuln){
      JsonNode node1 = node.path("cve").path("affects").path("vendor").path("vendor_data"); 
      Iterator<JsonNode> itr = node1.elements();
      while (itr.hasNext()) {
         JsonNode temp = itr.next();
         String vendor = temp.path("vendor_name").textValue();
         Iterator<JsonNode> itr1 = temp.path("product").path("product_data").elements();
         while (itr1.hasNext()) {
            JsonNode temp1 = itr1.next();
            String product = temp1.path("product_name").textValue();
            vuln.products.add(new CPEProduct(vendor,product));
         }
      }
      if (vuln.products.size() == 0) return false;
      return true;
   }

   // todo: advanced CPE expressions & versions of products
   public boolean getCPE (JsonNode node, NVDVulnerability vuln){
      JsonNode node1 = node.path("configurations").path("nodes"); 
      Iterator<JsonNode> itr = node1.elements();
      while (itr.hasNext()) {
         Iterator<JsonNode> itr1 = itr.next().path("cpe_match").elements();
         while (itr1.hasNext()) {
            JsonNode temp = itr1.next();
            vuln.products.add (new CPEProduct(temp.path("cpe23Uri").textValue()) );
         }
      }
      if (vuln.products.size() == 0) return false;  
      return true;
   }


   public boolean getDescription(JsonNode node, NVDVulnerability vuln){
      JsonNode node1 = node.path("cve").path("description").path("description_data"); 
      Iterator<JsonNode> itr = node1.elements();
      while (itr.hasNext()) {
         JsonNode temp = itr.next();
         if (temp.path("lang").textValue().equals("en")) vuln.description = temp.path("value").textValue();
      }
      if (vuln.description == null) return false;
      return true;
   }

   public boolean getCVSSv3(JsonNode node,NVDVulnerability vuln){
      JsonNode node1 = node.path("impact").path("baseMetricV3").path("cvssV3"); 
      if (node1.isMissingNode()) return false;
      vuln.cvssV3.vectorString = node1.path("vectorString").textValue();
      vuln.cvssV3.attackVector = node1.path("attackVector").textValue();
      vuln.cvssV3.attackComplexity = node1.path("attackComplexity").textValue();
      vuln.cvssV3.privilegesRequired = node1.path("privilegesRequired").textValue();
      vuln.cvssV3.userInteraction = node1.path("userInteraction").textValue();
      vuln.cvssV3.scope = node1.path("scope").textValue();
      vuln.cvssV3.confidentialityImpact = node1.path("confidentialityImpact").textValue();
      vuln.cvssV3.integrityImpact = node1.path("integrityImpact").textValue();
      vuln.cvssV3.availabilityImpact = node1.path("availabilityImpact").textValue();
      vuln.cvssV3.baseSeverity = node1.path("baseSeverity").textValue();
      vuln.cvssV3.baseScore = node1.path("baseScore").floatValue();
      return true;
   }

   public boolean getCVSSv2(JsonNode node,NVDVulnerability vuln){
      JsonNode node1 = node.path("impact").path("baseMetricV2").path("cvssV2"); 
      if (node1.isMissingNode()) return false;
      vuln.cvssV2.vectorString = node1.path("vectorString").textValue();
      vuln.cvssV2.accessVector = node1.path("accessVector").textValue();
      vuln.cvssV2.accessComplexity = node1.path("accessComplexity").textValue();
      vuln.cvssV2.authentication = node1.path("authentication").textValue();
      vuln.cvssV2.confidentialityImpact = node1.path("confidentialityImpact").textValue();
      vuln.cvssV2.integrityImpact = node1.path("integrityImpact").textValue();
      vuln.cvssV2.availabilityImpact = node1.path("availabilityImpact").textValue();
      vuln.cvssV2.baseScore = node1.path("baseScore").floatValue();
      return true;
   }

   public boolean getID (JsonNode node,NVDVulnerability vuln){
      vuln.ID = node.path("cve").path("CVE_data_meta").path("ID").textValue();
      if (vuln.ID == null) return false;
      return true;
   }

   public NVDVulnerability getNVDVulnerability(JsonNode node){
      NVDVulnerability vuln = new NVDVulnerability();
      if (getID (node,vuln)) {            // it's simple to get ID...
         if (!getCWEs(node,vuln)) {       // get CWEs
            log(vuln.ID + ": no CWEs"); 
            noCWE++;
         }
         if (!getDescription(node,vuln)) {  // get description
            log(vuln.ID+": no description"); 
            noDescription++;
         }
         if (!getProducts(node,vuln)) {   // get products
            if (!getCPE(node,vuln)){
               log(vuln.ID +": no product"); 
               noProduct++;
            }
         }
         if (!getCVSSv3(node,vuln)) {  // get 3 metrics
            noCVSSv3++;
            log(vuln.ID +": no CVSSv3 metrics");
         } 
         if (!getCVSSv2(node,vuln)) {  // get 2 metrics
            noCVSSv2++;
         }
      }
      return vuln;
   }

   public boolean parse (NVDProcessor processor){
     JsonToken current;

     try {
        current = jParser.nextToken();
        if (current != JsonToken.START_OBJECT) {
           log("error: could not find the root object.");
           return false;
        }
        while (jParser.nextToken() != JsonToken.END_OBJECT) {
           String fieldName = jParser.getCurrentName();
           current = jParser.nextToken();
           if (fieldName.equals("CVE_Items")) {
              if (current == JsonToken.START_ARRAY){

                 long startTime = System.nanoTime();

                 int i = 0;
                 while (jParser.nextToken() != JsonToken.END_ARRAY){
                     JsonNode node = jParser.readValueAsTree();
                     NVDVulnerability vuln = getNVDVulnerability(node);
                     processor.addNVDVulnerability(vuln);
                     i++;
                     //vuln.debugShow();
                 }

                 long stopTime = System.nanoTime();
                 log("wasted time (ms): "+ getms(startTime,stopTime));

                 log("processed "+i+" CVEs");
                 log("no CWE: "+ noCWE);
                 log("no description: "+noDescription);
                 log("no product: "+noProduct);
                 log("no CVSSv2: "+noCVSSv2);
                 log("no CVSSv3: "+noCVSSv3);
              }else {
                  //log("l2: Unprocessed property " + fieldName);
                  jParser.skipChildren();
              }
            } else {
                //log("l1: Unprocessed property " + fieldName);
                jParser.skipChildren();
            }
  
        }
     } catch (Exception e) {
         e.printStackTrace();
         log("failed :(((");
         return false;
     }

     return true;

   }

}