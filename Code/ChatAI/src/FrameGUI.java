import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class FrameGUI implements ActionListener, KeyListener {
	public static JFrame frame;	
	public static JTextField userText;
	public static JButton enter;
	public static JTextArea chatArea;
	
	public static Person[] names = { new Person("BURTHA"), new Person("RACHAEL") };
	
	public static FrameGUI gui;
	
	public static void main(String [] args) {
		gui = new FrameGUI();
		
		searchChat();
	}
	
	public FrameGUI() {
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		frame = new JFrame("Chat");
		enter = new JButton("Enter");
		enter.addActionListener(this);
		userText = new JTextField(10);
		userText.addKeyListener(this);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400,600));
		
		frame.setLayout(new BorderLayout());
		
		JPanel textEnter = new JPanel(new GridLayout(1,2));
		textEnter.add(userText);
		textEnter.add(enter);
		
		frame.add(textEnter, BorderLayout.SOUTH);
		frame.add(chatArea, BorderLayout.CENTER);
		
        frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(userText.getText().length() > 0) {
			chatArea.append("\nUSER: " + userText.getText());
			userText.setText("");
		}
	}
	
	public void userTalks() {
		String u = userText.getText();
		chatArea.append("\nUSER: " + u);
		
		boolean added = false;
		for(int z = 0; z < names.length; z++) {
			for(int y = 0; y < names[z].people.size(); y++) {
				if(names[z].people.get(y).name == "USER") {
					added = true;
					names[z].people.get(y).input = u;
					names[z].running = true;
					System.out.println(names[z].people.get(y).output);
				}
			}
			
			if(!added) {
				names[z].people.add(new Chat("USER"));
				names[z].people.get(names[z].people.size()-1).input = u;
			}
		}
		
		userText.setText("");
	}
	
	//public void peopleTalk() {
	//	for(int z = 0; z < names.length; z++) {
			//	chatArea.append("\n" + names[z].name + ": " + names[z].out);
			
	//	}
	//}
	
	public static void searchChat() {
		while(true) {
			for(int z = 0; z < names.length; z++) {
				int ran = z;
				//int ran = (int)(Math.random()*names.length);
				if(ran > names.length) ran -= names.length;
				if(names[ran].out != null && names[ran].out.length() > 0) {
					System.out.println("interation");
					chatArea.append("\n" + names[ran].name + ": " + names[ran].out);
					for(int x = 0; x < names.length+1; x++) {
						if(x != ran) {
							if(names[x].people.size() <= x) {
								for(int w = names[x].people.size()+1; w <= x; w++) {
									names[w].people.add(w, new Chat(names[w].name));
								}
							}
							names[x].people.get(x).input = names[ran].out;
						}
					}
				}
				try {
					Thread.sleep((int)(Math.random()*1500));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(userText.getText().length() > 0) {
				userTalks();
				//peopleTalk();
			}
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent arg0) {}
}
