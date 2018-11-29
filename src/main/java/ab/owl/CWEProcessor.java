
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

public class CWEProcessor extends AbstractOWLProcessor{

   CWEParser parser;

   public boolean isProcessingEnabled = true;
   public boolean isAppearancePhaseEnabled = true;
   public boolean isExploitLikehoodEnabled = true;
   public boolean isDetectionMethodEnabled = true;
   public boolean isMitigationMethodEnabled = true;

   public boolean initParser(String path){
      parser = new CWEParser();
      if (!parser.init(path)) return false;
      return true;
   }



   public void process(){
   if (isProcessingEnabled){
      NodeList lst = parser.getNodeList();

      for (int i=0; i<lst.getLength();i++){
          Node tmpnode = lst.item(i);
          CWEVulnerability att = parser.getCWEVulnerabilityByNode(tmpnode);

          // CWEID = <iri>#CWE_<ID>
          String CWEID = iri+"#CWE_"+att.ID; 
          // STIX's subclass "Vulnerability" is populated by the CWEs
          // <CWEID> is a subclass of Vulnerability
          //addSubClass(CWEID,baseIRI+"#Vulnerability");
          // it is also a subclass of CWE
          addSubClass(CWEID,iri+"#CWE");

 
          if (att.Name != null) addClassAnnotation (CWEID,att.Name,"en");
          if (att.Description != null) addClassComment(CWEID,att.Description,"en");

          if (att.Abstraction !=null) {
             String abstraction = iri+"#"+att.Abstraction;
             addSubClass(abstraction,iri+"#Abstraction");
             // <CWEID> hasAbstraction some <Abstraction>
             addClassProperty(CWEID, iri+"#hasAbstraction", abstraction);
          }

          for (int ii=0; ii<att.Scopes.size(); ii++){
             CAPECScope tmpscope = att.Scopes.get(ii);
             for (int iii=0; iii<tmpscope.Scopes.size(); iii++){
                  // scope = iri#scope
                  String scope = iri+"#"+tmpscope.Scopes.get(iii);
                  // <scope> is a subclass of the Scope
                  addSubClass(scope,iri+"#Scope");
                  // <CWEID> scopes some <scope>
                  addClassProperty(CWEID, iri+"#scopes", scope);

                  for (int iiii=0; iiii<tmpscope.Impacts.size();iiii++){
                     // impact = baseIRI#<Impact>
                     String impact = iri+"#"+tmpscope.Impacts.get(iiii);
                     // <Impact> is subclass of TechnicalImpact
                     addSubClass(impact,iri+"#TechnicalImpact");
                     // <CWEID> impacts some <TechnicalImpact>
                     addClassProperty(CWEID, iri+"#impactsTechnicalImpact", impact);
                     // scopeimpact = iri#<Scope>___<Impact>
                     String scopeimpact = iri+"#"+tmpscope.Scopes.get(iii)+"___"+tmpscope.Impacts.get(iiii);
                     // <scopeimpact> is a sublclass of ScopeImpact
                     addSubClass(scopeimpact,iri+"#ScopeAndTechnicalImpact");
                     // <CWEID> scopesANDimpacts some <scopeimpact>
                     addClassProperty(CWEID, iri+"#scopesANDimpacts", scopeimpact);
                  }
             }
          }

          if (isAppearancePhaseEnabled){
             for (int ii=0; ii<att.AppearancePhases.size(); ii++){
                String phase = iri+"#"+att.AppearancePhases.get(ii);
                // <phase> is subclass of Phase
                addSubClass(phase,iri+"#Phase");
                // <CWEID> appearedIn some <phase>
                addClassProperty(CWEID, iri+"#appearedAtPhase", phase);
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
                  String course = baseIRI+"#CourseOfAction";
                  // <method> is subclass of CourseOfAction
                  addSubClass(method,baseIRI+"#CourseOfAction");
                  // <CWEID> isDetectedBy some <CourseOfAction>
                  addClassProperty(CWEID, iri+"#isDetectedByCourseOfAction", method);
              }
          }

          if (isMitigationMethodEnabled){
             for (int ii=0; ii<att.MitigationMethods.size(); ii++){
                String method = iri+"#"+att.MitigationMethods.get(ii);
                String course = baseIRI+"#CourseOfAction";
                // <method> is subclass of CourseOfAction
                addSubClass(method,baseIRI+"#CourseOfAction");
                // <CWEID> isMitigatedBy some <method>
                addClassProperty(CWEID, iri+"#isMitigatedByCourseOfAction", method);
             }
          }

      }
   }
   }

}


