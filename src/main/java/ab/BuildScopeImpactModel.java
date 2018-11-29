
package ab.applications;

import ab.parsers.*;
import ab.data.*;
import ab.owl.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;

import java.util.*;

class BuildScopeImpactModel {

   // the CAPEC Attacks ontology IRI

   public static void main(String args[]){

      if (args.length != 0) {
         PManager conf = new PManager();
         if (conf.init(args[0])) {
            String CAPEC_OWL_IRI = conf.get("CAPEC_OWL_IRI");
            String CAPEC_OWL_FilePath = conf.get("CAPEC_OWL_FilePath");
            //String SCOPE_IMPACT_IRI = conf.get("SCOPE_IMPACT_IRI");
            String SCOPE_IMPACT_FilePath = conf.get("SCOPE_IMPACT_FilePath");
 
            ScopeImpactProcessor processor = new ScopeImpactProcessor();

            if (processor.initRead(CAPEC_OWL_FilePath,null)){
                System.out.println("building ScopeImpact model ...");
                processor.process();
                System.out.println("saving the  model ..."); 
                processor.save(SCOPE_IMPACT_FilePath);
 
            }else{ System.out.println("could not init the OWL processor");}


         } else { System.out.println("could not find the configuration file");}


      } else { System.out.println("give me the configuration!"); }

//////



   } 
}
