package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mysql.DatabaseConnection;
import services.HtmlBuilder;

@WebServlet(name = "StatisticsServlet", urlPatterns = {"/StatisticsServlet"})
public class StatisticsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final String servletName = "StatisticsServlet";
    private final String databaseName = "asterisk";
    private final String fileName = "statistics.html";
    
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
            connection = dbc.getDataSource().getConnection();
                        
            hb = new HtmlBuilder();
        
            content += "Statistics";
            
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
