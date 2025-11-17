package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * This is a Message model used to represent a message shared between two users. 
 * A {@code Message} stores the sender, receiver, textual body, a timestamp, and an optional reference to another {@code Message} that this message is replying to.
 */

public class Message {
	
	
	private LocalDateTime date = LocalDateTime.now();
	private DateTimeFormatter dateformatsort = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateformattable = DateTimeFormatter.ofPattern("yyyy-MM-d, H:mm");
	private DateTimeFormatter dateformatcasual = DateTimeFormatter.ofPattern("H:mm");
	
	
	private String sender;
	private String receiver;
	private String body;
	protected Message replyingTo;
	
	
   /**
     * Full constructor that sets all fields including the timestamp and reply reference.
     *
     * @param date the timestamp for the message.
     * @param sender the sender's identifier.
     * @param receiver the receiver's identifier.
     * @param bodytext the textual content of the message.
     * @param replyingTo optional message this message replies to.
     */
	public Message(LocalDateTime date, String sender, String receiver, String bodytext, Message replyingTo) {
		this.date = date;
		this.sender = sender;
		this.receiver = receiver;
		this.body = bodytext;
		this.replyingTo = replyingTo;
	}
	
   /**
     * Constructor that creates a message without a reply reference.
     *
     * @param date the timestamp for the message.
     * @param sender the sender's identifier.
     * @param receiver the receiver's identifier.
     * @param bodytext the textual content of the message.
     */
	public Message(LocalDateTime date, String sender, String receiver, String bodytext) {
		this.date = date;
		this.sender = sender;
		this.receiver = receiver;
		this.body = bodytext;
	}

	public LocalDateTime getDate() {
		return this.date;
	}
	
	public String getDateFormatted() {
		return date.format(dateformatsort);
	}
	
	public String getDateFormatted(String format) {
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

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String bodytext) {
		this.body = bodytext;
	}

	public Message getReplyingTo() {
		return replyingTo;
	}

	public void setReplyingTo(Message replyingTo) {
		this.replyingTo = replyingTo;
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
