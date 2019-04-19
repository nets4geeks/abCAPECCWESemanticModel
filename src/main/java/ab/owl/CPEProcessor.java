
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
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import ab.parsers.*;
import ab.data.*;

public class CPEProcessor extends AbstractOWLProcessor{

   public boolean CHECKDBPEDIA = true; // if it's true chek DBpedia
   public boolean CHECK_A = true;
   public boolean CHECK_O = true;
   public boolean CHECK_H = true;
   public boolean isProcessingEnabled = true;

   CPEParser parser;
   SpotlightParser sparser;
   HashMap<String,CPEProduct> productsMap;
   HashMap<String,String> vendorsMap;


   protected OWLProcessor coProcessor = null;

   protected String NOTFOUND="notfound";
   protected String ERROR="error";
   protected String ANNOTATED="annotated";
   protected String SKIPPED="skipped";
   protected String REJECTED="rejected";
   protected String MANUAL="manual";
   protected String FALSEANO="false";


   protected OWLClass ProductClass;
   protected OWLClass VendorClass;
   protected OWLClass DBpediaResourceClass;
   protected OWLClass AnnotationStatusClass;
   protected OWLNamedIndividual AnnotationError;
   protected OWLNamedIndividual AnnotationManual;
   protected OWLNamedIndividual AnnotationFalse;
   protected OWLNamedIndividual AnnotationNotFound;
   protected OWLNamedIndividual AnnotationSkipped;
   protected OWLNamedIndividual Annotated;
   protected OWLObjectProperty hasAnnotationStatus;
   protected OWLObjectProperty producesProduct;
   protected OWLObjectProperty isProducedByVendor;

   protected OWLDataProperty hasCPEName;
   protected OWLDataProperty hasCPEVendorName;

   public void logParameters(){
      log ("isProcessingEnabled="+isProcessingEnabled);
      log ("CHECKDBPEDIA="+CHECKDBPEDIA);
      log ("CHECK_A="+CHECK_A);
      log ("CHECK_O="+CHECK_O);
      log ("CHECK_H="+CHECK_H);
      log ("ANNOTATE_URL="+sparser.ANNOTATE_URL);
   }

   public void setAnnotateURL(String url){
      sparser.ANNOTATE_URL = url;
  }
 

   protected void createOWLObjects(){
      ProductClass = df.getOWLClass(IRI.create(iri+"#CPEProduct"));
      VendorClass = df.getOWLClass(IRI.create(iri+"#CPEVendor"));
      DBpediaResourceClass = df.getOWLClass(IRI.create(iri+"#DBpediaResource"));
      AnnotationStatusClass = df.getOWLClass(IRI.create(iri+"#AnnotationStatus"));

      AnnotationError = df.getOWLNamedIndividual(IRI.create(iri+"#AnnotationErrorStatus"));
      addIndividualToClass(AnnotationError,AnnotationStatusClass);
      AnnotationNotFound = df.getOWLNamedIndividual(IRI.create(iri+"#AnnotationNotFoundStatus"));
      addIndividualToClass(AnnotationNotFound,AnnotationStatusClass);

      AnnotationSkipped = df.getOWLNamedIndividual(IRI.create(iri+"#AnnotationSkipped"));
      addIndividualToClass(AnnotationSkipped,AnnotationStatusClass);

      Annotated = df.getOWLNamedIndividual(IRI.create(iri+"#AnnotatedStatus"));  
      addIndividualToClass(Annotated,AnnotationStatusClass);
      AnnotationFalse = df.getOWLNamedIndividual(IRI.create(iri+"#FalseAnnotatedStatus"));
      addIndividualToClass(AnnotationFalse,AnnotationStatusClass);
      AnnotationManual = df.getOWLNamedIndividual(IRI.create(iri+"#ManualAnnotatedStatus"));
      addIndividualToClass(AnnotationManual,AnnotationStatusClass);
      hasAnnotationStatus = df.getOWLObjectProperty(IRI.create(iri+"#hasAnnotationStatus"));   // status, that has been given by AI
      hasCPEName = df.getOWLDataProperty(IRI.create(iri+"#hasCPEName"));
      hasCPEVendorName = df.getOWLDataProperty(IRI.create(iri+"#hasCPEVendorName"));
      producesProduct = df.getOWLObjectProperty(IRI.create(iri+"#producesProduct"));
      isProducedByVendor = df.getOWLObjectProperty(IRI.create(iri+"#isProducedByVendor"));

   }

