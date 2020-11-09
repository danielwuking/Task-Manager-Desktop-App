import java.io.IOException;
import java.io.InputStream;

public class index {
	public static void main(String[] args){
		InputStream task = getTaskList();
		sendTaskToGUI(task);
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
	
	private static void sendTaskToGUI(InputStream task) {
		if (task == null) { 
			return;
		}/* not sure if this is necessary*/
		GUI.createGUI(task);
	}
}