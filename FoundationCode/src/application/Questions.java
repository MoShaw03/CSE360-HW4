package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Questions {
	
	private final ObservableList<Question> questionList = FXCollections.observableArrayList();
	
	public void addQuestion(Question question) {
		questionList.add(question);
	}
	
	public void addAllQuestions(ObservableList<Question> questions) {
		questionList.addAll(questions);
	}
	
	public void removeQuestion(Question question) {
		questionList.remove(question);
	}

	public ObservableList<Question> getQuestionList() {
		return questionList;
	}
}
