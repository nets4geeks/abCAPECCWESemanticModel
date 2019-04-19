
package ab.applications;

import ab.parsers.*;
import ab.data.*;
import ab.owl.*;

class BuildDBpediaProductModel {

   public static void main(String args[]){
 
      if (args.length != 0) {
         PManager conf = new PManager();
         if (conf.init(args[0])) {
            String CPE_FILE = conf.get("CPE_FILE");
            String LOG_FILE = conf.get("LOG_FILE");
            String BASECPEDBPEDIAOWL_TTLFILE= conf.get("BASECPEDBPEDIAOWL_TTLFILE");
            String CPEDBPEDIAOWL_IRI = conf.get("CPEDBPEDIAOWL_IRI");
            String CPEDBPEDIAOWL_TTLFILE = conf.get("CPEDBPEDIAOWL_TTLFILE");
            String ADDCPEDBPEDIAOWL_TTLFILE = conf.get("ADDCPEDBPEDIAOWL_TTLFILE");
            String CHECKDBPEDIA = conf.get("CHECKDBPEDIA");
            String ANNOTATE_URL = conf.get("ANNOTATE_URL");

            System.out.println(ADDCPEDBPEDIAOWL_TTLFILE);

            CPEProcessor processor = new CPEProcessor();

            if (processor.init(BASECPEDBPEDIAOWL_TTLFILE,CPE_FILE)){

               if (ADDCPEDBPEDIAOWL_TTLFILE !=null) {
                  if (! (processor.initCoProcessor(ADDCPEDBPEDIAOWL_TTLFILE)) ) System.out.println("failed to read additional source file, but continue ..." + ADDCPEDBPEDIAOWL_TTLFILE);
               }

               if (CHECKDBPEDIA !=null){
                  if (CHECKDBPEDIA.equals("NO")) processor.CHECKDBPEDIA = false;
               }

               if (ANNOTATE_URL !=null){
                  processor.setAnnotateURL(ANNOTATE_URL);
               }

               processor.process(LOG_FILE);
               processor.saveTTL(CPEDBPEDIAOWL_TTLFILE);


            } else { System.out.println("could not init the processor!"); }

         } else { System.out.println("could not find the configuration file!"); }

      } else { System.out.println("give the configuration file!"); }
   }

}
