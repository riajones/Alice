import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;

public class Facts{
	// This should probably be rebuilt as a hash table
	HashMap<String, String> facts;
	//ArrayList<Fact> facts;
	Facts(){
		facts = new HashMap(new Hashtable<String, Fact>());
		try{
			BufferedReader in = new BufferedReader(new FileReader("facts.alice"));
			String line;
			while((line = in.readLine()) != null){
				String a = "";
				String b = "";
				boolean sw = false;
				for(int i = 0; i < line.length(); i++){
					char next = line.charAt(i);
					if(!sw){
						if(next == ';')
							sw = true;
						else{
							a += next;
						}
					} else{
						b += next;
					}
				}
				Add(a,b);
			}
			in.close();
		} catch(Exception e){System.out.println ("Fact Read Error");}
	}
	
	void Write(){
		//System.out.println("Writing");
		String all_facts = "";
		Iterator<String> i = facts.keySet().iterator();
		while(i.hasNext()){
			String key = i.next();
			all_facts += key + ";" + facts.get(key) + "\n";
		}

		try{
			BufferedWriter wr = new BufferedWriter(new FileWriter("facts.alice"));
			//System.out.println(all_facts);
			wr.write(all_facts);
			wr.close();
		} catch(Exception e){}
	}
	
	void remove(String key){
		facts.remove(key);
	}
	
	void Add(String s, String d){
		//System.out.println(s + " = " + d);
		//Fact f = new Fact(s,d);
		facts.put(s,d);
	}
	
	String get(String fact){
		return facts.get(fact);
	}

	public class Fact{
		String name, def;
		Fact(String n, String d){
			name = n;
			def = d;
		}
	}
}