/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rothi
 */
public class SqlHelper {
    private final Connection connection;
    private PreparedStatement ps;

    public SqlHelper(Connection connection) {
        this.connection = connection;
    }
    
    public ResultSet getResultSet(String query) throws SQLException {
        ResultSet rs = null;
        
        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        return rs;
    }

    public PreparedStatement getPreparedStatement() {
        return ps;
    }
}