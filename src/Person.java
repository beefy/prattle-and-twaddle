import java.util.ArrayList;
import java.util.List;


public class Person implements Runnable {
	public boolean running = true;
	public String name;
	public List<Chat> people = new ArrayList<Chat>();
	public String out = "";
	Thread t;
	
	Person(String n) {
		name = n;
		t = new Thread(this, name);
		t.start();
		//people.add(new Chat("USER"));
	}
	
	public void run() {
		
		while(true) {	
				//System.out.println("running");
			for(int i = 0; i < people.size()+1; i++) {
				if(people.size() > 0) {
					people.get(i).refresh();
					out = people.get(i).output;
				}
				/*
				try {
				//	Thread.sleep((int)(Math.random()*800));
				} catch (InterruptedException e) {
					System.out.println("Found Exception");
					e.printStackTrace();
				} catch (NullPointerException e) {
					System.out.println("Found Exception");
					e.printStackTrace();
				}
				*/
			}
		}
	}
}
