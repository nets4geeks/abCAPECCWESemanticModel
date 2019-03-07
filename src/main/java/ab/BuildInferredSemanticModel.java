
package ab.applications;

import ab.parsers.*;
import ab.data.*;
import ab.owl.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;


class BuildInferredSemanticModel {

   public static void main(String args[]){
 
      if (args.length != 0) {

         PManager conf = new PManager();

         if (conf.init(args[0])) {
            String CAPEC_OWL_FilePath = conf.get("CAPEC_OWL_FilePath");
            String CAPEC_OWL_FilePath_Inferred = conf.get("CAPEC_OWL_FilePath_Inferred");
            String CAPEC_OWL_FilePath_InferredRDFXML = conf.get("CAPEC_OWL_FilePath_InferredRDFXML");         
            String CAPEC_OWL_FilePath_InferredTTL = conf.get("CAPEC_OWL_FilePath_InferredTTL");

            OWLProcessor processor1 = new OWLProcessor(); 
            System.out.println("starting reading of the ontology");
            if (processor1.initRead(CAPEC_OWL_FilePath,null)){
               processor1.initReasoner();
               processor1.fillOntology();
               processor1.save(CAPEC_OWL_FilePath_Inferred);
               processor1.saveRDFXML(CAPEC_OWL_FilePath_InferredRDFXML);
               processor1.saveTTL(CAPEC_OWL_FilePath_InferredTTL);

            } else { System.out.println("could not init OWL processor"); } 

         } else { System.out.println("could not find the configuration file!"); }

      } else { System.out.println("give the configuration file!"); }
   }

}
