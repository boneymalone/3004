/*
 * Handles all queries relating to the 'comparitives' table
 */
package database;

import java.sql.*;
/**
 *
 * @author Darren
 */
public class comparitives {
    Connection conn;
    private int questID;
    private int compareTo;

    public comparitives(int questID, int compareTo) {
        this.questID = questID;
        this.compareTo = compareTo;
    }

    public comparitives() {
        this(-1, -1);
    }
    
    /**
     * Establishes a Oracle connection.
     */
    private Connection getOracleConnection() {
        conn=null;
        try {
            /* Load the Oracle JDBC Driver and register it. */
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            /* Open a new connection */
            conn = DriverManager.getConnection("jdbc:oracle:thin:@oracle.students.itee.uq.edu.au:1521:iteeo", "CSSE3004GF", "pass123");
        } catch(Exception ex){
            System.out.println(ex.toString());
        }
        return conn;
    }
    
    /**
     * Runs the specified query.
     * 
     * @param query The query string to run.
     * @return ResultSet returned from running query.
     */
    private ResultSet runQuery(String query) {
        try {
            if(conn == null) {
                getOracleConnection();
            }
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            System.out.println("answers.runQuery(): " + e.toString());
            return null;
        }
    }
    
    /**
     * Closes any open Oracle connection.
     */
    private void closeOracleConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * Attempts to add this comparitive to the database. 
     * Database will not be checked for success.
     * Will not update existing comparitive.
     * 
     * Pre-condition: The questID and compareTo must be set to existing questIDs
     * 
     * @return  0    for attempt made.
     *          -1   for invalid comparitive properties.
     *          -2   for undefined error.
     */
    public int addComparitive() {
        try {
            if (getQuestID() == -1) {
                return -1;
            } else if (getCompareTo() == -1) {
                return -1;
            }
            
            getOracleConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM Comparitives WHERE questID=?");
            statement.setInt(1, getQuestID());
            statement.executeUpdate();
            
            statement = conn.prepareStatement("INSERT INTO Comparitives(questID, compareTo)"
                    + " VALUES(?, ?)");
            statement.setInt(1, getQuestID());
            statement.setInt(2, getCompareTo());
            statement.executeUpdate();
            
            /*String query= "INSERT INTO Comparitives(questID, compareTo) VALUES (" 
                    + getQuestID() + ", " + getCompareTo() + ")";  
            runQuery(query);*/
            statement.close();

            closeOracleConnection();
            return 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }
    
    /**
     * Attempts to edit this question in the database. 
     * Database will not be checked for success.
     * Will not add a new comparitive.
     * 
     * Pre-condition: The questID and compareTo must be set to existing questIDs
     * 
     * @return  0    for attempt made.
     *          -1   for invalid comparitive properties.
     *          -2   for undefined error.
     */
    public int editComparitive() {
        try {
            if (getQuestID() == -1) {
                return -1;
            } else if (getCompareTo() == -1) {
                return -1;
            }
            
            getOracleConnection();
            PreparedStatement statement = conn.prepareStatement("UPDATE Comparitives SET compareTo=? "
                    + " WHERE questID=?");
            statement.setInt(1, getCompareTo());
            statement.setInt(2, getQuestID());
            statement.executeUpdate();
            statement.close();
            /*
            String query = "UPDATE Comparitives SET compareTo=" + getCompareTo()
                            + " WHERE questID=" + getQuestID();
            runQuery(query);*/
            closeOracleConnection();
            return 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }

    /**
     * Attempts to delete this comparitive from the database. 
     * Database will not be checked for success.
     * 
     * Pre-condition: The questID and compareTo must be set to existing questIDs
     * 
     * @return  0    for attempt made
     *          -1   for unset IDs.
     *          -2   for undefined error.
     */
    public int deleteComparitive() {
        try {
            if (getQuestID() == -1) {
                return -1;
            } 
            
            getOracleConnection();
            PreparedStatement statement = conn.prepareStatement("DELETE FROM Comparitives WHERE questID=? ");
            statement.setInt(1, getQuestID());

            statement.executeUpdate();
            statement.close();

            /*String query = "DELETE FROM Comparitives WHERE questID=" + getQuestID() 
                    + " AND compareTo=" + getCompareTo();
            runQuery(query);*/

            closeOracleConnection();
            return 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }
    
    /**
     * Attempts to find comparitive by questID and compareTo. 
     * Updates properties of this instance of comparitives to result found.
     * 
     * Pre-condition: The questID and compareTo must be set to existing questIDs
     * 
     * @return  0    for attempt made
     *          -1   for unset or invalid questIDs.
     *          -2   for undefined error.
     */
    public int getComparitive() {
        try {
            if (getQuestID() == -1) {
                return -1;
            }
            
            getOracleConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Comparitives WHERE questID=?");
            statement.setInt(1, getQuestID());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                setQuestID(resultSet.getInt("questID"));
                setCompareTo(resultSet.getInt("compareTo"));
            }
            resultSet.close();
            statement.close();

/*            String query= "SELECT * FROM Comparitives WHERE questID=" 
                    + getQuestID();
            ResultSet resultSet = runQuery(query);
            while (resultSet.next()) {
                setQuestID(resultSet.getInt("questID"));
                setCompareTo(resultSet.getInt("compareTo"));
            }
            resultSet.close();*/
            closeOracleConnection();
            return 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }

    /**
     * @param questID the questID to set
     */
    public void setQuestID(int questID) {
        this.questID = questID;
    }

    /**
     * @return the questID
     */
    public int getQuestID() {
        return questID;
    }

    /**
     * @return the compareTo
     */
    public int getCompareTo() {
        return compareTo;
    }

    /**
     * @param compareTo the compareTo to set
     */
    public void setCompareTo(int compareTo) {
        this.compareTo = compareTo;
    }
}