package application;

/**
 * This class is a small implementation which is used by the UI to display and represent trust scores or reviewer ratings. 
 * For example, rating the helpfulness or trustworthiness of a reviewer. Certain aspects hold the reviewer's identifier and an integer rating.
 */

public class TrustedReview {
	
	private String reviewer;
	private int rating;
	
    /**
     * Construct a new TrustedReview with the specified reviewer id and rating.
     *
     * @param reviewer the reviewer's username or identifier.
     * @param rating   the numeric rating for the reviewer.
     */
	public TrustedReview(String reviewer, int rating) {
		this.reviewer = reviewer;
		this.rating = rating;
	}
	
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public String getReviewer() { return reviewer; }
	public int getRating() { return rating; }
	
}
