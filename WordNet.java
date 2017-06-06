import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.regex.*;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;



public class WordNet{
	HashMap<String, Word> words;
	HashMap<String, Word> buffer;
	WordNet(){
		words = new HashMap(new Hashtable<String, Word>());
		buffer = new HashMap(new Hashtable<String, Word>());

		try{
			BufferedReader in = new BufferedReader(new FileReader("synsets.alice"));
			String line;
			Pattern syn = Pattern.compile("wn_synset. VALUES .([0-9]+),[0-9]*,\'(.+)['],[']");
			while((line = in.readLine()) != null){
				Matcher m = syn.matcher(line);
				if(m.find()){
					//System.out.println(m.group(1) + " " + m.group(2));
					Word w = new Word();
					w.word = m.group(2);
					w.id = m.group(1);
					w.synonyms.add(m.group(2));
					if(buffer.get(m.group(1)) != null){
						for(int i = 0 ; i < buffer.get(m.group(1)).synonyms.size(); i++){
							w.synonyms.add(buffer.get(m.group(1)).synonyms.get(i));
						}
					}
					buffer.put(m.group(1), w);
				}
			}
			in.close();
		} catch(Exception e){System.out.println(e);}
		//System.out.println("Loaded All Synsets");
		try{
			BufferedReader in = new BufferedReader(new FileReader("defs.alice"));
			String line;
			Pattern p = Pattern.compile("wn_gloss. VALUES [(]([0-9]+),['](.+)['][)];");
			while((line = in.readLine()) != null){
				Matcher m = p.matcher(line);
				if(m.find()){
					buffer.get(m.group(1)).definition = m.group(2);
				}
			}
		} catch(Exception e){System.out.println(e + " Loading Definitions ");}
		AParser p = new AParser();
		/*
		System.out.println("Loaded All Definitions");
		for(int i = 0; i < buffer.get("109543566").synonyms.size(); i++)
			System.out.println("<" + buffer.get("109543566").synonyms.get(i) + ">");
		*/
		Iterator<String> i = buffer.keySet().iterator();
		while(i.hasNext()){
			String key = i.next();
			for(int j = 0; j < buffer.get(key).synonyms.size(); j++)
				words.put(p.lower_case(buffer.get(key).synonyms.get(j)), buffer.get(key));
		}
		//System.out.println("Words Fully Loaded");
	}
	
	String search_prep(String text){
		String result = "";
		for(int i = 0; i < text.length(); i++){
			if(text.charAt(i) == ' ')
				result += '_';
			else result += text.charAt(i);
		}
		return result;
	}
}