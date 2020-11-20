import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import java.util.regex.*;


public class GUI {   
	private static JFrame frame = new JFrame("Daniel's Task Manager for Linux");
	private static JScrollPane scrollPane = new JScrollPane();
	private static JTable table = new JTable();
	public static String sortKey = "PID";
	public static boolean sortReverse = true;
	
    public static void createGUI(InputStream data) throws IOException {            
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        placeComponents(data);
        frame.setVisible(true);
    }
    
    public static void updateGUI(InputStream data) throws IOException {
    	Vector<Vector<String>> dataVector = convertDataToDataVector(data);
    	TableModel model = convertDataToTableModel(dataVector);
		table.setModel(model);
    	table.repaint();
    }
 
    private static void placeComponents(InputStream data) throws IOException {
    	Vector<Vector<String>> dataVector = convertDataToDataVector(data);
    	TableModel model = convertDataToTableModel(dataVector);
		table = new JTable(model);
		table.setSize(1000, 1000);		
		addSortByColumnFunction(table);
		addKillByRowFunction(table);

		scrollPane = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
				);
		frame.getContentPane().add(scrollPane);       	    
    }
    
    private static void addSortByColumnFunction(JTable table){
    	table.getTableHeader().addMouseListener(new MouseAdapter() {
    	    @Override
    	    public void mouseClicked(MouseEvent e) {
    	        int col = table.columnAtPoint(e.getPoint());
    	        String name = table.getColumnName(col);
    	        if (sortKey == name) {
    	        	sortReverse = (sortReverse) ? false : true;
    	        }
    	        else {
    	        	sortKey = name;
    	        	sortReverse = true;
    	        }
    	        try {
					index.updateManager();
				} catch (IOException e1) {
				}
    	    }
    	});
    }
    
    private static void addKillByRowFunction(JTable table){
    	table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {	        	
	        	int selectRow = table.getSelectedRow();
	        	if (selectRow != -1) {
	        		String pid = table.getValueAt(selectRow, 0).toString();        		
	        		int result = confirmKillProcess(pid);
	        	    if (result == JOptionPane.YES_OPTION){
	    				index.killTaskByPid(pid);
	    				System.out.println(pid + "has been killed");
	        	    }
	        	}	        	
	        }
	    });
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
    
    private static  Vector<Vector<String>> getDataRowVectorFromTargetHeader
    						(Vector<int[]> targetHeaderIndex, InputStream is, int index) throws IOException{
    	int totalLength = index;
	    Vector<String> rowData = new Vector<String>();
	    Vector<Vector<String>> totalData = new Vector<Vector<String>>();
	    
	    int i = 0;
	    int value = -1;
	    while ((value = is.read()) != -1) {
	    	if (value == 10) {
	    		totalData.add(rowData);
	    		rowData = new Vector<String>();
	    		continue;
	    	}
	    	
		    if (index % totalLength == targetHeaderIndex.get(i)[0]) {
	    		String str = Character.toString((char)value);		    		
	    		index ++;
	    		while (index % totalLength < targetHeaderIndex.get(i)[1]) {
	    			value = is.read(); 
	    			str += Character.toString((char)value);
	//    			System.out.print((char)value);
	    			index ++;
	    		}
	    		rowData.add(str.trim());
	    		i ++;
	    		i %= targetHeaderIndex.size();
	    		continue;
	    	}
		    index ++;
	    }
	    return totalData;
    	
    }
    
    private static  Vector<Vector<String>> convertDataToDataVector(InputStream is) throws IOException {		
		int index = 0;	
		int value = -1;
		String headerStr = "";
		while ((value = is.read()) != 10) {
			headerStr += Character.toString((char)value);
			index ++;
		}
		Vector<String> allHeader = getAllHeader(headerStr);
		Vector<int[]> allHeaderIndex =  getHeaderIndex(allHeader);
		Vector<int[]> targetHeaderIndex =  selectTargetFromAllHeaderIndex(allHeaderIndex);
		Vector<Vector<String>> dataVector = getDataRowVectorFromTargetHeader(targetHeaderIndex, is, index);
		if (!sortReverse) Collections.reverse(dataVector); 
		return dataVector;
	}

	private static Vector<String> getAllHeader(String headerStr){
		Vector<String> header = new Vector<String>();
		Pattern p = Pattern.compile("([%#A-Z]+\\s+)");
            Matcher m = p.matcher(headerStr);
            while (m.find()) {
               header.add(m.group(1));              
            }
        return header;    
    }
	
	private static Vector<int[]> getHeaderIndex(Vector<String> allHeader){
		Vector<int[]> allHeaderIndex = new Vector<int[]>();
		int index = 0;
		for (String header : allHeader) {
			int[] temp = new int[2];
			temp[0] = index;
			temp[1] = index + header.length() - 1;
			allHeaderIndex.add(temp);
			index = temp[1] + 1;
		}
		
        return allHeaderIndex;    
    }
	
	private static Vector<int[]> selectTargetFromAllHeaderIndex(Vector<int[]> allHeaderIndex){
		Vector<int[]> targetHeaderIndex = new Vector<int[]>();
		for (int i = 0; i < allHeaderIndex.size(); i++) {
			if (i == 0 || i == 1 || i == 2 || i == 3 || i == 7 || i == 11 || i == 12) 
				targetHeaderIndex.add(allHeaderIndex.get(i));
		}
        return targetHeaderIndex;    
    }
    private static int confirmKillProcess(String pid) {
		int mType=JOptionPane.INFORMATION_MESSAGE;
		return JOptionPane.showConfirmDialog(frame,"Are you sure to kill process" + pid + "?",
	    		"Confirm to kill process", JOptionPane.YES_NO_OPTION,mType); 
    }
}




