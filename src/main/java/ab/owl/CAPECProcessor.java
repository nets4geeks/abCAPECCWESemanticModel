
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

public class CAPECProcessor extends AbstractOWLProcessor{

   CAPECParser parser;
   public boolean isProcessingEnabled = true;

   public boolean initParser(String path){
      parser = new CAPECParser();
      if (!parser.init(path)) return false;
      return true;
   }


   public void process(){
      NodeList lst = parser.getNodeList();

      // common for CAPEC & CWE
      // scopes some Scope
      addObjectPropertyRange(iri+"#scopes", iri+"#Scope");
      // impactsTechnicalImpact some TechnicalImpact
      addObjectPropertyRange(iri+"#impactsTechnicalImpact", iri+"#TechnicalImpact");
      // AttactPattern usesMethod some MethodOfAttack
      addObjectPropertyRange(iri+"#usesMethod", iri+"#MethodOfAttack");
      addObjectPropertyDomain(iri+"#usesMethod", baseIRI+"#AttackPattern");
      // Vulnerability(STIX) appearedAtPhase some Phase
      addObjectPropertyRange(iri+"#appearedAtPhase", iri+"#Phase");
      addObjectPropertyDomain(iri+"#appearedAtPhase", baseIRI+"#Vulnerability");
      // Vulnerability(STIX) isDetectedByCourseOfAction some CourseOfAction(STIX)
      addObjectPropertyRange(iri+"#isDetectedByCourseOfAction", baseIRI+"#CourseOfAction");
      addObjectPropertyDomain(iri+"#isDetectedByCourseOfAction", baseIRI+"#Vulnerability");
      // isMitigatedByCourseOfAction some CourseOfAction(STIX)
      addObjectPropertyRange(iri+"#isMitigatedByCourseOfAction", baseIRI+"#CourseOfAction");
      // addObjectPropertyDomain(iri+"#isMitigatedByCourseOfAction", baseIRI+"#Vulnerability");
      // Vulnerability(STIX) hasAbstraction some Abstraction
      addObjectPropertyRange(iri+"#hasAbstraction", iri+"#Abstraction");
      addObjectPropertyDomain(iri+"#hasAbstraction", baseIRI+"#Vulnerability");

      addObjectPropertyRange(iri+"#scopesANDimpacts", iri+"#ScopeAndTechnicalImpact");
      // targetsVulnerability(STIX) some Vulnerability(STIX)
      addObjectPropertyRange(baseIRI+"#targetsVulnerability", baseIRI+"#Vulnerability");

      addEquvalentClasses(baseIRI+"#AttackPattern", iri+"#CAPEC");
      addEquvalentClasses(baseIRI+"#Vulnerability", iri+"#CWE");


   if (isProcessingEnabled){

      for (int i=0; i<lst.getLength();i++){
          Node tmpnode = lst.item(i);
          CAPECAttack att = parser.getCAPECAttackByNode(tmpnode);
          // AttackID = CAPEC_<ID>
          String AttackID = iri+"#CAPEC_"+att.ID;

          // <Attack Pattern> is a subclass of iri#CAPEC
          addSubClass(AttackID,iri+"#CAPEC");
          if (att.Name != null) addClassAnnotation (AttackID,att.Name,"en");
          if (att.Description != null) addClassComment(AttackID,att.Description,"en");

          // <Attack Pattern> scopes <Scope>
          for (int ii=0; ii<att.Scopes.size(); ii++){
             CAPECScope tmpscope = att.Scopes.get(ii);
             for (int iii=0; iii<tmpscope.Scopes.size(); iii++){
                  // <scope> = iri#scope
                  String scope = iri+"#"+tmpscope.Scopes.get(iii);
                  // <scope> is subclass of Scope
                  addSubClass(scope,iri+"#Scope");

                  // Attack scopes some scope
                  addClassProperty(AttackID, iri+"#scopes", scope);

                  for (int iiii=0; iiii<tmpscope.Impacts.size();iiii++){
                     // impact = iri#<Impact>
                     String impact = iri+"#"+tmpscope.Impacts.get(iiii);
                     // <Impact> is subclass of TechnicalImpact
                     addSubClass(impact,iri+"#TechnicalImpact");
                     // Attack impactsTechnicalImpact some TechnicalImpact
                     addClassProperty(AttackID, iri+"#impactsTechnicalImpact", impact);
                     // !!! Attack hasScope<Scope>Impact some <Impact>
                     //addClassProperty(AttackID, scopeProperty, impact);
                  }
             }
          }

          for (int ii=0; ii<att.CWEs.size(); ii++){
             // CWEID = iri#CWE_<ID>
             String CWEID = iri+"#CWE_"+att.CWEs.get(ii);
             // ??? The problem is that some CWEs are empty
             // CWEID is a subclass of Vulnerability
             addSubClass(CWEID,baseIRI+"#CWE");
             // Attack targets some CWEID
             addClassProperty(AttackID, baseIRI+"#targetsVulnerability", CWEID);
          }


          for (int ii=0; ii<att.Methods.size(); ii++){
             // Method = iri#<Method>
             String Method = iri+"#"+att.Methods.get(ii);
             // <Method> is subclass of iri#MethodOfAttack
             addSubClass(Method,iri+"#MethodOfAttack");
             // Attack uses some Method
             addClassProperty(AttackID, iri+"#usesMethod", Method);
          }


          if (att.LikeHoodText != null) {
              // <Attack> scoresLikelihood some <VeryLow|Low|Medium|High|VeryHigh>LikeliHood
              addClassDataProperty(AttackID, iri+"#scoresLikelihood", att.LikeHoodText, XSDINTEGER);
          }

          if (att.SeverityText != null) {
              // <Attack> scoresSeverity some <VeryLow|Low|Medium|High|VeryHigh>LikeliHood
              addClassDataProperty(AttackID, iri+"#scoresSeverity", att.SeverityText, XSDINTEGER);
          }

      }
   }


   }

}


