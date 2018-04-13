package com.gracker.telegram;

import com.gracker.db.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by lohjinfeei on 06/04/2018.
 *
 * Reference for JSON: https://www.tutorialspoint.com/json/json_java_example.htm
 *
 */

public class Main {

    private static PrintWriter out;
    private static BufferedReader in;
    private static String ADMIN_PHONE;

    public static void main(String[] args) throws IOException, InterruptedException{

        System.out.println("================================================");
        System.out.println("    Telegram Client Ver 1.03 (12 April 2018)");
        System.out.println("================================================");

        System.out.println("MYSQL_HOST : " + System.getenv("MYSQL_HOST"));
        System.out.println("MYSQL_USER : " + System.getenv("MYSQL_USER"));
        System.out.println("MYSQL_PASSWORD : " + System.getenv("MYSQL_PASSWORD"));

        System.out.println();
        System.out.println("TG_HOST : " + System.getenv("TG_HOST"));
        System.out.println();
        System.out.println("ADMIN_PHONE : " + System.getenv("ADMIN_PHONE"));
        System.out.println();
        System.out.println("================================================");

        //Docker localhost =  "172.17.0.1"
        String TG_HOST = System.getenv("TG_HOST") == null ? "127.0.0.1" : System.getenv("TG_HOST");
        ADMIN_PHONE = System.getenv("ADMIN_PHONE");

        new Thread(new Console()).start();

        Thread.sleep(3000);

        InetAddress host = InetAddress.getByName(TG_HOST);
        Socket socket;

        try {
            socket = new Socket(host.getHostName(), 4458);
        } catch (IOException e) {
            System.out.println("Cannot connect to port 4458 of telegram-cli. Exiting...");
            return;
        }

        System.out.println("Connected to " + TG_HOST + ":4458");
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        try {
            workLoop();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in workLoop()");
        }

        out.close();
        in.close();
        socket.close();

        return;
    }

