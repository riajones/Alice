import java.util.ArrayList;
import java.util.regex.*;

class WhatQuestion extends Question{
	AParser parser;
	public WhatQuestion(AParser p, String question){
		question = question;
		parser = p;
		answer = solve();
	}
	
	String solve(){
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


		output = define();
		if(valid_answer(output))
			return output;

		output = check_definition();
		
		if(!valid_answer(output))
			output = "I don't know";
		return output;
	}
	
	String define(){
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
	}
	
	String check_definition(){
		String output = "";
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
		return output;
	}
	


}