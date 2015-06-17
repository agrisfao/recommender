package eu.semagrow.recommender.sparql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import jfcutils.http.GETHttpRequest;

import eu.semagrow.recommender.Defaults;
import eu.semagrow.recommender.domain.Recommendation;

/**
 * Query the SPARQL endpoint using a REST HTTP request on the federated SPARQL endpoint
 * @author celli
 *
 */
public class HTTPFederatedQuerier {

	private final static Logger log = Logger.getLogger(HTTPFederatedQuerier.class.getName());

	//SPARQL ENDPOINT
	private String sparqlEndpoint = Defaults.getString("sparqlEndpointSG");
	private String targetRdfType = Defaults.getString("target_rdftype");

	//pre-defined queries
	private String format = "application/sparql-results+xml";
	
	private String prefixes = "PREFIX dct: <http://purl.org/dc/terms/> " +
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
	
	private String groupByQuery;

	/**
	 * Constructor with URI to be recommended
	 * @param sourceURI the URI to be recommended
	 */
	public HTTPFederatedQuerier(String sourceURI){
		//GROUP BY QUERY for the federated endpoint
		this.groupByQuery = "SELECT distinct ?s (COUNT(?o) as ?NELEMENTS) WHERE { " +
		"<"+sourceURI + "> dct:subject ?o . " +
		"?s dct:subject ?o . " +
		"?s rdf:type <"+targetRdfType+"> . " +
		"}" +
		"GROUP BY ?s " +
		"ORDER BY DESC(?NELEMENTS) " +
		"LIMIT " + Recommendation.max_recommendations;

		log.info(this.groupByQuery);
	}

	/**
	 * Query the SPARQL endpoint to compute recommendations
	 * @param recoms
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public void computeRecommendations(List<Recommendation> recoms) throws MalformedURLException, IOException{
		if(recoms!=null){
			String url = this.sparqlEndpoint + "?accept=" + URLEncoder.encode(this.format,"UTF-8");
			url += "&prefixes=" + URLEncoder.encode(this.prefixes,"UTF-8");
			url += "&query=" + URLEncoder.encode(this.groupByQuery,"UTF-8");

			//HTTP request
			GETHttpRequest req = new GETHttpRequest();
			log.info(req.getUrlContentWithRedirect(url, 15000));
			//TODO: parse XML
		}
		else {
			log.warning("Recommendations list was not initialized");
		}
	}
	
	/*
	 * Test
	 */
	public static void main(String[] args){
		try {
			HTTPFederatedQuerier querier = new HTTPFederatedQuerier("http://agris.fao.org/aos/records/AU7500100");
			List<Recommendation> recoms = new java.util.LinkedList<Recommendation>();
			querier.computeRecommendations(recoms);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
