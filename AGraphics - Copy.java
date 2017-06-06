import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.*;
import java.util.ArrayList;



public class AGraphics{
	private ArrayList<String> to_draw;
	JFrame window;
	private int x_size, y_size;
	boolean entered;
	int prompt_x, prompt_y;
	float y_scale;
	Font alice_font;
	String input_text;
	AGraphics(){
		alice_font = new Font("Serif", Font.PLAIN, 20);
		
		to_draw = new ArrayList();
		APanel panel = new APanel();
		window = new JFrame();
		JTextField aTitle = new JTextField("Alice");
		window.setTitle(aTitle.getText());
		window.addMouseListener(new AMouse());
		window.addKeyListener(new AKeyboard());
		
		x_size = 1200;
		y_size = 800;
		y_scale = 1;
		input_text = "";
		entered = false;
		
		prompt_x = (int)(x_size * 0.1);
		prompt_y = (int)(y_size * 0.91);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);
		window.setSize(x_size,y_size);
		window.setVisible(true);

	}
	void set_size(int x, int y){
		x_size = x;
		y_size = y;
		window.setSize(x_size, y_size);
	}
	
	void draw_text(String text){
		if(text != null && !text.equals(" ") && !text.equals("\n\n")){
			to_draw.add(text);
			if(to_draw.size() > 5)
				to_draw.remove(0);
		}
	}
	
	String getInput(){
		if(entered){
			entered = false;
			String tmp = input_text;
			input_text = "";
			window.repaint();
			return tmp;
		}
		return null;
	}
	
	
	void draw_eye(Graphics g){
		int eye_x = x_size;
		int eye_y = (int)(y_size * y_scale);
		
		int circle_x = (int)(eye_x * 0.8);
		int circle_y = (int)(eye_y * 0.8);
		int outline_x = (int)(eye_x * 0.03);
		int outline_y = (int)(eye_y * 0.03);
		int c_x = (int)(eye_x * 0.3);
		int c_y = (int)(eye_y * 0.3);
		int pupil_x = (int)(eye_x * 0.48);
		int pupil_y = (int)(eye_y * 0.48);
		g.setColor(Color.BLACK);
		//System.out.println((int)(y_size * 0.1) + " " + circle_y);
		g.fillOval((int)(eye_x * 0.1), (y_size/2 - circle_y/2), circle_x, circle_y);
		g.setColor(Color.WHITE);
		g.fillOval((int)(eye_x * 0.1) + outline_x/2, (y_size/2 - circle_y/2) + outline_y/2, circle_x - outline_x, circle_y - outline_y);
		g.setColor(Color.BLUE);
		g.fillOval((int)(eye_x * 0.1) + c_x/2, (y_size/2 - circle_y/2) + c_y/2, circle_x - c_x, circle_y - c_y);
		g.setColor(Color.BLACK);
		g.fillOval((int)(eye_x * 0.1) + pupil_x/2, (y_size/2 - circle_y/2) + pupil_y/2, circle_x - pupil_x, circle_y - pupil_y);
	}
	
	private void draw_output(Graphics g){
		int x = (int)(x_size * 0.03);
			int y = (int)(y_size * 0.04);
			for(int i = 0; i < to_draw.size(); i++){
				String s_draw[] = word_wrap(to_draw.get(i));
				for(int j = 0; j < s_draw.length; j++){
					g.drawString(s_draw[j], x, y);
					y += 20;
					if(y > y_size - 10){
						y = 100;
						x += 100;
					}
				}
			}
	}

	
	class APanel extends JPanel{
		public APanel(){
			super();
			setBackground(Color.PINK);
			setFont(alice_font);
		}

		
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			//System.out.println("Painting");
			g.setColor(Color.BLACK);
			draw_output(g);
			g.fillRect(prompt_x, prompt_y, (int)(x_size * 0.8), (int)(y_size * 0.035));
			g.setColor(Color.WHITE);
			g.drawString(input_text, prompt_x + (int)(x_size * 0.03), prompt_y + (int)(y_size * 0.03));
			draw_eye(g);
		}

		void DrawStuff(){
			//Graphics g = new Graphics();
		}
	}
	
	private String[] word_wrap(String s){
		int s_length = s.length();
		if(s_length > 20){
			ArrayList<String> wrap = new ArrayList();
			int len = 0;
			String piece = "";
			for(int i = 0; i < s_length; i++){
				char c = s.charAt(i);
				len++;
				if(len > 15 && c == ' '){
					wrap.add(piece);
					piece = "";
					len = 0;
				}
				else piece += c;
			}
			wrap.add(piece);
			String result[] = new String[wrap.size()];
			for(int i = 0; i < result.length; i++){
				result[i] = wrap.get(i);
			}
			return result;
		} else{
			String res[] = {s};
			return res;
		}
	}
	
	void clear_output(){
		to_draw.clear();
	}
	
	class AMouse implements MouseListener{
		AMouse(){super();}
		@Override
		public void mouseClicked(MouseEvent e){
			Point loc = e.getLocationOnScreen();
			if(e.getButton() == 1 && loc.x >= prompt_x && loc.x <= prompt_x + x_size * 0.8 &&
				loc.y >= prompt_y && loc.y <= loc.y + y_size *  0.035){
			}
		}
		@Override
		public void mouseExited(MouseEvent e){}
		@Override
		public void mouseEntered(MouseEvent e){}
		@Override
		public void mouseReleased(MouseEvent e){}
		@Override
		public void mousePressed(MouseEvent e){}
	}
	class AKeyboard implements KeyListener{
		AKeyboard(){super();}
		@Override
		public void keyReleased(KeyEvent e){}
		@Override
		public void keyPressed(KeyEvent e){}
		@Override
		public void keyTyped(KeyEvent e){
			char in = e.getKeyChar();
			if(in == '\b'){
				String tmp = "";
				for(int i = 0; i < input_text.length() - 1; i++)
					tmp += input_text.charAt(i);
				input_text = tmp;
			} else if(in == '\n'){
				entered = true;
			} else input_text += in;
			window.repaint();
		}
	}

}