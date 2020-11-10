import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTable;


public class GUI {   
	private static JFrame frame = new JFrame("Task Manager for Linux");
	private static JPanel panel = new JPanel();
    public static void createGUI(InputStream data) {            
        frame.setSize(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        placeComponents(data);     
        frame.add(panel);
        frame.setVisible(true);
    }
    
    public static void updateGUI(InputStream data) {
    	panel = new JPanel();
//    	placeComponents(data);
    	frame.add(panel);
    }
    
    
 
    private static void placeComponents(InputStream data) {
        panel.setLayout(null); 
		try{
			String dataString =  parseDataToString(data);
			TableModel model = parseDataStringToTableModel(dataString);
			JTable table = new JTable(model);
			table.setSize(10000, 10000);
			panel.add(table);
		}catch (IOException exp) {
		    exp.printStackTrace();
		}       	    
    }
    
    private static TableModel parseDataStringToTableModel(String dataString) {
    	String dataRows[] = dataString.split("\n");
    	Vector<Vector<String>> dataVector = new Vector<Vector<String>>();
        for (int i = 12; i < 20; i++) {
        	dataRows[i] = dataRows[i].trim();  //UPDATE
            Vector<String> data = new Vector<String>();
            data.addAll(Arrays.asList(dataRows[i].split("\\s+")));
            dataVector.add(data);
        }
        
        Vector<String> header = new Vector<String>(2);
        for (String title : dataRows[11].split("\\s+")) {
	        header.add(title);
        }
//        System.out.println(dataVector.get(0));
        
    	TableModel model = new DefaultTableModel(dataVector, header); 
    	System.out.println(header);
    	System.out.println(dataVector.get(0).size());
    	System.out.println(header.size());
    	return model;
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

}





