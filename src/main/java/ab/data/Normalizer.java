
package ab.data;

import java.util.*;

// CAPEC attack as a object
public class Normalizer {

   public static String setDegree(String in){
      if (in.equals("Very Low")) in = "1";
      if (in.equals("Very High")) in = "8";
      if (in.equals("High to Very High")) in = "7";
      if (in.equals("Medium to High")) in = "5";
      if (in.equals("Low to Medium")) in = "3";
      if (in.equals("High")) in = "6";
      if (in.equals("Medium")) in = "4";
      if (in.equals("Low")) in = "2";
      return in;
   }

   public static String setScope(String in){
      if (in.equals("Access Control")) return "Access_Control";
      if (in.equals("Non-Repudiation")) return "Non_Repudiation";
      if (in.equals("Other")) return "Other_Scope";
      return in;
   }


   public static String setPhase(String in){
      if (in.equals("Other")) return "Other_Phase";
      return safe(in);
   }


  public static String setCourseOfAction(String in){
      if (in.equals("Other")) return "Other_CourseOfAction";
      return safe(in);
   }


   public static String setTechnicalImpact(String in){
      String tmp = in.toLowerCase();
      String in1 = tmp;
      if (tmp.equals("alter execution logic")) in1 = "Alter execution logic";
      if (tmp.equals("bypass protection mechanism")) in1 = "Bypass protection mechanism";
      if (tmp.equals("bypass protection mechanism")) in1 = "Bypass protection mechanism";
      if (tmp.equals("dos: amplification")) in1 = "DoS: amplification";
      if (tmp.equals("dos: instability")) in1 = "DoS: instability";
      if (tmp.equals("dos: resource consumption (cpu)")) in1 = "DoS: resource consumption (CPU)";
      if (tmp.equals("dos: resource consumption (memory)")) in1 = "DoS: resource consumption (memory)";
      if (tmp.equals("dos: resource consumption (other)")) in1 = "DoS: resource consumption (other)";
      if (tmp.equals("execute unauthorized code or commands")) in1 = "Execute unauthorized code or commands";
      if (tmp.equals("hide activities")) in1 = "Hide activities";
      if (tmp.equals("modify memory")) in1 = "Modify memory";
      if (tmp.equals("modify application data")) in1 = "Modify application data";
      if (tmp.equals("modify application data")) in1 = "Modify application data";
      if (tmp.equals("modify files or directories")) in1 = "Modify files or directories";
      if (tmp.equals("read memory")) in1 = "Read memory";
      if (tmp.equals("read application data")) in1 = "Read application data";
      if (tmp.equals("read files or directories")) in1 = "Read files or directories";
      if (tmp.equals("unexpected state")) in1 = "Unexpected state";
      if (tmp.equals("quality degradation")) in1 = "Quality degradation";
      if (tmp.equals("other")) in1 = "Other_Impact";

      if (in.equals("DoS: Crash, Exit, or Restart")) in1 = "DoS: crash / exit / restart";
      if (in.equals("Varies by Context")) in1 = "Varies by context";
      if (in.equals("\"Varies by context\"")) in1 = "Varies by context";
      if (in.equals("Gain Privileges or Assume Identity")) in1 = "Gain privileges / assume identity";


      return safe(in1);
   }


   public static String setMethodOfAttack(String in){
      return safe(in);
      
   }

   public static String safe(String in){
      String in1 = in;
      in1 = in1.replace(" ","_");
      in1 = in1.replace(":","_");
      in1 = in1.replace("(","_");
      in1 = in1.replace(")","_");
      in1 = in1.replace("/","_");
      return in1;
   }
   
   // todo remove all non-alphabetic symbols and add something to the begining if it's a digit
   public static String safe1(String in){
      String in1 = in;
      in1 = in1.replace(" ","_");
      in1 = in1.replace(":","_");
      in1 = in1.replace("(","_");
      in1 = in1.replace(")","_");
      in1 = in1.replace("/","_");
      in1 = in1.replace("\\","_");
      in1 = in1.replace("^","_");
      in1 = in1.replace("+","plus");
      in1 = in1.replace("#","sharp");
      in1 = in1.replace("$","dollar");
      in1 = in1.replace("@","at");
      in1 = in1.replace(".","dot");
      in1 = in1.replace("-","dash");
      in1 = in1.replace("'","_");
      in1 = in1.replace("&","and");
      in1 = in1.replace("!","_");
      in1 = in1.replace("?","_");
      in1 = in1.replace(",","_");
      in1 = in1.replace(">","_");
      in1 = in1.replace("<","_");
      in1 = in1.replace("\"","_");
      in1 = in1.replace("*","star");
      in1 = in1.replace("%","_");
      in1 = in1.replace("[","_");
      in1 = in1.replace("]","_");
      in1 = in1.replace("|","_");
      if (Character.isDigit(in1.charAt(0))) in1 = "x" + in1;
      return in1;
   }



}