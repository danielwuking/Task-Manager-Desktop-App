import java.io.IOException;
import java.io.InputStream;

public class index {
	public static void main(String[] args) throws InterruptedException{
		InputStream tasks = getTaskList();
		GUI.createGUI(tasks);
		while (true) {
			Thread.sleep(500);
			tasks = getTaskList();
			GUI.updateGUI(tasks);
		}
	}
	
	private static InputStream getTaskList() {	
		ProcessBuilder pb = new ProcessBuilder("top", "-l", "1");
		try {
		    Process p = pb.start();
		    InputStream is = p.getInputStream();
		    return is;
		}
		 catch (IOException exp) {
		    exp.printStackTrace();
		}
		return null;
	}
}