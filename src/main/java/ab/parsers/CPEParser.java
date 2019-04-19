
package ab.parsers;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;

import ab.data.*;

// eats CPE in XML
public class CPEParser extends AbstractParser{

   public CPEParser (){
      super();
      theMainListName = "cpe-item";
   }

   public CPEProduct getCPEProductByNode(Node node){
      CPEProduct product = new CPEProduct();
      
      NodeList nodelist = node.getChildNodes();
      for (int m = 0; m<nodelist.getLength(); m++){
         Node tmpnode = nodelist.item(m);
         String tmpname = tmpnode.getNodeName();

         if (tmpname.equals("#text")) continue;

         if (tmpname.equals("cpe-23:cpe23-item")) {
            NamedNodeMap nodemap = tmpnode.getAttributes();
            if (nodemap != null ) {
               for (int i=0; i< nodemap.getLength();i++) {
                  Attr attr = (Attr)nodemap.item(i);
                  if (attr.getName().equals("name")) product.initCPE(attr.getValue());
               }
            } else{
              //do something if cpe-23:cpe23-item does not have attributes
              log("cpe-23:cpe23-item does not have attributes");
            }

         } 

         if (tmpname.equals("title")) {
            NamedNodeMap nodemap = tmpnode.getAttributes();
            if (nodemap != null ) {
               for (int i=0; i< nodemap.getLength();i++) {
                  Attr attr = (Attr)nodemap.item(i);
                  if (attr.getName().equals("xml:lang")){
                     if (attr.getValue().equals("en-US")) {
                        Node tmpnode1 = getFirstChildNodeByName(tmpnode,"#text");
                        if (tmpnode1 != null) product.name = tmpnode1.getNodeValue();
                     }
                  } 
               }
            } else{
               //do something if title does not have attributes
              log("title does not have attributes");

            }
         } 

      }

      return product;
   }


}


