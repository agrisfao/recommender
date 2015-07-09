package eu.semagrow.recommender.support.agris;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import jfcutils.files.write.TXTWriter;
import jfcutils.http.GETHttpRequest;

import eu.semagrow.recommender.io.XMLParser;

/**
 * Access the AGRIS SPARQL endpoint to extract URIs and store them into a text file
 * @author celli
 *
 */
public class URIExtractor {

	//SPARQL ENDPOINT and TYPE
	private String agrisEndpoint = "http://202.45.139.84:10035/catalogs/fao/repositories/agris";

	/**
	 * Given an array of years (int) and the full path of the output file, query the AGRIS Sparql endpoint
	 * to get URIs of resources published in a specific year and store URIs in a text file
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		if(args.length<2){
			System.out.println("You have to specify the full path of the output file " +
					"and at least one input year.");
		}
		else {
			//read parameters
			String outputFilePath = args[0];
			System.out.println("# "+outputFilePath);
			List<Integer> years = new LinkedList<Integer>();
			for(int j=1; j<args.length; j++)
				years.add(Integer.parseInt(args[j]));
			System.out.println("# "+years);

			//workers
			URIExtractor extr = new URIExtractor();
			TXTWriter writer = new TXTWriter();

			//delete the file if it exists
			File f = new File(outputFilePath);
			if(f.exists())
				f.delete();

			//start the algorithm
			for(int i: years){
				try {
					System.out.println("...Analyzing submission year "+i);
					Set<String> agrisURIs = extr.extractURI(i);
					System.out.println("\t* found "+agrisURIs.size()+" URIs");
					writer.appendStrings(agrisURIs, outputFilePath, true);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Extract URIs
	 */
	private Set<String> extractURI(int submissionYear) throws XPathExpressionException, MalformedURLException, ParserConfigurationException, SAXException, IOException{
		XMLParser parser = new XMLParser();
		String query = "select distinct ?s {?s <http://purl.org/dc/terms/dateSubmitted> \""+submissionYear+"\"}";
		//query to exclude AGRICOLA <http://ring.ciard.net/node/10972>
		//String query = "select distinct ?s {?s <http://purl.org/dc/terms/dateSubmitted> \""+submissionYear+"\" . FILTER (NOT EXISTS { ?s <http://purl.org/dc/terms/source> <http://ring.ciard.net/node/10972> . })}";
		
		String url = this.agrisEndpoint + "?accept=" + URLEncoder.encode("application/rdf+xml","UTF-8") + "&query=" + URLEncoder.encode(query,"UTF-8");
		//the output URIs
		Set<String> termsURIs = new HashSet<String>();
		//HTTP request
		GETHttpRequest req = new GETHttpRequest();
		parser.parseURI(req.getUrlContentWithRedirect(url, 15000), termsURIs);
		return termsURIs;
	}

}
