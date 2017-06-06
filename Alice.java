import java.util.Scanner;
import java.util.Date;
import java.util.Random;

public class Alice{
	AGraphics graphics;
	boolean running, isGraphics;
	Thread input_thread, timer_thread;
	Facts facts;
	Scanner scan;
	WordNet wn;
	AOrganizer org;
	Reminder remind = new Reminder();
	
	// Timers
	int input_received, input_delay, blink;
	int comment_count;
	int max_input_delay;
	
	// Comments
	String processing_time_comments[];
	String input_delay_comments[];
	
	Alice(){
		isGraphics = true;
		init();
	}
	Alice(boolean g){
		isGraphics = g;
		init();
	}
	private void salutation(){
		Date date = new Date();
		int hour = date.getHours();
		String text = "";
		if(hour < 12 && hour >= 5)
			text = "Good Morning";
		else if(hour < 18 && hour >= 12)
			text = "Good Afternoon";
		else
			text = "Good Evening";
		String name = facts.get("name");
		if(name.equals("")){
			System.out.println("My Name is Alice.\nWhat is your name?");
			name = scan.nextLine();
			facts.Add("name", name);
			name += " nice to meet you.";
		}
		if(isGraphics) graphics.draw_text(text + " " + name);
		System.out.println(text + " " + name);
	}
	
	private void init(){
		// Initializing Wait Times
		max_input_delay = 1500;
		
		// Initializing Timers
		input_received = 0;
		comment_count = 0;
		blink = 0;
		input_delay = max_input_delay;

		// Initializing AI Comments
		get_p_time_comments();
		get_input_delay_comments();
		
		org = new AOrganizer();
		facts = new Facts();
		running = true;
		scan = new Scanner(System.in);

		wn = new WordNet();
		if(isGraphics){
			graphics = new AGraphics();
		}
		org.graphics = graphics;
		
		input_thread = new Thread(new AInput());
		input_thread.start();
		timer_thread = new Thread(new ATime_Manager());
		timer_thread.start();
		
		salutation();
	}
	
	
	class AInput implements Runnable{
		AParser parser;
		AInput(){
			parser = new AParser(facts);
			parser.wn = wn;
			parser.org = org;
			input_delay = max_input_delay;
			parser.reminder = remind;
		}
		@Override
		public void run(){
			String text;
			while(running){
				if(isGraphics){
					text = graphics.getInput();
				} else text = scan.nextLine();
				if(text != null){
					blink = 10;
					parser.init(text);
					input_received = 950;
					comment_count = 0;
					String answer = parser.interpret();
					if(isGraphics){
						if(answer.equals("CLEAR GRAPHICS!\n")){
							//System.out.println("Clearing Graphics");
							graphics.clear_output();
						} else graphics.draw_text(answer);
					}
					else{
						if(answer.equals("CLEAR GRAPHICS!\n")){
							for(int i = 0; i < 50; i++)
								System.out.println("\n");
						}
						else System.out.println(answer);
					}
					running = parser.end();
					if(isGraphics && !running){
						graphics.window.setVisible(false);
						graphics.window.dispose();
					}
					comment_count = 0;
					input_received = 0;
					input_delay = max_input_delay;
				} else{
					try{
						Thread.sleep(1);
					} catch(Exception e){}
				}
			}
			
			facts.Write();
			org.write_paths();
			remind.write();
			System.exit(0);
		}
	}
	
	class ATime_Manager implements Runnable{
		Random rand;
		String comment;
		String last_comment;
		int wait_time;
		int refresh_timer;
		boolean blinking;
		ATime_Manager(){
			rand = new Random();
			wait_time = 0;
			refresh_timer = 100;
			blinking = false;
			last_comment = "";
		}
		@Override
		public void run(){
			while(running){
				
				try{
					Thread.sleep(9);
				} catch(Exception e){}
				
				String reminder_text = remind.check();
				if(!reminder_text.equals("")){
					if(isGraphics){
						graphics.draw_text(reminder_text);
					} else{
						System.out.println(reminder_text);
					}
				}

				if(input_received > 0){
					input_received--;
					if(input_received == 0 && comment_count < 30){
						comment_count++;
						input_received += 550;
						comment = processing_time_comments[rand.nextInt(processing_time_comments.length)];
						while(comment.equals(last_comment))
							comment = processing_time_comments[rand.nextInt(processing_time_comments.length)];
						if(comment != null && !comment.equals("")){
							last_comment = comment;
							if(isGraphics){
								graphics.draw_text(comment);
							} else{
								System.out.println(comment);
							}
						}
					}
				} else if(input_delay > 0){
					input_delay--;
					if(input_delay == 0 && comment_count < 4){
						comment_count++;
						input_delay = max_input_delay;
						comment = input_delay_comments[rand.nextInt(input_delay_comments.length)];
						while(comment.equals(last_comment))
							comment = input_delay_comments[rand.nextInt(input_delay_comments.length)];
						if(comment != null && !comment.equals("")){
							last_comment = comment;
							if(isGraphics){
								graphics.draw_text(comment);
							} else{
								System.out.println(comment);
							}
						}
					}
				}
				
				if(blink > 0 && isGraphics){
					blink--;
					if(!blinking){
						graphics.y_scale -= 0.1;
						if(blink == 0){
							blink = 10;
							blinking = true;
						}
					}
					else{
						graphics.y_scale += 0.1;
						if(graphics.y_scale >= 1)
							blinking = false;
					}
					graphics.window.repaint();
				}
				
				if(isGraphics){
					if(refresh_timer > 0){
						refresh_timer--;
						if(refresh_timer == 0){
							refresh_timer = 100;
							graphics.window.repaint();
						}
					}
				}
			}
		}
	}

	
	void get_p_time_comments(){
		processing_time_comments = new String[4];
		processing_time_comments[0] = "Hmmmm.....";
		processing_time_comments[1] = "This is a tricky one";
		processing_time_comments[2] = "Give me another second";
		processing_time_comments[3] = "";
	}
	
	void get_input_delay_comments(){
		input_delay_comments = new String[6];
		input_delay_comments[0] = "Hello?";
		input_delay_comments[1] = "Are you there? I'm waiting";
		input_delay_comments[2] = "Go ahead, say something";
		input_delay_comments[3] = "What are you thinking about?";
		input_delay_comments[4] = "Ask me a question";
		input_delay_comments[5] = "";
	}
}