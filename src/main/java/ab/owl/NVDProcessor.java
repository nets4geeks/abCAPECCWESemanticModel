
package ab.owl;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

import ab.parsers.*;
import ab.data.*;


// NVD Processor
public class NVDProcessor extends AbstractOWLProcessor{

   protected String nvdFolder;
   protected String nvdLog;

   protected OWLClass CVE;
   protected static String CVEName = "#CVE";

   protected OWLClass PRODUCT;
   protected static String PRODUCTName = "#CPEProduct";

//   protected OWLClass VENDOR;
//   protected static String VENDORName = "#Vendor";

   protected IRI iriCPE = null;
   protected IRI iriCAPECCWE = null;

   protected OWLClass CWE;
   protected static String CWEName = "#CWE";

   protected OWLObjectProperty AFFECTS;
   protected static String AFFECTSName = "#affectsProduct";
   protected OWLObjectProperty ISAFFECTED;
   protected static String ISAFFECTEDName = "#isAffectedByCVE";


   protected OWLObjectProperty PROBLEMS;
   protected static String PROBLEMSName = "#problemsCWE";

   protected OWLObjectProperty ISPROBLEMED;
   protected static String ISPROBLEMEDName = "#isProblemedByCVE";

   // do not need to parse external xml files
   public boolean initParser(String path){
      return true;
   }


   public boolean init(String _iri, String _nvdFolder, String _nvdLog,String _iriCPE,String _iriCAPECCWE){
      if (initCreate(_iri,null)){
        nvdFolder = _nvdFolder;
        nvdLog = _nvdLog;
        log("initializing me ...");
        // that might make the process faster ...
        CVE = df.getOWLClass(IRI.create(iri+CVEName));
        PRODUCT = df.getOWLClass(IRI.create(iri+PRODUCTName));
        CWE = df.getOWLClass(IRI.create(iri+CWEName));
        AFFECTS = df.getOWLObjectProperty(IRI.create(iri+AFFECTSName));
        PROBLEMS = df.getOWLObjectProperty(IRI.create(iri+PROBLEMSName));
        ISAFFECTED = df.getOWLObjectProperty(IRI.create(iri+ISAFFECTEDName));
        ISPROBLEMED = df.getOWLObjectProperty(IRI.create(iri+ISPROBLEMEDName));

        iriCPE = IRI.create(_iriCPE);
        iriCAPECCWE = IRI.create(_iriCAPECCWE);

        return true;
      }
      log("could not init me!");
      return false; 
   }

   public void addNVDVulnerability(NVDVulnerability vuln){
      OWLNamedIndividual iCVE  = df.getOWLNamedIndividual(IRI.create(iri+"#"+vuln.ID));
      addIndividualToClass(iCVE,CVE);

      addIndividualAnnotation (iCVE,vuln.ID,"en");
      if (vuln.description != null) addIndividualComment(iCVE,vuln.description,"en");

      for (int i=0;i<vuln.products.size();i++){
         CPEProduct product = vuln.products.get(i);
         OWLNamedIndividual iProduct  = df.getOWLNamedIndividual(IRI.create(iriCPE+"#"+product.nameSafe));
         addIndividualsProperty(iCVE,AFFECTS,iProduct);
         addIndividualsProperty(iProduct,ISAFFECTED,iCVE);
      }

      for (int i=0;i<vuln.CWEs.size();i++){
         String cwe = vuln.CWEs.get(i);
         OWLNamedIndividual iCWE  = df.getOWLNamedIndividual(IRI.create(iriCAPECCWE+"#i"+cwe.replace("-","_")));
         //addIndividualToClass(iCWE,CWE); // !!!reasoner is able to get this
         addIndividualsProperty(iCVE,PROBLEMS,iCWE);
         addIndividualsProperty(iCWE,ISPROBLEMED,iCVE);
      }

   }


   public void process(){

     File dir = new File(nvdFolder);
      if(dir.isDirectory()) {
         long startTime = System.nanoTime();
         for(File item : dir.listFiles()){
            if (item.getPath().endsWith(".json") &  !(item.getPath().endsWith("modified.json")) ){
                //String fname = "nvdcve-1.0-2018.json";
                String fname = item.getName();
                log("processing "+fname+" ...");
                NVDJacksonParser parser = new NVDJacksonParser();
                if (parser.init(nvdFolder+fname,nvdLog+fname+".log")){
                   parser.DEBUG=false;
                   parser.parse(this);
                   parser.DEBUG=true;
                   parser.free();
                } else  {
                   log("process: could not init paser"); 
                }
            }
         }
        long stopTime = System.nanoTime();
        log("process (ms): "+ getms(startTime,stopTime));


      }

   }

   



}


