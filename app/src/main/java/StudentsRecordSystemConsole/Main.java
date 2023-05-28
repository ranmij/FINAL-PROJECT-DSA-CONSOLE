package StudentsRecordSystemConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clancy Sanchez
 */
public class Main  extends DatabaseHandler {
    private final String[] column_names = getColumnNames();
    private int column_name_count = column_names.length;
    
    private final int DIVIDER = 25;
    private final int WIDTH = 35;
    private final int FIRST_COLUMN_DIVIDER = 16;
    private final int TABLE_WIDTH = FIRST_COLUMN_DIVIDER * column_name_count;
    private final int PAD_LEFT = 40;
    private final int TABLE_PAD_LEFT = 10;
    
    public Main(String databasePath) {
        super(databasePath);
        column_name_count = getColumnNames().length;
    }
    
    /**
     * Will draw the menu in the screen :>
     */
    private void showMenu() {
        drawLine(WIDTH);
        System.out.println("                                        |OPTIONS                |ANS      |"); // since once lang naman to gagawin print nalang natin
        drawLine(WIDTH);
        drawLineString("ADD STUDENT", 1);
        drawLineString("EDIT STUDENT", 2);
        drawLineString("SEARCH STUDENT", 3);
        drawLineString("DELETE STUDENT", 4);
        drawLineString("SHOW LIST", 5);
        drawLineString("EXIT", 6);
        drawLine(WIDTH); 
        System.out.println("                                        ---------------MENU----------------");
    }
    
    private void drawLine(int lineWidth) {
        for(int i = 0; i < lineWidth + PAD_LEFT; i++) {
            if (i == PAD_LEFT || i == lineWidth+PAD_LEFT-1)
                System.out.print("+");
            else if (i < PAD_LEFT)
                System.out.print(" ");
            else
                System.out.print("-");
        }
        System.out.println();
    }
    
    private void tableDrawLine(int lineWidth) {
        for(int i = 0; i < lineWidth + TABLE_PAD_LEFT; i++) {
            if (i == TABLE_PAD_LEFT || i == lineWidth+TABLE_PAD_LEFT-1)
                System.out.print("+");
            else if (i < TABLE_PAD_LEFT)
                System.out.print(" ");
            else
                System.out.print("-");
        }
        System.out.println();
    }
    
    private void drawTable(ArrayList<String[]> rowData) {
        // MARGIN TOP
        for(int i = 0; i < 2; i++) {
            System.out.println();
        }
        String[] columnHeader = column_names;
        tableDrawLine(TABLE_WIDTH);
        drawRowTable(columnHeader);
        tableDrawLine(TABLE_WIDTH);
        if (!rowData.isEmpty()) {
            for(int index = 0; index < rowData.size(); index++) {
                drawRowTable(rowData.get(index));
            }
        } else {
            String[] emptyColumns = new String[column_name_count];
            for(int i = 0; i < emptyColumns.length; i++) {
                emptyColumns[i] = " ";
            }
            drawRowTable(emptyColumns);
        }
        tableDrawLine(TABLE_WIDTH);
    
    }
    
    private void drawRowTable(String[] rowData) {
        int total_length = 0;
        for (String row : rowData) {
            total_length += row.length();
        }
        int table_width = TABLE_WIDTH - total_length;
        int index = 0;
        int column_divider = FIRST_COLUMN_DIVIDER-rowData[index].length();
        for (int i = 0; i <= table_width+TABLE_PAD_LEFT; i++) {
                if(i == column_divider+TABLE_PAD_LEFT && index < rowData.length) {
                    if (++index < rowData.length) {
                        System.out.print("|" + rowData[index].replace("-", " "));
                        column_divider += FIRST_COLUMN_DIVIDER;
                        column_divider -= rowData[index].length();
                    }
                } else if (i == TABLE_PAD_LEFT) {
                    System.out.print("|" + rowData[index].replace("-", " "));
                } else if (i == table_width+TABLE_PAD_LEFT-1) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
        }
        System.out.println();
    }
    
    private void drawLineString(String option, int choice) {
        int row_width = WIDTH-option.length();        // minus the length of the string and the choice
        int divider =  DIVIDER - (WIDTH - row_width);
        for(int i = 0; i <= row_width + PAD_LEFT; i++) {
            if (i == PAD_LEFT+1) {
                System.out.print(option);
            } else if (i == PAD_LEFT || i == row_width+PAD_LEFT || i == divider+PAD_LEFT){
                System.out.print("|");
            } else if (i == row_width+PAD_LEFT-2) {
                System.out.print(choice);
            } else if(i < PAD_LEFT) {
                System.out.print(" ");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
    
    private void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void driverCode() {
        Scanner input = new Scanner(System.in);
        String[] data = new String[column_names.length-1];
        String[] rowData = new String[column_names.length];
        String query = null;
        int choice = 0, id = 0;
        ArrayList<String[]> currDisplayedData = fillTable();
        while(true) {
            clearScreen();
            showMenu();
            drawTable(currDisplayedData);
            System.out.print(">> ");
            choice = input.nextInt();
            input.nextLine(); // clear the buffer
            switch(choice) {
                case 1:
                    for (int i = 0; i < data.length; i++) {
                        System.out.print(column_names[i+1].replace("_", " ").toUpperCase()+": ");
                        data[i] = input.nextLine();
                    }
                    currDisplayedData = insertData(data);
                    break;
                case 2:
                    System.out.print(String.format("ID [%s]: ", (id == 0)? "null": id));
                    String sid = input.nextLine();
                    if(!sid.isBlank() || !sid.isEmpty())
                        id = Integer.parseInt(sid);
                    //input.nextLine(); // clear the buffer
                    for(int i = 0; i < currDisplayedData.size(); i++) {
                        String[] row = currDisplayedData.get(i);
                        if(Integer.parseInt(row[0]) == (id)){
                            rowData = row;
                            break;
                        }
                    }
                    String[] updatedData = new String[column_names.length];
                    String line;
                    for(int i = 0; i < rowData.length-1; i++) {
                        System.out.print(String.format("%s [%s]: ", column_names[i+1], rowData[i+1]));
                        line = input.nextLine();
                        updatedData[i+1] = (line.isEmpty() || line.isBlank())? rowData[i+1] : line;
                    }
                    updatedData[0] = rowData[0];
                    currDisplayedData = updateData(updatedData);
                    break;
                case 3:
                    System.out.print(String.format("Search [%s]: ", query));
                    String squery = input.nextLine();
                    if(!squery.isBlank() || !squery.isEmpty()) {
                        query = squery;
                    }
                    currDisplayedData = searchData(query);
                    break;
                case 4:
                    System.out.print("ID: ");
                    id = input.nextInt();
                    input.nextLine(); // clear the buffer
                    for(int i = 0; i < currDisplayedData.size(); i++) {
                        String[] row = currDisplayedData.get(i);
                        if(Integer.parseInt(row[0]) == id){
                            currDisplayedData = deleteData(row[0]);
                            break;
                        }
                    }
                    break;
                case 5:
                    currDisplayedData = fillTable();
                    clearScreen();
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}