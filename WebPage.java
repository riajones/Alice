import java.util.regex.*;
import java.util.ArrayList;

public class WebPage{
	String html, url;
	int weight, result_num;
	WebPage(){
		init();
	}
	void init(){
		html = "";
		url = "";
		weight = 0;
		result_num = -1;
	}
	int analyze(String text){
		int value = 0;
		AParser p = new AParser();
		ArrayList<String> words = p.split_words(p.remove_stop(text));
		for(int i = 0; i < words.size(); i++){
			Pattern pat = Pattern.compile(words.get(i));
			Matcher m = pat.matcher(html);
			while(m.find())
				value++;
		}

		words = p.split_words(p.remove_stop(read()));
		value += (int)(words.size() / 50);
		
		return value - result_num;
	}
	
	String read(){
		String text = "";
		String piece = "";
		boolean tag = false;
		for(int i = 0; i < html.length(); i++){
			if(html.charAt(i) == '<'){
				tag = true;
				text += piece;
				piece = "";
			} else if(html.charAt(i) == '>'){
				tag = false;
			} else{
				if(!tag) piece += html.charAt(i);
			}
		}
		text += piece;
		
		return text;
	}
}