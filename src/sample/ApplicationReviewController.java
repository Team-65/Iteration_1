package sample;


import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sun.java2d.pipe.AlphaPaintPipe;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ApplicationReviewController {
    @FXML
    Button approve;
    @FXML
    Button reject;
    @FXML
    Button goBack;
    @FXML
    TextField repID;
    @FXML
    TextField registryNo;
    @FXML
    TextField prodSource;
    @FXML
    TextField prodType;
    @FXML
    TextField address;
    @FXML
    TextField phoneNo;
    @FXML
    TextField email;
    @FXML
    TextField dateApp;
    @FXML
    TextField nameApp;
    @FXML
    TextArea commentsField;

    Connection conn = connect();

    @FXML
    void setGoBack(ActionEvent event){
        ScreenUtil work = new ScreenUtil();
        work.pullUpScreen("WorkFlow.fxml", "Main Menu", event);
    }

    @FXML

    //TODO: Fix setApprove - needs correct query fields for sql
    /**
     * Sets an Application status to "APPROVED" and adds comments to the Application.
     */
    void setApprove() throws SQLException{
        Statement stm;
        stm = conn.createStatement();
        //get comments
        String comments = commentsField.getText();
        //update alcohol status
        String sql = "UPDATE ALCOHOL SET status = 'approved', comments = 'comments'  WHERE id = apptoassgn";
        stm.executeUpdate(sql);
        //update inbox for account
        sql = "UPDATE REVIEWS SET w.inbox.remove(apptoassgn) WHERE username = w.username";
        stm.executeUpdate(sql);
    }

    @FXML

    //TODO: fix setReject - needs correct query fields for sql
    /**
     * Sets an Application status to "REJECTED" and adds comments to the Application.
     */
    void setReject() throws SQLException{
        Statement stm;
        stm = conn.createStatement();
        //get comments
        String comments = commentsField.getText();
        //update alcohol status
        String sql = "UPDATE ALCOHOL SET status = 'REJECTED', comments = comments WHERE id = apptoassgn";
        stm.executeUpdate(sql);
        //update inbox for worker
        sql = "UPDATE REVIEWS SET w.inbox.remove(apptoassgn) WHERE username = w.username";
        stm.executeUpdate(sql);
    }

    /**
     * Gets a list of all Applications that have the status "UNASSIGNED".
     * @return Returns an ArrayList of the IDs of the unassigned Applications. The IDs are represented
     * by Strings.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static ArrayList<String> getUnassigForms() throws ClassNotFoundException, SQLException {
        Connection conn = connect();
        Statement stm;
        stm = conn.createStatement();
        String sql = "SELECT * FROM ALCOHOL WHERE ALCOHOL.STATUS = 'Unassigned'"; // Use Select _ from _ Where _ format and set this statement = sql
        ArrayList<String> unassforms = new ArrayList<>();
        ResultSet unassAlc = stm.executeQuery(sql);
        ResultSetMetaData rsmd = unassAlc.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while(unassAlc.next()){
            int i = 1;
            while(i <= columnCount){
                unassforms.add(unassAlc.getString("id"));
            }
        }
        return unassforms;
    }

    /**
     * Finds the government account in the database with the least number of applications in its
     * inbox.
     * @return Returns the government Account with the smallest number of applications in its
     * inbox.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    Account getSmallWorker() throws ClassNotFoundException, SQLException{//TODO: find out fields + name for govt. worker
        Statement stm;
        stm = conn.createStatement();
        String sql = "SELECT AID.MIN(CNT(FID)) FROM REVIEWS";
        ResultSet smallWorker = stm.executeQuery(sql);
        Account worker = new Account(smallWorker.getString("id"), 0,
                ArrayToArrayList((String[]) smallWorker.getArray("inbox").getArray()));
        return worker;
    }

    /**
     * Converts an Array object to an ArrayList object. The datatype stored is String.
     * @param input The Array of Strings to be converted to an ArrayList.
     * @return Returns an ArrayList of Strings.
     */
    ArrayList<String> ArrayToArrayList(String[] input){
        ArrayList<String> returnThing = new ArrayList<String>();
        for(int i=0; i<input.length; i++){
            returnThing.add(input[i]);
        }
        return returnThing;
    }

    /**
     * Takes a government account and an application ID and assigns the application to
     * the specified account by adding the ID string to the account's inbox.
     *
     * @param w Government account that the application will be assigned to.
     * @param apptoassgn String representing the ID of the application to be assigned.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    void addToInbox(Account w, String apptoassgn) throws ClassNotFoundException, SQLException{
        Statement stm;
        stm = conn.createStatement();
        //update alcohol status
        String sql = "UPDATE ALCOHOL SET status = 'assigned' WHERE id = apptoassgn";
        stm.executeUpdate(sql);
        //update inbox for worker
        sql = "UPDATE REVIEWS SET inbox.add(apptoassgn) WHERE id = w.id";
        stm.executeUpdate(sql);
    }

    /**
     * Gets a list of unassigned forms then assigns them to government account inboxes.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    //adds all the unassigned forms to workers inboxes
    void addAllUnassigned() throws ClassNotFoundException, SQLException{
        ArrayList<String> unassigForms = getUnassigForms();
        for (int i = 0; i < unassigForms.size(); i++) {
            addToInbox(getSmallWorker(), unassigForms.get(i));
        }
    }

    /**
     * Establishes a connection to the database.
     * @return Returns a Connection object.
     */
    public static Connection connect(){
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Java DB Driver not found. Add the classpath to your module.");
            e.printStackTrace();
            return null;
        }

        System.out.println("Java DB driver registered!");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:derby:ProjectC;create=true");
        } catch (SQLException e) {
            System.out.println("Connection failed. Check output console.");
            e.printStackTrace();
            return connection;
        }
        System.out.println("Java DB connection established!");

        return connection;
    }
}
