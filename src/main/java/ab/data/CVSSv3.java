
package ab.data;

import java.util.*;

//     "cvssV3" : {
//        "version" : "3.0",
//        "vectorString" : "CVSS:3.0/AV:N/AC:H/PR:N/UI:N/S:U/C:N/I:N/A:H",
//        "attackVector" : "NETWORK",
//        "attackComplexity" : "HIGH",
//        "privilegesRequired" : "NONE",
//        "userInteraction" : "NONE",
//        "scope" : "UNCHANGED",
//        "confidentialityImpact" : "NONE",
//        "integrityImpact" : "NONE",
//        "availabilityImpact" : "HIGH",
//        "baseScore" : 5.9,
//        "baseSeverity" : "MEDIUM"
//      },

public class CVSSv3{
   public String vectorString;
   public String attackVector;
   public String attackComplexity;
   public String privilegesRequired;
   public String userInteraction;
   public String scope;
   public String confidentialityImpact;
   public String integrityImpact;
   public String availabilityImpact;
   public String baseSeverity;
   public float baseScore;   

   public CVSSv3(){
   }


  public void debugShow(){
      System.out.println("CVSSv3:::");
      System.out.println("vectorString: "+vectorString);
      System.out.println("attackVector: "+attackVector);
      System.out.println("attackComplexity: "+attackComplexity);
      System.out.println("privilegesRequired: "+privilegesRequired);
      System.out.println("userInteraction: "+userInteraction);
      System.out.println("scope: "+scope);
      System.out.println("confidentialityImpact: "+confidentialityImpact);
      System.out.println("integrityImpact: "+integrityImpact);
      System.out.println("availabilityImpact: "+availabilityImpact);
      System.out.println("baseSeverity: "+baseSeverity);
      System.out.println("baseScore: "+baseScore);
  }

}