
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

public class CAPECProcessor2 extends AbstractOWLProcessor{

   CAPECParser parser;
   public boolean isProcessingEnabled = true;

   public boolean initParser(String path){
      parser = new CAPECParser();
      if (!parser.init(path)) return false;
      return true;
   }


   public void process(){
      NodeList lst = parser.getNodeList();

   if (isProcessingEnabled){
      log("process ...");
    
      addObjectPropertyRange(iri+"#targetsCWE", iri+"#CWE");
      addObjectPropertyDomain(iri+"#targetsCWE", iri+"#CAPEC");
      addInverseProperties(iri+"#targetsCWE",iri+"#isTargetedBy");

      addObjectPropertyRange(iri+"#appearedAtPhase", iri+"#Phase");
      addObjectPropertyDomain(iri+"#appearedAtPhase", iri+"#CWE");

      addObjectPropertyRange(iri+"#hasCWEAbstraction", iri+"#CWEAbstraction");
      addObjectPropertyDomain(iri+"#hasCWEAbstraction", iri+"#CWE");


      addObjectPropertyRange(iri+"#hasCAPECAbstraction", iri+"#CAPECAbstraction");
      addObjectPropertyDomain(iri+"#hasCAPECAbstraction", iri+"#CAPEC");


      addObjectPropertyRange(iri+"#impactsTechnicalImpact", iri+"#TechnicalImpact");

      addObjectPropertyRange(iri+"#isDetectedBy", iri+"#DetectionMethod");
      addObjectPropertyDomain(iri+"#isDetectedBy", iri+"#CWE");
      addInverseProperties(iri+"#detectsCWE",iri+"#isDetectedBy");

      addObjectPropertyRange(iri+"#isMitigatedBy", iri+"#MitigationMethod");
      addObjectPropertyDomain(iri+"#isMitigatedBy", iri+"#CWE");
      addInverseProperties(iri+"#mitigatesCWE",iri+"#isMitigatedBy");

      addObjectPropertyRange(iri+"#scopes", iri+"#Scope");

      addObjectPropertyRange(iri+"#usesMethod", iri+"#MethodOfAttack");
      addObjectPropertyDomain(iri+"#usesMethod", iri+"#CAPEC");
      addInverseProperties(iri+"#usesMethod",iri+"#isUsedByCAPEC");


      for (int i=0; i<lst.getLength();i++){
          Node tmpnode = lst.item(i);
          CAPECAttack att = parser.getCAPECAttackByNode(tmpnode);
          // AttackID = CAPEC_<ID> (class)
          String AttackID = iri+"#CAPEC_"+att.ID;
          // AttackIDi = instCAPEC_<ID> (instance)
          String iAttackID = iri+"#iCAPEC_"+att.ID;

          // <AttackID> is a subclass of iri#CAPEC
          addSubClass(AttackID,iri+"#CAPEC");
          if (att.Name != null) addClassAnnotation (AttackID,att.Name,"en");
          if (att.Description != null) addClassComment(AttackID,att.Description,"en");

          if (att.Abstraction != null) {
             String abstraction = iri+"#"+att.Abstraction+"CAPECAbstraction";
             addIndividualToClass(abstraction,iri+"#CAPECAbstraction");
             // <AttackID> hasCAPECAbstraction value <CAPECAbstraction>
             addClassPropertyValue(AttackID, iri+"#hasCAPECAbstraction", abstraction);

          }

          // iAttackID is individual of the AttackID class
          addIndividualToClass(iAttackID,AttackID);

          // <Attack Pattern> scopes <Scope>
          for (int ii=0; ii<att.Scopes.size(); ii++){
             CAPECScope tmpscope = att.Scopes.get(ii);
             for (int iii=0; iii<tmpscope.Scopes.size(); iii++){
                  // <scope> = iri#scope
                  String scope = iri+"#"+tmpscope.Scopes.get(iii);
                  // <scope> is subclass of Scope
                  addIndividualToClass(scope,iri+"#Scope");

                  // Attack scopes vlaue scope
                  addClassPropertyValue(AttackID, iri+"#scopes", scope);

                  for (int iiii=0; iiii<tmpscope.Impacts.size();iiii++){
                     // impact = iri#<Impact>
                     String impact = iri+"#"+tmpscope.Impacts.get(iiii);
                     // <Impact> is an individual of TechnicalImpact
                     addIndividualToClass(impact,iri+"#TechnicalImpact");
                     // Attack impactsTechnicalImpact value TechnicalImpact
                     addClassPropertyValue(AttackID, iri+"#impactsTechnicalImpact", impact);
                     // !!! Attack hasScope<Scope>Impact some <Impact>
                     //addClassProperty(AttackID, scopeProperty, impact);
                  }
             }
          }

          for (int ii=0; ii<att.CWEs.size(); ii++){
             // CWEID = iri#CWE_<ID>, instance
             String CWEID = iri+"#iCWE_"+att.CWEs.get(ii);
             // ??? The problem is that some CWEs are empty
             // CWEID is a subclass of Vulnerability
             //addSubClass(CWEID,baseIRI+"#CWE");
             // Attack targets value CWEID
             addClassPropertyValue(AttackID, iri+"#targetsCWE", CWEID);
          }


          for (int ii=0; ii<att.Methods.size(); ii++){
             // Method = iri#<Method>
             String Method = iri+"#"+att.Methods.get(ii);
             // <Method> is an individual of iri#MethodOfAttack
             addIndividualToClass(Method,iri+"#MethodOfAttack");
             // Attack uses value Method
             addClassPropertyValue(AttackID, iri+"#usesMethod", Method);
          }


          if (att.LikeHoodText != null) {
              // <Attack> scoresLikelihood value Integer
              addClassDataProperty(AttackID, iri+"#scoresLikelihood", att.LikeHoodText, XSDINTEGER);
          }

          if (att.SeverityText != null) {
              // <Attack> scoresSeverity value Integer
              addClassDataProperty(AttackID, iri+"#scoresSeverity", att.SeverityText, XSDINTEGER);
          }


          for (int ii=0; ii<att.Skills.size(); ii++){
             addClassDataProperty(AttackID, iri+"#scoresRequeredSkill", att.Skills.get(ii), XSDINTEGER);
          }

      }
      showStat();
   }


   }

}


