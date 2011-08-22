<%-- INDIVIDUAL DARREN
    Document   : geolocation
    Created on : 12/08/2011, 7:13:37 PM
    Author     : Darren
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="database.*"%>
<%@page import="java.text.*"%>
<%@page import="java.io.*"%>

<%
try{
    String action = request.getParameter("action"); //"load" or "set"
    String dataType = request.getParameter("type"); //"user" or "question" or "poll"
    String dataID = request.getParameter("id"); //ID of the above type
    
    if (action.equals("set")) {
        String location = request.getParameter("location"); //The number generated by Google Maps
        if (location.equals(null)) {
            out.println("{");
            out.println("\"error\": " + "\"undefined coords\"");
            out.println("}");
        } else {
            if ((!dataType.equals("user")) && (!dataType.equals("question")) && (!dataType.equals("poll"))) {
                out.println("{");
                out.println("\"error\": " + "\"undefined type\"");
                out.println("}");
            } else {
                if (dataType.equals("user")) {
                    users user = new users();
                    user.setUserID(Integer.parseInt(dataID));
                    user.getUser();
                    user.setLocation(location);
                    user.editUser();
                } else if (dataType.equals("question")) {
                    questions question = new questions();
                    question.setQuestID(Integer.parseInt(dataID));
                    question.getQuestion();
                    question.setLocation(location);
                    question.editQuestion();
                } else if (dataType.equals("poll")) {
                    polls poll = new polls();
                    poll.setPollID(Integer.parseInt(dataID));
                    poll.getPoll();
                    poll.setLocation(location);
                    poll.editPoll();
                }
                out.println("{");
                out.println("\"status\": " + "\"Location changed\"");
                out.println("}");
            }
        }                   
    } else if (action.equals("load")) {
        if ((!dataType.equals("user")) && (!dataType.equals("question")) && (!dataType.equals("poll"))) {
            out.println("{");
            out.println("\"error\": " + "\"undefined type\"");
            out.println("}");
        } else {
            String location = "";
            if (dataType.equals("user")) {
                users user = new users();
                user.setUserID(Integer.parseInt(dataID));
                user.getUser();
                location = user.getLocation();
            } else if (dataType.equals("question")) {
                questions question = new questions();
                question.setQuestID(Integer.parseInt(dataID));
                question.getQuestion();
                location = question.getLocation();
            } else if (dataType.equals("poll")) {
                polls poll = new polls();
                poll.setPollID(Integer.parseInt(dataID));
                poll.getPoll();
                location = poll.getLocation();
            }
            if(!location.equals("")){
                out.println("{");
                out.println("\"location\": \"" + location + "\"");
                out.println("}");
             } else {
                out.println("{");
                out.println("\"error\": " + "\"No data found\"");
                out.println("}");
             }
        }
    } else {
        out.println("{");
        out.println("\"error\": " + "\"undefined action\"");
        out.println("}");
    }
} catch(Exception e){
    out.write(e.toString());
}

%>