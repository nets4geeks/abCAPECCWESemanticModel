package ab.parsers;

import java.util.Properties;
import java.io.*;

public class PManager{

   private Properties props;

   public PManager(){
   }

   
   public boolean init (String fname){
     try {
        props = new Properties();
        if (fname !=null)props.load(new FileInputStream(fname));
        return true;
     } catch (Exception e){
       return false;
     }

   }

   public String get(String name){
     return props.getProperty(name); 

   }


}
