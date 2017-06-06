import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.regex.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Calendar;
import java.util.GregorianCalendar;

// http://www.programcreek.com/2012/07/java-example-for-using-stanford-postagger/
public class AParser{
	AOrganizer org;
	ArrayList<String> words;
	ArrayList<String> pos;
	String orig_text = "";
	String lower_in;
	String why;
	Facts facts;
	WordNet wn;
	Reminder reminder;
	
	AParser(){
		
	}
	AParser(Facts f){
		facts = f;
	}
	
	void init(String input){
		orig_text = input;
		words = new ArrayList();
		pos = new ArrayList();
		lower_in = lower_case(orig_text);
		words = split_words(lower_in);
		why = "";
		//interpret();
		//definition();
	}
	
	
	String interpret(){
		String answer = "";
		answer += "\n";
		if(words.get(0).equals("who")){
			answer += answer_what();
		} else if(lower_in.equals("what is your name")){
			answer += "Alice";
		} else if(words.get(0).equals("search")){
			String search_phrase = "";
			for(int i = 1; i < words.size(); i++)
				search_phrase += words.get(i) + " ";
			GSearch s = new GSearch(search_phrase);
			answer += s.display_best();
		} else if(words.get(0).equals("show")){
			show();
			answer += "done";
		} else if(check_say()){
			answer += say();
		} else if(words.contains("hello")){
			answer += say_hello();
		} else if(words.contains("sqrt") || (words.contains("square") && words.contains("root"))){
			answer += sqrt();
		} else if(words.contains("list") && words.contains("reminders")){
			for(int i = 0; i < reminder.to_remember.size(); i++){
				int[] date = reminder.to_remember.get(i).date;
				answer += reminder.to_remember.get(i).text + " at " + date[2] + ":" + date[1] + " " + date[4] + "/" + date[3] + "/" + date[5];
			}
			if(reminder.to_remember.size() == 0) answer += "you haven't asked me to remember anything";
		} else if(words.get(0).equals("remind")){
			answer += remind();;
		} else if(words.get(0).equals("synonym") || words.get(0).equals("synonyms")){
			answer += find_synonymns();
		} else if(words.get(0).equals("math")){
			String solution = do_math();
			if(solution != null)
				answer += solution;
			else answer += "that isn't a real equation";
		} else if(words.get(0).equals("sort") || words.get(0).equals("organize")){
			answer += organize();
		} else if(lower_in.equals("what time is it")){
			answer += tell_time();
		} else if(words.get(0).equals("clear")){
			Pattern p = Pattern.compile("clear (\\w+ ?)");
			Matcher m = p.matcher(lower_in);
			if(m.find()){
				facts.remove(remove_stop(m.group(1)));
			} else answer = "CLEAR GRAPHICS!";
		} else if(words.get(0).equals("what") || (words.contains("tell") && words.contains("about")) || words.get(0).equals("define")){
			answer += answer_what();
		} else if(words.get(0).equals("where")){
			// This requires POS tagging for prepositions
		} else if(words.get(0).equals("why")){
			if(why != null)
				answer += why;
		} else if(words.get(0).equals("when")){
			answer += answer_when();
		} else if(!end()){
			answer += "goodbye";
		} else{
			Pattern syn = Pattern.compile("((.+ *)+) +is +((.* *)+)");
			Matcher m = syn.matcher(lower_in);
			if(m.find()){
				// This should only add nouns and verbs
				facts.Add(remove_stop(m.group(1)), m.group(3));
			} else{
				for(int i = 0; i < words.size(); i++){
					String word = lower_case(words.get(i));
					if(word.equals("alice")){
						
					} else if(wn.words.get(lower_in) != null){
						answer += wn.words.get(lower_in);
					} else{
						
					}
				}
			}
		}
		
		if(answer.equals("\n")){
			String search_phrase = "";
			for(int i = 1; i < words.size(); i++)
				search_phrase += words.get(i) + " ";
			GSearch s = new GSearch(search_phrase);
			answer += s.display_best();
		}
		if(answer.equals("\n")) answer += "I don't understand what you are saying.";
		answer += "\n";
		return answer;
	}
	
