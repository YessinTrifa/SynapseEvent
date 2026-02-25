package com.synapseevent;

import com.synapseevent.utils.MaConnection;
import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        try {
            MaConnection db = MaConnection.getInstance();

            System.out.println("Is connected? " + db.isConnected());

            Connection conn = db.requireConnection();

            if (conn != null) {
                System.out.println("Connection SUCCESSFUL 🎉");
            }

        } catch (Exception e) {
            System.out.println("Connection FAILED ❌");
            e.printStackTrace();
        }
    }
}