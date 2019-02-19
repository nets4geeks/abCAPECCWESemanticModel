
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


// a simple processor for anything you want
public class OWLProcessor extends AbstractOWLProcessor{

   // do not need to parse external xml files
   public boolean initParser(String path){
      return true;
   }


}


