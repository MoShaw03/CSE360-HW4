package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Question {
	
	
	private LocalDateTime date = LocalDateTime.now();
	private DateTimeFormatter dateformatsort = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateformattable = DateTimeFormatter.ofPattern("yyyy-MM-d, H:mm");
	private DateTimeFormatter dateformatcasual = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' H:mm");

	private String user;
	private String topic;
	private String body;
	private boolean answered;
	
	protected Answers answers = new Answers();
	protected Reviews reviews = new Reviews();
	
	public Question(LocalDateTime date, String user, String topic, String body, boolean answered) {
		this.date = date;
		this.user = user;
		this.topic = topic;
		this.body = body;
		this.answered = answered;
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
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Answers getAnswers() {
		return answers;
	}
	public void addAnswer(Answer answer) {
		answers.addAnswer(answer);
	}
	public void setAnswers(Answers answers) {
		this.answers = answers;
	}	
	public boolean isAnswered() {
		return answered;
	}
	public void setAnswered(boolean answered) {
		this.answered = answered;	
	}
	/**
	 * Return the {@link Reviews} collection relevant to this question.
	 *
	 * @return the reviews collection. 
	 */

    public Reviews getReviews() {
        return reviews;
    }
    
    /**
     * Replace the reviews collection for this question.
     *
     * @param reviews new {@link Reviews} instance.
     */

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }
    
    /**
     * Add a {@link Review} to this question's reviews collection.
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
