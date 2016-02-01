
// Source File Name:   Cli2.java

package com.saturn.testsaturn;

import java.io.*;
import java.sql.*;
import java.util.StringTokenizer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBconn
{

    public DBconn(String prgName)
    {
        inParms = "";
        maxLines = 10;
        this.prgName = prgName;
    }

    public DBconn(String prgName, String inParms)
    {
        maxLines = 10;
        this.prgName = prgName;
        this.inParms = inParms;
    }

  
    public DBconn(String prgName, String inParms, int maxLines)
    {
        this.prgName = prgName;
        this.inParms = inParms;
        this.maxLines = maxLines;
    }

    public String[] getOutput()
        throws SQLException, IOException
    {
        String output[] = new String[maxLines];
        int mycount;
        DataSource ds;
        
        try {
            Context initContext = new InitialContext();
            ds = (DataSource) initContext.lookup("jdbc/myD1");
            Connection conn = ds.getConnection();
            Statement s = conn.createStatement();
 
            String proc = "call " + prgName + "(";
            StringTokenizer st = new StringTokenizer(inParms, "\t");
            for (String comm = "\""; st.hasMoreElements(); comm = ",\"") {
                String c = st.nextToken();
                proc = proc + comm + c + "\"";
            }

            proc = proc + ")";

            ResultSet rs = s.executeQuery(proc);
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            mycount = 0;

            do {
                if (!rs.next()) {
                    break;
                }
                String newRow = "";
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) {
                        newRow = newRow + "\t";
                    }
                    String str = rs.getString(i) == null ? "" : rs.getString(i);
                    if (str.length() == 0) {
                        str = "|";
                    }
                    newRow = newRow + str;
                }

                if (mycount >= maxLines) {
                    output[mycount - 1] = "***** THE PROCESS HAS MORE DATA  *****";
                    break;
                }
                output[mycount++] = newRow;
            } while (true);
            rs.close();
            s.close();
            conn.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(" DB connection error. " + e.getMessage());

        }

        return output;
    }

    private String prgName;
    private String inParms;
    private int maxLines;
}