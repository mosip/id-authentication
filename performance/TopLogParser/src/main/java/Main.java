import worker.LogParser;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		LogParser parser = new LogParser();
		parser.parseTopLog("");
		parser.processParsedLogData();
	}

}
