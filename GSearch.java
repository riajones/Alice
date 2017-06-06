import java.util.ArrayList;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.regex.*;

import java.util.Scanner;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.IOException;


public class GSearch{
	String s_url, orig, response;
	int best_index;
	ArrayList<WebPage> links;
	
	GSearch(){
		init();
	}
	

	
	GSearch(String text){
		init();
		run(text);
	}
	
	private void init(){
		s_url = "https://www.google.com";
		links = new ArrayList();
		AParser p = new AParser();
		best_index = 0;
	}
	void run(String text){
		AParser p = new AParser();
		ArrayList<String> words = p.split_words(text);
		if(words.size() > 0){
			s_url += "/#q=" + words.get(0);
			for(int i = 1; i < words.size(); i++){
				String word = words.get(i);
				if(word.length() > 0 && word != " " && word != null)
					s_url += "%20" + words.get(i);
			}
		}
		// Creating a new thread for this might be a good idea
		search(text);
		int best_weight = -99999;
		best_index = 0;
		ArrayList<String> l = find_links(response);
		for(int i = 0; i < l.size(); i++){
			WebPage page = new WebPage();
			page.url = l.get(i);
			page.html = load(page.url);
			page.result_num = i;
			page.weight = page.analyze(text);
			links.add(page);
			if(page.weight > best_weight){
				best_weight = page.weight;
				best_index = i;
			}
		}
	}
	
	void img_search(String text){
		//Pattern p = Pattern.compile("imgurl=(http[:a-zA-Z\\./0-9_-]+\.(jpg|png|jpeg))");
	}
	
	ArrayList<String> find_links(String text){
		ArrayList<String> l = new ArrayList();

		Pattern p[] = new Pattern[5];
		p[0] = Pattern.compile("google");
		p[1] = Pattern.compile("youtube");
		p[2] = Pattern.compile("encrypted");
		p[3] = Pattern.compile("account");
		p[4] = Pattern.compile("schema");
		
		Pattern q = Pattern.compile("https?://[^\"&<> ]+");
		Matcher m = q.matcher(response);
		while(m.find()){
			boolean found = false;
			for(int i = 0; i < p.length; i++){
				Matcher mtch = p[i].matcher(m.group(0));
				if(mtch.find()) found = true;
			}
			if(!found){
				for(int i = 0; i < l.size(); i++){
					if(l.get(i).equals(m.group(0))){
						found = true;
						break;
					}
				}
				if(!found){
					l.add(m.group(0));
				}
			}
		}
		return l;
	}
	
	String display_best(){
		//return links.get(best_index).read();
		return links.get(best_index).url;
	}
	
	void view_links(){
		for(int i = 0; i < links.size(); i++)
			System.out.println(links.get(i).url);
	}
	

	void search(final String query){
		try{
			final URL url;
			url = new URL("https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8"));
			final URLConnection connection = url.openConnection();

			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.addRequestProperty("User-Agent", "Google Chrome/36");

			final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8");

			while(reader.hasNextLine()){
				final String line = reader.nextLine();
				response += line + "\n";

			}
			reader.close();
		} catch(Exception e){System.out.println(e);}
	}
	
	String load(final String target){
		String html = "";
		try{
			final URL url = new URL(target);
			final URLConnection con = url.openConnection();
			con.setConnectTimeout(60000);
			con.setReadTimeout(60000);
			con.addRequestProperty("User-Agent", "Google Chrome/36");
			
			final Scanner read = new Scanner(con.getInputStream(), "UTF-8");
			while(read.hasNextLine()){
				html += read.nextLine();
			}
			read.close();
		} catch(Exception e){}
		return html;
	}
	
}