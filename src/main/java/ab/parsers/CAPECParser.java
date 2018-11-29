
package ab.parsers;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;

import ab.data.*;

// eats CAPEC in XML
public class CAPECParser extends AbstractParser{

   public NodeList patterns;

   public CAPECParser (){
      super();
      theMainListName = "capec:Attack_Pattern";
   }

   // get CAPEC Attack by its ID
   // return null if there is no node or getCAPECAttackByNode failed
   public CAPECAttack getCAPECAttackByID(String ID){
      Node node = getNodeByID(ID);
      if (node == null) {
         log("could not find node with ID "+ID);
         return null;
      }
      return getCAPECAttackByNode(node);
   }


   public CAPECAttack getCAPECAttackByNode(Node node){

      CAPECAttack attack = new CAPECAttack();

      // get all attributes
      NamedNodeMap nodemap = node.getAttributes();
      if (nodemap == null ) {
         log("node has no attributes");
         return null;
      }
      // iterate all attributes
      for (int i=0; i< nodemap.getLength();i++) {
         Attr attr = (Attr)nodemap.item(i);
         if (attr.getName().equals("Name")) attack.Name = attr.getValue();
         if (attr.getName().equals("ID")) attack.ID = attr.getValue();
      }
      if ( attack.ID ==null ) {
         log("node has no ID");
         return null;
      }

      String ID = attack.ID;

      // get all child nodes
      NodeList nodelist = node.getChildNodes();
      // iterate all child nodes
      for (int m = 0; m<nodelist.getLength(); m++){
          Node tmpnode = nodelist.item(m);
          String tmpname = tmpnode.getNodeName();
          
          if (tmpname.equals("#text")) continue;

          if (tmpname.equals("capec:Description")){
             Node tmpnode1 = getFirstChildNodeByName(getFirstChildNodeByName(getFirstChildNodeByName(tmpnode,"capec:Summary"),"capec:Text"),"#text");
             if (tmpnode1 != null) {
                attack.Description = tmpnode1.getNodeValue();
             }else{
                log("could not read description of attack (ID="+ID+")");
             }
             continue;
          }

          if (tmpname.equals("capec:Typical_Likelihood_of_Exploit")){
             Node tmpnode1 = getFirstChildNodeByName(getFirstChildNodeByName(tmpnode,"capec:Likelihood"),"#text");
             if (tmpnode1 != null) {
                attack.LikeHoodText = Normalizer.setDegree(tmpnode1.getNodeValue());
             }else{
                log("could not read Likehood of attack (ID="+ID+")");
             }
             continue;
          }

          // get scope & impact
          if (tmpname.equals("capec:Attack_Motivation-Consequences")){
             NodeList tmpnodelist = tmpnode.getChildNodes();
             for (int i=0;i<tmpnodelist.getLength();i++){
                 Node tmpnode1 = tmpnodelist.item(i);
                 if (tmpnode1.getNodeName().equals("capec:Attack_Motivation-Consequence")){
                     CAPECScope tmpScope = new CAPECScope();
                     NodeList tmpnodelist1 = tmpnode1.getChildNodes();
                     
                     for (int k=0;k<tmpnodelist1.getLength();k++){
                         Node tmpnode2 = tmpnodelist1.item(k);
                         String tmpnode2name = tmpnode2.getNodeName();
                         if (tmpnode2name.equals("#text")) continue;

                         Node tmpnode3 = getFirstChildNodeByName(tmpnode2,"#text");
                         if (tmpnode3 !=null){
                             if (tmpnode2name.equals("capec:Consequence_Scope")){
                                tmpScope.Scopes.add(Normalizer.setScope(tmpnode3.getNodeValue()));
                             }
                             if (tmpnode2name.equals("capec:Consequence_Technical_Impact")){
                                tmpScope.Impacts.add(Normalizer.setTechnicalImpact(tmpnode3.getNodeValue()));
                             }
                         } else{
                             log("could not read scope (ID="+ID+")");
                         }
                      }
                      attack.Scopes.add(tmpScope);
                   } 
               }
               continue;
            }

           // CWEs
           if (tmpname.equals("capec:Related_Weaknesses")){
              NodeList tmpnodelist = tmpnode.getChildNodes();
              for (int i=0;i<tmpnodelist.getLength();i++){
                   Node tmpnode1 = tmpnodelist.item(i);
                   if (tmpnode1.getNodeName().equals("capec:Related_Weakness")){
                      Node tmpnode2 = getFirstChildNodeByName(getFirstChildNodeByName(tmpnode1,"capec:CWE_ID"),"#text");
                      if (tmpnode2 != null) {
                         attack.CWEs.add(tmpnode2.getNodeValue());
                      } else {
                         log("could not read CWEs (ID="+ID+")");
                      }
                   }
              }
              continue;
           }

           if (tmpname.equals("capec:Typical_Severity")){
              Node tmpnode1 = getFirstChildNodeByName(tmpnode,"#text");
              if (tmpnode1 != null){
                 attack.SeverityText = Normalizer.setDegree(tmpnode1.getNodeValue());
              } else {
                 log("could not read Severity (ID="+ID+")");
              }
              continue;
           }

           if (tmpname.equals("capec:Methods_of_Attack")){
              NodeList tmpnodelist = tmpnode.getChildNodes();
              for (int i=0;i<tmpnodelist.getLength();i++){
                  Node tmpnode1 = tmpnodelist.item(i);
                  if (tmpnode1.getNodeName().equals("capec:Method_of_Attack")){
                     Node tmpnode2 = getFirstChildNodeByName(tmpnode1,"#text");
                     if (tmpnode2 != null) {
                        attack.Methods.add( Normalizer.setMethodOfAttack(tmpnode2.getNodeValue()));
                     } else {
                        log("could not read CWEs (ID="+ID+")");
                     }
                  }
              }
              continue;
    

           }

             // could be multitagged
          /* if (tmpname == "capec:Resources_Required"){
             Node tmpnode1 = getFirstChildNodeByName(getFirstChildNodeByName(tmpnode,"capec:Text"),"#text");
             if (tmpnode1 != null) {
                attack.ResourcesRequired = tmpnode1.getNodeValue();
             }else{
                log("could not read Resources_Required of attack (ID="+ID+")");
             }
             continue;
          }*/

      }
 
      return attack;
   }

}


