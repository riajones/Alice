import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.regex.*;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;


class SQLTranslator{
	static HashMap<String, Word> words;
	public static void main(String args[]){
		words = new HashMap(new Hashtable<String, Word>());
		try{
			BufferedReader in = new BufferedReader(new FileReader("synsets.txt"));
			String line;
			Pattern syn = Pattern.compile("wn_synset. VALUES .([0-9]+),[0-9]*,\'([a-zA-Z_ ]+)");
			while((line = in.readLine()) != null){
				Matcher m = syn.matcher(line);
				if(m.find()){
					//System.out.println(m.group(1) + " " + m.group(2));
					Word w = new Word();
					w.word = m.group(2);
					w.id = m.group(1);
					words.put(m.group(1), w);
				}
			}
			in.close();
		} catch(Exception e){System.out.println(e);}
		System.out.println("Loaded All Synsets");
		try{
			BufferedReader in = new BufferedReader(new FileReader("defs.txt"));
			String line;
			Pattern p = Pattern.compile("wn_gloss. VALUES [(]([0-9]+),[']([a-zA-Z ;\\\"\']+)['][)];");
			while((line = in.readLine()) != null){
				Matcher m = p.matcher(line);
				if(m.find()){
					words.get(m.group(1)).definition = m.group(2);
				}
				
			}
		} catch(Exception e){System.out.println(e);}
		System.out.println("Loaded All Definitions");
		/*
		String pat = "wn_gloss. VALUES .";
		for(int i = 0; i < words.size(); i++){
			pat += words.get(i).id;
			pat += ",\'([a-zA-Z- ;:]+)";
			Pattern def = Pattern.compile(pat);
			Matcher m = def.matcher(entire_file);
			if(m.find()){
				words.get(i).definition = m.group(1);
			}
		}
		*/
		
		String output = "";
		Iterator<String> i = words.keySet().iterator();
		while(i.hasNext()){
			String key = i.next();
			String out = key + ";" + words.get(key).word + ";" + words.get(key).definition + "\n\n";
			//System.out.println(out);
			output += out;
		}
		System.out.println("Created Output Buffer");
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter("db.txt"));
			w.write(output);
			w.close();
		} catch(Exception e){System.out.println(e);}
	}
}