
package ab.data;

import java.util.*;


public class CPEProduct{
   public String part;       // the part field from CPE
   public String vendor;     // the vendor field from CPE
   public String product;    // the product field from CPE
   public String version;    // the version field from CPE
   public String update;     // the update field from CPE
   public String name;       // long name from CPE XML
   public String CPE;        // the CPE string i.e. 
   public String nameShort;  // long name from CPE XML without version
   public String nameSafe;   // safe1(vendor)+"_"+safe1(product), identifier of a product in the semantic model
   public String vendorSafe; // {safe1(vendor)}_vendor, identifier of a vendor in the semantic model (+_vendor to distinct products and vendors having the same names)
   public String nameCPEStyle; // "part:vendor:product"
   public String label; // "vendor:product"

   public CPEProduct(){
   }

   public CPEProduct(String _vendor, String _product){ 
      vendor = _vendor;
      product = _product;
      nameSafe = getSafeName();
      vendorSafe = safe1(vendor)+"_vendor"; 

   }

   public CPEProduct(String cpe){ 
      initCPE(cpe);
   }

   public void initCPE(String cpe){
      CPE = cpe;
      String[] parts = cpe.split(":");
      part = parts[2];
      vendor = parts[3];
      product = parts[4];
      version = parts[5];
      update = parts[6];
      nameShort = getNameShort();
      nameSafe = getSafeName();
      vendorSafe = safe1(vendor)+"_vendor"; 
      nameCPEStyle = getCPEStyleName();
      label = getCPELabel();
   }

   public String getNameShort(){
      if (name!=null){
         if (!version.equals("*")) {
            if (!update.equals("*")){
               return name.replace(version,"").replace(update,"");
            }
            return name.replace(version,"");
         }
         return name;
      }
      return null;
   }

   public String getCPEStyleName(){
      return part+":"+vendor+":"+product;
   }

   public String getCPELabel(){
      return vendor+":"+product;
   }

   public void debugShow(){
      System.out.println("----------------------------");
      System.out.println ("CPE: "+CPE);
      System.out.println ("name: "+name);
      System.out.println ("short name: "+nameShort);
      System.out.println ("safe name: "+nameSafe);
      System.out.println ("part: "+part);
      System.out.println ("vendor: "+vendor);
      System.out.println ("product: "+product);
      System.out.println ("version: "+version);
      System.out.println("----------------------------");

   }

   // don't change this!
   public String getSafeName(){
      if ( (!product.equals("")) & (!vendor.equals("")) ){
         return safe1(vendor)+"_"+safe1(product);
      }
      return null; 
   }

   // don't change this!
   public static String safe1(String in){
      String in1 = in;
      in1 = in1.replace(" ","_");
      in1 = in1.replace(":","colon");
      in1 = in1.replace("(","op");
      in1 = in1.replace(")","cp");
      in1 = in1.replace("/","sl");
      in1 = in1.replace("\\","sl");
      in1 = in1.replace("^","cr");
      in1 = in1.replace("+","plus");
      in1 = in1.replace("#","sharp");
      in1 = in1.replace("$","dollar");
      in1 = in1.replace("@","at");
      in1 = in1.replace(".","dot");
      in1 = in1.replace("-","dash");
      in1 = in1.replace("'","_");
      in1 = in1.replace("&","and");
      in1 = in1.replace("!","em");
      in1 = in1.replace("?","qm");
      in1 = in1.replace(",","comma");
      in1 = in1.replace(">","gt");
      in1 = in1.replace("<","ls");
      in1 = in1.replace("\"","qt");
      in1 = in1.replace("*","star");
      in1 = in1.replace("%","pr");
      in1 = in1.replace("[","sb");
      in1 = in1.replace("]","sb");
      in1 = in1.replace("|","p");
      if (Character.isDigit(in1.charAt(0))) in1 = "n" + in1;
      return in1;
   }


}