package eu.semagrow.recommender.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import eu.semagrow.recommender.domain.Recommendation;
import eu.semagrow.recommender.domain.ScoredURI;

/**
 * Write the output to an RDF file
 * @author celli
 *
 */
public class RDFWriter {

	//for RDF output
	private static final String rdfxml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<rdf:RDF " +
	"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
	"xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" " +
	"xmlns:semag=\"http://semagrow.eu/rdf/recom#\">";

	/**
	 * Write a list of recommendations as RDF/XML
	 * @param recoms the list of recommendations
	 * @param filepath the full path of the output file
	 * @throws IOException
	 */
	public void writeRDFXML(List<Recommendation> recoms, String filepath) throws IOException {

		BufferedWriter out = new BufferedWriter(new FileWriter(filepath));
		out.write(RDFWriter.rdfxml_header);
		out.newLine();

		//write all recommendations
		for(Recommendation r: recoms){
			StringBuffer recomBuffer = new StringBuffer();
			this.computeRDFXML(recomBuffer, r);
			out.write(recomBuffer.toString());
		}

		out.write("</rdf:RDF>");
		out.flush();
		out.close();

	}

	/*
	 * compute the RDF/XML portion for a recommendation
	 */
	private void computeRDFXML(StringBuffer recomBuffer, Recommendation recom){
		if(recom.getAgris_uri()!=null && recom.getRecommended_uris().length>0){
			recomBuffer.append("<rdf:Description rdf:about=\""+recom.getAgris_uri()+"\">\n");
			int i = 1;
			for(ScoredURI recomURL: recom.getRecommended_uris()){
				if(recomURL!=null){
					recomBuffer.append("\t<semag:rel><rdf:Description>\n");
					recomBuffer.append("\t\t<semag:order rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">"+i+"</semag:order>\n");
					recomBuffer.append("\t\t<semag:recom>\n");
					recomBuffer.append("\t\t\t<rdf:Description rdf:about=\""+recomURL.getUri()+"\"><rdfs:type rdf:resource=\"http://semagrow.eu/rdf/recom#CrawledDocument\"/></rdf:Description>\n");
					recomBuffer.append("\t\t</semag:recom>\n");
					if(recomURL.getScore()!=null)
						recomBuffer.append("\t\t<semag:score rdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">"+recomURL.getScore()+"</semag:score>\n");
					recomBuffer.append("\t</rdf:Description></semag:rel>\n");
					i++;
				}
			}
			recomBuffer.append("</rdf:Description>\n");
		}
	}

	/*
	 * Test
	 */
	public static void main(String[] args) throws Exception{
		List<Recommendation> recoms = new java.util.ArrayList<Recommendation>();
		Recommendation r = new Recommendation("http://agris.fao.org/aos/records/US8051305");
		r.addRecommendation(new ScoredURI("http://www.cabi.org/nutrition/general-nutrition-diet/", 0.1), 2);
		r.addRecommendation(new ScoredURI("http://www.nal.usda.gov/awic/pubs/Ferrets06/feed_nutrit_metab.htm", 0.1), 1);
		recoms.add(r);
		r = new Recommendation("http://agris.fao.org/aos/records/US8051306");
		r.addRecommendation(new ScoredURI("http://www.nal.usda.gov/awic/pubs/Ferrets06/feed_nutrit_metab.htm", 0.1), 2);
		r.addRecommendation(new ScoredURI("http://jaxmice.jax.org/strain/000648.html", 0.1), 1);
		recoms.add(r);
		RDFWriter writer = new RDFWriter();
		writer.writeRDFXML(recoms, "C:/Users/celli/Desktop/test.xml");
	}

}
