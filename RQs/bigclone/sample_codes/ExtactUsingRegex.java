import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExtactUsingRegex {
	// find AND start AND end AND compile AND matcher
	public static List<String> extractMatches(String text, String pattern) {
		List<String> matches = new LinkedList<String>();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		while(m.find()) {
			matches.add(text.subSequence(m.start(), m.end()).toString());
		}
		return matches;
	}
	//while\s*\(\s*[a-z,A-Z,0-9,_,$]+\s*\.\s*find\s*\(
	//[a-z,A-Z,0-9,_,$]+\s*\.\s*start\s*\(
	//[a-z,A-Z,0-9,_,$]+\s*\.\s*end\s*\(
	//[a-z,A-Z,0-9,_,$]+\s*\.\s*compile\s*\(
	//[a-z,A-Z,0-9,_,$]+\s*\.\s*matcher\s*\(
}
