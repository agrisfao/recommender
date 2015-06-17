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

import org.xml.sax.SAXException;

import jfcutils.http.GETHttpRequest;
import jfcutils.util.MapUtil;
import eu.semagrow.recommender.Defaults;
import eu.semagrow.recommender.domain.Recommendation;
import eu.semagrow.recommender.io.XMLParser;

/**
 * Query the SPARQL endpoint using a REST HTTP request on different SPARQL endpoints
 * @author celli
 *
 */
public class HTTPSeparatedQuerier {

	private final static Logger log = Logger.getLogger(HTTPSeparatedQuerier.class.getName());

	//SPARQL ENDPOINT and TYPE
	private String sparqlEndpoint1 = Defaults.getString("sparqlEndpoint1");
	private String sparqlEndpoint2 = Defaults.getString("sparqlEndpoint2");
	private String targetRdfType = Defaults.getString("target_rdftype");

	//pre-defined queries
	private String format1 = "application/rdf+xml";
	private String format2 = "application/sparql-results+xml";

	private String prefixes = "PREFIX dct: <http://purl.org/dc/terms/> " +
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	private String mainQuery;
	private String combinationQuery;
	private String sourceURI;

	/**
	 * Constructor with URI to be recommended
	 * @param sourceURI the URI to be recommended
	 */
	public HTTPSeparatedQuerier(String sourceURI){
		this.sourceURI = sourceURI;
		//GROUP BY QUERY for the federated endpoint
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
			String url = this.sparqlEndpoint1 + "?accept=" + URLEncoder.encode(this.format1,"UTF-8");
			url += "&query=" + URLEncoder.encode(this.prefixes+" "+this.mainQuery,"UTF-8");
			//the terms URIs
			Set<String> termsURIs = new HashSet<String>();
			//HTTP request
			GETHttpRequest req = new GETHttpRequest();
			parser.parseURI(req.getUrlContentWithRedirect(url, 15000), termsURIs);

			//*******************************************
			//QUERY2: compute combinations
			Map<String, Integer> termsURIsOccurr = new HashMap<String, Integer>();
			for(String uri: termsURIs){
				//TODO: here we can extract more information (title, subjects...)
				this.combinationQuery = "SELECT ?url WHERE {" +
				"?url dct:subject <"+uri+"> . " +
				"?url rdf:type <"+targetRdfType+"> . " +
				"}";
				url = this.sparqlEndpoint2 + "?accept=" + URLEncoder.encode(this.format2,"UTF-8");
				url += "&query=" + URLEncoder.encode(this.prefixes+" "+this.combinationQuery,"UTF-8");
				//HTTP request
				parser.parseURI(req.getUrlContentWithRedirect(url, 15000), termsURIsOccurr);
			}
			
			//sort the map and creare the Recommendation
			if(termsURIsOccurr.size()>0){
				Map<String, Integer> sortedMap = MapUtil.sortByValue(termsURIsOccurr);
				//create the recommendation 
				try {
					Recommendation r = new Recommendation(this.sourceURI);
					int i =1;
					for(String s: sortedMap.keySet()){
						r.addRecommendation(s, i);
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
			HTTPSeparatedQuerier querier = new HTTPSeparatedQuerier("http://agris.fao.org/aos/records/PH2011000084");
			List<Recommendation> recoms = new java.util.LinkedList<Recommendation>();
			querier.computeRecommendations(recoms);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
