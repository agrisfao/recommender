package eu.semagrow.recommender.io;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BindingURISaxParser extends DefaultHandler {
	
	//outputs
	private Set<String> uris;
	
	//data
	private boolean isURI;
	
	//flag to identify the binging
	private boolean isBinding;
	private String binding;

	//to read the entire content
	private StringBuffer buffer;
	
	public BindingURISaxParser(Set<String> uris, String binding) {
		this.binding = binding;
		this.uris = uris;
		this.isURI = false;
		this.isBinding = false;
	}
	
	/**
	 * Recognize an element
	 */
	public void startElement (String namespaceURI, String localName, String rawName, Attributes atts) {	
		if(rawName.equalsIgnoreCase("uri") && this.isBinding){
			this.buffer = new StringBuffer();
			this.isURI = true;
		} else if(rawName.equalsIgnoreCase("binding")){
			String name = atts.getValue("name");
			if(name.equalsIgnoreCase(this.binding))
				this.isBinding = true;
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
		if(rawName.equalsIgnoreCase("uri") && this.isBinding){
			this.isURI = false;
			this.isBinding = false;
			String term = new String(this.buffer);
			if(term!=null && !term.trim().equals("")  && !term.contains("??") && !term.contains("\n") && !term.contains("&")){
				this.uris.add(term);
			}
		}
	}

}
