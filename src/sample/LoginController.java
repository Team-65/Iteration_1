package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.sql.*;
import java.util.Scanner;

/**
 * Controller for Login screen.
 */
public class LoginController {
    @FXML
    private TextField usernameField, passwordField;
    @FXML
    private Label errorBox;
    @FXML
    private Button loginButton;

    ScreenUtil screenUtil = new ScreenUtil();

    private String username;
    private String password;
    private AccountsUtil aUtil = new AccountsUtil();

    private Connection conn = connect();

    @FXML
    /**
     * Initializes the Login screen.
     */
    public void initialize(){
        usernameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    login(new ActionEvent(loginButton, (Node) loginButton));
                }
            }
        });
    }

    /**
     * Logs in a user by checking the username and password, both of which are of type Text.
     * If the username specified is not in the database, the screen displays an error message
     * indicating that the username does not exist and will not log the user in.
     * @param event Function is run when the user clicks the Login button in the User Interface.
     */
    public void login(ActionEvent event){
        username = usernameField.getText();
        password = passwordField.getText();

        try{
             /*if(aUtil.contains(username)){*/
            if(databaseContainsUser(conn)){
                aUtil.setUser_id(username);
                screenUtil.pullUpScreen("MainMenu.fxml", "Main Menu", event);
            }else{
                errorBox.setText("Username does not exist!");
            }
        }catch(SQLException e) {
            errorBox.setText("Database Error");
            e.printStackTrace();
        }
    }

    /**
     * Logs in a guest with limited accessibility to the rest of the application.
     * @param event Triggers when the "Log in as Guest" button is pressed. 
     */
    public void guestLogin(ActionEvent event){
           aUtil.setUser_id("guest");
           screenUtil.pullUpScreen("MainMenu.fxml", "Main Menu", event);
    }

    /**
     * Opens the Create Account screen when the "Create Account" button is pressed
     * in the user interface.
     * @param event ActionEvent object representing the button click.
     */
    public void openCreateAccount(ActionEvent event){

        screenUtil.pullUpScreen("NewAccount.fxml", "New Account", event);

    }

    public void clearData(){
        aUtil.clearData();
    }

    /**
     * Checks if the database contains a user.
     * @param conn Connection representing the connection to the database.
     * @return Returns a boolean that is "true" if the database contains the user, and "false" otherwise.
     * @throws SQLException
     */

    private boolean databaseContainsUser(Connection conn) throws SQLException {
        boolean contains = false;

        ResultSet rset;
        Statement stmt;

            String usernameQuery = "SELECT * FROM ACCOUNT WHERE ACCOUNT.USERNAME = " + "'" + username + "'";
            stmt = conn.createStatement();

            rset = stmt.executeQuery(usernameQuery);

            contains = rset.next();

            rset.close();
            stmt.close();

        return contains;
    }

    /**
     * Function for connecting to the database.
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
            connection = DriverManager.getConnection("jdbc:derby:DATABASE\\ProjectC;create=true");
        } catch (SQLException e) {
            System.out.println("Connection failed. Check output console.");
            e.printStackTrace();
            return connection;
        }
        System.out.println("Java DB connection established!");

        return connection;
    }
}