   public boolean init(String baseFile,String cpeFile){
      if (initRead(baseFile,cpeFile)){
         initReasoner();
         createOWLObjects();
         //fillOntology();
         return true;
      }
      return false; 
   }

   public boolean initParser(String path){
      parser = new CPEParser();
      if (!parser.init(path)) return false;
      sparser = new SpotlightParser();
      if (!sparser.init(null)) return false;
      return true;
   }


   public boolean initCoProcessor(String path){
      coProcessor = new OWLProcessor();
      if (coProcessor.initRead(path,null)) {
          coProcessor.initReasoner(); 
          //coProcessor.fillOntology();
          return true;
      }

      coProcessor = null;
      return false;
   }


   // parses CPE XML DOM tree 
   // & 
   // creates a hashmap that contains <product.nameSafe,product instance>
   public void createProductsMap(){
      log("creating products map...");
      long startTime = System.nanoTime();
      productsMap = new HashMap<String,CPEProduct>();
      vendorsMap = new HashMap<String,String>();
      NodeList lst = parser.getNodeList();
      int add = 0;
      int addVendor = 0;
      for (int i=0; i<lst.getLength();i++) {
         CPEProduct product =  parser.getCPEProductByNode(lst.item(i));

         if (!productsMap.containsKey(product.nameSafe)){
            productsMap.put(product.nameSafe,product);
            add++;
         }

         if (!vendorsMap.containsKey(product.vendorSafe)){
            vendorsMap.put(product.vendorSafe,product.vendor);
            addVendor++;
         }
      }
      log("createProductsMap: CPEs total: "+ Integer.toString(lst.getLength()));
      log("createProductsMap: added products: "+ Integer.toString(add));
      log("createProductsMap: added vendors: "+ Integer.toString(addVendor));
      long stopTime = System.nanoTime();
      log("createProductsMap: process (ms): "+ getms(startTime,stopTime));
   }



   public void process(String log){
      if (isProcessingEnabled){
         if (initLog(log)){
            logParameters();
            createProductsMap();
            askDBpedia1();
            askDBpedia2();
            closeLog();
         }
      }
   }


