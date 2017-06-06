import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Reminder{
	ArrayList<Idea> to_remember;
	
	Reminder(){
		to_remember = new ArrayList();
		read();
	}
	
	String check(){
		String text = "";
		int date[] = new int[6];
		Calendar cal = new GregorianCalendar();

		date[0] = cal.get(Calendar.SECOND);
		date[1] = cal.get(Calendar.MINUTE);
		date[2] = cal.get(Calendar.HOUR_OF_DAY);
		date[3] = cal.get(Calendar.DAY_OF_MONTH);
		date[4] = cal.get(Calendar.MONTH);
		date[5] = cal.get(Calendar.YEAR);

		for(int i = 0; i < to_remember.size(); i++){
			Idea id = to_remember.get(i);
			boolean found[] = new boolean[6];
			for(int j = id.date.length - 1; j >= 0; j--){
				found[j] = false;
				if(id.date[j] <= date[j]){
					//System.out.println(j);
					found[j] = true;
				}
			}
			boolean match = true;
			for(int j = 0; j < found.length; j++)
				if(!found[j]) match = false;
			if(match){
				text += id.text + "\n";
				to_remember.remove(i);
			}
		}
		return text;
	}
	
	void add(String text, int sec, int min, int hr, int day, int month, int year){
		to_remember.add(new Idea(text, sec, min, hr, day, month, year));
	}
	
	void add(String text, int time[]){
		to_remember.add(new Idea(text, time));
	}
	
	void write(){
		try{
			BufferedWriter write = new BufferedWriter(new FileWriter("reminders.alice"));
			String buffer = "";
			for(int i = 0; i < to_remember.size(); i++){
				Idea id = to_remember.get(i);
				for(int j = 0; j < id.date.length; j++){
					buffer += id.date[j] + "_";
				}
				buffer += ";" + id.text + ";;\n";
			}
			write.write(buffer);
		} catch(Exception e){}
	}
	
	void read(){
		try{
			BufferedReader read = new BufferedReader(new FileReader("reminders.alice"));
			String line;
			Pattern p = Pattern.compile("(.+);(.+);;");
			while((line = read.readLine()) != null){
				Matcher m = p.matcher(line);
				if(m.find()){
					int d[] = new int[6];
					String num = "";
					int index = 0;
					for(int i = 0; i < m.group(1).length(); i++){
						if(m.group(1).charAt(i) == '_'){
							d[index] = Integer.parseInt(num);
							num = "";
							index++;
						} else num += m.group(1).charAt(i);
					}
					to_remember.add(new Idea(m.group(2), d));
				}
			}
			read.close();
		} catch(Exception e){}
	}
	
	class Idea{
		int date[];
		String text;
		Idea(String t, int sec, int min, int hr,  int day, int month, int year){
			date = new int[6];
			text = t;
			
			date[0] = sec;
			date[1] = min;
			date[2] = hr;
			date[3] = day;
			date[4] = month;
			date[5] = year;
		}
		Idea(String t, int time[]){
			date = new int[6];
			text = t;
			date[0] = time[0];
			date[1] = time[1];
			date[2] = time[2];
			date[3] = time[3];
			date[4] = time[4];
			date[5] = time[5];
		}
	}
	
	public static void main(String[] args){
		Reminder r = new Reminder();
		Calendar cal = new GregorianCalendar();
		int date[] = new int[6];
		
		date[0] = cal.get(Calendar.SECOND);
		date[1] = cal.get(Calendar.MINUTE);
		date[2] = cal.get(Calendar.HOUR_OF_DAY);
		date[3] = cal.get(Calendar.DAY_OF_MONTH);
		date[4] = cal.get(Calendar.MONTH);
		date[5] = cal.get(Calendar.YEAR);

		for(int i = 0; i < date.length; i++)
			System.out.print(date[i] + " ");
		System.out.println();
		
		date[1] = 41;
		
		r.add("working", date);
		
		String output = "";
		while(output.equals("")){
			output = r.check();
		}
		System.out.println(output);
		
		
	}
}