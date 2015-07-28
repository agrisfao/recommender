## Recommender System for the SemaGrow Stack federation

The Recommender System is a piece of software - entirely based on JAVA - that computes meaningful combinations 
between some datasets federated by [SemaGrow](http://www.semagrow.eu/), generating a new triplestore: the “Recommender Database”. 
This work was funded by the European Commission under EU FP7 project [SemaGrow](http://www.semagrow.eu/) (Grant No. 318497).

The Recommender System computes meaningful combinations between two or more datasets federated by SemaGrow: 
the computation of combinations is based on the matching of AGROVOC URIs between datasets.

### System Requirements

- java >= 1.6 (mandatory)
- git >= 1.8.1.4 (to download the project from GitHub: other solutions may be adopted)
- maven >= 3.0.3 (to edit the code and build a new jar: you can also work with the provided command line application, without using Maven)
- linux environment (the provided command line application comes with a bash script to execute the code. A developer can replace the bash script with another one, as a bat script for Windows)

### Execute the command line application

The folder `executable` contains the command line application, including the bash script `start.sh` to run the recommender system. It contains two folders:
- `lib`: containing the needed JAR files (the "recommender system" jar and all dependances: dependances can be found in the `maven-source/target/classes/` folder)
- `resources`: containing the configuration file `defaults.properties` (that can be found in the `maven-source/target/classes/` folder)

The file `defaults.properties` contains input parameters for the recommender system:
* `sourceFilePath` is the path of the input file, containing one URI for each line. The system computes recommendations for each URI available in this file (e.g. /work/recommender/recomm/input.txt)
* `outputFilePath` defines the location of the output files. The path should include the default name of the file. Then, the system will add a timestamp to each produced file (e.g. /work/recommender/recomm/data/output.xml)
* `sparqlEndpointSG` is the [SemaGrow](http://www.semagrow.eu/) SPARQL endpoint federating target datasets of interest. It should contain at least the dataset of URIs defined in `sourceFilePath` and the output datasets (whose entities will be recommended to the client) specified by the `target_rdftype` parameter
* `max_recommendations` is the maximun number of desired recommendations for each entity
* `target_rdftype` defines the output of the recommender system: only concepts of that type will be considered recommendation for a specific URI

The Recommender System can run also by querying two individual SPARQL endpoints (for more than two endpoints, there is the need to use the federated mode). In the file `defaults.properties` simply configure the following properties:
* `type_recommendation` should be set to `individual` 
* `sparqlEndpoint1` is the SPARQL endpoint of the dataset of URIs defined in `sourceFilePath` 
* `sparqlEndpoint2` is the SPARQL endpoint of the output datasets (whose entities will be recommended to the client) specified by the `target_rdftype` parameter

### The Maven project

The folder `maven-source` contains the Maven source project. You can use it to edit the code or to build the "recommender system" jar file, needed by the command line application:

`cd maven-source/`   
`mvn clean install`  

You will find the created jar in the directory `maven-source/target`. In addition to that, the directory `maven-source/target/classes/` contains also all dependances (jars to be added to the classpath of the command line application) and the configuration file `default.properties` (to be added to the classpath of the command line application).
