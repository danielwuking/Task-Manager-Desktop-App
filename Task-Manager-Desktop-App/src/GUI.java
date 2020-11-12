import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


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
    	Vector<Vector<String>> dataVector = convertDataToRowVector(data);
    	TableModel model = convertDataToTableModel(dataVector);
		table.setModel(model);
    	table.repaint();
    }
 
    private static void placeComponents(InputStream data) throws IOException {
    	Vector<Vector<String>> dataVector = convertDataToRowVector(data);
    	TableModel model = convertDataToTableModel(dataVector);
		table = new JTable(model);
		table.setSize(1000, 1000);
		table.getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int col = table.columnAtPoint(e.getPoint());
		        String name = table.getColumnName(col);
		        System.out.println("Column index selected " + col + " " + name);
		    }
		});
		
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
	            // do some actions here, for example
	            // print first column value from selected row	        	
	        	int selectRow = table.getSelectedRow();
	        	if (selectRow != -1) {
	        		System.out.println(table.getValueAt(selectRow, 0).toString());
	        	}	        	
	        }
	    });
		scrollPane = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
				);
		frame.getContentPane().add(scrollPane);       	    
    }
    
    private static TableModel convertDataToTableModel(Vector<Vector<String>> dataVector) throws IOException {
    	Vector<String> header = getHeader();
    	TableModel model = new DefaultTableModel(dataVector, header);
		return model;
    }
    
    private static Vector<String> getHeader(){
    	Vector<String> header = new Vector<String>();
    	header.add("PID");
    	header.add("COMMAND");
    	header.add("%CPU");
    	header.add("TIME");
    	header.add("MEM");
    	header.add("PPID");
    	header.add("STATE");
    	return header;
    }
    
    private static  Vector<Vector<String>> convertDataToRowVector(InputStream is) throws IOException {    
		int PID[] = new int[3];
		PID[0] = -1;
		PID[2] = -1;
		int COMMAND[] = new int[3];
		COMMAND[0] = -1;
		COMMAND[2] = -1;
		int CPU[] = new int[3];
		CPU[0] = -1;
		CPU[2] = -1;
		int TIME[] = new int[3];
		TIME[0] = -1;
		TIME[2] = -1;
		int MEM[] = new int[3];
		MEM[0] = -1;
		MEM[2] = -1;
		int PPID[] = new int[3];
		PPID[0] = -1;
		PPID[2] = -1;
		int STATE[] = new int[3];
		STATE[0] = -1;
		STATE[2] = -1;
		
		int index = 0;
		int totalLength = 0;
		
		int value = -1;
	    while ((value = is.read()) != 10) {
	    	char charValue = (char)value;
//	        System.out.print(charValue);
	        if (charValue == 'P' && PID[0] == -1) {
	        	PID[0] = index;
	        }
	        else if (charValue == 'C' && COMMAND[0] == -1) {
	        	COMMAND[0] = index;
	        	PID[1] = COMMAND[0];	
	        	PID[2] = 1; 
	        }
	        else if (charValue == 'C' && CPU[0] == -1 && COMMAND[0] != -1) {
	        	CPU[0] = index-1;
	        	COMMAND[1] = CPU[0];
	        	COMMAND[2] = 1;
	        }	
	        else if (charValue == 'M' && TIME[0] == -1 && CPU[0] != -1) {
	        	TIME[0] = index-2;
	        	CPU[1] = TIME[0];
	        	CPU[2] = 1;
	        }
	        else if (charValue == '#' && TIME[0] != -1 && TIME[2] == -1) {
	        	TIME[1] = index;
	        	TIME[2] = 1;
	        }
	        else if (charValue == 'M' && MEM[0] == -1 && TIME[0] != -1) {
	        	MEM[0] = index;
	        }	
	        else if (charValue == 'P' && MEM[0] != -1 && MEM[2] == -1) {
	        	MEM[1] = index;
	        	MEM[2] = 1;
	        }
	        else if (charValue == 'I' && PPID[0] == -1 && MEM[0] != -1) {
	        	PPID[0] = index-2;
	        }
	        else if (charValue == 'S' && STATE[0] == -1 && PPID[0] != -1) {
	        	STATE[0] = index;
	        	PPID[1] = STATE[0];
	        	PPID[2] = 1;
	        }
	        else if (charValue == 'B' && STATE[0] != -1 && STATE[2] == -1) {
	        	STATE[1] = index;
	        	STATE[2] = 1;
	        }
	        index ++;
	    }

	    totalLength = index;
	    Vector<String> rowData = new Vector<String>();
	    Vector<Vector<String>> totalData = new Vector<Vector<String>>();

	    while ((value = is.read()) != -1) { 
//	    	System.out.print((char)value);
	    	if (value == 10) {
	    		totalData.add(rowData);
	    		rowData = new Vector<String>();
	    		continue;
	    	}
	    	
	    	if (index % totalLength == PID[0]) {
	    		String str = Character.toString((char)value);		    		
	    		index ++;
	    		while (index % totalLength < PID[1]) {
	    			value = is.read(); 
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}

	    		rowData.add(str);
	    		continue;
	    	}
	    	else if (index % totalLength == COMMAND[0]) {
	    		String  str = Character.toString((char)value);
	    		index ++;
	    		while (index % totalLength < COMMAND[1]) {
	    			value = is.read();
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str);
	    		continue;
	    	}
	    	else if (index % totalLength == CPU[0]) {
	    		String  str = Character.toString((char)value);
	    		index ++;
	    		while (index % totalLength < CPU[1]) {
	    			value = is.read();
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str);
	    		continue;
	    	}
	    	else if (index % totalLength == TIME[0]) {
	    		String  str = Character.toString((char)value);
	    		index ++;
	    		while (index % totalLength < TIME[1]) {
	    			value = is.read();
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str);
	    		continue;
	    	}
	    	else if (index % totalLength == MEM[0]) {
	    		String  str = Character.toString((char)value);
	    		index ++;
	    		while (index % totalLength < MEM[1]) {
	    			value = is.read();
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str);
	    		continue;
	    	}
	    	else if (index % totalLength == PPID[0]) {
	    		String  str = Character.toString((char)value);
	    		index ++;
	    		while (index % totalLength < PPID[1]) {
	    			value = is.read();
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str);
	    		continue;
	    	}
	    	else if (index % totalLength == STATE[0]) {
	    		String  str = Character.toString((char)value);
	    		index ++;
	    		while (index % totalLength < STATE[1]) {
	    			value = is.read();
	    			str += Character.toString((char)value);
//	    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str);
	    		continue;
	    	}
	    	index ++;
	    }
	    return totalData;
	}
    
}