   // for vendors
   // the rest might be ask Spotlight ...
   public void askDBpedia2(){
      log("start making vendors ....");
      Set set = vendorsMap.entrySet();
      Iterator iterator = set.iterator();

      int total =0; // total vendors  
      int skipped =0; // skipped by DBpedia
      int errorpedia = 0;
      int notfoundpedia =0;
      int countpedia =0;
      int manual =0;
      int falseanno =0;
      int notfound =0;
      int count =0;
      int error =0;

      while(iterator.hasNext()) {
         Map.Entry me = (Map.Entry)iterator.next();
         String key = (String)me.getKey();
         String value = (String)me.getValue();
         total++;
 
         OWLNamedIndividual vendorIndividual  = df.getOWLNamedIndividual(IRI.create(iri+"#"+key));

         // check the main model if entity exists
         if (doesIndividualExist(vendorIndividual)) {
             // check if label not equals vendor name ...
             if (!getLabel(vendorIndividual).equals(value)) {
                 // there are different individuals ...
                 OWLNamedIndividual thedifferent = getDifferentEntityByPattern("http://dbpedia.org/resource/",vendorIndividual);
                 if ( thedifferent !=null ){
                    // adds the entity, that should be marked as false annotated,
                    // because the main model has differentAs entities
                    addVendorIndividual(key,value,null,AnnotationFalse);
                    falseanno++;
                    log("false (from the main model): "+key+" = "+thedifferent.toString());
                    // if there is no manual annotation, mark as notfound
                    // and it won't try to make it be annotated (you should do it by hand)
                    if (getSameEntityByPattern("http://dbpedia.org/resource/",vendorIndividual)==null){
                       addVendorIndividual(key,value,null,AnnotationNotFound);
                       notfound++;
                       log("notfound (from the main model): " +key);
                       continue; // done
                    }
                 }

                 OWLNamedIndividual thesame = getSameEntityByPattern("http://dbpedia.org/resource/",vendorIndividual);
                 // there are the same individuals...
                 if ( thesame !=null ){
                    manual++;
                    // adds the entity, already annotated (manually) by the main model
                    addVendorIndividual1(key,value,thesame,AnnotationManual);
                    log("manual (from the main model): "+key+" = "+thesame.toString());
                    continue; // done
                 } 
             }
         }

         // check the additional model, if it is
         if (coProcessor != null) {
            if (coProcessor.doesIndividualExist(vendorIndividual)) {
              // check if entity is annotated
              if (coProcessor.doesIndividualsRelationshipExist(vendorIndividual, hasAnnotationStatus, Annotated)){
                 OWLNamedIndividual dbInd = coProcessor.getSameEntityByPattern("http://dbpedia.org/resource/",vendorIndividual);
                 if ( dbInd !=null ) {
                    addVendorIndividual1(key,value,dbInd,Annotated);
                    count++;
                    log("annotated (from additional model): "+key+" = "+dbInd.toString());
                    // done with the product
                    continue;
                 }
                 else {
                    log("error (from additional model): said it's annotated but there is no a dbpedia entry" +key);
                    error++;
                    // and give it a chance to be annotated
                 }
              } 
              // check if entity is not found
              if (coProcessor.doesIndividualsRelationshipExist(vendorIndividual, hasAnnotationStatus, AnnotationNotFound)){
                 addVendorIndividual(key,value,null,AnnotationNotFound);
                 notfound++;
                 log("notfound (from additional model): " +key);
                 continue;
              }
            }
         }


         // ask Spotlight /////////////////////////////////////////////////////////////////////////////////
         if (CHECKDBPEDIA == true) {
            DBpediaResource res = null;
            res = sparser.getGeneralAnnotation(value);

            if (res == null){
               errorpedia++;
               addVendorIndividual(key,value,null,AnnotationError);
               log ("error (from DBpedia): "+key+":"+value);
               continue;
            }

            if (!res.isEmpty()){
                 if ( (res.checkType("DBpedia:Company")) | (res.checkType("DBpedia:Organisation")) | (res.checkType("DBpedia:Developer")) ){
                    addVendorIndividual(key,value,res,Annotated);
                    log ("annotated (from DBpedia):" +key+":"+value+ " ::: " + res.URI);
                    countpedia++;
                 }else{
                    log ("rejected (from DBpedia): "+key+":"+value+ " ::: " + res.URI);
                    addVendorIndividual(key,value,null,AnnotationNotFound);
                    notfoundpedia++;
                 }
            }else {
               log ("notfound (from DBpedia): "+key+":"+value);
               addVendorIndividual(key,value,null,AnnotationNotFound);
               notfoundpedia++;
               continue;
            }

         } else {
            // skip that and forget
            skipped++;
            log ("skipped (DBpedia checks are disabled): "+key+":"+value);
         }
         // ask Spotlight /////////////////////////////////////////////////////////////////////////////////
      }

      log(":::::::::::::::::::::::VENDORS SUMMARY::::::::::::::::");
      log("                       TOTAL CPEs: "+Integer.toString(total) );
      log("         MANUAL (from main model): "+Integer.toString(manual));
      log("          FALSE (from main model): "+Integer.toString(falseanno));
      log("     ANNOTATED (from both models): "+Integer.toString(count) );
      log("   ERRORS (from additional model): "+Integer.toString(error));
      log("     NOT FOUND (from both models): "+Integer.toString(notfound) );
      //log("          REJECTED (from DBpedia): "+Integer.toString(rejected) );
      log("           SKIPPED (from DBpedia): "+Integer.toString(skipped));
      log("            ERRORS (from DBpedia): "+Integer.toString(errorpedia));
      log("         NOT FOUND (from DBpedia): "+Integer.toString(notfoundpedia));
      log("         ANNOTATED (from DBpedia): "+Integer.toString(countpedia));

   }


