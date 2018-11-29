
package ab.owl;

import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.reasoner.structural.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import java.io.*;
import java.util.*;
import java.util.stream.*;

// common OWL functions
public abstract class AbstractOWLProcessor{

   public boolean DEBUG;
   public static final int XSDINTEGER = 1;


   protected OWLOntology o;
   protected OWLOntologyManager man;
   protected OWLDataFactory df;
//   protected OWLReasonerFactory rf;
   protected OWLReasoner reaz;
//      OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
//      OWLReasoner reasoner = reasonerFactory.createReasoner(ontologia);

   protected String IRIName;
   protected IRI iri;       // the current ontology IRI
   protected IRI baseIRI;   // the parrent ontology IRI

   private String processorName;

   // it needs to create an instance
   // and apply to the instance 
   //    the initCreate method
   // or
   //    the initCopy method
   // sometimes it needs to add a baseIRI using the addBaseIRI method
   // also the initParser method has to be overwritten
   public AbstractOWLProcessor(){
     processorName = getClass().getName();
     DEBUG = true;
     man = OWLManager.createOWLOntologyManager();
   }

   public IRI getIRI(){
      return o.getOntologyID().getOntologyIRI().get();
   }

   // creates an ontology with the base iri <_IRIName> and the xml file <_parserFile>
   public boolean initCreate(String _IRIName, String _parserFile){
      IRIName = _IRIName;
      try {
         iri= IRI.create(IRIName);
         o = man.createOntology(iri);
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      if (!initPost()) return false;
      if (!initParser(_parserFile)) return false;
      return true;
   }

   // uses an existen ontology <_o> with the base iri <_IRIName> and the xml file <_parserFile>
   public boolean initCopy(String _IRIName, String _parserFile, OWLOntology _o){
      IRIName = _IRIName;
      o = _o;
      try {
         iri= IRI.create(IRIName);
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      if (!initPost()) return false;
      if (!initParser(_parserFile)) return false;
      return true;
   }

   // read an existen ontology from a file 
   // fileName - local file of ontology, _parserFile - the xml file for parser
   public boolean initRead(String fileName, String _parserFile){
      try {
         File file = new File(fileName);
         o = man.loadOntologyFromOntologyDocument(file);
         iri = o.getOntologyID().getOntologyIRI().get();
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      if (!initPost()) return false;
      if (!initParser(_parserFile)) return false;
      return true;
   }

   // should be run in any init<Something>
   public boolean initPost(){
      df = o.getOWLOntologyManager().getOWLDataFactory();
//      rf = new StructuralReasonerFactory();
//      reaz = rf.createReasoner(o);
      reaz = new Reasoner.ReasonerFactory().createReasoner(o);
      log("using "+reaz.getReasonerName()+" "+reaz.getReasonerVersion().toString());
      return true;
   }

  // needs to be overwritten in a child class to init a parser
   abstract public boolean initParser(String _parserName);
   


   protected void log(String msg){
     if (DEBUG) {
        System.out.println("["+processorName+"]: "+msg);
     }
   }

   public OWLOntology getOntology(){
      return o;
   }

   public void addBaseIRI(String iriname){
      baseIRI = IRI.create(iriname);
//      addImportDeclaration(iriname);
   }



   public void addImportDeclaration(String iriname){
      IRI tmpiri = IRI.create(iriname);
      OWLImportsDeclaration importDeclaration=man.getOWLDataFactory().getOWLImportsDeclaration(tmpiri);
      man.applyChange(new AddImport(o, importDeclaration));
   }

/*   public void addImportDeclaration(String iriname, String urlname){
      IRI tmpiri = IRI.create(iriname);
      man.getIRIMappers().add(new SimpleIRIMapper(tmpiri, IRI.create(urlname)));
      OWLImportsDeclaration importDeclaration=man.getOWLDataFactory().getOWLImportsDeclaration(tmpiri);
      man.applyChange(new AddImport(o, importDeclaration));
   }*/


   public void addSubClass(String name, String parentname){
      OWLClass cls = df.getOWLClass(IRI.create(name));
      OWLClass parent = df.getOWLClass(IRI.create(parentname));
      addSubClass(cls,parent);
   }

   public void addSubClass(OWLClass cls, OWLClass parent){
      OWLSubClassOfAxiom p_sub_a = df.getOWLSubClassOfAxiom(cls, parent);
      o.add(p_sub_a);
   }



   public void addClassAnnotation(String name, String label, String lang){
      OWLClass cls = df.getOWLClass(IRI.create(name));
      OWLAnnotation labelAnno = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(label, lang));
      OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), labelAnno);
      o.add(ax1);
   }

