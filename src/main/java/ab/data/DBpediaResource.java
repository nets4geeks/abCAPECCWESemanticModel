
package ab.data;

import java.util.*;


public class DBpediaResource{
   public String surfaceForm;
   public String URI = null;
   public String simularityScore;
   public String types;

   public DBpediaResource(){
   }

   public void debugShow(){
      System.out.println("---------------------------------------------------");
      System.out.println("surfaceForm: "+surfaceForm);
      System.out.println("URI: "+URI);
      System.out.println("simularityScore: "+simularityScore);
      System.out.println("types: "+types);
      System.out.println("---------------------------------------------------");
  }

  public double getScore(){
     return Double.parseDouble(simularityScore);
  }

  public boolean isEmpty(){
     if (URI == null) return true;
     return false;
  }

  public boolean checkType(String typeName){
     if (types !=null){
        String[] parts = types.split(",");
        for (int i=0;i<parts.length;i++){
           if ( typeName.equals(parts[i])) return true;
        }
     }
     return false;
  } 

}