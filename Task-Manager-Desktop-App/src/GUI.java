import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GUI {   
	private static JFrame frame = new JFrame("Task Manager for Linux");
	private static JPanel panel = new JPanel();
    public static void createGUI(InputStream data) {            
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        placeComponents(data);        
        frame.add(addScrollPane(panel));
        frame.setVisible(true);
    }
    
    public static void updateGUI(InputStream data) {
    	panel = new JPanel();
    	placeComponents(data);
    	frame.add(addScrollPane(panel));
    }
 
    private static void placeComponents(InputStream data) {
        panel.setLayout(null); 
		try{
			JTextArea text =  new JTextArea(parseDataToString(data));
			text.setBounds(0, 0, 3000, 3000);
			panel.add(text);
		}catch (IOException exp) {
		    exp.printStackTrace();
		}       	    
    }
    
    private static String parseDataToString(InputStream data) throws IOException {
    	final int bufferSize = 1024;
    	final char[] buffer = new char[bufferSize];
    	final StringBuilder out = new StringBuilder();
    	Reader in = new InputStreamReader(data, StandardCharsets.UTF_8);
    	int charsRead;
    	while((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
    	    out.append(buffer, 0, charsRead);
    	}
    	return out.toString();
    }
    
    private static JScrollPane addScrollPane(JPanel panel) {
    	JScrollPane js = new JScrollPane(panel, 
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        		);
    	js.setBounds(0, 0, 3000, 3000);
        return js;
    }    

}





