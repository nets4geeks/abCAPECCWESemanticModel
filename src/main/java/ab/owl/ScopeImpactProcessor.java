
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

public class ScopeImpactProcessor extends AbstractOWLProcessor{

   // we need a OWLProcessor to read the CAPEC&CWE model
   //protected OWLProcessor reader;

   // instead of initing a parser here the reader of an ontology is inited
   public boolean initParser(String path){
      return true;
//      reader = new OWLProcessor();
//      if (reader.initRead(path,null)) return true;
//      else return false;
      
   }

   public void process(){

      // obtains a list of abstractions from the CAPEC&CWE model
      Set<OWLClass> abstractions = getSubClasses("#Abstraction");
      // obtain a list of scopesimpacts
      Set<OWLClass> scopesimpacts = getSubClasses("#ScopeAndTechnicalImpact");
 
      for (OWLClass scopeimpact : scopesimpacts) {
         String shortScopeImpact = getShortIRI(scopeimpact);

         String defClassName = iri+"#CWE_ScopesANDImpacts___"+shortScopeImpact;
         OWLClass defClass = df.getOWLClass(IRI.create(defClassName));
         addSubClass(defClassName,getIRI()+"#CWE");

         Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
         arguments.add (defClass);

         OWLObjectProperty scopeProperty = df.getOWLObjectProperty(IRI.create(getIRI()+"#scopesANDimpacts" ));
         arguments.add(df.getOWLObjectSomeValuesFrom(scopeProperty,scopeimpact));

         // remove me
         //OWLObjectProperty abstractionProperty = df.getOWLObjectProperty(IRI.create(reader.getIRI()+"#hasAbstraction" ));
         //String abstractionClassName = reader.getIRI()+"#base";
         //OWLClass abstraction = df.getOWLClass(IRI.create(abstractionClassName));
         //arguments.add(df.getOWLObjectSomeValuesFrom(abstractionProperty,abstraction));
         //

         OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
         o.add(axiom);
      }

   }

}