    private static void workLoop() throws IOException{
        OutgoingDao odao = new OutgoingDao();
        ContactDao cdao = new ContactDao();

        while (true) {
            List<Outgoing> messages = odao.getPendingMessage();
            for (Outgoing m : messages) {
                String tphone = m.getPhone().trim();

                if (Pattern.matches("g[0-9]+", tphone))
                {
                    tphone = tphone.substring(1);
                    int chat_id = Integer.parseInt(tphone);
                    System.out.println("Chat: " + tphone + " > " + m.getMessage());
                    sendMessageChatEx(chat_id, m.getMessage());
                    odao.setDoneMessage(m.getMsg_id());
                    System.out.println("-----");
                }
                else if (!Pattern.matches("[0-9]+", tphone)) {
                    System.out.println("Invalid Phone: " + tphone);
                    odao.setErrorMessage(m.getMsg_id());
                    System.out.println("-----");
                }
                else if(sendMessagePhone(tphone, m.getMessage()))
                {
                    System.out.println("Msg: " + tphone + " > " + m.getMessage());
                    odao.setDoneMessage(m.getMsg_id());
                    System.out.println("-----");
                } else {
                    System.out.println("Msg unknown to : " + tphone + m.getMessage());
                    odao.setErrorMessage(m.getMsg_id());
                    cdao.cleanContacts();
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private static boolean sendMessagePhone(String phone, String message)
    {
        ContactDao dao = new ContactDao();
        int id = dao.findUserIdByPhone(phone);
        if (id <= 0)
        {
            id = addNewContact(phone);
            if (id <= 0) {
                System.out.println("Error Sending Message to: " + phone);
                return false;
            }
        }

        int res = sendMessageUserEx(id, message);
        if (res == 3) { // Retry sending to phone because contact id not in contact list.
            id = addNewContact(phone);
            if (id <= 0) {
                System.out.println("Error Sending Message to: " + phone);
                return false;
            }
            res = sendMessageUserEx(id, message);
        }

        if (res == 0)
            return true;
        else
            return false;
    }

    private static int addNewContact(String phone){
        out.println("add_contact " + phone + " " + phone + " M");
        out.flush();
        String s = null;

        try {
            for (int i = 0; i < 6; i++) {
                while (in.ready() && (s = in.readLine()) != null) {
                    if (s.startsWith("[]")) {
                        System.out.println("--invalid phone: " + phone);
                        return 0;
                    }
                    else if (s.startsWith("[{") || s.startsWith("{"))
                    {
                        System.out.println("json: " + s);

                        JSONObject object = null;

                        Object obj = new JSONTokener(s).nextValue();
                        if (obj instanceof JSONObject)
                        {
                            object = (JSONObject) obj;
                        }
                        else if (obj instanceof JSONArray)
                        {
                            if (((JSONArray) obj).length() == 0){
                                System.out.println("--invalid phone: " + phone);
                                return 0;
                            }

                            object = ((JSONArray) obj).getJSONObject(0);
                        }

                        if (object == null) {
                            System.out.println("--unknown error: " + phone);
                            return 0;
                        }

                        String jphone = object.getString("phone");
                        int jid = object.getInt("id");

                        System.out.println("TG - phone: " + phone + " id:" + jid);
                        ContactDao dao = new ContactDao();
                        dao.updateContact(jid, jphone);

                        return jid;
                    } else {
                        System.out.println("rx: " + s);
                    }
                }
                Thread.sleep(1000);
            }

            System.out.println("--timeout: " + phone);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("--exception: " + phone);
        }
        return 0;
    }

    private static int sendMessageCommon(String peer_cmd) {
        System.out.println("SendMessageCommon: " + peer_cmd);
        out.println(peer_cmd);
        out.flush();
        String s = null;

        //{"result": "FAIL", "error_code": 71, "error": "RPC_CALL_FAIL 400: PEER_ID_INVALID"}
        //{"result": "SUCCESS"}
        try {
            for (int i = 0; i < 3; i++) {
                while (in.ready() && (s = in.readLine()) != null) {
                    if (s.startsWith("[{") || s.startsWith("{"))
                    {
                        System.out.println("json: " + s);
                        JSONObject object = null;

                        Object obj = new JSONTokener(s).nextValue();
                        if (obj instanceof JSONObject)
                        {
                            object = (JSONObject) obj;
                        }
                        else if (obj instanceof JSONArray)
                        {
                            if (((JSONArray) obj).length() == 0){
                                System.out.println("--unknown error");
                                return 1;
                            }

                            object = ((JSONArray) obj).getJSONObject(0);
                        }

                        if (object == null) {
                            System.out.println("--unknown error");
                            return 2;
                        }

                        String result = object.getString("result");

                        if (result.equals("SUCCESS"))
                            return 0;
                        else if (result.equals("FAIL")) {
                            String error = object.getString("error");
                            System.out.println("Send Error: " + error);

                            if (error.contains("PEER_ID_INVALID")){
                                return 3;
                            }
                            return 4;
                        }
                    } else {
                        System.out.println("rx: " + s);
                    }
                }
                Thread.sleep(1000);
            }

            System.out.println("--timeout");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("--exception");
        }
        return 5;
    }

    private static int sendMessageUserEx(int uid, String message) {
        String peer_cmd = "msg user#" + uid + " " + message;
        return sendMessageCommon(peer_cmd);
    }

    private static boolean sendMessageChatEx(int chatId, String message) {
        String peer_cmd = "msg chat#" + chatId + " " + message;
        int res = sendMessageCommon(peer_cmd);
        if (res == 0)
            return true;

        if (res == 3) {
            if (ADMIN_PHONE != null)
                sendMessagePhone(ADMIN_PHONE, "Error: Group id not registered. Please add telegram-cli phone number to group.");
        }
        return false;
    }
}
