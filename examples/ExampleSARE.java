import org.regex.*;

public class ExampleSARE {
	public static void main(String[] args) {
		String regex = "([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)";
		String data  = "192.255.255.150";
		
		SAREParser sare = new SAREParser(regex);
		ConsoleOut co = new ConsoleOut();

		sare.registerAction(0,co);
		sare.registerAction(1,co);
		sare.registerAction(2,co);
		sare.registerAction(3,co);
		sare.registerAction(4,co);

		sare.process(data);
	}
}

class ConsoleOut implements GroupActionInterface {
	public void start() {
		System.out.println("Starting");
	}

	public void end() {
		System.out.println("End");
	}

	public void match(int count, String full, String group) {
		System.out.println("[" + count + "] - " + group);
	}
}
