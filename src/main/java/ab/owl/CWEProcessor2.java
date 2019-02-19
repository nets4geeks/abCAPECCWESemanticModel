
package ab.owl;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

import ab.parsers.*;
import ab.data.*;

public class CWEProcessor2 extends AbstractOWLProcessor{

   CWEParser parser;

   public boolean isProcessingEnabled = true;
   public boolean isAppearancePhaseEnabled = true;
   public boolean isExploitLikehoodEnabled = true;
   public boolean isDetectionMethodEnabled = true;
   public boolean isMitigationMethodEnabled = true;
   public boolean isCAPECEnabled = true;

   public boolean initParser(String path){
      parser = new CWEParser();
      if (!parser.init(path)) return false;
      return true;
   }

   public void process(){
   if (isProcessingEnabled){
      log("process ...");
      NodeList lst = parser.getNodeList();

      for (int i=0; i<lst.getLength();i++){
          Node tmpnode = lst.item(i);
          CWEVulnerability att = parser.getCWEVulnerabilityByNode(tmpnode);

          // CWEID = <iri>#CWE_<ID>
          String CWEID = iri+"#CWE_"+att.ID; 
          String iCWEID = iri+"#iCWE_"+att.ID; 
          addSubClass(CWEID,iri+"#CWE");
          // iCWEID is a subclass of CWEID
          addIndividualToClass(iCWEID,CWEID);

 
          if (att.Name != null) addClassAnnotation (CWEID,att.Name,"en");
          if (att.Description != null) addClassComment(CWEID,att.Description,"en");

          if (att.Abstraction !=null) {
             String abstraction = iri+"#"+att.Abstraction+"CWEAbstraction";

             addIndividualToClass(abstraction,iri+"#CWEAbstraction");
             // <CWEID> hasAbstraction value <Abstraction>
             addClassPropertyValue(CWEID, iri+"#hasCWEAbstraction", abstraction);
          }

          for (int ii=0; ii<att.Scopes.size(); ii++){
             CAPECScope tmpscope = att.Scopes.get(ii);
             for (int iii=0; iii<tmpscope.Scopes.size(); iii++){
                  // scope = iri#scope
                  String scope = iri+"#"+tmpscope.Scopes.get(iii);
                  // <scope> is an individual of the Scope
                  addIndividualToClass(scope,iri+"#Scope");
                  // <CWEID> scopes value <scope>
                  addClassPropertyValue(CWEID, iri+"#scopes", scope);

                  for (int iiii=0; iiii<tmpscope.Impacts.size();iiii++){
                     // impact = baseIRI#<Impact>
                     String impact = iri+"#"+tmpscope.Impacts.get(iiii);
                     // <Impact> is an instance of TechnicalImpact
                     addIndividualToClass(impact,iri+"#TechnicalImpact");
                     // <CWEID> impacts some <TechnicalImpact>
                     addClassPropertyValue(CWEID, iri+"#impactsTechnicalImpact", impact);
                     // scopeimpact = iri#<Scope>___<Impact>
                     //String scopeimpact = iri+"#"+tmpscope.Scopes.get(iii)+"___"+tmpscope.Impacts.get(iiii);
                     // <scopeimpact> is a sublclass of ScopeImpact
                     //addSubClass(scopeimpact,iri+"#ScopeAndTechnicalImpact");
                     // <CWEID> scopesANDimpacts some <scopeimpact>
                     //addClassProperty(CWEID, iri+"#scopesANDimpacts", scopeimpact);
                  }
             }
          }

          if (isAppearancePhaseEnabled){
             for (int ii=0; ii<att.AppearancePhases.size(); ii++){
                String phase = iri+"#"+att.AppearancePhases.get(ii);
                // <phase> is an individual of Phase
                addIndividualToClass(phase,iri+"#Phase");
                // <CWEID> appearedIn value <phase>
                addClassPropertyValue(CWEID, iri+"#appearedAtPhase", phase);
             }
          }

          if (isCAPECEnabled){
             for (int ii=0; ii<att.CAPECs.size(); ii++){
                String capec = iri+"#i"+att.CAPECs.get(ii);
                // <CWEID> isTargetsBy value <capec>
                addClassPropertyValue(CWEID, iri+"#isTargetedBy", capec);
             }
          }


          if (isExploitLikehoodEnabled){
            if (att.ExploitLikehood !=null) {
               // <CWEID> scores value <ExploitLikeliHood>
               addClassDataProperty(CWEID, iri+"#scoresExploitLikelihood", att.ExploitLikehood, XSDINTEGER);
            }
          }

          if (isDetectionMethodEnabled){
              for (int ii=0; ii<att.DetectionMethods.size(); ii++){
                  String method = iri+"#"+att.DetectionMethods.get(ii);
                  String course = iri+"#DetectionMethod";
                  // <method> is an individual of DetectionMethod
                  addIndividualToClass(method,course);
                  // <CWEID> isDetectedBy some <CourseOfAction>
                  addClassPropertyValue(CWEID, iri+"#isDetectedBy", method);
              }
          }

          if (isMitigationMethodEnabled){
             for (int ii=0; ii<att.MitigationMethods.size(); ii++){
                String method = iri+"#"+att.MitigationMethods.get(ii);
                String course = iri+"#MitigationMethod";
                // <method> is subclass of CourseOfAction
                addIndividualToClass(method,course);
                // <CWEID> isMitigatedBy some <method>
                addClassPropertyValue(CWEID, iri+"#isMitigatedBy", method);
             }
          }

      }
   }
   showStat();
   }

}


