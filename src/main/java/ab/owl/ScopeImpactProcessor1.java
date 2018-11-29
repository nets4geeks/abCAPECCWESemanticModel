
package ab.owl;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

import ab.parsers.*;
import ab.data.*;

public class ScopeImpactProcessor1 extends AbstractOWLProcessor{


   ArrayList<String> abstractionNames;
   ArrayList<String> names;


   // nothing to do here 
   public boolean initParser(String path){
      return true;
   }



/*   private int printClasses(OWLClass cls, OWLClass abstraction){
      reaz.flush();
      Set<OWLClass> subinfs = getSubClasses(cls);
      int i=0;
      for (OWLClass subcls : subinfs) {
         i++;
         System.out.println(getShortIRI(abstraction)+"|"+getShortIRI(subcls)+"|"+CWEVulnerability.getURL(getShortIRI(subcls))+"|"+getLabel(subcls));
      }
      return i;

   }*/


   public void showScopesImpacts(){
      Set<OWLClass> scopesimpacts = getSubClasses("#ScopeAndTechnicalImpact");
      showClassesListShort(scopesimpacts);

   }

   public ArrayList<String> getScopesImpactsSorted(){
      ArrayList<String> lst = new ArrayList<String>();
      Set<OWLClass> scopesimpacts = getSubClasses("#ScopeAndTechnicalImpact");
      for (OWLClass scopeimpact : scopesimpacts) {
         lst.add(getShortIRI(scopeimpact));
      }
      Collections.sort(lst);
      return lst;
   }


   public ArrayList<String> getAbstractionsSorted(){
      ArrayList<String> lst = new ArrayList<String>();
      Set<OWLClass> scopesimpacts = getSubClasses("#Abstraction");
      for (OWLClass scopeimpact : scopesimpacts) {
         lst.add(getShortIRI(scopeimpact));
      }
      Collections.sort(lst);
      return lst;
   }


   // build the OWL model and do nothing
   public void build(){
      abstractionNames = getAbstractionsSorted();
      names = getScopesImpactsSorted();

      OWLObjectProperty abstractionProperty = df.getOWLObjectProperty(IRI.create(iri+"#hasAbstraction" ));    
      OWLObjectProperty scopeProperty = df.getOWLObjectProperty(IRI.create(iri+"#scopesANDimpacts" ));

      for (int i=0;i<names.size();i++){
         String shortName = names.get(i);
         String name = iri+"#"+shortName;
         OWLClass scopeimpact = df.getOWLClass(IRI.create(name));

         String defClassName = iri+"#CWE_ScopesANDImpacts___"+shortName;
         OWLClass defClass = df.getOWLClass(IRI.create(defClassName));
         addSubClass(defClassName,iri+"#CWE_Inferred");
         addDefinedClass(defClass,scopeProperty,scopeimpact);

         for (int k=0; k<abstractionNames.size();k++){
            String curAbsName = abstractionNames.get(k);
            OWLClass abs = df.getOWLClass(IRI.create(iri+"#"+curAbsName));

            String defAClassName = defClassName+"___"+curAbsName;
            OWLClass defAClass = df.getOWLClass(IRI.create(defAClassName));
            addSubClass(defAClassName,iri+"#CWE_Inferred");
            addDefinedClass(defAClass,scopeProperty,scopeimpact, abstractionProperty,abs);
         }
      }
   }


   public void process(){

      //abstractionNames = getAbstractionsSorted();
      //names = getScopesImpactsSorted();

      reaz.flush();

      OWLObjectProperty abstractionProperty = df.getOWLObjectProperty(IRI.create(iri+"#hasAbstraction" ));    
      OWLObjectProperty scopeProperty = df.getOWLObjectProperty(IRI.create(iri+"#scopesANDimpacts" ));

      ArrayList<StaterEntry> staters = new ArrayList<StaterEntry>();


      for (int i=0;i<names.size();i++){
         String shortName = names.get(i);
         String name = iri+"#"+shortName;
         OWLClass scopeimpact = df.getOWLClass(IRI.create(name));

         String defClassName = iri+"#CWE_ScopesANDImpacts___"+shortName;
         OWLClass defClass = df.getOWLClass(IRI.create(defClassName));
         System.out.println("Scope___Impact: "+getShortIRI(defClass));
         
         StaterEntry stater = new StaterEntry();
         stater.name = getShortIRI(defClass);

         for (int k=0; k<abstractionNames.size();k++){
            String curAbsName = abstractionNames.get(k);
            OWLClass abs = df.getOWLClass(IRI.create(iri+"#"+curAbsName));

            String defAClassName = defClassName+"___"+curAbsName;
            OWLClass defAClass = df.getOWLClass(IRI.create(defAClassName));
            int p = genABS (defAClass, curAbsName);
            AbstractionEntry a = new AbstractionEntry();
            a.name = curAbsName;
            a.val = p;
            stater.items.add(a);

         }
         staters.add(stater);

      }

      StaterEntry.show(staters);

   }



   public int genABS(OWLClass cls, String abs){
      
      Set<OWLClass> subinfs = getSubClasses(cls);
      int i=0;
      for (OWLClass subcls : subinfs) {
         if (!subcls.asOWLClass().getIRI().toString().equals("http://www.w3.org/2002/07/owl#Nothing") ){
           i++;
           String cwe = getShortIRI(subcls);
           System.out.println("  "+abs+" | "+cwe+" | "+ getLabel(subcls)+" | "+CWEVulnerability.getURL(cwe));
         }
      }
      //System.out.println("total: "+i);
      return i;
   }



}



