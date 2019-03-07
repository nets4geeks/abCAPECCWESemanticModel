

# Generator of CAPEC & CWE semantic models

## CAPEC & CWE model (itmutgu style)
* [The target OWL Model](snapshots/20181222v2.owl)

To test the target model you can use [Protege](https://protege.stanford.edu/) & the FaCT++ reasoner or Pellet reasoner. 

For DL use the standard DL query tab:
* [The examples of DL queries](doc/examples_of_DL_queries.pdf).

For SPARQL use the [snap-sparql-query](https://github.com/protegeproject/snap-sparql-query) plugin:
* [The examples of SPARQL queries](doc/examples_of_SPARQL_queries.pdf). (note: the requests containing data properties only work with Pellet)

To create the destination model: 
1. clone & run ./compile (you only need java & maven to do that).
2. run ./runBuildSemanticModelv2 to build the OWL file (do not forget to edit a properties file, download required data etc.)

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

## License

It is a free software in any meaning you wish.
No legal claims are accepted too.