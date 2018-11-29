
package ab.data;

import java.util.*;


public class StaterEntry{
   public String name;
   public ArrayList<AbstractionEntry> items;

   public StaterEntry(){
      items = new ArrayList<AbstractionEntry>();
   }


   public static void show(ArrayList<StaterEntry> staters){
      System.out.println("\nSome statistics: ");

      for (int i=0; i< staters.size(); i++){
         StaterEntry stater = staters.get(i);
         System.out.print(stater.name);
         for (int ii=0; ii< stater.items.size(); ii++){
            AbstractionEntry a = stater.items.get(ii);
            if (a.val>10){
              System.out.print("     "+a.name+": "+a.val);
            }
            //System.out.print("    "+a.val);
         }
         System.out.println();

      }
   }

}