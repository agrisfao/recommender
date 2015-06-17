package eu.semagrow.recommender.domain;

import java.util.Arrays;

import eu.semagrow.recommender.Defaults;

/**
 * Domain object to represent a recommendation
 * @author celli
 *
 */
public class Recommendation {
	
	private String agris_uri;
	private String[] recommended_uris;
	
	public static final int max_recommendations = Integer.valueOf(Defaults.getString("max_recommendations"));
	
	/**
	 * Initialize the array with a size equals to Recommendation.max_recommendations
	 */
	public Recommendation(String agris_uri) {
		this.agris_uri = agris_uri;
		this.recommended_uris = new String[Recommendation.max_recommendations];
	}
	
	/**
	 * Add a recommendation to the Array
	 * @param uri the recommendation
	 * @param position the score of the recommendation, from 1 to Recommendation.max_recommendations
	 * @throws Exception if the position is not valid, out of the size of the Array
	 */
	public void addRecommendation(String uri, int position) throws Exception{
		if(position>0 && position<=Recommendation.max_recommendations){
			this.recommended_uris[position-1] = uri;
		}
		else
			throw new Exception("Position not valid!");
	}

	public String getAgris_uri() {
		return agris_uri;
	}

	public void setAgris_uri(String agrisUri) {
		agris_uri = agrisUri;
	}

	public String[] getRecommended_uris() {
		return recommended_uris;
	}

	public void setRecommended_uris(String[] recommendedUris) {
		recommended_uris = recommendedUris;
	}
	
	public String toString() {
		return this.agris_uri + ": "+Arrays.toString(this.recommended_uris);
	}

}
