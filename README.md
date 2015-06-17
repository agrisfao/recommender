## Recommender System for the SemaGrow Stack federation

The Recommender System is a piece of software - entirely based on JAVA - that computes meaningful combinations 
between the some datasets federated by [SemaGrow](http://www.semagrow.eu/), and generates a new triplestore: the “Recommender Database”. 
The Recommender System was funded by [SemaGrow](http://www.semagrow.eu/) FP7 EU Project.

It computes meaningful combinations between two or more datasets federated by SemaGrow: 
the computation of combinations is based on the matching of AGROVOC URIs between datasets.

The file `application/defaults.properties` contains input parameters for the recommender system:
* `sourceFilePath` is the path of the input file, containing one URI for each line. The system computes recommendations for each URI available in this file
* `sparqlEndpointSG` is the [SemaGrow](http://www.semagrow.eu/) SPARQL endpoint federating the datasets of interest. It should contain at least URIs defined in `sourceFilePath` and the datasets of recommendation specified by the `target_rdftype` parameter
* `max_recommendations` is the maximun number of desired recommendations for each entity
* `target_rdftype` defines the output of the recommender system: only concepts of that type will be considered recommendation for a specific URI
* `outputFilePath` defines the location of the output files  

The file `application/start.sh` is a bash script to run the recommender system. 
