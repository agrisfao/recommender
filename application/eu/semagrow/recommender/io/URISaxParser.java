package eu.semagrow.recommender.io;

import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class URISaxParser extends DefaultHandler {
	
	//outputs
	private Set<String> uris;
	private List<String> listUris;

	//data
	private boolean isURI;

	//to read the entire content
	private StringBuffer buffer; 
	
	public URISaxParser(Set<String> uris) {
		this.uris = uris;
	}
	
	public URISaxParser(List<String> uris) {
		this.listUris = uris;
		this.isURI = false;
	}

	/**
	 * Recognize an element
	 */
	public void startElement (String namespaceURI, String localName, String rawName, Attributes atts) {	
		if(rawName.equalsIgnoreCase("uri")){
			this.buffer = new StringBuffer();
			this.isURI = true;
		}
	}

	/**
	 * Extract content from XML
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) {
		//BUFFER reader
		if(this.isURI){
			this.buffer.append(ch, start, length);
		}
	}
	
	/**
	 * The end of an element. For big elements like ABSTRACT, to allow the buffering of all content
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String namespaceUri, String localName, String rawName)
	throws SAXException {
		if(rawName.equalsIgnoreCase("uri")){
			this.isURI = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("") && !term.contains("??") && !term.contains("\n") && !term.contains("&")){
				this.addToNotNullCollection(term);
			}
		}
	}
	
	/*
	 * Add to the Set or to the List
	 */
	private void addToNotNullCollection(String term){
		if(this.uris!=null)
			this.uris.add(term);
		else if(this.listUris!=null)
			this.listUris.add(term);
	}

}
