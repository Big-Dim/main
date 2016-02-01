
package com.saturn.testsaturn;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBconn_s
{

    public DBconn_s(String prgName)
    {
        v = new Vector();
        arrRSMD = new ArrayList<ResultSetMetaData>();
        set = 0;
        rows = 0;
        cols = 0;
        setV = new Vector();
        this.prgName = "";
        inParms = "";
        maxLines = 10;
        this.prgName = prgName;
    }

    public DBconn_s(String prgName, String inParms)
    {
        v = new Vector();
        arrRSMD = new ArrayList<ResultSetMetaData>();
        set = 0;
        rows = 0;
        cols = 0;
        setV = new Vector();
        this.prgName = "";
        this.inParms = "";
        maxLines = 10;
        this.prgName = prgName;
        this.inParms = inParms;
    }

    public DBconn_s(String prgName, String inParms, int maxLines)
    {
        v = new Vector();
        arrRSMD = new ArrayList<ResultSetMetaData>();
        set = 0;
        rows = 0;
        cols = 0;
        setV = new Vector();
        this.prgName = "";
        this.inParms = "";
        this.prgName = prgName;
        this.inParms = inParms;
        this.maxLines = maxLines;
    }

    public int set(int set)
    {
        setV = new Vector();
        if(set > v.size())
            return 0;
        setV = (Vector)v.elementAt(set);
        rows = setV.size();
        if(rows > 0)
        {
            Vector rw = (Vector)setV.elementAt(0);
            this.set = set;
            rows = setV.size();
            cols = rw.size();
        }
        return rows;
    }

    public int colsize()
    {
        return cols;
    }

    public int rowsize()
    {
        return rows;
    }

    public String getString(int row, int col)
    {
        String ret = "";
        if(row > rows || col > cols)
            return "";
        try
        {
            Vector rw = (Vector)setV.elementAt(row);
            ret = (String)rw.elementAt(col);
        }
        catch(Exception e)
        {
            return ret;
        }
        if(ret == null)
            ret = "";
        return ret;
    }

    public void getOutput() throws SQLException, NamingException        
    {
        v.clear();
        arrRSMD.clear();

        DataSource ds = null;
        Connection conn;
        Statement s;
      
        Context initContext = new InitialContext();
        ds = (DataSource) initContext.lookup("jdbc/myD1");
        conn = ds.getConnection();
        s = conn.createStatement();
        String proc = "call " + prgName + "(";
        System.out.println("Connection to db. ---- " + s.toString());

        StringTokenizer st = new StringTokenizer(inParms, "\t");
        for(String comm = "\""; st.hasMoreElements(); comm = ",\"")
        {
            String c = st.nextToken();
            proc = proc + comm + c + "\"";
        }

        proc = proc + ")";

        
        boolean isResultSet = s.execute(proc);
        if(!isResultSet)
        {
            out = "No result";
            System.out.println("The first result is not a ResultSet.");
            return;
        }


            ResultSet rs = s.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            v.add(getRes(rs, 0));
            arrRSMD.add(rsmd);
            for(; s.getMoreResults(); getRes(rs, 1)){
                rs = s.getResultSet();
                v.add(getRes(rs,1));
                arrRSMD.add(rs.getMetaData());
            }
            rs.close();
            s.close();
            conn.close();

        
        return;
    }



    private Vector getRes(ResultSet rs, int mode) throws SQLException
    {
        int tp;
        String s;
        Vector ret = new Vector();       
        
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            Vector newRow;
            for(; rs.next(); ret.add(newRow))
            {
                if(mode == 0)
                {
                    String newRow1 = "";
                    for(int i = 1; i <= cols; i++)
                    {
                        if(i > 1)
                            newRow1 = newRow1 + "\t";
                        newRow1 = newRow1 + rs.getString(i);
                    }

                    out = newRow1;
                }
                newRow = new Vector();
                for(int i = 1; i <= cols; i++)
                {
                    
                    try{
                        s = rs.getString(i);
                    }catch(SQLException e){
                        s="null";
                    }
                    newRow.add(s);
                }

            }

        

        
        
        return ret;
    }


    public String out;
    public Vector v;
    public ArrayList <ResultSetMetaData> arrRSMD;
    private int set;
    private int rows;
    private int cols;
    private Vector setV;
    private String prgName;
    private String inParms;
    private int maxLines;

   
}