package com.gracker.db;
import java.sql.*;
import java.util.List;
import java.util.LinkedList;

/**
 * Created by JinFeei on 7/15/2014.
 */
public class ContactDao {
    Connection connection = null;
    PreparedStatement psmt = null;
    ResultSet resultSet = null;
    public ContactDao() {
    }
    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = DbConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public void insert(Contact contact) {
        try {
            String query = "INSERT INTO contacts(user_id, phone, nickname) VALUES(?,?,?)";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, contact.getUser_id());
            psmt.setString(2, contact.getPhone());
            psmt.setString(3, contact.getNickname());
            psmt.executeUpdate();
            System.out.println("Data inserted Successfully");
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
    }

    public void updateUserId(String phone, int user_id) {
        try {
            String query = "UPDATE contacts SET user_id=? WHERE phone=?";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, user_id);
            psmt.setString(2, phone);
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

    public void update(Contact contact) {
        try {
            String query = "UPDATE contacts SET user_id=?, phone=?, nickname=?  WHERE contact_id=?";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, contact.getUser_id());
            psmt.setString(2, contact.getPhone());
            psmt.setString(3, contact.getPhone());
            psmt.setInt(4, contact.getContact_id());
            psmt.executeUpdate();
            System.out.println("Data Updated Successfully");
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

    public void updateContact(int user_id, String phone)
    {
        int _user_id = findUserIdByPhone(phone);
        if (_user_id == user_id)
            return;

        if (_user_id == -1) {
            Contact contact = new Contact(0, user_id, phone, phone);
            this.insert(contact);
            return;
        }

        updateUserId(phone, user_id);
    }

    public int findUserIdByPhone(String phone) {
        try {
            String query = "SELECT * FROM contacts WHERE phone=?";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setString(1, phone);
            resultSet = psmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
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
        return -1;
    }

    public String findPhoneByUserId(int user_id) {
        try {
            String query = "SELECT * FROM contacts WHERE  user_id=?";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, user_id);
            resultSet = psmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("phone");
            }
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
        return null;
    }

    public void cleanContacts()
    {
        try {
            String query = "DELETE FROM contacts WHERE user_id=0";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    public void delete(int contact_id) {
        try {
            String query = "DELETE FROM contacts WHERE contact_id=?";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            psmt.setInt(1, contact_id);
            psmt.executeUpdate();
            System.out.println("Data deleted Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    public List<Contact> selectNew()
    {
        List<Contact> contacts = new LinkedList<Contact>();
        try {
            String query = "SELECT * FROM contacts WHERE user_id = 0";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            resultSet = psmt.executeQuery();
            Contact contact = null;
            while (resultSet.next()) {
                contact = new Contact();
                contact.setContact_id(resultSet.getInt("contact_id"));
                contact.setUser_id(resultSet.getInt("user_id"));
                contact.setPhone(resultSet.getString("phone"));
                contact.setNickname(resultSet.getString("nickname"));
                contacts.add(contact);
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
        return contacts;
    }

    public void dataShow() {
        try {
            String query = "SELECT * FROM contacts";
            connection = getConnection();
            psmt = connection.prepareStatement(query);
            resultSet = psmt.executeQuery();
            while (resultSet.next()) {
                System.out.println("User ID " + resultSet.getInt("user_id")
                        + ", Phone " + resultSet.getString("phone") + ", Nickname "
                        + resultSet.getString("nickname"));
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
    }
}
