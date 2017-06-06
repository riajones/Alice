import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.*;

class TestMain{
	public static void main(String args[]){
		WebPage p = new WebPage();
		String html = "";
		try{
			System.out.println("Reading");
			
			boolean paragraph = false;
			BufferedReader in = new BufferedReader(new FileReader("TestHtml.txt"));
			String line;
			Pattern st = Pattern.compile("<p>");
			Pattern en = Pattern.compile("</p>");
			while((line = in.readLine()) != null){
				Matcher m = st.matcher(line);
				if(m.find()) paragraph = true;

				if(paragraph) html += line + "\n";
				
				m = en.matcher(line);
				if(m.find()) paragraph = false;
			}
			in.close();
			p.html = html;

			System.out.println("Searching");
			System.out.println(p.read());

		} catch(Exception e){System.out.println(e);}
	}
}