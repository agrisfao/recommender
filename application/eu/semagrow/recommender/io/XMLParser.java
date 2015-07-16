package eu.semagrow.recommender.io;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.semagrow.recommender.domain.ScoredURI;

/**
 * XML parser
 * @author celli
 *
 */
public class XMLParser {

	private final static Logger log = Logger.getLogger(XMLParser.class.getName());

	/**
	 * Parse an XML with <uri> elements and stores the content in a Set
	 * @param xmlStr the input XML
	 * @param termsURIs the output Set
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseURI(String xmlStr, Set<String> termsURIs) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		if(termsURIs!=null && xmlStr!=null){
			SAXParserFactory  spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(false);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlStr)), new URISaxParser(termsURIs));
		}
		else {
			log.warning("Set of terms URIs was not initialized");
		}
	}
	
	/**
	 * Parse an XML with <uri> elements and stores the content in a Set
	 * @param xmlStr the input XML
	 * @param termsURIs the output Set
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseScoredURI(String xmlStr, Set<ScoredURI> termsURIs) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		if(termsURIs!=null && xmlStr!=null){
			SAXParserFactory  spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(false);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlStr)), new UnscoredURISaxParser(termsURIs));
		}
		else {
			log.warning("Set of terms URIs was not initialized");
		}
	}
	
	/**
	 * Parse an XML with <uri> elements nested into a <binding> element with bindingName "name" attribute, and stores the content in a Set
	 * @param xmlStr the input XML
	 * @param bindingName the name of the binding element whose content is the desired uri
	 * @param termsURIs the output Set
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseURI(String xmlStr, String bindingName, Set<String> termsURIs) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		if(termsURIs!=null && xmlStr!=null && bindingName!=null){
			SAXParserFactory  spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(false);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlStr)), new BindingURISaxParser(termsURIs, bindingName));
		}
		else {
			log.warning("Set of terms URIs was not initialized");
		}
	}

	/**
	 * Parse an XML with <uri> elements and stores the content in a Map, counting the occurrences
	 * @param xmlStr
	 * @param termsURIsOccurr
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseURI(String xmlStr, Map<String, Integer> termsURIsOccurr) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		if(termsURIsOccurr!=null && xmlStr!=null){
			//parse XML
			List<String> listUris = new LinkedList<String>();
			SAXParserFactory  spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(false);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlStr)), new URISaxParser(listUris));

			//count occurrences
			for (String uri: listUris) {
				if(termsURIsOccurr.containsKey(uri)) {
					int value = termsURIsOccurr.get(uri);
					value = value+1;
					termsURIsOccurr.put(uri, value);
				}
				else
					termsURIsOccurr.put(uri, 1);
			}
		}
		else {
			log.warning("Set of terms URIs was not initialized");
		}
	}
	
	/**
	 * Parse an XML with one <literal> element
	 * @param xmlStr
	 * @return the integer value, null in case of errors
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public Integer parseLiteral(String xmlStr) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException{
		if(xmlStr!=null){
			//parse XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlStr));
			Document doc = builder.parse(is);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//literal");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				String value = nodes.item(i).getTextContent();
				if(value!=null){
					value = value.trim();
					return Integer.valueOf(value);
				}
			}
		}
		return null;
	}

}
