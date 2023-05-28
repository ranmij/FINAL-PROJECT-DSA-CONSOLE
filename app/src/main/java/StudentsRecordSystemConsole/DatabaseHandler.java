package StudentsRecordSystemConsole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clancy Sanchez
 */
public class DatabaseHandler extends DatabaseUtility {
    private final String tableName;
    private String[] columnNames;
    DatabaseHandler(String databaseName) {
        String defaultPath = System.getProperty("user.home") + "\\SchoolRecordSystem\\";
        if (!Files.exists(Paths.get((databaseName.endsWith(".db"))? defaultPath+databaseName : defaultPath+databaseName+".db"))) {
            try {
                Files.createDirectories(Paths.get(System.getProperty("user.home") + "\\SchoolRecordSystem"));
                setDatabaseName(System.getProperty("user.home") + "\\SchoolRecordSystem\\" + databaseName);
                if (!initTables()) {
                    Logger.getLogger(DatabaseHandler.class.getName()).log(Level.INFO, "Can't initialize table");
                    System.exit(1);
                }
            } catch (IOException ex) {
                Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            setDatabaseName(System.getProperty("user.home") + "\\SchoolRecordSystem\\" + databaseName);
        }
        tableName = getTableName();
        columnNames = getColumnNames();
    }
    
    /**
     * Initialize the database when the application first run
     * @return Boolean 
     */
    private boolean initTables(){
        try (java.util.Scanner input = new java.util.Scanner(System.in)) {
            String user_input;
            int index;
            String[] tableNames = {"students_tbl", "user_tbl", "teacher_tbl", "employee_tbl", "product_tbl", "department_tbl",
                                    "subjects_tbl", "books_tbl", "people_tbl"};
            String[] sqlCreateTable = {
                "CREATE TABLE students_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, first_name VARCHAR(50) NOT NULL, last_name VARHCAR(50) NOT NULL, age INT NOT NULL, year VARHCAR(20) NOT NULL, course VARCHAR(10) NOT NULL, student_no VARHCAR(10) NOT NULL);",
                "CREATE TABLE user_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, full_name VARHCAR(50) NOT NULL, address VARCHAR(50) NOT NULL, phone VARHCAR(50) NOT NULL, email VARCHAR(50) NOT NULL);",
                "CREATE TABLE teacher_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, first_name VARCHAR(50) NOT NULL, last_name VARCHAR(50) NOT NULL,  email VARCHAR(50) NOT NULL, course VARCHAR(10) NOT NULL, date_created DATE NOT NULL DEFAULT CURRENT_DATE);",
                "CREATE TABLE employee_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, employee_name VARCHAR(50) NOT NULL, age INT NOT NULL, phone VARCHAR(50) NOT NULL, email VARCHAR(50) NOT NULL, date_joined DATE NOT NULL DEFAULT CURRENT_DATE);",
                "CREATE TABLE product_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, product_name VARHCAR(50) NOT NULL, price INT NOT NULL, stock INT NOT NULL, date_added DATE NOT NULL DEFAULT CURRENT_DATE);",
                "CREATE TABLE department_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, department VARHCAR(50) NOT NULL);",
                "CREATE TABLE subjects_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, subject_name VARHCAR(50), teacher_name VARCHAR(50) NOT NULL)",
                "CREATE TABLE books_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, book_title VARCHAR(50), author VARCHAR(50) NOT NULL, isbn VARCHAR(20) NOT NULL, date_published DATE NOT NULL DEFAULT CURRENT_DATE);",
                "CREATE TABLE people_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, full_name VARCHAR(50) NOT NULL, status VARHCAR(20) NOT NULL, address VARCHAR(50) NOT NULL, date_added DATE NOT NULL DEFAULT CURRENT_DATE);"
            };
            
            System.out.println("This is the first time the program is being run...");
            System.out.print("Do you want to change the default table?: (y/yes | n/no): ");
            user_input = input.nextLine();
            if(user_input.equalsIgnoreCase("y") || user_input.equalsIgnoreCase("yes")) {
                System.out.println("Please choose a table for your system:");
                for(int i = 0; i < tableNames.length; i++) {
                    System.out.println(String.format("[%d] %s -- %s", i+1, tableNames[i], sqlCreateTable[i].substring(0, sqlCreateTable[i].length()-1)));
                }
                System.out.print("Choice >> ");
                index = input.nextInt()-1;
                if (ExecuteQuery(String.format("DROP TABLE IF EXISTS %s", tableNames[index]))) {
                     return ExecuteQuery(sqlCreateTable[index]);
                }
            } else {
                if (ExecuteQuery(String.format("DROP TABLE IF EXISTS %s", tableNames[0]))) {
                    return ExecuteQuery(sqlCreateTable[0]);
                }
            }
        }
        return false;
    }
    
    public String[] getColumnNames() {
        ArrayList<String> columnContainer = new ArrayList();
        HashMap<Connection, ResultSet> resData = null;
        resData = RetrieveQuery(String.format("SELECT name FROM PRAGMA_TABLE_INFO('%s');", tableName));
        Connection key = (Connection) resData.keySet().toArray()[0];
        ResultSet colNames = resData.get(key);
        try {
           if (colNames != null) {
               int index = 0;
               while(colNames.next()) {
                   columnContainer.add(colNames.getString("name"));
                    //columns[index++] = columnNames.getString("name");
               }
               String[] columns = new String[columnContainer.size()];
               for(String col: columnContainer) {
                   columns[index++]  = col;
               }
               return columns;
           } else {
               return null;
           }
        }catch(SQLException e) {
            return null;
        } finally {
            Close(key);
        }
    }
    
    private String getTableName() {
        HashMap<Connection, ResultSet> resData = null;
        resData = RetrieveQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence';");
        Connection key = (Connection) resData.keySet().toArray()[0];
        ResultSet tableData = resData.get(key);
        try {
            if(tableData.next()) {
                return tableData.getString("name");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Close(key);
        }
        return null;
    }
    
    private ArrayList<String[]> fillData(HashMap<Connection, ResultSet> resData) {
        ArrayList<String[]> data = new ArrayList<>();
        Connection key = (Connection) resData.keySet().toArray()[0];
        ResultSet tableData = resData.get(key);
        try {
            if(tableData.next()) {
                do {
                    String[] rowData = new String[columnNames.length];
                    for(int index = 0; index < columnNames.length; index++) {
                        rowData[index] = tableData.getString(columnNames[index]);
                    }
                    data.add(rowData);
                } while(tableData.next());
            } else {
                return new ArrayList();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Close(key);
        }
        return data;
    }
    
    private String buildSelectQuery() {
        String query = "SELECT ";
        for(int i = 0; i < columnNames.length; i++) {
            if (i < columnNames.length-1)
                query += String.format("%s, ", columnNames[i]);
            else
                query += String.format("%s ", columnNames[i]);
            
        }
        query += String.format("FROM %s;", tableName);
        return query;
    }
    
    private String buildUpdateQuery(String[] data) { 
        String query = String.format("UPDATE %s SET ", tableName);
        for(int i = 0; i < data.length; i++) {
            if(i < columnNames.length-1 && i != 0)
                query += String.format("%s = '%s', ", columnNames[i], data[i]);
            else if (columnNames[i].equals("age") || columnNames[i].equals("price") || columnNames[i].equals("stock"))
                query += String.format("%s = %s, ", columnNames[i], data[i]);
            else if (i == columnNames.length-1)
                query += String.format("%s = '%s' ", columnNames[i], data[i]);
        }
        query += String.format("WHERE id = %s;", data[0]);
        return query;
    }
    
    private String buildInsertQuery(String[] data) {
        String query = String.format("INSERT INTO %s (", tableName);
        for(int i = 0; i < data.length; i++) {
            if(i < data.length-1)
                query += String.format("%s,", columnNames[i+1]);
            else
                query += String.format("%s", columnNames[i+1]);
        }
        query += ") VALUES(";
        for(int i = 0; i < data.length; i++) {
            if (i < data.length-1 && (!columnNames[i+1].equalsIgnoreCase("age") && !columnNames[i+1].equalsIgnoreCase("price") && !columnNames[i+1].equalsIgnoreCase("stock")))
                query += String.format("'%s', ", data[i]);
            else if (columnNames[i+1].equals("age") || columnNames[i+1].equals("price") || columnNames[i+1].equals("stock"))
                query += String.format("%s,", data[i]);
            else
                query += String.format("'%s');", data[i]);
            
        }
        return query;
    }
    
    private String buildSearchQuery(String searchQuery) {
        String query = "SELECT ";
        for(int i = 0; i < columnNames.length; i++) {
            if (i < columnNames.length-1)
                query += String.format("%s, ", columnNames[i]);
            else
                query += String.format("%s ", columnNames[i]);
        }
        query += String.format("FROM %s WHERE ", tableName);
        for(int i = 0; i < columnNames.length; i++) {
            if(!columnNames[i].equals("id") && i < columnNames.length-1) {
                query += String.format("%s LIKE  '%s%%' OR ", columnNames[i], searchQuery);
            } else if (i == columnNames.length-1) {
                query += String.format("%s LIKE  '%s%%';", columnNames[i], searchQuery);
            }
        }
        return query;
    }
    
    public ArrayList<String[]> fillTable() {
        HashMap<Connection, ResultSet> resData = null;
        resData = RetrieveQuery(buildSelectQuery());
        return fillData(resData);
    }
    
    public ArrayList<String[]> searchData(String searchQuery) {
        HashMap<Connection, ResultSet> resData = null;
        resData = RetrieveQuery(buildSearchQuery(searchQuery));
        return fillData(resData);
    }
    
    public ArrayList<String[]> deleteData(String id) {
        HashMap<Connection, ResultSet> resData = null;
        if(ExecuteQuery(String.format("DELETE FROM %s WHERE id = %s", tableName, id))) {
            resData = RetrieveQuery(buildSelectQuery());
            return fillData(resData);
        }
        return new ArrayList();
    }
    
    public ArrayList<String[]> updateData(String[] data) {
        HashMap<Connection, ResultSet> resData = null;
        if(ExecuteQuery(buildUpdateQuery(data))) {
            resData = RetrieveQuery(buildSelectQuery());
            return fillData(resData);
        }
        return new ArrayList();
    }
    
    public ArrayList<String[]> insertData(String[] data) {
        HashMap<Connection, ResultSet> resData = null;
        if(ExecuteQuery(buildInsertQuery(data))){
            resData = RetrieveQuery(buildSelectQuery());
            return fillData(resData);
        }
        return new ArrayList();
    }
    
}
