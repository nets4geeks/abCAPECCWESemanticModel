
package ab.applications;

import ab.parsers.*;
import ab.data.*;
import ab.owl.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;


class BuildSemanticModel {

   public static void main(String args[]){
 
      if (args.length != 0) {

         PManager conf = new PManager();

         if (conf.init(args[0])) {
            String CAPECFilePath = conf.get("CAPECFilePath");
            String CWEFilePath = conf.get("CWEFilePath");;
            String STIX20_IRINAME = conf.get("STIX20_IRINAME");
            String CAPEC_OWL_IRI = conf.get("CAPEC_OWL_IRI");
            String CAPEC_OWL_FilePath = conf.get("CAPEC_OWL_FilePath");
            
            CAPECProcessor capecprocessor = new CAPECProcessor(); 
            if (capecprocessor.initCreate(CAPEC_OWL_IRI,CAPECFilePath)){


                capecprocessor.addBaseIRI(STIX20_IRINAME);
                System.out.println("...processing "+ CAPECFilePath);
                capecprocessor.process();

                OWLOntology ont = capecprocessor.getOntology();
 
                CWEProcessor cweprocessor = new CWEProcessor();
                if (cweprocessor.initCopy(CAPEC_OWL_IRI,CWEFilePath,ont)){
                   cweprocessor.addBaseIRI(STIX20_IRINAME);

                   //cweprocessor.isDetectionMethodEnabled = false;
                   //cweprocessor.isMitigationMethodEnabled = false;
                   System.out.println("...processing "+ CWEFilePath);
                   cweprocessor.process();

                   cweprocessor.save(CAPEC_OWL_FilePath);
                   System.out.println("well done! see the result in the "+CAPEC_OWL_FilePath);


                } else { System.out.println("could not init OWL processor"); }

            } else { System.out.println("could not init CAPEC processor"); }

         } else { System.out.println("could not find the configuration file!"); }

      } else { System.out.println("give the configuration file!"); }
   }

}
