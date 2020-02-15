package servlets;

import asterisk.Cel;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mysql.DatabaseConnection;
import services.HtmlBuilder;

@WebServlet(name = "CelServlet", urlPatterns = {"/CelServlet"})
public class CelServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private final String servletName = "CelServlet";
    private final String databaseName = "asterisk";
    private final String fileName = "cel.html";
    
    private DatabaseConnection dbc;
    private HtmlBuilder hb;
    private Cel cel;
    
    @Override
    public void init( ServletConfig config ) {
        dbc = new DatabaseConnection(servletName, databaseName);
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();
        
        displayCelTable(out);
    }
    
    @Override
    public void destroy() {
        System.out.println("-------------------------------------------------------");
        System.out.println("Destroy method has been called and servlet is destroyed");
        System.out.println("-------------------------------------------------------");
    }
    
    private void displayCelTable(PrintWriter out) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String content = "";
            String sql = "SELECT * FROM cel ORDER BY id DESC LIMIT 200";
            
            connection = dbc.getDataSource().getConnection();
            
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            
            cel = new Cel();
            hb = new HtmlBuilder();
     
            content += "<table class=\"table-responsive table-striped table-sm\">";
            content += "<thead>";
            content += "<tr>";

            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                content += "<th>" + cel.getCelColumn(i) + "</th>";
            }

            content += "</tr>";
            content += "</thead>";
            content += "<tbody>";
            
            while(rs.next()) {
                content += "<tr>";
                for(int i = 0; i < rsmd.getColumnCount(); i++) {
                    content += "<td>" + rs.getString(i + 1) + "</td>";
                }
                content += "</tr>";
            }
            rs.close();
            
            content += "</tbody>";
            content += "</table>";
            
            hb.setContent(fileName, content);
            out.print(hb.getDocument());
        }

        catch(SQLException e) {
            System.out.println(e);
        }
        finally {         
            try {
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch(SQLException e) {
                System.out.println(e);
            }
            
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
}