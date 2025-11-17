package application;

import java.time.LocalDateTime;

public class Review {
    private Integer id;                  
    private LocalDateTime date;
    private String userName;
    private String body;
    private String reply;
    private boolean answer;             

    /**
     * This represents the data of a review which is either tied to a Question or an Answer. 
     * Includes a db primary key, timestamp, the reviewer's username, the text of the review, and a reference to the target being reviewed.
     */    
    
    public Review() {}

    /**
     * Review Constructor.
     *
     * @param id       database primary key.
     * @param date     created or last-updated timestamp; if {@code null}, callers may choose to set one later.
     * @param userName reviewer username.
     * @param body     textual content of the review (may be empty).
     * @param reply    the id of the target being reviewed stored as a String.
     * @param isAnswer true if {@code reply} is an Answer id; false if it's a Question id.
     */
    public Review(Integer id, LocalDateTime date, String userName, String body, String reply, boolean isAnswer) {
        this.id = id;
        this.date = date;
        this.userName = userName;
        this.body = body;
        this.reply = reply;
        this.answer = isAnswer;
    }
    
    
    /**
     * Constructor for creating a new Review with the current timestamp.
     *
     * @param userName reviewer username.
     * @param body     textual content of the review.
     * @param reply    id of the target being reviewed.
     * @param isAnswer true if {@code reply} is an Answer id; false if it's a Question id.
     */
    public Review(String userName, String body, String reply, boolean isAnswer) {
        this(null, LocalDateTime.now(), userName, body, reply, isAnswer);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public boolean isAnswer() { return answer; }
    public void setAnswer(boolean answer) { this.answer = answer; }

    @Override public String toString() {
        return "Review{" +
                "id=" + id +
                ", date=" + date +
                ", userName='" + userName + '\'' +
                ", reply='" + reply + '\'' +
                ", isAnswer=" + answer +
                '}';
    }

}
