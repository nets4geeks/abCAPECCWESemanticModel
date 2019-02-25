

# Generator of CAPEC & CWE semantic models

## Clear CAPEC & CWE model (itmutgu style)
* [The destination OWL Model](snapshots/20181222v2.owl)

To test the destination model you can use [Protege](https://protege.stanford.edu/) & the FaCT++ reasoner or the Pellet reasoner. 

For DL use the standard DL query tab:
* [The examples of DL queries](doc/examples_of_DL_queries.pdf).

For SPARQL use the [snap-sparql-query](https://github.com/protegeproject/snap-sparql-query) plugin:
* [The examples of SPARQL queries](doc/examples_of_SPARQL_queries.pdf). (note: the requests containing data properties only work with Pellet)

To create the destination model yourself 
1. clone & run ./compile (you only need java & maven to do that).
2. run ./runBuildSemanticModelv2 to build the OWL file (do not forget to edit a properties file, download required data etc.)

## The A10-16 model (obsolete)

* [The destination OWL Model](snapshots/20181129.owl)

To create the destination model yourself:
1. clone & run ./compile (you only need java & maven to do that).
2. ./runBuildSemanticModel to build the OWL file (do not forget to edit a properties file, download required data etc.)

## Author

[Andrei Brazhuk](https://scholar.google.com/citations?user=lxR8RLkAAAAJ&hl)

## License

It is a free software in any meaning you wish.
No legal claims are accepted too.