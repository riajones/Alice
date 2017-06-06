import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.*;
import java.util.Scanner;

public class AOrganizer{
	int os = 0;
	HashMap<String, String> directory_paths;
	AGraphics graphics;
	AOrganizer(){
		directory_paths = new HashMap(new Hashtable<String, String>());
		read_paths();
	}
	
	File browse_to(String target){
		File file = new File(find_root());
		ArrayList<String> path = parse_url(target);
		if(path.size() == 1) return file;
		for(int i = 0; i < path.size(); i++){
			File subs[] = file.listFiles();
			boolean found = false;
			for(int j = 0; j < subs.length; j++){
				if(path.get(i).equals(subs[j].getName())){
					found = true;
					file = subs[j];
				}
			}
			if(!found){
				System.out.println("Invalid URL");
				file = null;
				break;
			}
		}
		return file;
	}
	
	ArrayList<String> parse_url(String url){
		ArrayList<String> path = new ArrayList();
		String name = "";
		for(int i = 0; i < url.length(); i++){
			char next = url.charAt(i);
			if(next == '\\'){
				path.add(name);
				name = "";
			} else name += next;
		}
		if(name != null && name != "")
			path.add(name);
		return path;
	}
	
	String find_root(){
		String path = "..";
		File file = new File(".");
		boolean running = true;
		while(running){
			path += "/..";
			if(new File(path) != null){
				file = new File(path);
				String t_files[] = new String[0];
				switch(os){
					case 0:
						t_files = new String[3];
						t_files[0] = "Users";
						t_files[1] = "Program Files";
						t_files[2] = "Windows";
						break;
					default:
						break;
				}
				boolean isRoot = true;
				File filez[] = file.listFiles();
				for(int i = 0; i < t_files.length; i++){
					boolean found = false;
					for(int j = 0; j < filez.length; j++){
						if(filez[j].getName().equals(t_files[i])) found = true;
					}
					if(!found) isRoot = false;
				}
				if(isRoot) running = false;
			} else running = false;
		}
		return file.getPath();
	}
	
	String sort(String target){
		String msg = "";
		String new_path = "";
		File f;
		
		// This system of path entry needs work (Doesn't work in graphics mode)
		if(directory_paths.get(target) == null){
			System.out.println("I don't know where that folder it.\nPlease enter the path or type SCAN");
			new_path = find_root() + "\\";
			if(graphics == null){
				Scanner scan = new Scanner(System.in);
				new_path += scan.nextLine();
			} else{
				while(target == null){
					new_path += graphics.getInput();
				}
			}
			directory_paths.put(target, new_path);
			System.out.println(new_path);
			f = new File(new_path);
		} else{
			f = new File(directory_paths.get(target));
			msg = "Directory Found";
		}
		
		InputStream in;
		File subs[] = f.listFiles();
		for(int i = 0; i < subs.length; i++){
			String name = subs[i].getName();
			System.out.println(name);
			String type = "";
			for(int j = name.length() - 3; j < name.length(); j++){
				type += name.charAt(j);
			}
			
			// This sorting scheme should be improved
			if(type.equals("txt") || type.equals("pdf") || type.equals("doc") || type.equals("csv") || type.equals("ocx")){
				type = "text";
			} else if(type.equals("mp3") || type.equals("wma") || type.equals("wav")){
				type = "music";
			} else if(type.equals("mp4") || type.equals("avi") || type.equals("flv") || type.equals("mkv")){
				type = "video";
			} else if(type.equals("png") || type.equals("jpg") || type.equals("peg") || type.equals("psd")){
				type = "photo";
			} else{
				type = "NONE";
			}
			
			System.out.println(type);
			if(!type.equals("NONE")){
				boolean found = false;
				for(int j = 0; j < subs.length; j++)
					if(subs[j].getName().equals(type)) found = true;
				if(!found){
					File folder = new File(f.getPath() + "\\" + type);
					folder.mkdir();
				}
				
				byte[] buffer = new byte[2048];
				int length;
				try{
					in = new FileInputStream(subs[i]);
					File cpy = new File(f.getPath() + "\\" + type + "\\" + name);
					OutputStream out = new FileOutputStream(cpy);
					while((length = in.read(buffer)) > 0){
						out.write(buffer, 0, length);
					}
					in.close();
					out.close();
					subs[i].delete();
				} catch(Exception e){}
			}
		}

		return msg;
	}
	
	private void read_paths(){
		Pattern p = Pattern.compile("(.+)=(.+);");
		try{
			BufferedReader in = new BufferedReader(new FileReader("paths.alice"));
			String line;
			while((line = in.readLine()) != null){
				Matcher m = p.matcher(line);
				if(m.find()) directory_paths.put(m.group(1), m.group(2));
			}
		} catch(Exception e){}
	}
	
	void write_paths(){
		String buffer = "";
		Iterator<String> it = directory_paths.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			buffer += key + "=" + directory_paths.get(key) + ";";
		}
		try{
			BufferedWriter wr = new BufferedWriter(new FileWriter("paths.alice"));
			wr.write(buffer);
			wr.close();
		} catch(Exception e){}
	}
}

/*
		try{
			BufferedReader in = new BufferedReader(new FileReader("facts.txt"));
			String line;
			while((line = in.readLine()) != null){
				
			}
		} catch(Exception e){System.out.println("");}
*/

		/*
		//File file = new File("../../Casual JAVA/Alice");
		File file = new File(find_root());
		//System.out.println(file.getPath());
		File[] roots = file.listFiles();
		for(int i = 0; i < roots.length; i++){
			if(roots[i].getName().equals("Users")){
				String path = roots[i].getPath();
				System.out.println("Found Users:\n" + path);
				file = roots[i];
			}
		}
		roots = file.listFiles();
		if(roots != null)
			for(int i = 0; i < roots.length; i++)
				System.out.println(roots[i].getName());
			*/