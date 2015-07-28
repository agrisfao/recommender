package eu.semagrow.recommender.io;

import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.semagrow.recommender.domain.ScoredURI;

public class UnscoredURISaxParser extends DefaultHandler {
	
	//outputs
	private Set<ScoredURI> uris;
	private List<ScoredURI> listUris;

	//data
	private boolean isURI;
	private boolean isScore;

	//to read the entire content
	private StringBuffer buffer; 
	
	//tmp
	private ScoredURI tmpURI;
	
	public UnscoredURISaxParser(Set<ScoredURI> uris) {
		this.uris = uris;
		this.isURI = false;
	}
	
	public UnscoredURISaxParser(List<ScoredURI> uris) {
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
		} else if(rawName.equalsIgnoreCase("literal")){
			this.buffer = new StringBuffer();
			this.isScore = true;
		} else if(rawName.equalsIgnoreCase("result")){
			this.tmpURI = new ScoredURI();
		}
	}

	/**
	 * Extract content from XML
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) {
		//BUFFER reader
		if(this.isURI || this.isScore){
			this.buffer.append(ch, start, length);
		}
	}
	
	/**
	 * The end of an element. For big elements like ABSTRACT, to allow the buffering of all content
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String namespaceUri, String localName, String rawName)
	throws SAXException {
		if(rawName.equalsIgnoreCase("uri") && tmpURI!=null){
			this.isURI = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("") && !term.contains("??") && !term.contains("\n") && !term.contains("&") && !term.contains(" ")){
				tmpURI.setUri(term);
			}
		} else if(rawName.equalsIgnoreCase("literal") && tmpURI!=null){
			this.isScore = false;
			String term = new String(this.buffer);
			if(term!=null){
				tmpURI.setCommonURIs(Integer.valueOf(term.trim()));
			}
		} else if(rawName.equalsIgnoreCase("result")){
			if(tmpURI!=null && tmpURI.getUri()!=null){
				this.addToNotNullCollection(tmpURI);
			}
		}
	}
	
	/*
	 * Add to the Set or to the List
	 */
	private void addToNotNullCollection(ScoredURI term){
		if(this.uris!=null)
			this.uris.add(term);
		else if(this.listUris!=null)
			this.listUris.add(term);
	}


}
