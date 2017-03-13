package dk.kasper.lightsim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConsoleHandler implements Runnable {
	private List<ConsoleListener> listeners = new ArrayList<ConsoleListener>();
	private boolean running;
	
	public void addListener(ConsoleListener toAdd) {
		listeners.add(toAdd);
	}
	
	public void run() {
		running = true;
		while(running) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			try {
				line = bufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sendLine(line);
		}
	}
	
	public void close() {
		running = false;
	}
	
	private void sendLine(String line) {
		for(ConsoleListener cl : listeners) {
			cl.consoleLineEntered(line);
		}
	}
}
