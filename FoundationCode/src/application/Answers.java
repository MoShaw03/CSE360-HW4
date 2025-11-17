package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Answers {
	
	private final ObservableList<Answer> answerList = FXCollections.observableArrayList();
	
	public void addAnswer(Answer answer) {
		answerList.add(answer);
	}
	
	public void addAllAnswers(ObservableList<Answer> answers) {
		answerList.addAll(answers);
	}
	
	public void removeAnswer(Answer answer) {
		answerList.remove(answer);
	}
	
	public int length() {
		return answerList.size();
	}
	
	public Answer getAnswer(int index) {
		return answerList.get(index);
	}

	public ObservableList<Answer> getAnswerList() {
		return answerList;
	}

}
