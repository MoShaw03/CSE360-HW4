package application;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailRecognizer {
	public static String checkForValidEmail(String email){
		
		/* Since the program doesn't need to be picky about email address formats, 
		 * the input validation is mainly for allowing the user to see if they typed
		 * their email incorrectly
		 */

		Pattern validemail = Pattern.compile(".+@.+\\..+");  // Regex pattern for "characters @ characters . characters"
		Matcher matcher = validemail.matcher(email);
		if(matcher.find()){return "";}
		else {return "Invalid email address! Make sure you spelled your email address correctly.";}
	}
}
