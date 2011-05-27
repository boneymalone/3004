/*
 * Handles all queries relating to the 'questions' table
 */
package database;

import java.sql.*;
/**
 *
 * @author Darren
 */
public class questions {
    Connection conn;
    private int pollID;
    private int questID;
    private String demographic;
    private String responseType;
    private String questionText;

    public questions(int questID, int pollID, String demographic, String responseType, String questionText) {
        this.questID = questID;
        this.pollID= pollID;
        this.demographic = demographic;
        this.responseType = responseType;
        this.questionText = questionText;
    }

    public questions(int questID) {
        this(questID, -1, "N", "N", "");
    }

    public questions(int pollID, String demographic, String responseType, String questionText) {
        this(-1, pollID, demographic, responseType, questionText);
    }

    public questions() {
        this(-1, -1, "N", "N", "");
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
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
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
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    /**
     * Closes any open Oracle connection.
     */
    private void closeOracleConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * Attempts to locate all answerIDs for answers to this question in
     * the database. 
     * Will not check for success.
     * 
     * Pre-condition: The questID must be set to an existing question
     * 
     * @return  ResultSet   for attempt made.
     *          null        for error.
     */
    public ResultSet getAnswers() {
        try {
            if (getQuestID() == -1) {
                return null;
            }
            
            getOracleConnection();
            String query= "SELECT answerID FROM Answers WHERE questID="
                    + getQuestID();  
            ResultSet resultSet = runQuery(query);
            closeOracleConnection();
            return resultSet;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
    
    /**
     * Attempts to add this question to the database. 
     * Database will not be checked for success.
     * Will not update existing question.
     * 
     * Pre-condition: The questID must be set to an non-existing questID
     * 
     * @return  0    for attempt made.
     *          -1   for invalid question properties.
     *          -2   for undefined error.
     */
    public int addQuestion() {
        try {
            if (getPollID() == -1) {
                return -1;
            } else if (getQuestID() == -1) {
                return -1;
            } else if (getQuestionText().equals("")) {
                return -1;
            }
            
            getOracleConnection();
            String query= "INSERT INTO Questions(questID, demographic, "
                    + "responseType, question) VALUES (" + getQuestID() + ", '" 
                    + getDemographic() + "', '" + getResponseType() + "', '" 
                    + getQuestionText() + "', pollID=" + getPollID()
                    + ")";  
            runQuery(query);

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
     * Will not add a new question.
     * 
     * Pre-condition: The questID must be set to an existing question
     * 
     * @return  0    for attempt made.
     *          -1   for invalid question properties.
     *          -2   for undefined error.
     */
    public int editQuestion() {
        try {
            if (getPollID() == -1) {
                return -1;
            } else if (getQuestID() == -1) {
                return -1;
            } else if (getQuestionText().equals("")) {
                return -1;
            }
            
            getOracleConnection();
            String query= "UPDATE Questions SET demographic='" + getDemographic() 
                    + "', responseType='" + getResponseType() + "', question='" 
                    + getQuestionText() + "', pollID=" + getPollID() + ", WHERE questID=" + getQuestID();  
            runQuery(query);

            closeOracleConnection();
            return 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }

    /**
     * Attempts to delete this question from the database. 
     * Database will not be checked for success.
     * 
     * Pre-condition: The questID must be set to an existing question
     * 
     * @return  0    for attempt made
     *          -1   for unset question ID.
     *          -2   for undefined error.
     */
    public int deleteQuestion() {
        try {
            if (getQuestID() == -1) {
                return -1;
            } 
            getOracleConnection();
            
            /* Delete answers under question */
            String query= "SELECT answerID FROM Answers WHERE questID=" + getQuestID();
            ResultSet resultSet = runQuery(query);
            
            /* Calls each answer to delete itself and its children */
            while (resultSet.next()) {
                answers temp = new answers();
                temp.setAnswerID(resultSet.getInt("answersID"));
                temp.deleteAnswer();
            }
            
            /* Delete comparitives under question */
            query= "SELECT * FROM Comparitives WHERE questID=" + getQuestID() 
                    + " OR compareTo=" + getQuestID();
            resultSet = runQuery(query);
            
            /* Calls each comparitive to delete itself and its children */
            while (resultSet.next()) {
                comparitives temp = new comparitives();
                temp.setQuestID(resultSet.getInt("questID"));
                temp.setCompareTo(resultSet.getInt("compareTo"));
                temp.deleteComparitive();
            }

            /* Delete question */
            query= "DELETE FROM Questions WHERE questID=" + getQuestID();
            runQuery(query);

            closeOracleConnection();
            return 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }
    
    /**
     * Attempts to find question by questID. 
     * Updates properties of this instance of questions to result found.
     * 
     * Pre-condition: The questID must be set to an existing question
     * 
     * @return  0    for attempt made
     *          -1   for unset or invalid question ID.
     *          -2   for undefined error.
     */
    public int getQuestion() {
        try {
            if (getQuestID() == -1) {
                return -1;
            } 
            getOracleConnection();
            String query= "SELECT * FROM Questions WHERE questID=" + getQuestID();
            ResultSet resultSet = runQuery(query);
            while (resultSet.next()) {
                setPollID(resultSet.getInt("pollID"));
                setQuestID(resultSet.getInt("questID"));
                setDemographic(resultSet.getString("demographic"));
                setResponseType(resultSet.getString("responseType"));
                setQuestionText(resultSet.getString("questionText"));
                closeOracleConnection();
                return 0;
            }
            closeOracleConnection();
            return -1;
        } catch (Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }
    
    /**
     * @param pollID the pollID to set
     */
    public void setPollID(int pollID) {
        this.pollID = pollID;
    }

    /**
     * @param questID the questID to set, use -1 to automatically set next
     * available ID.
     */
    public void setQuestID(int questID) {
        if (questID != -1) {
            this.questID = questID;
        } else {
            try {
                getOracleConnection();
                String query= "SELECT MAX(questID) FROM Questions";  
                ResultSet resultset = runQuery(query);
                resultset.next();
                this.questID = resultset.getInt(1) + 1;
                closeOracleConnection();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * @param demographic the demographic to set
     */
    public void setDemographic(String demographic) {
        this.demographic = demographic;
    }

    /**
     * @param responseType the responseType to set
     */
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    /**
     * @param question the question to set
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * @return the pollID
     */
    public int getPollID() {
        return pollID;
    }

    /**
     * @return the questID
     */
    public int getQuestID() {
        return questID;
    }

    /**
     * @return the demographic
     */
    public String getDemographic() {
        return demographic;
    }

    /**
     * @return the responseType
     */
    public String getResponseType() {
        return responseType;
    }

    /**
     * @return the question
     */
    public String getQuestionText() {
        return questionText;
    }
}