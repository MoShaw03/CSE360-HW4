package application;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

public class Answer {
	
	private LocalDateTime date = LocalDateTime.now();
	private DateTimeFormatter dateformatsort = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateformattable = DateTimeFormatter.ofPattern("yyyy-MM-d, H:mm");
	private DateTimeFormatter dateformatcasual = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' H:mm");
	
	private String user;
	private String body;
	private boolean helpful;
	
	protected Question question;
	protected Reviews reviews = new Reviews();
	
	public Answer (LocalDateTime date, String user, String body, Question question, boolean helpful) {
		this.date = date;
		this.user = user;
		this.body = body;
		this.question = question;
		question.answers.addAnswer(this);
		this.helpful = helpful;
	}
	public String getDate() {
		return date.format(dateformatsort);
	}
	public String getDate(String format) {
		switch(format) {
		case "casual":
			return date.format(dateformatcasual).replaceFirst(String.valueOf(date.getDayOfMonth()), date.getDayOfMonth() + daySuffix(date.getDayOfMonth()));
		case "table":
			return date.format(dateformattable);
		case "sort":
		default:
			return date.format(dateformatsort);
		}
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public boolean isHelpful() {
		return helpful;
	}
	public void setHelpful(boolean helpful) {
		this.helpful = helpful;
		// Checks to see if any of the other answers are already helpful, before setting the question to be answered or unanswered
		ObservableList<Answer> answerlist = question.answers.getAnswerList();
		for(Answer i : answerlist) {
			if(i.isHelpful()) {
				return;
			}
		}
		question.setAnswered(helpful);
	}
	/**
	 * Return the {@link Reviews} collection relevant to this answer.
	 *
	 * @return the reviews collection. 
	 */
    public Reviews getReviews() {
        return reviews;
    }
    /**
     * Replace the reviews collection for this answer.
     *
     * @param reviews new {@link Reviews} instance.
     */
    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }
    /**
     * Add a {@link Review} to this answer's reviews collection.
     *
     * @param review the review to add.
     */
    public void addReview(Review review) {
        this.reviews.add(review);
    }
	private String daySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

}
