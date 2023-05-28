package StudentsRecordSystemConsole;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clancy Sanchez
 */
public class DatabaseUtility {
    private String databaseName;        // databaseName
    
    DatabaseUtility() {
        
    }
    /**
     * Initialize the database name
     * @param databaseName 
     */
    public DatabaseUtility(String databaseName) {
        if (databaseName.endsWith(".db")) {
            this.databaseName = databaseName;
        } else {
            this.databaseName = databaseName + ".db";
        }
    }
    
    /**
     * Database name setter
     * @param databaseName 
     */
    public void setDatabaseName(String databaseName) {
        if (databaseName.endsWith(".db")) {
            this.databaseName = databaseName;
        } else {
            this.databaseName = databaseName + ".db";
        }
    }
    
  
    /**
     * Creates new connection for every call
     * @return connection
     */
    private Connection CreateDatabaseConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.databaseName);
            if (connection != null) {
                return connection;
            } else {
                return null;
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseUtility.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Use for closing the connection
     * @param connection 
     */
    public void Close(Connection connection) {
        try {
            if(connection != null)
                connection.close();
        } catch(SQLException e) {
            Logger.getLogger(DatabaseUtility.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Executes query that doesn't retrieve data from database
     * @param query
     * @return Boolean
     */
    public boolean ExecuteQuery(String query) {
        Connection connection = CreateDatabaseConnection();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                statement.executeUpdate(query);
                return true;
            } catch (SQLException e) {
                Logger.getLogger(DatabaseUtility.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                Close(connection);
            }
        }
        return false;
    }

    /**
     * Executes queries that retrieve data from database
     * @param query
     * @return ResultSet
     */
    public HashMap<Connection, ResultSet> RetrieveQuery(String query) {
        Connection connection = CreateDatabaseConnection();
        HashMap<Connection, ResultSet> dataConnection = new HashMap<>();
        if(connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                dataConnection.put(connection, statement.executeQuery(query));
                return dataConnection;
            } catch (SQLException e) {
                Logger.getLogger(DatabaseUtility.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }
}
