package eu.semagrow.recommender.sparql;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import jfcutils.http.GETHttpRequest;
import jfcutils.util.DateTime;

import eu.semagrow.recommender.Defaults;
import eu.semagrow.recommender.domain.Recommendation;
import eu.semagrow.recommender.domain.Score;
import eu.semagrow.recommender.domain.ScoredURI;
import eu.semagrow.recommender.io.XMLParser;

/**
 * Query the SPARQL endpoint using a REST HTTP request on the federated SPARQL endpoint, running a federated SPARQL query:
 * 
 * PREFIX dct: <http://purl.org/dc/terms/>
 * PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
 * SELECT distinct ?s (COUNT(?o) as ?NELEMENTS) WHERE {
 * <$URI> dct:subject ?o .
 * ?s dct:subject ?o .
 * ?s rdf:type <$RDF_TYPE> .
 * } 
 * GROUP BY ?s 
 * ORDER BY DESC(?NELEMENTS)
 * LIMIT 5
 * 
 * This algorithm is unscored, it does not compute the similarity score.
 * 
 * @author fabrizio celli
 *
 */
public class HTTPFederatedQuerier {

	private final static Logger log = Logger.getLogger(HTTPFederatedQuerier.class.getName());

	//SPARQL ENDPOINT
	private String sparqlEndpoint = Defaults.getString("sparqlEndpointSG");
	private String targetRdfType = Defaults.getString("target_rdftype");

	//pre-defined format and queries
	private String format = "application/sparql-results+xml";

	private String prefixes = "PREFIX dct: <http://purl.org/dc/terms/> " +
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	private String groupByQuery;
	private String sourceURI;
	private String countSubjects;

	/**
	 * Constructor with URI to be recommended
	 * @param sourceURI the URI to be recommended
	 */
	public HTTPFederatedQuerier(String sourceURI){
		this.sourceURI = sourceURI;
		
		//GROUP BY QUERY for the federated endpoint
		this.groupByQuery = "SELECT distinct ?s (COUNT(distinct ?o) as ?NELEMENTS) WHERE { " +
		"<"+sourceURI + "> dct:subject ?o . " +
		"?s dct:subject ?o . " +
		"?s rdf:type <"+targetRdfType+"> . " +
		"}" +
		"GROUP BY ?s " +
		"ORDER BY DESC(?NELEMENTS) " +
		"LIMIT " + (Recommendation.max_recommendations+1);
		
		//count subject query
		this.countSubjects = "SELECT (count(?o) as ?cnt) { " +
		"<"+sourceURI + "> dct:subject ?o . " +
		"}";

		//log.info(this.groupByQuery);
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
			String url = null;
			
			//HTTP request
			GETHttpRequest req = new GETHttpRequest();
			
			//the terms URIs: ORDERED SET
			List<ScoredURI> termsURIs = new LinkedList<ScoredURI>();
			Integer totalSubjects = null;
		
			//the federated query
			url = this.sparqlEndpoint + "?accept=" + URLEncoder.encode(this.format,"UTF-8");
			url += "&prefixes=" + URLEncoder.encode(this.prefixes,"UTF-8");
			url += "&query=" + URLEncoder.encode(this.groupByQuery,"UTF-8");
			parser.parseScoredURI(req.getUrlContentWithRedirect(url, 15000), termsURIs);

			//create recommendations
			if(termsURIs.size()>0){
				
				//get total subjects
				url = this.sparqlEndpoint + "?accept=" + URLEncoder.encode(this.format,"UTF-8");
				url += "&prefixes=" + URLEncoder.encode(this.prefixes,"UTF-8");
				url += "&query=" + URLEncoder.encode(this.countSubjects,"UTF-8");
				try {
					totalSubjects = parser.parseLiteral(req.getUrlContentWithRedirect(url, 15000));
				}
				catch (Exception e){}
				
				//compute recommendations with score
				try {
					Recommendation r = new Recommendation(this.sourceURI);
					int i =1;
					for(ScoredURI uri: termsURIs){
						Score score = null;
						if(totalSubjects!=null && uri.getCommonURIs()!=null){
							score = new Score(uri.getCommonURIs(), totalSubjects);
							uri.setScore(score.gerSimilarityScore());
						}
						r.addRecommendation(uri, i);
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
			HTTPFederatedQuerier querier = new HTTPFederatedQuerier("http://agris.fao.org/aos/records/TH2014001739");
			List<Recommendation> recoms = new java.util.LinkedList<Recommendation>();
			querier.computeRecommendations(recoms);
			System.out.println(recoms);
			String endDate = DateTime.getDateTime();
			log.info(startDate + " -- " + endDate + " ["+DateTime.dateDiffSeconds(startDate, endDate)+"s]");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
