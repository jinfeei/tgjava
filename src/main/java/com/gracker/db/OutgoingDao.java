package com.gracker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JinFeei on 7/17/2014.
 */
public class OutgoingDao {
    Connection connection = null;
    PreparedStatement psmt = null;
    ResultSet resultSet = null;
    public OutgoingDao() {
    }

    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = DbConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public List<Outgoing> getPendingMessage()
    {
        List<Outgoing> outgoings = new LinkedList<Outgoing>();
        try {
            String query = "SELECT * FROM outgoing WHERE msg_status = 0";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            resultSet = psmt.executeQuery();
            Outgoing outgoing = null;
            while (resultSet.next()) {
                outgoing = new Outgoing();
                outgoing.setMsg_id(resultSet.getInt("msg_id"));
                outgoing.setStatus(resultSet.getInt("msg_status"));
                outgoing.setPhone(resultSet.getString("msg_phone"));
                outgoing.setMessage(resultSet.getString("msg_text"));

                outgoings.add(outgoing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
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
        return outgoings;
    }

    public void setErrorMessage(int msg_id)
    {
        updateMessage(msg_id, 4);
    }

    public void setDoneMessage(int msg_id)
    {
        updateMessage(msg_id, 2);
    }

    public void setProcessingMessage(int msg_id)
    {
        updateMessage(msg_id, 1);
    }

    public void insertMessage(String phone, String message) {
        try {
            String query = "INSERT outgoing (msg_phone, msg_text) VALUES (?, ?)";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setString(1, phone);
            psmt.setString(2, message);
            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (psmt != null)
                    psmt.close();
                if (connection != null)
                    connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMessage(int msg_id, int status)
    {
        try {
            String query = "UPDATE outgoing SET msg_status=? WHERE msg_id=?";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, status);
            psmt.setInt(2, msg_id);
            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (psmt != null)
                    psmt.close();
                if (connection != null)
                    connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

