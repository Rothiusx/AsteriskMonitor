package servlets;

import asterisk.Call;
import asterisk.Dashboard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mysql.DatabaseConnection;
import services.HtmlBuilder;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/DashboardServlet"})
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String servletName = "DashboardServlet";
    private final String databaseName = "asterisk";
    private final String fileName = "dashboard.html";
    
    private DatabaseConnection dbc;
    private HtmlBuilder hb;
    
    @Override
    public void init( ServletConfig config ) {
        dbc = new DatabaseConnection(servletName, databaseName);
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();
        
        displayDashboard(out);
    }
    
    @Override
    public void destroy() {
        System.out.println("-------------------------------------------------------");
        System.out.println("Destroy method has been called and servlet is destroyed");
        System.out.println("-------------------------------------------------------");
    }
    
    private void displayDashboard(PrintWriter out) {
        Connection connection = null;
        Dashboard dashboard;
        String content = "";

        try {
            connection = dbc.getDataSource().getConnection();
            dashboard = new Dashboard(connection);
            hb = new HtmlBuilder();
            
            content += "<table><tbody><tr>";
            content += "<td valign=\"top\">";
            
            content += "<table>";
            content += "<tbody>";
            content += "<tr>";
            content += "<td><h3>Agents currently on call:</h3></td>";
            content += "<td><h3>" + dashboard.getAgentsOnCall().size() + "</td>";
            content += "</tr>";
            content += "<tr>";
            content += "<td><h3>Currently active agents:</h3></td>";
            content += "<td><h3>" + dashboard.getActiveAgents().size() + "</td>";
            content += "</tr>";
            content += "<tr>";
            content += "<td><h3>Callers waiting in queue:</h3></td>";
            content += "<td><h3>" + dashboard.getCallersInQueue().size() + "</h3></td>";
            content += "</tr>";
            content += "<tr>";
            content += "<td><h3>Completed calls in 24 hours:</td>";
            content += "<td><h3>" + dashboard.getCompletedCalls().size() + "</h3></td>";
            content += "</tr>";
            content += "<tr>";
            content += "<td><h3>Most called queue in 24 hours:&nbsp;&nbsp;&nbsp;</h3></td>";
            content += "<td><h3>" + dashboard.getMostCalledQueue() + "</h3></td>";
            content += "</tr>";
            content += "</tbody>";
            content += "</table>";
            
            content += "</td>";
            content += "<td>";
            
            if(dashboard.getCompletedCalls().size() > 0) {
                content += getPieChart(dashboard.getCompletedCalls());
                content += "<div id=\"chartContainer\" style=\"height: 370px; width: 100%;\"></div>";
                content += "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>";
            }
            
            content += "</td>";
            content += "</tr></tbody></table>";
            
            content += "<p>&nbsp;</p>";
            content += "<table class=\"table table-responsive\">";
            content += "<tbody><tr><td>";

            content += "<h2 align=\"center\">Active Agents</h2>";
            content += "<p></p>";
                
            content += "<table class=\"table table-sm table-responsive\">";
            content += "<thead>";
            content += "<tr>";
            content += "<th><h3 align=\"center\">Agent</h3></th>";
            content += "<th>&nbsp;</th>";
            content += "<th><h3 align=\"center\">Queue</h3></th>";
            content += "</tr>";
            content += "</thead>";
            content += "<tbody>";
                
            if(dashboard.getActiveAgents().size() > 0) {
                for (int i = 0; i < dashboard.getActiveAgents().size(); i++) {
                    content += "<tr>";
                    content += "<td><h4 align=\"center\">" + dashboard.getActiveAgents().get(i)
                            .getAgentChanname() + "</h4></td>";
                    
                    boolean matchFound = false;
                    for (int j = 0; j < dashboard.getAgentsOnCall().size(); j++) {
                        if(dashboard.getActiveAgents().get(i).getAgentChanname()
                                .equals(dashboard.getAgentsOnCall().get(j).getAgentChanname())) {
                            matchFound = true;
                            content += "<td><img src=\"images/red_dot_1.png\" style=\"height:30px\"></td>";
                        }
                    }
                    if(!matchFound) {
                        content += "<td><img src=\"images/green_dot_1.png\" style=\"height:30px\"></td>";
                    }
                    content += "<td><h4 align=\"center\">" + dashboard.getActiveAgents().get(i)
                            .getQueueName() + "</h4></td>";
                    content += "</tr>";
                }
            }
                
            content += "</tbody>";
            content += "</table>";
            
            content += "</td>";
            
            if(dashboard.getAgentsOnCall().size() > 0) {
                content += "<td>&nbsp;&nbsp;</td>";
                content += "<td>";
                
                content += "<h2 align=\"center\">List of Calling Agents</h2>";
                content += "<p></p>";
                
                content += "<table class=\"table table-sm table-responsive\">";
                content += "<thead>";
                content += "<tr>";
                content += "<th><h3 align=\"center\">Agent</h3></th>";
                content += "<th>&nbsp;</th>";
                content += "<th><h3 align=\"center\">Caller</h3></th>";
                content += "<th><h3 align=\"center\">Queue</h3></th>";
                content += "</tr>";
                content += "</thead>";
                content += "<tbody>";
                
                for (int i = 0; i < dashboard.getAgentsOnCall().size(); i++) {
                    content += "<tr>";
                    content += "<td ><h4 align=\"center\">" + dashboard.getAgentsOnCall().get(i).getAgentChanname() + "</h4></td>";
                    content += "<td><img src=\"images/green_arrow_1.png\" style=\"height:30px\"></td>";
                    content += "<td><h4 align=\"center\">" + dashboard.getAgentsOnCall().get(i).getCallerChanname() + "</h4></td>";
                    content += "<td><h4 align=\"center\">" + dashboard.getAgentsOnCall().get(i).getQueueName() + "</h4></td>";
                    content += "</tr>";
                }
                
                content += "</tbody>";
                content += "</table>";
                content += "</td>";
            }
            
            content += "</tr></tbody></table>";
            
            hb.setContent(fileName, content);                 
            out.print(hb.getDocument());
        }

        catch(SQLException e) {
            System.out.println(e);
        }
        finally {               
            try {
                if(connection != null) {
                    connection.close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
    }
    
    private String getPieChart(ArrayList<Call> completedCalls) {
        String content = "";
        int salesCounter = 0;
        int techCounter = 0;
        int billingCounter = 0;
        
        content += "<script>\n" +
                "window.onload = function() {\n" +
                "var chart = new CanvasJS.Chart(\"chartContainer\", {\n" +
                "   theme: \"light2\", // \"light1\", \"light2\", \"dark1\", \"dark2\"\n" +
                //"   exportEnabled: true,\n" +
                //"   animationEnabled: true,\n" +
                "   title: {\n" +
                "       text: \"Completed Calls per Queue\"\n" +
                "   },\n" +
                "   data: [{\n" +
                "       type: \"pie\",\n" +
                "       startAngle: 25,\n" +
                "       toolTipContent: \"{y} - #percent %\",\n" +
                "       indexLabelFontSize: 20,\n" +
                "       indexLabel: \"{label} - {y}\",\n" +
                "       dataPoints: [\n";

        for (int i = 0; i < completedCalls.size(); i++) {
            if(completedCalls.get(i).getQueueName().equals("Sales"))
                salesCounter++;
            else if(completedCalls.get(i).getQueueName().equals("Tech"))
                techCounter++;
            else if(completedCalls.get(i).getQueueName().equals("Billing"))
                billingCounter++;
        }
        
        if(salesCounter > 0)
            content += "{ y:" + salesCounter + ", label: \"Sales\" },\n";
        if(techCounter > 0)
            content += "{ y:" + techCounter + ", label: \"Tech\" },\n";
        if(billingCounter > 0)
            content += "{ y:" + billingCounter + ", label: \"Billing\" },\n";
        
        content += "        ]\n" +
                "   }]\n" +
                "});\n" +
                "chart.render();\n" +
                "}\n" +
                "</script>";

        return content; 
    }
}
