/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asterisk;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import services.SqlHelper;

/**
 *
 * @author rothi
 */
public class Dashboard {
    private final SqlHelper sh;
    private ArrayList<Call> agentsOnCall;
    private ArrayList<Call> completedCalls;
    private ArrayList<String> callersInQueue;
    private ArrayList<Call> activeAgents;
    private String mostCalledQueue;
    
    public Dashboard(Connection connection) {
        sh = new SqlHelper(connection);
    }
    
    public ArrayList<Call> getAgentsOnCall() {
        try {
            agentsOnCall = new ArrayList<>();

            String agentChanname = "";
            String callerChanname = "";
            String queueName = "";
            String linkedid = "";
            String query = "";

            query = "SELECT channame, appdata, peer, linkedid FROM cel "
                    + "WHERE eventtype LIKE 'BRIDGE_ENTER' "
                    + "AND channame LIKE 'SIP/%' "
                    + "AND appname LIKE 'Queue' "
                    + "AND peer LIKE 'SIP/%' "
                    + "AND (eventtime > DATE_SUB(now(), INTERVAL 1 HOUR)) "
                    + "ORDER BY id DESC;";
            ResultSet rs1 = sh.getResultSet(query);

            while(rs1.next()) {
                agentChanname = rs1.getString("peer").substring(4, 8);
                callerChanname = rs1.getString("channame").substring(4, 8);
                queueName = rs1.getString("appdata").substring(0, 1).toUpperCase() + rs1.getString("appdata").substring(1);
                linkedid = rs1.getString("linkedid");
                System.out.println("Call ID to compare: " + linkedid);

                query = "SELECT channame, linkedid FROM cel "
                        + "WHERE eventtype LIKE 'BRIDGE_EXIT' "
                        + "AND linkedid LIKE '" + linkedid + "' "
                        + "AND(eventtime > DATE_SUB(now(), INTERVAL 1 HOUR)) "
                        + "ORDER BY id DESC;";
                ResultSet rs2 = sh.getResultSet(query);

                boolean matchFound = false;
                while(rs2.next()) {
                    if(rs2.getString("linkedid").equals(linkedid)) {
                        matchFound = true;
                    }
                }
                rs2.close();
                if(!matchFound) {
                    agentsOnCall.add(new Call(agentChanname, callerChanname, queueName, linkedid));
                }
            }
            rs1.close();
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        finally {
            try {
                if(sh.getPreparedStatement() != null) {
                    sh.getPreparedStatement().close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        return agentsOnCall;
    }
    
    public ArrayList<Call> getCompletedCalls() {
        try {
            completedCalls = new ArrayList<>();

            String queueName = "";
            String linkedid = "";
            String query = "";

            query = "SELECT appdata, linkedid FROM cel "
                    + "WHERE eventtype LIKE 'BRIDGE_ENTER' "
                    + "AND channame LIKE 'SIP/%' "
                    + "AND appname LIKE 'Queue' "
                    + "AND (eventtime > DATE_SUB(now(), INTERVAL 24 HOUR)) "
                    + "ORDER BY id DESC;";
            ResultSet rs1 = sh.getResultSet(query);

            while(rs1.next()) {
                queueName = rs1.getString("appdata").substring(0, 1).toUpperCase() + rs1.getString("appdata").substring(1);
                linkedid = rs1.getString("linkedid");

                query = "SELECT linkedid FROM cel "
                        + "WHERE eventtype LIKE 'BRIDGE_EXIT' "
                        + "AND appname LIKE 'Queue' "
                        + "AND linkedid LIKE '" + linkedid + "' "
                        + "AND(eventtime > DATE_SUB(now(), INTERVAL 24 HOUR)) "
                        + "ORDER BY id DESC;";
                ResultSet rs2 = sh.getResultSet(query);
                
                while(rs2.next()) {
                    if(rs2.getString("linkedid").equals(linkedid)) {
                        completedCalls.add(new Call(null, null, queueName, linkedid));
                    }
                }
                rs2.close();
            }
            rs1.close();   
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        finally {
            try {
                if(sh.getPreparedStatement() != null) {
                    sh.getPreparedStatement().close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        return completedCalls;
    }
    
    public ArrayList<String> getCallersInQueue() {
        try {
            callersInQueue = new ArrayList<>();

            String callerChanname = "";
            String linkedid = "";
            String query = "";

            query = "SELECT channame, linkedid FROM cel "
                    + "WHERE eventtype LIKE 'CHAN_START' "
                    + "AND exten LIKE '911' "
                    + "AND (eventtime > DATE_SUB(now(), INTERVAL 2 HOUR)) "
                    + "ORDER BY id DESC;";
            ResultSet rs1 = sh.getResultSet(query);

            while(rs1.next()) {
                callerChanname = rs1.getString("channame").substring(4, 8);
                linkedid = rs1.getString("linkedid");

                query = "SELECT DISTINCT linkedid FROM cel "
                        + "WHERE (eventtype LIKE 'BRIDGE_ENTER' "
                        + "OR eventtype LIKE 'LINKEDID_END') "
                        + "AND (eventtime > DATE_SUB(now(), INTERVAL 2 HOUR));";
                ResultSet rs2 = sh.getResultSet(query);

                boolean matchFound = false;
                while(rs2.next()) {
                    if(rs2.getString("linkedid").equals(linkedid)) {
                        matchFound = true;
                    }
                }
                rs2.close();
                if(!matchFound) {
                    callersInQueue.add(callerChanname);
                }
            }
            rs1.close();
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        finally {
            try {
                if(sh.getPreparedStatement() != null) {
                    sh.getPreparedStatement().close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        return callersInQueue;
    }
    
    public String getMostCalledQueue() {
        try {
            mostCalledQueue = "None";
            
            String query = "";

            query = "SELECT appdata FROM cel "
                    + "WHERE eventtype LIKE 'BRIDGE_ENTER' "
                    + "AND appname LIKE 'queue' "
                    + "AND appdata NOT LIKE '' "
                    + "AND (eventtime > DATE_SUB(now(), INTERVAL 24 HOUR)) "
                    + "GROUP BY appdata "
                    + "ORDER BY COUNT(*) DESC "
                    + "LIMIT 1;";
            ResultSet rs = sh.getResultSet(query);

            while(rs.next()) {
                String queueName = rs.getString("appdata");
                mostCalledQueue = queueName.substring(0, 1).toUpperCase() + queueName.substring(1);
            }
            rs.close();
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        finally {
            try {
                if(sh.getPreparedStatement() != null) {
                    sh.getPreparedStatement().close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        return mostCalledQueue;
    }
    
    public ArrayList<Call> getActiveAgents() {
        try {
            activeAgents = new ArrayList<>();

            String agentNumber = "";
            String queueName = "";
            String eventtime = "";
            String query = "";

            query = "SELECT MAX(eventtime) AS eventtime, cid_num, appname, appdata FROM cel "
                    + "WHERE eventtype LIKE 'APP_START' "
                    + "AND appname LIKE 'AddQueueMember' "
                    + "GROUP BY cid_num, appname, appdata;";
            
            ResultSet rs1 = sh.getResultSet(query);       
            while(rs1.next()) {
                agentNumber = rs1.getString("cid_num");
                queueName = rs1.getString("appdata");
                eventtime = rs1.getString("eventtime");

                query = "SELECT eventtime, cid_num, appname, appdata FROM cel "
                        + "WHERE eventtype LIKE 'APP_START' "
                        + "AND appname LIKE 'RemoveQueueMember' "
                        + "AND cid_num LIKE '" + agentNumber + "' "
                        + "AND appdata LIKE '" + queueName + "' "
                        + "AND eventtime > '" + eventtime + "' "
                        + "ORDER BY id DESC;";
                
                ResultSet rs2 = sh.getResultSet(query);
                
                boolean matchFound = false;
                while(rs2.next()) {
                    if(rs2.getString("cid_num").equals(agentNumber)) {
                        matchFound = true;
                        
                    }
                }
                rs2.close();
                if(!matchFound) {
                    activeAgents.add(new Call(agentNumber, null, queueName.substring(0, 1).toUpperCase() + queueName.substring(1), null));
                }
            }
            rs1.close();   
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        finally {
            try {
                if(sh.getPreparedStatement() != null) {
                    sh.getPreparedStatement().close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        return activeAgents;
    }     
}
