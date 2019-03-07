
package ab.data;

import java.util.*;

// "cvssV2" : {
//    "version" : "2.0",
//    "vectorString" : "AV:N/AC:M/Au:N/C:N/I:N/A:C",
//    "accessVector" : "NETWORK",
//    "accessComplexity" : "MEDIUM",
//    "authentication" : "NONE",
//    "confidentialityImpact" : "NONE",
//    "integrityImpact" : "NONE",
//    "availabilityImpact" : "COMPLETE",
//    "baseScore" : 7.1
//  }

public class CVSSv2{
   public String vectorString;
   public String accessVector;
   public String accessComplexity;
   public String authentication;
   public String confidentialityImpact;
   public String integrityImpact;
   public String availabilityImpact;
   public float baseScore;   

   public CVSSv2(){
   }


  public void debugShow(){
     System.out.println("CVSSv2:::");
     System.out.println("vectorString: "+vectorString);
     System.out.println("accessVector: "+accessVector);
     System.out.println("accessComplexity: "+accessComplexity);
     System.out.println("authentication: "+authentication);
     System.out.println("confidentialityImpact: "+confidentialityImpact);
     System.out.println("integrityImpact: "+integrityImpact);
     System.out.println("availabilityImpact: "+availabilityImpact);
     System.out.println("baseScore: "+baseScore);
  }

}