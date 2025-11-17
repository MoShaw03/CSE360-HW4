package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Container class that manages an observable list of {@link Message} objects.
 * 
 * This class wraps a JavaFX {@link ObservableList} to provide simple add/remove/get operations.
 * 
 */

public class Messages {
    /**
     * The internal observable list holding messages. 
     * Use {@link #getMessageList()} to obtain the list for reading.
     */
	private final ObservableList<Message> messageList = FXCollections.observableArrayList();
	
	public void addMessage(Message message) {
		messageList.add(message);
	}
	public void addAllMessages(ObservableList<Message> messages) {
		messageList.addAll(messages);
	}
	public void removeMessage(Message message){
		messageList.remove(message);
	}
	public Message getMessage (int index) {
		return messageList.get(index);
	}
	public ObservableList<Message> getMessageList(){
		return messageList;
	}
}
