import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;

public class Facts{
	// This should probably be rebuilt as a hash table
	
	ArrayList<Fact> facts;
	Facts(){
		facts = new ArrayList();
		try{
			BufferedReader in = new BufferedReader(new FileReader("facts.txt"));
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
		} catch(Exception e){
			
		}
	}
	
	void Write(){
		System.out.println("Writing");
		String all_facts = "";
		for(int i = 0; i < facts.size(); i++){
			Fact f = facts.get(i);
			all_facts += f.name + ";" + f.def + "\n";
		}
		try{
			BufferedWriter wr = new BufferedWriter(new FileWriter("facts.txt"));
			System.out.println(all_facts);
			wr.write(all_facts);
			wr.close();
		} catch(Exception e){}
		
	}
	
	void Add(String s, String d){
		Fact f = new Fact(s,d);
		facts.add(f);
	}
	
	String get(String fact){
		String result = "";
		for(int i = 0; i < facts.size(); i++){
			if(facts.get(i).name.equals(fact))
				result = facts.get(i).def;
		}
		return result;
	}

	public class Fact{
		String name, def;
		Fact(String n, String d){
			name = n;
			def = d;
		}
	}
}