   public void addClassComment(String name, String label, String lang){
      OWLClass cls = df.getOWLClass(IRI.create(name));
      OWLAnnotation commentAnno = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral(label, lang));
      OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), commentAnno);
      o.add(ax1);
   }


   // <className> <propertyName> SOME <valueName>
   public void addClassProperty(String className, String propertyName, String valueName){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLClass value = df.getOWLClass(IRI.create(valueName));
      OWLObjectProperty property = df.getOWLObjectProperty(IRI.create(propertyName));
      OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(cls, df.getOWLObjectSomeValuesFrom(property,value));
      o.add(ax1);
   }

   // <className> <propertyName> VALUE <indName>
   public void addClassPropertyValue(String className, String propertyName, String indName){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLNamedIndividual ind  = df.getOWLNamedIndividual(IRI.create(indName));
      OWLObjectProperty property = df.getOWLObjectProperty(IRI.create(propertyName));
      OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(cls, df.getOWLObjectHasValue(property,ind));
      o.add(ax1);
   }


   public void addClassDataProperty(String className, String propertyName, String value, Integer type){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLDataProperty property = df.getOWLDataProperty(IRI.create(propertyName));
      if (type == XSDINTEGER){
         OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(cls, df.getOWLDataHasValue(property, df.getOWLLiteral( Integer.parseInt(value) )));
         o.add(ax1);
      }
   }
   // df.OWLDataHasValue getOWLDataHasValue(OWLDataPropertyExpression property, OWLLiteral value)


   // <propertyName> domain <domainName>
   public void addObjectPropertyDomain(String propertyName, String domainName){
      OWLObjectPropertyDomainAxiom ax1 = df.getOWLObjectPropertyDomainAxiom(df.getOWLObjectProperty(IRI.create(propertyName)), df.getOWLClass(IRI.create(domainName)) );
      o.add(ax1);
   }

   // <propertyName> range <domainName>
   public void addObjectPropertyRange(String propertyName, String rangeName){
      OWLObjectPropertyRangeAxiom ax1 = df.getOWLObjectPropertyRangeAxiom(df.getOWLObjectProperty(IRI.create(propertyName)), df.getOWLClass(IRI.create(rangeName)) );
      o.add(ax1);
   }


   public void addIndividualToClass(String individualName, String className){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLNamedIndividual ind  = df.getOWLNamedIndividual(IRI.create(individualName));
      OWLClassAssertionAxiom ax1 = df.getOWLClassAssertionAxiom(cls, ind);
      o.add(ax1);
   }

   
   public void addEquvalentClasses(String class1Name, String class2Name){

      OWLClass class1 = df.getOWLClass(IRI.create(class1Name));
      OWLClass class2 = df.getOWLClass(IRI.create(class2Name));

      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (class1);
      arguments.add (class2);
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }


   // <cls> equvalents to <prop> some <val>
   public void addDefinedClass(OWLClass cls, OWLObjectProperty prop, OWLClass val){
      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (cls);
      arguments.add(df.getOWLObjectSomeValuesFrom(prop,val));
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }

   // <parent> equvalents to <cls> and <prop> some <val>
   public void addDefinedClass(OWLClass parent, OWLClass cls, OWLObjectProperty prop, OWLClass val){
      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (parent);
      arguments.add( df.getOWLObjectIntersectionOf(cls, df.getOWLObjectSomeValuesFrom(prop, val)) );
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }

   // <parent> equvalents to   (<prop1> some <val1>) and (<prop1> some <val1>)
   public void addDefinedClass(OWLClass parent, OWLObjectProperty prop1, OWLClass val1, OWLObjectProperty prop2, OWLClass val2){
      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (parent);
      arguments.add( df.getOWLObjectIntersectionOf(df.getOWLObjectSomeValuesFrom(prop1, val1), df.getOWLObjectSomeValuesFrom(prop2, val2) ) );
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }



 
   // <class> and <property> some <value>
   public OWLObjectIntersectionOf genObjectIntersectionOf (OWLClass cls, OWLObjectProperty prop, OWLClass val){
      return df.getOWLObjectIntersectionOf(cls, df.getOWLObjectSomeValuesFrom(prop, val));
   }



   // to use:
   //for (OWLClass cls : clses) {
   //    String s = cls.toString();
   //    Log.d(TAG, s.substring(s.indexOf("#") + 1, s.length() -1));
   //}
   // wants an IRI & short classname
   public Set<OWLClass> getSubClasses(String _iri, String className){
      OWLClass cls = df.getOWLClass(IRI.create(_iri+className));
      return getSubClasses(cls);
   }
   // wants an OWLClass as an argument
   public Set<OWLClass> getSubClasses(OWLClass cls){
      NodeSet<OWLClass> subClasses = reaz.getSubClasses(cls, true);
      Set<OWLClass> clses = subClasses.getFlattened();
      return clses;
   }
   // wants a shortname, uses the current ontology iri
   public Set<OWLClass> getSubClasses(String className){
      return getSubClasses(iri.toString(),className);
   }


   public void showClassesList(Set<OWLClass> clses){
      for (OWLClass cls : clses) {
         String s = cls.asOWLClass().getIRI().toString();
         String s1 = getShortIRI(cls.asOWLClass());
         System.out.println("SHORT ::"+s1+"::    LONG: "+s);
      }
   }

   public void showClassesListShort(Set<OWLClass> clses){
      for (OWLClass cls : clses) {
         String s1 = getShortIRI(cls.asOWLClass());
         System.out.println(s1);
      }
   }




  public int showSubClasses(OWLClass cls){
      // reaz.flush();
      Set<OWLClass> subinfs = getSubClasses(cls);
      System.out.println ("Class "+getShortIRI(cls)+"("+cls.asOWLClass().getIRI().toString() +")" +" has sublclasses:");
      int i=0;
      for (OWLClass subcls : subinfs) {
         if (!subcls.asOWLClass().getIRI().toString().equals("http://www.w3.org/2002/07/owl#Nothing") ){
           i++;
           System.out.println("   "+getShortIRI(subcls)+" | "+subcls.asOWLClass().getIRI().toString());
         }
      }
      System.out.println("total: "+i);
      return i;
   }