   // for products
   // uses the main model, which contains the entities, checked by hand
   // uses the additional model, to add entities to the main model
   // the rest might be ask Spotlight ...
   // must be called firstly
   public void askDBpedia1(){
      log("start making products ....");
      Set set = productsMap.entrySet();
      Iterator iterator = set.iterator();

      int total = 0; // common count
      int manual =0; // manually anotated from the main model
      int manual_a =0; 
      int manual_h =0;
      int manual_o =0;
      int falseanno =0; // false annotated from the main model
      int false_a = 0;
      int false_h = 0;
      int false_o = 0;
      int count = 0; // annotated from the additional model
      int error = 0; // error from additional model
      int notfound = 0; // not found from additional model
      int rejected = 0; // rejected from DBpedia
      int skipped =0; // skipped from the DBpedia check
      int errorpedia = 0; // error from DBpedia
      int notfoundpedia = 0; // not found from DBpedia
      int countpedia = 0; // annotated from DBpedia

      while(iterator.hasNext()) {
         total++;
         Map.Entry me = (Map.Entry)iterator.next();
         String key = (String)me.getKey();
         CPEProduct product = (CPEProduct)me.getValue();

         OWLNamedIndividual productIndividual  = df.getOWLNamedIndividual(IRI.create(iri+"#"+product.nameSafe));

         // check the main model if entity exists
         if (doesIndividualExist(productIndividual)) {
             // check if label not equals product.nameCPEStyle, i.e. entity has not been processed ...
             if (!getLabel(productIndividual).equals(product.label)) {
                 // there are the same individuals...
                 OWLNamedIndividual thesame = getSameEntityByPattern("http://dbpedia.org/resource/",productIndividual);
                 if ( thesame !=null ){
                    manual++;
                    if (product.part.equals("a")) manual_a++;
                    if (product.part.equals("h")) manual_h++;
                    if (product.part.equals("o")) manual_o++;

                    // adds the entity, already annotated (manually) by the main model
                    addProductIndividual1(product,thesame,AnnotationManual);
                    log("manual (from the main model): "+product.nameSafe+" = "+thesame.toString());
                 } 
                 // there are different individuals ...
                 OWLNamedIndividual thedifferent = getDifferentEntityByPattern("http://dbpedia.org/resource/",productIndividual);
                 if ( thedifferent !=null ){
                    // adds the entity, that should be marked as false annotated,
                    // because the main model has differentAs entities
                    addProductIndividual(product,null,AnnotationFalse);
                    falseanno++;
                    if (product.part.equals("a")) false_a++;
                    if (product.part.equals("h")) false_h++;
                    if (product.part.equals("o")) false_o++;

                    log("false (from the main model): "+product.nameSafe+" = "+thedifferent.toString());
                    // if there is no manual annotation, mark as notfound
                    // and it won't try to make it be annotated (you should do it by hand)
                    if (getSameEntityByPattern("http://dbpedia.org/resource/",productIndividual)==null){
                       addProductIndividual(product,null,AnnotationNotFound);
                       notfound++;
                       log("notfound (from the main model): " +product.nameSafe);
                    }

                 }
             }
             // skip the product if the proper label already exists, i.e. entity has been processed by hand
             continue;
         }

         // check the additional model, if it is
         if (coProcessor != null) {
            if (coProcessor.doesIndividualExist(productIndividual)) {
              // check if entity is annotated
              if (coProcessor.doesIndividualsRelationshipExist(productIndividual, hasAnnotationStatus, Annotated)){
                 OWLNamedIndividual dbInd = coProcessor.getSameEntityByPattern("http://dbpedia.org/resource/",productIndividual);
                 if ( dbInd !=null ) {
                    addProductIndividual1(product,dbInd,Annotated);
                    count++;
                    log("annotated (from additional model): "+product.nameSafe+" = "+dbInd.toString());
                    // done with the product
                    continue;
                 }
                 else {
                    log("error (from additional model): said it's annotated but there is no a dbpedia entry" +product.nameSafe);
                    error++;
                    // and give it a chance to be annotated
                 }
              } 
              // check if entity is not found
              if (coProcessor.doesIndividualsRelationshipExist(productIndividual, hasAnnotationStatus, AnnotationNotFound)){
                 addProductIndividual(product,null,AnnotationNotFound);
                 notfound++;
                 log("notfound (from additional model): " +product.nameSafe);
                 continue;
              }
            }
         }

         // check online //////////////////////////////////////////////////////////////////////////////////////////////
         if (CHECKDBPEDIA == true) {

         boolean isSkipped = true;
         // trying to annotate
         DBpediaResource res = null;
         // operating systems
         if (product.part.equals("o") & (CHECK_O == true)){
            isSkipped = false;
            res = sparser.getAnnotationOfSoftware(product.nameShort);
         } 
         // applications
         if (product.part.equals("a") & (CHECK_A == true) ){
            isSkipped = false;
            res = sparser.getAnnotationOfSoftware(product.nameShort);
         } 
         // hardware
         if (product.part.equals("h") & (CHECK_H == true) ){
            isSkipped = false;
            res = sparser.getAnnotationOfDevice(product.nameShort);
            if (res !=null) {
               if (!res.isEmpty()){
                  // skip weapon, engines, & instruments
                  if ( res.checkType("DBpedia:Weapon") || res.checkType("DBpedia:Engine") || res.checkType("DBpedia:Instrument") ) {
                     rejected++;
                     log ("rejected (from DBpedia): "+product.nameSafe); //!!!
                     res.URI = null; // will give the "not found" status below
                  }
               }
            }
         }

         // even hasn't tried to annotate ...
         if (res==null & isSkipped==true){
            skipped++;
            log ("skipped (from DBpedia): "+ product.nameSafe);
            //addIndividualToClass(productIndividual,ProductClass);
            // ... and forget about it
            continue;
         }

         // tried to annotate but didn't manage to do that
         if (res==null & isSkipped==false){
            errorpedia++;
            // add error status
            addProductIndividual(product,null,AnnotationError);
            log ("error (from DBpedia): "+product.nameSafe);
            continue;
         }

         // has got the empty result
         if (res.isEmpty()){
            // not found
            log ("notfound (from DBpedia): "+product.nameSafe);
            addProductIndividual(product,null,AnnotationNotFound);
            notfoundpedia++;
            continue;
         }

         // annotated
         addProductIndividual(product,res,Annotated);
         log ("annotated (from DBpedia):" +product.nameSafe +" === "+ res.URI);
         countpedia++;

         }  else {  // if CHECKDBPEDIA
            // do nothing with the rest
            skipped++;
            log ("skipped (DBpedia checks are disabled): "+ product.nameSafe);
         } 
         // check online /////////////////////////////////////////////////////////////////////////////////////////////

      }

      log(":::::::::::::::PRODUCTS SUMMARY:::::::::::::");
      log("                       TOTAL CPEs: "+Integer.toString(total) );
      log("         MANUAL (from main model): "+Integer.toString(manual));

      log("       MANUAL_A (from main model): "+Integer.toString(manual_a));
      log("       MANUAL_H (from main model): "+Integer.toString(manual_h));
      log("       MANUAL_O (from main model): "+Integer.toString(manual_o));

      log("          FALSE (from main model): "+Integer.toString(falseanno));

      log("        FALSE_A (from main model): "+Integer.toString(false_a));
      log("        FALSE_H (from main model): "+Integer.toString(false_h));
      log("        FALSE_O (from main model): "+Integer.toString(false_o));

      log("     ANNOTATED (from both models): "+Integer.toString(count) );
      log("   ERRORS (from additional model): "+Integer.toString(error));
      log("     NOT FOUND (from both models): "+Integer.toString(notfound) );
      log("          REJECTED (from DBpedia): "+Integer.toString(rejected) );
      log("           SKIPPED (from DBpedia): "+Integer.toString(skipped));
      log("            ERRORS (from DBpedia): "+Integer.toString(errorpedia));
      log("         NOT FOUND (from DBpedia): "+Integer.toString(notfoundpedia));
      log("         ANNOTATED (from DBpedia): "+Integer.toString(countpedia));
   }