	String answer_what(){
		String extra = "";
		
		// Trying to solve as a math equation
		String solution = do_math();
		if(solution != null)
			return solution;
		
		//Checking against predefined facts
		Pattern q = Pattern.compile("is +((.+ ?)+)");
		Matcher m = q.matcher(lower_in);
		String output = "";
		if(m.find()){
			output = facts.get(remove_stop(m.group(1)));
			if(output == null){
				output = "";
				boolean found = false;
				for(int i = 0; i < words.size(); i++){
					if(found){
						String w = "";
						w = facts.get(words.get(i));
						if(w != null){
							output += w + " ";
						}
					}
					else if(words.get(i).equals("is")) found = true;
				}
			}
			if(!valid_answer(output)) output = "I don't know";
		}

		if(valid_answer(output) && !output.equals("I don't know"))
			return output;

		q = Pattern.compile("about +((.+ *)+)");
		m = q.matcher(lower_in);
		if(m.find()){
			//System.out.println("Adding Extra");
			extra = m.group(1) + " is ";
			output = facts.get(remove_stop(m.group(1)));
			if(output == null){
				output = "";
				boolean found = false;
				for(int i = 0; i < words.size(); i++){
					if(found){
						String w = "";
						w = facts.get(words.get(i));
						if(w != null){
							output += w + " ";
						}
					}
					else if(words.get(i).equals("is")) found = true;
				}
			}
			if(!valid_answer(output)) output = "I don't know";
		}
		if(valid_answer(output) && !output.equals("I don't know")){
			if(output.equals("I don't know")){
				return output + " what " + extra;
			} else return extra + output;
		}

		// Checking Word Definitions
		Pattern def = Pattern.compile("what does (.+) mean");
		m = def.matcher(lower_in);
		if(m.find()){
			try{
				String search = wn.search_prep(m.group(1));
				String definition = wn.words.get(search).definition;
				if(valid_answer(definition))
					return definition;
				for(int i = 0; i < wn.words.get(search).synonyms.size(); i++){
					definition = wn.words.get(wn.words.get(search).synonyms.get(i)).definition;
					if(valid_answer(definition))
						return definition;
				}
			} catch(Exception e){}
		}
		def = Pattern.compile("what is ?(a?) (.+)");
		m = def.matcher(lower_in);
		if(m.find()){
			try{
				String search = wn.search_prep(m.group(2));
				String definition = wn.words.get(search).definition;
				if(valid_answer(definition)){
					return m.group(1) + " " + m.group(2) + " is a " + definition;
				}
				for(int i = 0; i < wn.words.get(search).synonyms.size(); i++){
					definition = wn.words.get(wn.words.get(search).synonyms.get(i)).definition;
					if(valid_answer(definition))
						return m.group(1) + " " + m.group(2) + " is a " + definition;
				}
			} catch(Exception e){}
			
			if(!valid_answer(output)) return "I don't know";
		}


		
		// Checking Definitions for overlap
		ArrayList<String> q_words = split_words(remove_stop(lower_in));

		int best_overlap = -1;
		Iterator<String> it = wn.words.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			int overlap = 0;
			int unique = 0;
			String definition = wn.words.get(key).definition;
			if(valid_answer(definition)){
				ArrayList<String> def_words = split_words(definition);
				for(int i = 0; i < q_words.size(); i++){
					boolean found = false;
					for(int j = 0; j < def_words.size(); j++){
						if(q_words.get(i).equals(def_words.get(j))){
							overlap++;
							found = true;
						}
					}
					if(found) unique++;
				}
				overlap += unique * 5;
				if(overlap > best_overlap){
					//System.out.println("New Best Overlap: " + overlap);
					best_overlap = overlap;
					output = key + "   " + wn.words.get(key).definition;
				}
			}
		}
		
