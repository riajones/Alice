import java.util.ArrayList;

public class Word{
	String word;
	String definition;
	String id;
	//ArrayList<Word> hypernmyns;
	//ArrayList<Word> hyponmyns;
	ArrayList<String> synonyms;
	Word(){
		//hypernmyns = new ArrayList();
		//hyponmyns = new ArrayList();
		synonyms = new ArrayList();
	}
}