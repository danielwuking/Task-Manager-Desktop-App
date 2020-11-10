import java.io.IOException;
import java.io.InputStream;

public class index {
	public static void main(String[] args) throws InterruptedException{
		startManager();
//		while (true) {
//			Thread.sleep(3000); /* update data per 3sec */
//			updateManager();
//		}		
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
	
	private static void startManager() {
		InputStream tasks = getTaskList();
		GUI.createGUI(tasks);
	} 
	
	private static void updateManager() {
		InputStream tasks = getTaskList();
		GUI.updateGUI(tasks);		
	}
}