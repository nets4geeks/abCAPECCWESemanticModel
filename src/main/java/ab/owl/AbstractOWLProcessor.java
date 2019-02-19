
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
import java.time.Duration;
import java.time.Instant;


// common OWL functions
public abstract class AbstractOWLProcessor{

   public boolean DEBUG;
   public static final int XSDINTEGER = 1;


   protected OWLOntology o;
   protected OWLOntologyManager man;
   protected OWLDataFactory df;
   protected OWLReasoner reaz;

   protected String IRIName;
   protected IRI iri;       // the current ontology IRI
   protected IRI baseIRI;   // the parrent ontology IRI

   private String processorName;

   // the reasoner name
   private String reasonerName;

   // it needs to create an instance
   // and apply to the instance 
   //    the initCreate method
   // or
   //    the initCopy method
   // or 
   //    the initRead method
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

   // uses an existen ontology <_o> with the base iri <_IRIName> and with parser file (_parserFile) 
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

   // copy an existen ontology <_o>  without a parser
   public boolean initCopy(OWLOntology _o){
      o = _o;
      IRIName = getIRI().toString();

      try {
         iri= IRI.create(IRIName);
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      if (!initPost()) return false;
      if (!initParser(null)) return false;

      System.out.println(IRIName);

      return true;
   }


   protected String getms(long start, long stop){
      long diff = (stop-start)/1000000;
      return Long.toString(diff) + " ms";
   }


   // read an existen ontology from a file 
   // fileName - local file of ontology, _parserFile - the xml file for parser
   public boolean initRead(String fileName, String _parserFile){

      long startTime = System.nanoTime();
      log("initRead: " + fileName);

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

     long stopTime = System.nanoTime();
     log("initRead: "+ getms(startTime,stopTime));

      return true;
   }

   // should be run in any init<Something>
   public boolean initPost(){
      df = o.getOWLOntologyManager().getOWLDataFactory();
      return true;
      // enjoy yourself and init a reasoner by hand
   }


   public boolean initReasoner(){
      // init reasoner
      reaz = new Reasoner.ReasonerFactory().createReasoner(o);
      reasonerName = reaz.getReasonerName()+" "+reaz.getReasonerVersion().toString();
      log("initReasoner: "+ reasonerName);
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

   // <name> is a subclass of <parentname>, arguments are stings
   public void addSubClass(String name, String parentname){
      OWLClass cls = df.getOWLClass(IRI.create(name));
      OWLClass parent = df.getOWLClass(IRI.create(parentname));
      addSubClass(cls,parent);
   }

   // <cls> is a subclass of <parent>, arguments are classes
   public void addSubClass(OWLClass cls, OWLClass parent){
      OWLSubClassOfAxiom p_sub_a = df.getOWLSubClassOfAxiom(cls, parent);
      o.add(p_sub_a);
   }


   // add class anotation
   public void addClassAnnotation(String name, String label, String lang){
      OWLClass cls = df.getOWLClass(IRI.create(name));
      OWLAnnotation labelAnno = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(label, lang));
      OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), labelAnno);
      o.add(ax1);
   }

   // add class comment
   public void addClassComment(String name, String label, String lang){
      OWLClass cls = df.getOWLClass(IRI.create(name));
      OWLAnnotation commentAnno = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral(label, lang));
      OWLAxiom ax1 = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), commentAnno);
      o.add(ax1);
   }


   // <className> <propertyName> SOME <valueName>, arguments are strings
   public void addClassProperty(String className, String propertyName, String valueName){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLClass value = df.getOWLClass(IRI.create(valueName));
      OWLObjectProperty property = df.getOWLObjectProperty(IRI.create(propertyName));
      OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(cls, df.getOWLObjectSomeValuesFrom(property,value));
      o.add(ax1);
   }

   // <className> <propertyName> VALUE <indName>, arguments are strings
   public void addClassPropertyValue(String className, String propertyName, String indName){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLNamedIndividual ind  = df.getOWLNamedIndividual(IRI.create(indName));
      OWLObjectProperty property = df.getOWLObjectProperty(IRI.create(propertyName));
      OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(cls, df.getOWLObjectHasValue(property,ind));
      o.add(ax1);
   }

   // add data property to a class, arguments are strings
   public void addClassDataProperty(String className, String propertyName, String value, Integer type){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLDataProperty property = df.getOWLDataProperty(IRI.create(propertyName));
      if (type == XSDINTEGER){
         OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(cls, df.getOWLDataHasValue(property, df.getOWLLiteral( Integer.parseInt(value) )));
         o.add(ax1);
      }
   }


   // <propertyName> domain <domainName>, arguments are strings
   public void addObjectPropertyDomain(String propertyName, String domainName){
      OWLObjectPropertyDomainAxiom ax1 = df.getOWLObjectPropertyDomainAxiom(df.getOWLObjectProperty(IRI.create(propertyName)), df.getOWLClass(IRI.create(domainName)) );
      o.add(ax1);
   }

   // <propertyName> range <domainName>, arguments are strings
   public void addObjectPropertyRange(String propertyName, String rangeName){
      OWLObjectPropertyRangeAxiom ax1 = df.getOWLObjectPropertyRangeAxiom(df.getOWLObjectProperty(IRI.create(propertyName)), df.getOWLClass(IRI.create(rangeName)) );
      o.add(ax1);
   }

   // add an individual to a class, arguments are strings
   public void addIndividualToClass(String individualName, String className){
      OWLClass cls = df.getOWLClass(IRI.create(className));
      OWLNamedIndividual ind  = df.getOWLNamedIndividual(IRI.create(individualName));
      OWLClassAssertionAxiom ax1 = df.getOWLClassAssertionAxiom(cls, ind);
      o.add(ax1);
   }

   
   // create equvalent classes, arguments are strings
   public void addEquvalentClasses(String class1Name, String class2Name){

      OWLClass class1 = df.getOWLClass(IRI.create(class1Name));
      OWLClass class2 = df.getOWLClass(IRI.create(class2Name));

      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (class1);
      arguments.add (class2);
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }


   // <cls> equvalents to <prop> some <val>, arguments are classes
   public void addDefinedClass(OWLClass cls, OWLObjectProperty prop, OWLClass val){
      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (cls);
      arguments.add(df.getOWLObjectSomeValuesFrom(prop,val));
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }

   // <parent> equvalents to <cls> and <prop> some <val>, arguments are classes
   public void addDefinedClass(OWLClass parent, OWLClass cls, OWLObjectProperty prop, OWLClass val){
      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (parent);
      arguments.add( df.getOWLObjectIntersectionOf(cls, df.getOWLObjectSomeValuesFrom(prop, val)) );
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }

   // <parent> equvalents to (<prop1> some <val1>) and (<prop1> some <val1>), arguments are classes
   public void addDefinedClass(OWLClass parent, OWLObjectProperty prop1, OWLClass val1, OWLObjectProperty prop2, OWLClass val2){
      Set<OWLClassExpression> arguments=new HashSet<OWLClassExpression>();
      arguments.add (parent);
      arguments.add( df.getOWLObjectIntersectionOf(df.getOWLObjectSomeValuesFrom(prop1, val1), df.getOWLObjectSomeValuesFrom(prop2, val2) ) );
      OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(arguments);
      o.add(axiom);
   }


   public void addInverseProperties(String property1Name, String property2Name){
      OWLObjectProperty property1 = df.getOWLObjectProperty(IRI.create(property1Name));
      OWLObjectProperty property2 = df.getOWLObjectProperty(IRI.create(property2Name));
      o.add(df.getOWLInverseObjectPropertiesAxiom(property1, property2));
   }


   // <class> and <property> some <value>, arguments are classes
   public OWLObjectIntersectionOf genObjectIntersectionOf (OWLClass cls, OWLObjectProperty prop, OWLClass val){
      return df.getOWLObjectIntersectionOf(cls, df.getOWLObjectSomeValuesFrom(prop, val));
   }

 
   // get subclasses
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

  public int getAxiomsCount(){
     return o.getAxiomCount();
  }


  public void showStat(){
     log("-----------------------");

     int i1=0;
     Set<AxiomType<?>> s = AxiomType.TBoxAxiomTypes;
     for (AxiomType a : s){
         i1=i1+o.getAxiomCount(a);
     }
     log("showStat: tbox axioms "+ i1 );

     int i2=0;
     s = AxiomType.ABoxAxiomTypes;
     for (AxiomType a : s){
         i2=i2+ o.getAxiomCount(a);
     }
     log("showStat: abox axioms "+ i2 );

     int i3=0;
     s = AxiomType.RBoxAxiomTypes;
     for (AxiomType a : s){
         i3=i3+ o.getAxiomCount(a);
     }
     log("showStat: rbox axioms "+ i3 );

     int i = i1+i2+i3;
     // the differnce is annotations & declarations etc.
     log("showStat: abox+rbox+tbox "+ i);
     log("showStat: total axioms "+ getAxiomsCount());
     log("-----------------------");

  }


   // you need a reasoner to do so ...
   public void reasonerFlush(){
        //Instant start = Instant.now();
        log("reasonerFlush: axioms before "+ getAxiomsCount());

        long startTime = System.nanoTime();
        reaz.flush();
        long stopTime = System.nanoTime();
        //Instant end = Instant.now();
        log("reasonerFlush: "+ getms(startTime,stopTime));
        log("reasonerFlush: axioms after "+ getAxiomsCount());

   }


   // you need a reasoner to do so ...
   public void fillOntology(){
      reasonerFlush();
      log("fillOntology: axioms before "+ getAxiomsCount());
      showStat();
      InferredOntologyGenerator gen = new InferredOntologyGenerator(reaz);
      long startTime = System.nanoTime();
      gen.fillOntology(df, o);
      long stopTime = System.nanoTime();
      log("fillOntology: "+ getms(startTime,stopTime));
      log("fillOntology: axioms after "+ getAxiomsCount());
      showStat();


   }

   // save an ontology to a file
   public boolean save(String filepath){
      try {
         File fileout = new File(filepath);
         long startTime = System.nanoTime();
         man.saveOntology(o, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));
         long stopTime = System.nanoTime();
         log("save (saveOntology): "+ getms(startTime,stopTime));
         showStat();

      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      return true;
   } 


} // That's all


