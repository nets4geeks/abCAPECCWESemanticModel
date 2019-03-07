
package ab.parsers;


// To read XML the standard package javax.xml.parsers has been used; 
// it contains API, able to manipulate an object model (DOM - Document Object Model) of XML
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;

import ab.data.*;



public abstract class AbstractParser{

   // to enable/disable log messages set this by hand as true/false
   public boolean DEBUG;

   // it wants to be populated in subclass
   protected String theMainListName;
   // here will be elements having theMainListName
   protected NodeList theMainList;

   protected DocumentBuilderFactory dbFactory;
   protected DocumentBuilder dBuilder;
   protected Document doc; 

   private String parserName;
   private String dataFile;

   public AbstractParser (){
      parserName = getClass().getName();
      DEBUG = true;
   }

   // to build DOM give absolute path to data file  
   public boolean init(String _dataFile){
      dataFile = _dataFile;
      try {
         File fXmlFile = new File(dataFile);
         dbFactory = DocumentBuilderFactory.newInstance();
         dBuilder = dbFactory.newDocumentBuilder();
         doc = dBuilder.parse(fXmlFile);
      } catch (Exception e) {
         e.printStackTrace();
         log("could not read xml file "+dataFile);
         return false;
      }

      theMainList = getNodeListByName(theMainListName);
      if (theMainList == null) {
         log("couldn't find any node with name "+theMainListName);
         return false;
      }
      return true;
   }

  
   public NodeList getNodeList(){
      return theMainList;
   }

   protected void log(String msg){
     if (DEBUG) {
        System.out.println("["+parserName+"]: "+msg);
     }
   }

   // to get list of nodes give node name
   // if there is not any node return null
   protected NodeList getNodeListByName(String tag){
      NodeList nodelist = doc.getElementsByTagName(tag);
      if (nodelist.getLength() == 0) return null;
      return nodelist;
   }

   // to get child node from parent node by its name
   // if nothing is found return null
   protected Node getFirstChildNodeByName(Node parent,String tag){
      if (parent == null) return null;
      NodeList nodelist = parent.getChildNodes();
      //iterate all child nodes
      for (int m = 0; m<nodelist.getLength(); m++){
          Node tmpnode = nodelist.item(m);
          if (tmpnode.getNodeName().equals(tag)) return tmpnode;
      }
      return null;
   }


   // to get node having "ID" attribute give this attribute as a string
   // if nothing is found return null
   protected Node getNodeByID(NodeList nodelist,String ID){
      for (int i=0;i<nodelist.getLength();i++){
         Node node = nodelist.item(i);
         NamedNodeMap nodemap = node.getAttributes();
         if (nodemap !=null) {
            Attr attr = (Attr)nodemap.getNamedItem("ID");
            if (attr !=null) {
               if (ID.equals(attr.getValue())) return node;
            }
         }
       }
      return null;
   }

   // to get node from theMainList and having "ID" attribute give this attribute as a string
   protected Node getNodeByID(String ID){
     return getNodeByID(theMainList,ID);
   }

   



   // remove
   public void debugShowNodeByID(String ID){
      Node node = getNodeByID(ID);
      debugShowNode(node);
   }

   // remove
   public void debugShowNode(Node node){

      System.out.println("------------------");
      System.out.println("NODE: "+node.getNodeName());

      NamedNodeMap nodemap = node.getAttributes();
      System.out.println("ATTRIBUTES:");
      if (nodemap !=null){
         for (int k = 0; k < nodemap.getLength(); k++) {
           Attr attr = (Attr) nodemap.item(k);
           System.out.println(attr.getName()+"="+ attr.getValue());
         }
      }

      System.out.println("CHILD NODES:");
      NodeList list1 = node.getChildNodes();
      for (int m = 0; m<list1.getLength(); m++){
          Node tmp = list1.item(m);
          System.out.println(tmp.getNodeName());
      }

      System.out.println("------------------");

   }

   // remove
   public void debugShowNodeList(String tag){
      NodeList nodelist = getNodeListByName(tag);
      for (int i=0;i<nodelist.getLength();i++){
         Node node = nodelist.item(i);
          System.out.print("name:"+node.getNodeName());
          NamedNodeMap nodemap = node.getAttributes();
          if (nodemap !=null){
             for (int k = 0; k < nodemap.getLength(); k++) {
               Attr attr = (Attr) nodemap.item(k);
               System.out.print(" |attr:" + attr.getName() + " value:" + attr.getValue());
             }
          }
          System.out.println("\n");
       }
    }

}