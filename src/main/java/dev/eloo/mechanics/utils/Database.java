package dev.eloo.mechanics.utils;

import dev.eloo.mechanics.core.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final String HOST;
    private final String PORT;
    private final String USER;
    private final String PASS;
    private final String DATA;
    private Connection con;

    public Database(Settings settings) {
        this.HOST = settings.HOSTNAME;
        this.PORT = settings.PORT;
        this.USER = settings.USERNAME;
        this.PASS = settings.PASSWORD;
        this.DATA = settings.DATABASE;
        this.con = this.connect();
    }

    public Connection getCon() {
        try {
            if(con != null && !con.isClosed()) {
                if(con.isValid(10)) {
                    return con;
                }
            }
            con = connect();
        } catch (SQLException e) {
            con = null;
            e.printStackTrace();
        }
        return con;
    }

    public void close() {
        try {
            if(this.con != null && (!(this.con.isClosed()))) {
                this.con.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Connection connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://"
                    +this.HOST+":"
                    +this.PORT+"/"
                    +this.DATA+"?user="
                    +this.USER+"&password="
                    +this.PASS+"&autoReconnect=true&useSSL=false");
            return con;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
