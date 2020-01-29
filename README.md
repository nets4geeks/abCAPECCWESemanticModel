

# Generator of CAPEC & CWE & CPE & NVD semantic models


## CPE & DBpedia Model
* [Target RDF dataset](cpemodel/CPEDBpediaModel.ttl)
* [Test NVD dataset (compressed)](cpemodel/NVDSemanticModel.ttl.tar.gz)

To create the target model 
(do not forget to edit a properties file, download required data etc.): 

1. clone & run ./compile (you only need java & maven to do that)
2. To build the RDF dataset run  ./runBuildDBpediaProductModel (is not so easy, because it requires asking DBpedia Spotlight)

To create the test NVD model:

3. Download the NVD data (e.g. run ./update_NVDfeeds)
4. To build the NVD dataset run ./runBuildNVDModel

More details is [here](http://injoit.org/index.php/j1/article/download/743/736)

If you want to refer to the CPE/DBpedia model, please cite:
>Brazhuk A. Building annotated semantic model of software products towards integration of DBpedia with NVD vulnerability dataset //International Journal of Open Information Technologies ISSN: 2307-8162 - vol. 7, - no.7, - 2019 - C. 35-41


## CAPEC & CWE model (itmutgu style)
* [Target OWL Model](snapshots/20181222v2.owl)

To test the target model you can use [Protege](https://protege.stanford.edu/) & the FaCT++ reasoner or Pellet reasoner. 

For DL use the standard DL query tab:
* [Examples of DL queries](doc/examples_of_DL_queries.pdf).

For SPARQL use the [snap-sparql-query](https://github.com/protegeproject/snap-sparql-query) plugin:
* [Examples of SPARQL queries](doc/examples_of_SPARQL_queries.pdf). (note: the requests containing data properties only work with Pellet)

To create the target model: 
1. clone & run ./compile (you only need java & maven to do that).
2. run ./runBuildSemanticModelv3 to build the OWL file (do not forget to edit a properties file, download required data etc.)

Full description is [here](http://injoit.org/index.php/j1/article/download/686/675)

If you want to refer to the CAPEC&CWE model, please cite:
>Brazhuk A. Semantic model of attacks and vulnerabilities based on CAPEC and CWE dictionaries //International Journal of Open Information Technologies. – 2019. – Т. 7. – №. 3. – С. 38-41.


## The A10-16 model 

* [The target OWL Model](snapshots/20181129.owl)

To create the target model:
1. clone & run ./compile (you only need java & maven to do that).
2. ./runBuildSemanticModel to build the OWL file (do not forget to edit a properties file, download required data etc.)

## Author

[Andrei Brazhuk](https://scholar.google.com/citations?user=lxR8RLkAAAAJ&hl)

