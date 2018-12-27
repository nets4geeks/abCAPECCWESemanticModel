

# The generator of the CAPEC & CWE semantic models

## Documentation
* [The example of OWL Model](snapshots/20181222v2.owl)

To test the model you can use [Protege](https://protege.stanford.edu/) & the FaCT++ reasoner or the Pellet reasoner. 
For DL use the standard DL query tab:
* [The examples of DL queries](doc/examples_of_DL_queries.pdf).

For SPARQL use the [snap-sparql-query](https://github.com/protegeproject/snap-sparql-query) plugin:
* [The examples of SPARQL queries](doc/examples_of_SPARQL_queries.pdf). (note: the requests containing data properties only work with Pellet)

## Using

Clone & run ./compile (you only need java & maven to do that).

Then run one of the next scrips:
(you also should edit a properties file, download required data etc.)

* ./runBuildSemanticModelv2 to build the OWL file of the CAPEC&CWE model in the "instance" (itmutgu) style (i.e. snapshots/20181222v2.owl).
* ./runBuildSemanticModel to build the OWL file of the CAPEC&CWE model in the A10-16 style (i.e. snapshots/20181129.owl) - obsolete.

## Author

[Andrei Brazhuk](https://scholar.google.com/citations?user=lxR8RLkAAAAJ&hl)

## License

It is a free software in any meaning you wish.
No legal claims are accepted too.