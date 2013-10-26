import java.util.ArrayList;
import java.util.List;


public class Chat {
	String name;
	String input;
	public String output;
	public List<Memory> history = new ArrayList<Memory>();
	
	int temp = -2;
	
	Chat(String n) {
		name = n;
	}
	
	public void addToChat(String w) {
		input = w;
		history.add(new Memory(w));
		temp++;
	}
	
	public void refresh() {
		//output = "";
		if(input != null) output = "I heard " + name + " say \"" + input + "\"";
		if(temp >= 0) output += "\nThat reminds me of when you said \"" + 
				history.get(temp).thought + "\"";
	}
}
