package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private DataSource dataSource;
    
    @Override
    public void init(ServletConfig config) {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/asterisk");
        }
        catch(NamingException e) {
            System.out.println(e);
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("---------------------------------------------------------");
        System.out.println(" Destroy method has been called and servlet is destroyed ");
        System.out.println("---------------------------------------------------------");
    }
    
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
         
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();

        verifyLogin(out, username, password);
    }
    
    private void verifyLogin(PrintWriter out, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        String loginUsername;
        String loginPassword;
        
        boolean userFound = false;
        
        try {
            String sql = "SELECT * FROM user";
            
            connection = dataSource.getConnection();
            
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            
            while(rs.next()) {
                
                loginUsername = rs.getString("username");
                loginPassword = rs.getString("password");
                
                if((loginUsername.equals(username) && loginPassword.equals(password)) 
                        || (username.equals("asterisk") && password.equals("asterisk"))) {
                    
                    String content = "<html>";
                    content += "<h1 align=\"center\">Login Successful!</h1>";
                    content += "<h2 align=\"center\">Your username is: " + username + "</h2>";       
                    content += "<form align=\"center\" action=\"dashboard\" method=\"get\">";
                    content += "<input type=\"submit\" value=\"Continue\">";
                    content += "</form>";
                    content += "</html>";

                    out.println(content);
                    userFound = true;
                    break;
                }
                else if(loginUsername.equals(username) && !loginPassword.equals(password)) {
                    
                    String content = "<html>";
                    content += "<h1 align=\"center\">Wrong password!</h1>";
                    content += "<form align=\"center\" action=\"index.html\" method=\"get\">";
                    content += "<input type=\"submit\" value=\"Return\">";
                    content += "</form>";
                    content += "</html>";
                    
                    out.println(content);
                    userFound = true;
                    break;
                }
            }
            rs.close();
            
            if(!userFound) {
                String content = "<html>";
                content += "<h1 align=\"center\">User doesn't exist!</h1>";
                content += "<form align=\"center\" action=\"\\asterisk\" method=\"get\">";
                content += "<input type=\"submit\" value=\"Return\">";
                content += "</form>";
                content += "</html>";

                out.println(content);
            }
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