		if(output == null)
			output = "I don't know";
		return output;
	}
	
	String answer_when(){
		String answer = answer_what();
		boolean logic = false;
		String days[] = get_days();
		String months[] = get_months();
		Pattern patterns[] = new Pattern[2];
		patterns[0] = Pattern.compile("[0-9]+:[09]{2}");
		patterns[1] = Pattern.compile(" ?(am|pm) ?");
		for(int i = 0; i < patterns.length; i++){
			Matcher m = patterns[i].matcher(answer);
			if(m.find()){
				logic = true;
				break;
			}
		}
		if(!logic){
			for(int i = 0; i < months.length; i++){
				Pattern pat = Pattern.compile(months[i]);
				Matcher m = pat.matcher(answer);
				if(m.find()){	
					logic = true;
					break;
				}
			}
		}
		if(!logic){
			for(int i = 0; i < days.length; i++){
				Pattern pat = Pattern.compile(days[i]);
				Matcher m = pat.matcher(answer);
				if(m.find()){
					logic = true;
					break;
				}
			}
		}
		if(!logic){
			answer = "Your question doesn't make sense in this context.";
		}
		return answer;
	}
	
	String tell_time(){
		Date d = new Date();
		String hour = d.getHours() + "";
		if(hour.length() == 1)
			hour = "0" + hour;
		String min = d.getMinutes() + "";
		if(min.length() == 1)
			min = "0" + min;
		String sec = d.getSeconds() + "";
		if(sec.length() == 1)
			sec = "0" + sec;
		return hour + ":" + min + ":" + sec;
	}
	
	String remove_stop(String text){
		ArrayList<String> wordz = split_words(text);
		String output = "";
		String stops[] = get_stops();
		
		for(int i = 0; i < wordz.size(); i++){
			boolean found = false;
			String w = wordz.get(i);
			for(int j = 0; j < stops.length; j++){
				if(w.equals(stops[j]))
					found = true;
			}
			if(!found){
				if(valid_answer(output))
					output += " " + w;
				else output += w;
			}
		}
		
		return output;
	}
	
	String find_synonymns(){
		//System.out.println("Finding Synonyms");
		Pattern eq = Pattern.compile("synonyms? *([a-zA-Z\'\"]+)");
		Matcher m = eq.matcher(lower_in);
		if(m.find()){
			String result = "";
			Word w = wn.words.get(wn.search_prep(m.group(1)));
			if(w != null){
				for(int i = 0; i < w.synonyms.size(); i++){
					if(!w.synonyms.get(i).equals(m.group(1)))
						result += w.synonyms.get(i) + "  ";
				}
			}
			
			if(result != null)
				return result;
			
			return "I don't know any synonyms for " + m.group(1);
			
		} else return "I don't know any";
	}
	
	String say_hello(){
		String ans;
		Random rand = new Random();
		Word hello = wn.words.get("hello");
		String greeting = hello.synonyms.get(rand.nextInt(hello.synonyms.size()));
		ans = greeting + " " + facts.get("name");
		return ans;
	}
	
	String do_math(){
		double answer = 0;
		Pattern eq = Pattern.compile("([0-9]+) *([+*-/]?)");
		Matcher m = eq.matcher(lower_in);
		List<Double> terms = new ArrayList<Double>();
		ArrayList<String> operators = new ArrayList();
		while(m.find()){
			terms.add(Double.parseDouble(m.group(1)));
			operators.add(m.group(2));
		}
		
		if(terms.size() > 0){
			for(int i = 0; i < operators.size(); i++){
				if(operators.get(i).equals(" +")){
					operators.remove(i);
					operators.add(i, "+");
				} else if(operators.get(i).equals(" -")){
					operators.remove(i);
					operators.add(i, "-");
				} else if(operators.get(i).equals(" *")){
					operators.remove(i);
					operators.add(i, "*");
				} else if(operators.get(i).equals(" /")){
					operators.remove(i);
					operators.add(i, "/");
				}
			}
			
			String ops[] = {"*", "/"};
			answer = 0;
			for(int i = 0; i < ops.length; i++){
				while(operators.contains(ops[i])){
					int index = operators.indexOf(ops[i]);
					if(index < terms.size() - 1){
						boolean insert = false;
						switch(i){
							case 0:
								answer = terms.get(index) * terms.get(index + 1);
								insert = true;
								break;
							case 1:
								answer = terms.get(index) / terms.get(index + 1);
								insert = true;
								break;
							default:
								break;
						}
						if(insert){
							terms.remove(index);
							terms.add(index, answer);
							terms.remove(index + 1);
						}
					}
					operators.remove(index);
				}
			}
			answer = terms.get(0);
			for(int i = 1; i < terms.size(); i++){
				if(operators.get(i-1).equals("+")){
					answer += terms.get(i);
				} else if(operators.get(i-1).equals("-")){
					answer -= terms.get(i);
				} else{System.out.println("Operator Not Found: " + operators.get(i));}
			}
			return Double.toString(answer);
		}
		return null;
	}
	
	String sqrt(){
		String answer = "";
		Pattern p = Pattern.compile("[0-9.]+");
		Matcher m = p.matcher(lower_in);
		if(m.find()){
			double num = Double.parseDouble(m.group(0));
			answer = Double.toString(Math.sqrt(num));
		}
		return answer;
	}
	
	String organize(){
		String ans = "Invalid Expression";
		Pattern p = Pattern.compile("((sort)|(organize)) +(.+)");
		Matcher m = p.matcher(lower_in);
		if(m.find()){
			//System.out.println(m.group(1) + " " + m.group(4));
			ans = org.sort(m.group(4));
		}
		return ans;
	}
	
	String remind(){
		Pattern sub = Pattern.compile("(?:to|of) +(.+?)(?: in|$| at)");
		Pattern ti = Pattern.compile("(?:in|at) +(.+) *(?:to|$)");
		Pattern sec = Pattern.compile("(seconds?)");
		Pattern min = Pattern.compile("(minutes?)");
		Pattern hr = Pattern.compile("(hours?)");
		Pattern dy = Pattern.compile("days?");
		Pattern mo = Pattern.compile("months?");
		Pattern yr = Pattern.compile("years?");
		Matcher m = sub.matcher(lower_in);
		String subject = "";
		Calendar cal = new GregorianCalendar();
		int date[] = get_date();
		
		boolean found[] = {false, false};
		
		if(m.find()){
			found[0] = true;
			subject = m.group(1);
		}
		m = ti.matcher(lower_in);
		if(m.find()){
			found[1] = true;
			String time = m.group(1);
			ArrayList<String> w = split_words(lower_case(time));
			int index = -1;
			if(w.contains("tomorrow")) date[3]++;
			
			Matcher mtch = sec.matcher(time);
			if(mtch.find()) if(w.indexOf(mtch.group(1)) > 0) date[0] += Integer.parseInt(w.get(w.indexOf(mtch.group(1))-1));
			
			mtch = min.matcher(time);
			if(mtch.find()) if(w.indexOf(mtch.group(1)) > 0) date[1] += Integer.parseInt(w.get(w.indexOf(mtch.group(1))-1));
			
			mtch = hr.matcher(time);
			if(mtch.find()) if(w.indexOf(mtch.group(1)) > 0) date[2] += Integer.parseInt(w.get(w.indexOf(mtch.group(1))-1));
			
			mtch = dy.matcher(time);
			if(mtch.find()) if(w.indexOf(mtch.group(1)) > 0) date[3] += Integer.parseInt(w.get(w.indexOf(mtch.group(1))-1));
			
			mtch = mo.matcher(time);
			if(mtch.find()) if(w.indexOf(mtch.group(1)) > 0) date[4] += Integer.parseInt(w.get(w.indexOf(mtch.group(1))-1));
			
			mtch = yr.matcher(time);
			if(mtch.find()) if(w.indexOf(mtch.group(1)) > 0) date[5] += Integer.parseInt(w.get(w.indexOf(mtch.group(1))-1));
		}
		if(found[0] && found[1]){
			reminder.add(subject, date);
			return "will do";
		}
		return "I didn't catch that " + found[0] + " " + found[1];
	}
	
	void show(){
		//Desktop d = new Desktop.isDesktopSupported() ? Desktop.getDesktop(): null;
		//if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE
	}
	
	boolean check_say(){
		ArrayList<String> check = split_words(remove_stop(lower_in));
		for(int i = 0; i < check.size() && i < 3; i++)
			if(check.get(i).equals("say")) return true;
		return false;
	}
	
	String say(){
		String response = "";
		Random rand = new Random();
		ArrayList<String> phrase = split_words(remove_stop(lower_in));
		boolean begin = false;
		for(int i = 0; i < phrase.size(); i++){
			if(begin){
				Word w = wn.words.get(phrase.get(i));
				if(w != null){
					response += w.synonyms.get(rand.nextInt(w.synonyms.size())) + " ";
				} else response += phrase.get(i) + " ";
			} else if(phrase.get(i).equals("say")) begin = true;
		}
		String tmp = response;
		response = "";
		for(int i = 0; i < tmp.length(); i++){
			if(tmp.charAt(i) == '_' || tmp.charAt(i) == '-') response += ' ';
			else response += tmp.charAt(i);
		}
		return response;
	}
	
	String[] get_days(){
		String days[] = {"monday", "tuesday", "wednessday", "thursday", "friday", "saturday", "sunday"};
		return days;
	}
	
	String[] get_months(){
		String[] months = {"january", "february", "match", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
		return months;
	}
	
	boolean valid_answer(String text){
		if(text == null || text.equals(" ") || text.equals(""))
			return false;
		return true;
	}
	
		String lower_case(String text){
		String lower = "";
		for(int i = 0; i < text.length(); i++){
			char next = text.charAt(i);
			if((int)next > 64 && (int)next < 91){
				lower += (char)(next + 32);
			} else{
				lower += next;
			}
		}
		return lower;
	}
	
	ArrayList<String> split_words(String text){
		ArrayList<String> split = new ArrayList();
		String word = "";
		char breakpoints[] = {' ', ',', '.', '\'', '\"', '(', ')', '?', '!', '<', '>'};
		for(int i = 0; i < text.length(); i++){
			char next = text.charAt(i);
			boolean pass = true; 
			for(int j = 0; j < breakpoints.length; j++){
				if(next == breakpoints[j]){
					pass = false;
					break;
				}
			}
			if(pass) word += next;
			else{
				split.add(word);
				word = "";
			}
		}
		split.add(word);
		return split;
	}
	
	boolean end(){
		if(lower_in.equals("good night alice") || lower_in.equals("quit") || lower_in.equals("good bye alice") || lower_in.equals("goodbye alice")){
			return false;
		}
		return true;
	}
	
	int[] get_date(){
		Calendar cal = new GregorianCalendar();
		int date[] = new int[6];
		
		date[0] = cal.get(Calendar.SECOND);
		date[1] = cal.get(Calendar.MINUTE);
		date[2] = cal.get(Calendar.HOUR_OF_DAY);
		date[3] = cal.get(Calendar.DAY_OF_MONTH);
		date[4] = cal.get(Calendar.MONTH);
		date[5] = cal.get(Calendar.YEAR);
		return date;
	}
	
	String[] get_stops(){
		String ret[] = {"a",
		"about",
		"above",
		"after",
		"again",
		"against",
		"all",
		"alice",
		"am",
		"an",
		"and",
		"any",
		"are",
		"aren't",
		"as",
		"at",
		"be",
		"because",
		"been",
		"before",
		"being",
		"below",
		"between",
		"both",
		"but",
		"by",
		"can't",
		"cannot",
		"could",
		"couldn't",
		"did",
		"didn't",
		"do",
		"does",
		"doesn't",
		"doing",
		"don't",
		"down",
		"during",
		"each",
		"few",
		"for",
		"from",
		"further",
		"had",
		"hadn't",
		"has",
		"hasn't",
		"have",
		"haven't",
		"having",
		"he",
		"he'd",
		"he'll",
		"he's",
		"her",
		"here",
		"here's",
		"hers",
		"herself",
		"him",
		"himself",
		"his",
		"how",
		"how's",
		"i",
		"i'd",
		"i'll",
		"i'm",
		"i've",
		"if",
		"in",
		"into",
		"is",
		"isn't",
		"it",
		"it's",
		"its",
		"itself",
		"let's",
		"me",
		"more",
		"most",
		"mustn't",
		"my",
		"myself",
		"no",
		"nor",
		"not",
		"of",
		"off",
		"on",
		"once",
		"only",
		"or",
		"other",
		"ought",
		"our",
		"ours",
		"ourselves",
		"out",
		"over",
		"own",
		"same",
		"shan't",
		"she",
		"she'd",
		"she'll",
		"she's",
		"should",
		"shouldn't",
		"so",
		"some",
		"such",
		"than",
		"that",
		"that's",
		"the",
		"their",
		"theirs",
		"them",
		"themselves",
		"then",
		"there",
		"there's",
		"these",
		"they",
		"they'd",
		"they'll",
		"they're",
		"they've",
		"this",
		"those",
		"through",
		"to",
		"too",
		"under",
		"until",
		"up",
		"very",
		"was",
		"wasn't",
		"we",
		"we'd",
		"we'll",
		"we're",
		"we've",
		"were",
		"weren't",
		"what",
		"what's",
		"when",
		"when's",
		"where",
		"where's",
		"which",
		"while",
		"who",
		"who's",
		"whom",
		"why",
		"why's",
		"with",
		"won't",
		"would",
		"wouldn't",
		"you",
		"you'd",
		"you'll",
		"you're",
		"you've",
		"your",
		"yours",
		"yourself",
		"yourselves"};
		return ret;
	}
}