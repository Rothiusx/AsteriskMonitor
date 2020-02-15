/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysql;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author rothi
 */
public class DatabaseConnection {
    private DataSource dataSource;
    
    public DatabaseConnection(String servletName, String databaseName) {
        try {
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println("Servlet " + servletName + " is initialized");
            
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/" + databaseName);
            
            System.out.println("DataSource: " + dataSource);
            System.out.println("----------------------------------------------------------------------------------------------------");
        }
        catch(NamingException e) {
            System.out.println(e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