/*   public String getShortIRI(IRI src){
      String tmp = src.toString();
      return tmp.substring(tmp.indexOf('#')+1,tmp.length()-1);
   }*/


   public String getShortIRI(OWLClass src){
     // String tmp = src.toString();
     // return tmp.substring(tmp.indexOf('#')+1,tmp.length()-1);
     return src.asOWLClass().getIRI().getShortForm();

   }



  public String getLabel(OWLEntity e) {
     Stream<OWLAnnotation> labels = EntitySearcher.getAnnotations(e, o);

     for (Iterator<OWLAnnotation> iterator = labels.iterator(); iterator.hasNext(); ){
        OWLAnnotation an = iterator.next();
        if (an.getProperty().isLabel()) {
           OWLAnnotationValue val = an.getValue();
           if (val instanceof IRI) {
               return ((IRI) val).toString();
           } else if (val instanceof OWLLiteral) {
                OWLLiteral lit = (OWLLiteral) val;
                return lit.getLiteral();
           } else if (val instanceof OWLAnonymousIndividual) {
                OWLAnonymousIndividual ind = (OWLAnonymousIndividual) val;
                return ind.toStringID();
           }
        }
     }
     return e.toString();
  }

  public String getComment(OWLEntity e) {
     Stream<OWLAnnotation> labels = EntitySearcher.getAnnotations(e, o);

     for (Iterator<OWLAnnotation> iterator = labels.iterator(); iterator.hasNext(); ){
        OWLAnnotation an = iterator.next();
        if (an.getProperty().isComment()) {
           OWLAnnotationValue val = an.getValue();
           if (val instanceof IRI) {
               return ((IRI) val).toString();
           } else if (val instanceof OWLLiteral) {
                OWLLiteral lit = (OWLLiteral) val;
                return lit.getLiteral();
           } else if (val instanceof OWLAnonymousIndividual) {
                OWLAnonymousIndividual ind = (OWLAnonymousIndividual) val;
                return ind.toStringID();
           }
        }
     }
     return e.toString();
  }





   public boolean save(String filepath){
      try {
         File fileout = new File(filepath);
         man.saveOntology(o, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      return true;
   } 

}