   public String sho(String status, CPEProduct product, DBpediaResource res){
      if (res !=null) return status+"|"+product.nameSafe+"|"+res.URI +"|"+product.part+":"+product.vendor+":"+product.product+"|"+product.nameShort;
      return status+"|"+product.nameSafe+"|"+"null"+"|"+product.part+":"+product.vendor+":"+product.product+"|"+product.nameShort;
   }


   public OWLNamedIndividual setVendor(String vendorSafe,String vendorName){
      OWLNamedIndividual vendorIndividual  = df.getOWLNamedIndividual(IRI.create(iri+"#"+vendorSafe));
      addIndividualToClass(vendorIndividual,VendorClass);
      addIndividualAnnotation(vendorIndividual,vendorName,"en");
      addIndividualDataProperty(vendorIndividual,hasCPEVendorName,vendorName);
      return vendorIndividual;
   }


   public void addVendorIndividual(String vendorSafe,String vendorName, DBpediaResource res, OWLNamedIndividual annotationStatus){
      OWLNamedIndividual vendorIndividual = setVendor(vendorSafe,vendorName);
      addIndividualsProperty(vendorIndividual,hasAnnotationStatus,annotationStatus);
      if (res!=null){
         OWLNamedIndividual dbpediaIndividual  = df.getOWLNamedIndividual(IRI.create(res.URI));
         addIndividualToClass(dbpediaIndividual,DBpediaResourceClass);
         addSameIndividuals(dbpediaIndividual,vendorIndividual);
      }
   }


