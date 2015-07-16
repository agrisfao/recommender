package eu.semagrow.recommender.domain;

/**
 * A URI with the score of relevance
 * @author celli
 *
 */
public class ScoredURI implements Comparable<ScoredURI> {
	
	private String uri;
	private Double score;
	public Integer commonURIs;
	
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
	
	public Integer getCommonURIs() {
		return commonURIs;
	}

	public void setCommonURIs(Integer commonURIs) {
		this.commonURIs = commonURIs;
	}

	/**
	 * Only the URI should be the same
	 */
	public int compareTo(ScoredURI o) {
        return this.uri.compareTo(o.getUri());
    }
	
	public String toString() {
		return "["+this.getScore()+" - "+this.getUri()+"]";
	}

}
