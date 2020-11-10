import java.io.IOException;
import java.io.InputStream;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class index {
	public static void main(String[] args) throws InterruptedException, IOException{
		startManager();
		updateManagerRegularly();
	}
	
	private static void updateManagerRegularly() {
		Timer timer = new Timer(0, new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   try {
					updateManager();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			   }
			});

			timer.setDelay(3000); // delay for 3 seconds
			timer.start();
	}
	
	
	private static InputStream getTaskList() {	
		String command = 
				"top -l 1 | grep \" \" | "
				+ "awk '{ printf(\"%-8s  %-8s  %-8s %-8s %-8s \\n\", "
				+ "$1, $2, $3, $4, $8. $12, $13); }' ";
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
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
	
	private static void startManager() throws IOException {
		InputStream tasks = getTaskList();
		GUI.createGUI(tasks);
	} 
	
	private static void updateManager() throws IOException {
		InputStream tasks = getTaskList();
		GUI.updateGUI(tasks);		
	}
}