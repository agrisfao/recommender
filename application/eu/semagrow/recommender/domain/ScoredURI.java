package eu.semagrow.recommender.domain;

/**
 * A URI with the score of relevance
 * @author celli
 *
 */
public class ScoredURI {
	
	private String uri;
	private Double score;
	
	public ScoredURI(String uri, Double score){
		this.uri = uri;
		this.score = score;
	}
	
	public ScoredURI(){}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Double getScore() {
		return score;
	}
	
	public void setScore(Double score) {
		this.score = score;
	}
	
	public String toString() {
		return "["+this.getScore()+" - "+this.getUri()+"]";
	}

}