   public void addVendorIndividual1(String vendorSafe,String vendorName, OWLNamedIndividual dbpediaIndividual, OWLNamedIndividual annotationStatus){
      OWLNamedIndividual vendorIndividual = setVendor(vendorSafe,vendorName);
      addIndividualsProperty(vendorIndividual,hasAnnotationStatus,annotationStatus);
      if (dbpediaIndividual!=null){
         addIndividualToClass(dbpediaIndividual,DBpediaResourceClass);
         addSameIndividuals(dbpediaIndividual,vendorIndividual);
      }
   }


   public OWLNamedIndividual setProduct(CPEProduct product){
      OWLNamedIndividual productIndividual  = df.getOWLNamedIndividual(IRI.create(iri+"#"+product.nameSafe));
      addIndividualToClass(productIndividual,ProductClass);
      addIndividualAnnotation (productIndividual,product.label,"en");
      addIndividualComment(productIndividual,product.nameShort,"en");
      addIndividualDataProperty(productIndividual,hasCPEName,product.nameCPEStyle);

      OWLNamedIndividual vendorIndividual = df.getOWLNamedIndividual(IRI.create(iri+"#"+product.vendorSafe));; 
      //OWLNamedIndividual vendorIndividual = setVendor(product.vendorSafe,product.vendor);

      addIndividualsProperty(vendorIndividual,producesProduct,productIndividual);
      addIndividualsProperty(productIndividual,isProducedByVendor,vendorIndividual);

      return productIndividual;
   }

   public void addProductIndividual(CPEProduct product, DBpediaResource res, OWLNamedIndividual annotationStatus){
      OWLNamedIndividual productIndividual  = setProduct(product);
      addIndividualsProperty(productIndividual,hasAnnotationStatus,annotationStatus);
      // if you want to add the DBpedia reference set res to not null and give AnnotatedStatus
      if (res!=null){
         OWLNamedIndividual dbpediaIndividual  = df.getOWLNamedIndividual(IRI.create(res.URI));
         addIndividualToClass(dbpediaIndividual,DBpediaResourceClass);
         addSameIndividuals(dbpediaIndividual,productIndividual);
      }
   }

   public void addProductIndividual1(CPEProduct product, OWLNamedIndividual dbpediaIndividual, OWLNamedIndividual annotationStatus){
      OWLNamedIndividual productIndividual  = setProduct(product);
      addIndividualsProperty(productIndividual,hasAnnotationStatus,annotationStatus);
      // if you want to add the DBpedia reference set dbpediaIndividual to not null and give AnnotatedStatus
      if (dbpediaIndividual!=null){
         addIndividualToClass(dbpediaIndividual,DBpediaResourceClass);
         addSameIndividuals(dbpediaIndividual,productIndividual);
      }
   }


   public void showProductsMap(){
      Set set = productsMap.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry me = (Map.Entry)iterator.next();
         String key = (String)me.getKey();
         CPEProduct product = (CPEProduct)me.getValue();
         System.out.println("  key: " +key);
         product.debugShow();
         //System.out.println(" name: " +product.name);
         //System.out.println("short: " +product.nameShort);
         System.out.println("--------------------------------------");
      }
   }
}


