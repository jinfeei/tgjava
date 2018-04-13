package com.gracker.telegram;

import com.gracker.db.Incoming;
import com.gracker.db.IncomingDao;
import com.gracker.db.OutgoingDao;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lohjinfeei on 13/04/2018.
 */
public class Console implements Runnable {

    private boolean started;
    private BufferedReader tgin;

    public static Process proc;
    public Console()
    {
        started = false;
        try {
            String tg_cmd = "/usr/bin/telegram-cli -k /etc/telegram-cli/tg.pub -P 4458 -W --json --accept-any-tcp --disable-readline";

            proc = Runtime.getRuntime().exec(tg_cmd);
            System.out.println("telegram-cli started: " + tg_cmd);

            tgin = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            started = true;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    private void doJson(String s){
        JSONObject object = null;

        Object obj = new JSONTokener(s).nextValue();
        if (obj instanceof JSONObject)
        {
            object = (JSONObject) obj;
        }
        else if (obj instanceof JSONArray)
        {
            if (((JSONArray) obj).length() == 0){
                return;
            }

            object = ((JSONArray) obj).getJSONObject(0);
        }

        JSONObject jsonFrom = object.getJSONObject("from");
        JSONObject jsonTo = object.getJSONObject("to");

        int msg_id = object.getInt("id");
        int fid = jsonFrom.getInt("id");
        String fphone = jsonFrom.getString("phone");

        int tid = jsonTo.getInt("id");
        String ttype = jsonTo.getString("type");
        String tphone;

        String message = object.getString("text");

        if (ttype.equals("chat")) {
            tphone = jsonTo.getString("title");
            System.out.println("*** CHAT fr:" + fphone + " to:" + tphone + " id:" + tid + " msg:" + message);

            if (message.trim().toLowerCase().startsWith("iden")) {
                System.out.println("IDEN received from: " + fphone);
                OutgoingDao odao = new OutgoingDao();
                //odao.insertMessage(fphone, "Group ID: " + tid + " Group Title: " + tphone);
                odao.insertMessage("g" + tid, "Group ID:" + tid + " Group Title:" + tphone + " (Requested by:" + fphone + ")");
            }
        } else {
            tphone = jsonTo.getString("phone");
            System.out.println("*** MSG  fr:" + fphone + " to:" + tphone + " id:" + tid + " msg:" + message);

            if (message.trim().toLowerCase().startsWith("iden")) {
                System.out.println("IDEN received from: " + fphone);
                OutgoingDao odao = new OutgoingDao();
                odao.insertMessage(fphone, "IDEN: your ID: " + fid + " my ID: " + tid);
            }
        }
        Incoming msg = new Incoming(tid, msg_id, fphone, message);
        IncomingDao idao = new IncomingDao();
        idao.insert(msg);
    }

    @Override
    public void run() {
        boolean workdone = false;
        if (!started) {
            System.out.println("Cannot run because Console object not created.");
            return;
        } else {
            System.out.println("Console loop started.");
        }

        while (true) {
            workdone = false;

            try {
                String s = null;
                if (tgin.ready() && (s = tgin.readLine()) != null) {
                    workdone = true;

                    if (s.startsWith("{") || s.startsWith("[{")) {
                        System.out.println("json: " + s);
                        doJson(s);
                    } else {
                        System.out.println("console: " + s);
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Exception in Console loop...");
                e.printStackTrace();
                break;
            }

            try {
                if (!workdone) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) { e.printStackTrace();}
        }
    }
}
