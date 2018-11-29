
package ab.data;

import java.util.*;

// CAPEC attack as a object
public class CAPECAttack{
   public String ID;
   public String Name;
   public String Description;
   public String ResourcesRequired;
   public String LikeHoodText;
   public String SeverityText;
   public List<CAPECScope> Scopes;
   public List<String> CWEs;
   public List<String> Methods;

   public CAPECAttack(){
      Scopes = new ArrayList<CAPECScope>();
      CWEs = new ArrayList<String>();
      Methods = new ArrayList<String>();
   }


   // remove me
   public void debugShowAttack(){
      System.out.println("----------------------");
      System.out.println("ID: "+ID);
      System.out.println("NAME: "+Name);
      System.out.println("DESCRIPTION: "+Description);
      //System.out.println("RESOURCES_REQUIRED: "+ResourcesRequired);
      System.out.println("LIKEHOOD: "+LikeHoodText);
      System.out.println("SEVERITY: "+SeverityText);
      System.out.println("SCOPES & IMPACTS:");
      for (int i=0;i<Scopes.size();i++){
          CAPECScope tmp = Scopes.get(i);
          System.out.println (tmp.toString());
      }
      System.out.println("CWES: "+CWEs);
      System.out.println("----------------------");

   }

}