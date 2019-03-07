
package ab.data;

import java.util.*;


public class Product{
   public String vendor;
   public String product;

   public Product(){
   }

   public Product(String _vendor, String _product){ 
      vendor = _vendor;
      product = _product;
   }

   public Product(String cpe){ 
      String[] parts = cpe.split(":");
      vendor = parts[3];
      product = parts[4];
   }



}