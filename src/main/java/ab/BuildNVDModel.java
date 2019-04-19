
package ab.applications;

import ab.parsers.*;
import ab.data.*;
import ab.owl.*;

class BuildNVDModel {

   public static void main(String args[]){
 
      if (args.length != 0) {
         PManager conf = new PManager();
         if (conf.init(args[0])) {
            String NVD_FOLDER = conf.get("NVD_FOLDER");
            String LOG_FOLDER = conf.get("LOG_FOLDER");
            String NVDOWL_IRI = conf.get("NVDOWL_IRI");
            String NVDOWL_FILE = conf.get("NVDOWL_FILE");
            String NVDOWL_TTLFILE = conf.get("NVDOWL_TTLFILE");
            String CPEDBPEDIAOWL_IRI = conf.get("CPEDBPEDIAOWL_IRI");
            String CAPEC_OWL_IRI = conf.get("CAPEC_OWL_IRI");

            String fname = "nvdcve-1.0-2018.json";

            NVDProcessor processor = new NVDProcessor();
            // this takes IRI & !!!folder with CVE files!!! as arguments
            if (processor.init(NVDOWL_IRI,NVD_FOLDER,LOG_FOLDER,CPEDBPEDIAOWL_IRI,CAPEC_OWL_IRI)){
               processor.process();
               processor.save(NVDOWL_FILE);
               processor.saveTTL(NVDOWL_TTLFILE);
            } else {
               System.out.println("could not init processor");
            }

         } else { System.out.println("could not find the configuration file!"); }

      } else { System.out.println("give the configuration file!"); }
   }

}
