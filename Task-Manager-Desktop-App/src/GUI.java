import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTable;


public class GUI {   
	private static JFrame frame = new JFrame("Task Manager for Linux");
	private static JScrollPane scrollPane = new JScrollPane();
	private static JTable table = new JTable();
    public static void createGUI(InputStream data) throws IOException {            
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        placeComponents(data);     
        frame.setVisible(true);
    }
    
    public static void updateGUI(InputStream data) throws IOException {
    	TableModel model = convertDataToTableModel(data);
		table.setModel(model);
    	table.repaint();
    }
 
    private static void placeComponents(InputStream data) throws IOException {
		TableModel model = convertDataToTableModel(data);
		table = new JTable(model);
		table.setSize(1000, 1000);
		scrollPane = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
				);
		frame.getContentPane().add(scrollPane);       	    
    }
    
    private static TableModel convertDataToTableModel(InputStream data) throws IOException {
    	String dataString =  parseDataToString(data);
		TableModel model = parseDataStringToTableModel(dataString);
		return model;
    }
    
    private static TableModel parseDataStringToTableModel(String dataString) {
    	String dataRows[] = dataString.split("\n");
    	Vector<Vector<String>> dataVector = new Vector<Vector<String>>();
        for (int i = 11; i < dataRows.length; i++) {
        	dataRows[i] = dataRows[i].trim();  //UPDATE
            Vector<String> data = new Vector<String>();
            data.addAll(Arrays.asList(dataRows[i].split("\\s+")));
            dataVector.add(data);
        }
        
        Vector<String> header = new Vector<String>();
        for (String title : dataRows[10].split("\\s+")) {
	        header.add(title);
        }
        System.out.println(dataVector.get(0));
        
    	TableModel model = new DefaultTableModel(dataVector, header); 
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





