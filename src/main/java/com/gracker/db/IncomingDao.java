package com.gracker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by JinFeei on 7/17/2014.
 */
public class IncomingDao {
    Connection connection = null;
    PreparedStatement psmt = null;
    ResultSet resultSet = null;
    public IncomingDao() {
    }

    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = DbConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public boolean insert(Incoming incoming) {
        try {
            String query = "INSERT INTO incoming(msg_id, msg_phone, msg_text, msg_rid) VALUES(?,?,?,?)";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, incoming.getMsg_id());
            psmt.setString(2, incoming.getPhone());
            psmt.setString(3, incoming.getMessage());
            psmt.setInt(4, incoming.getMsg_rid());
            psmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (psmt != null)
                    psmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
