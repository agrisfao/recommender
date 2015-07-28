package eu.semagrow.recommender.domain;

/**
 * Similarity Score Algorithm
 * @author celli
 *
 */
public class Score {
	
	//threshold for the scoring index
	private static final Integer _threshold = 6;
	private static final Integer _maxThreshold = 10;
	
	//number of common URIs
	private Integer common;
	
	//number of URIs in the target tataset (e.g. AGRIS)
	private Integer totalTarget;
	
	public Score(Integer common, Integer totalTarget){
		this.common = common;
		this.totalTarget = totalTarget;
	}

	public Integer getCommon() {
		return common;
	}

	public void setCommon(Integer common) {
		this.common = common;
	}
	
	public Integer getTotalTarget() {
		return totalTarget;
	}

	public void setTotalTarget(Integer totalTarget) {
		this.totalTarget = totalTarget;
	}

	/**
	 * Compute the similarity index
	 * 1 - (6-min(6,#common)/6)
	 * @return the similarity index
	 */
	public Double getSimilarityIndex() {
		Integer rFactor = Math.min(_threshold, this.common);
		return 1.0 - ((_threshold-rFactor)*1.0/_threshold);
	}
	
	/**
	 * Return the similarity score: (common/min(totalTarget,10)) * similarityIndex
	 * @return the similarity score
	 */
	public Double gerSimilarityScore(){
		Double firstTerm = (this.common * 1.0)/Math.min(this.totalTarget, _maxThreshold);
		return firstTerm * this.getSimilarityIndex();
	}

}
