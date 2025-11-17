package application;

import java.time.LocalDateTime;

/**
 * Represents an item for moderation (Question, Answer, or Review).
 */
public class ModerationObj {
    /** Content type of the moderated item. */
    public enum ContentType { QUESTION, ANSWER, REVIEW }

    private final ContentType contentType;
    private final int id;
    private final LocalDateTime date;
    private final String author;
    private String text;
    private boolean deleted;

    /**
     * Creates an object/item for Staff to moderate.
     *
     * @param contentType content type (QUESTION, ANSWER, REVIEW)
     * @param id          database identifier
     * @param date        creation timestamp
     * @param author      author's username
     * @param text        current text
     * @param deleted     true if soft-deleted
     */
    public ModerationObj(ContentType contentType, int id, LocalDateTime date, String author, String text, boolean deleted) {
        this.contentType = contentType;
        this.id = id;
        this.date = date;
        this.author = author;
        this.text = text;
        this.deleted = deleted;
    }

    /** @return content type. */        
    public ContentType getContentType() { return contentType; }
    
    /** @return database id. */         
    public int getId() { return id; }
    
    /** @return creation timestamp. */  
    public LocalDateTime getDate() { return date; }
    
    /** @return author username. */     
    public String getAuthor() { return author; }
    
    /** @return body text. */           
    public String getText() { return text; }
    
    /** @return true if deleted. */     
    public boolean isDeleted() { return deleted; }

    /** Sets updated text after a staff edit. */          
    public void setText(String text) { this.text = text; }
    
    /** Marks item for soft-deletion by Staff. */     
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override 
    public String toString() 
    { 
    	return contentType + "#" + id + " by " + author + ": " + text; 
    	}
}