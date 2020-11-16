import java.io.IOException;
import java.io.InputStream;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class index {
	public static String sortKey = "PID";
	
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
			timer.setDelay(3000); // delay for 0.5 seconds
			timer.start();
	}
	
	public static void killTaskByPid(String Pid) {	
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", 
				"kill " + Pid);
		pb.redirectError();
		try {
		    pb.start();
		}
		 catch (IOException exp) {
		    exp.printStackTrace();
		}
	}
	
	private static InputStream getTaskList(String sortKey) {	
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", 
				"top -o " + sortKey + " -l 1  | grep \"  \" ");
		pb.redirectError();
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
		InputStream data = getTaskList(sortKey);
		GUI.createGUI(data);
	} 
	
	public static void updateManager() throws IOException {
		InputStream data = getTaskList(sortKey);
		GUI.updateGUI(data);		
	}
}