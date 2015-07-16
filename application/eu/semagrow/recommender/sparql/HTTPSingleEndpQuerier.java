package eu.semagrow.recommender.sparql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import jfcutils.http.GETHttpRequest;
import jfcutils.util.DateTime;
import jfcutils.util.MapUtil;

import org.xml.sax.SAXException;

import eu.semagrow.recommender.Defaults;
import eu.semagrow.recommender.domain.Recommendation;
import eu.semagrow.recommender.domain.Score;
import eu.semagrow.recommender.domain.ScoredURI;
import eu.semagrow.recommender.io.XMLParser;

/**
 * Query the SPARQL endpoint using a REST HTTP request on the federated SPARQL endpoint, running two disting SPARQL queries:
 * 
 * PREFIX dct:<http://purl.org/dc/terms/>
 * SELECT ?term WHERE { 
 *    <$URI> dct:subject ?term . 
 * }
 *  
 * PREFIX dct:<http://purl.org/dc/terms/>
 * SELECT ?url WHERE {
 *    ?url dct:subject <$URI_FROM_PREVIOUS_QUERY>.
 *    ?url rdf:type <$RDF_TYPE> . 
 * }
 * 
 * @author celli
 *
 */
public class HTTPSingleEndpQuerier {
	
	private final static Logger log = Logger.getLogger(HTTPSingleEndpQuerier.class.getName());

	//SPARQL ENDPOINT
	private String sparqlEndpoint = Defaults.getString("sparqlEndpointSG");
	private String targetRdfType = Defaults.getString("target_rdftype");
	
	//pre-defined format and queries
	private String format = "application/sparql-results+xml";
	
	private String prefixes = "PREFIX dct: <http://purl.org/dc/terms/> " +
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	private String mainQuery;
	private String combinationQuery;
	private String sourceURI;
	
	/**
	 * Constructor with URI to be recommended
	 * @param sourceURI the URI to be recommended
	 */
	public HTTPSingleEndpQuerier(String sourceURI){
		this.sourceURI = sourceURI;
		this.mainQuery = "SELECT ?term WHERE { " +
		"<"+sourceURI + "> dct:subject ?term . " +
		"}";
		//log.info(this.mainQuery);
	}

	/**
	 * Query the SPARQL endpoint to compute recommendations
	 * @param recoms
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 */
	public void computeRecommendations(List<Recommendation> recoms) throws MalformedURLException, IOException, XPathExpressionException, ParserConfigurationException, SAXException{
		if(recoms!=null){
			XMLParser parser = new XMLParser();
			//*******************************************
			//QUERY1: get the URIs of terms to be combined
			String url = this.sparqlEndpoint + "?accept=" + URLEncoder.encode(this.format,"UTF-8");
			url += "&query=" + URLEncoder.encode(this.prefixes+" "+this.mainQuery,"UTF-8");
			//the terms URIs
			Set<String> termsURIs = new HashSet<String>();
			
			//log.info(url);
			//HTTP request
			GETHttpRequest req = new GETHttpRequest();
			parser.parseURI(req.getUrlContentWithRedirect(url, 15000), termsURIs);

			//*******************************************
			//QUERY2: compute combinations
			Map<String, Integer> termsURIsOccurr = new HashMap<String, Integer>();
			for(String uri: termsURIs){
				//TODO: here we can extract more information (title, subjects...)
				this.combinationQuery = "SELECT distinct ?url WHERE {" +
				"?url dct:subject <"+uri+"> . " +
				"?url rdf:type <"+targetRdfType+"> . " +
				"} limit 100";
				url = this.sparqlEndpoint + "?accept=" + URLEncoder.encode(this.format,"UTF-8");
				url += "&query=" + URLEncoder.encode(this.prefixes+" "+this.combinationQuery,"UTF-8");
				
				//log.info(url);
				//HTTP request
				parser.parseURI(req.getUrlContentWithRedirect(url, 15000), termsURIsOccurr);
			}
			
			//sort the map and creare the Recommendation
			if(termsURIsOccurr.size()>0){
				Map<String, Integer> sortedMap = MapUtil.sortByValueDescending(termsURIsOccurr);
				//create the recommendation 
				try {
					Recommendation r = new Recommendation(this.sourceURI);
					int i =1;
					for(String s: sortedMap.keySet()){
						Score score = new Score(sortedMap.get(s), termsURIs.size());
						r.addRecommendation(new ScoredURI(s, score.gerSimilarityScore()), i);
						i++;
						if(i>Recommendation.max_recommendations)
							break;
					}
					recoms.add(r);
				} catch(Exception e){
					log.warning("Problem generating recommendations");
				}
			}
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
			String startDate = DateTime.getDateTime();
			HTTPSingleEndpQuerier querier = new HTTPSingleEndpQuerier("http://agris.fao.org/aos/records/TH2014001739");
			List<Recommendation> recoms = new java.util.LinkedList<Recommendation>();
			querier.computeRecommendations(recoms);
			log.info(recoms.toString());
			String endDate = DateTime.getDateTime();
			log.info(startDate + " -- " + endDate + " ["+DateTime.dateDiffSeconds(startDate, endDate)+"s]");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
