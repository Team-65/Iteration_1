package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

/**
 * Created by Adonay on 3/27/2017.
 */
public class MainMenuController{

    private FXMLLoader fxmlLoader;

    private String username;


    public void setUsername(String username) {
        this.username = username;

    }

    @FXML private Button openSearchButton, openInboxButton, createNewApplicationButton, logOutButton;
    ScreenUtil work = new ScreenUtil();

    /**
     *This is the code that figures out where the UI will go when certain buttons are clicked. Right now, the only
     * transition that can be seen from this is an instant change and deletion of the different panels.
     * @param event - action that activates the changing menu procedure, i.e. clicking on a button in the menu
     */
    public void buttonClicked(ActionEvent event) {

        if(event.getSource() == openSearchButton){
            work.pullUpScreen("SearchMenu.fxml", "Search", event);

        }else if(event.getSource() == openInboxButton){
            work.pullUpScreen("WorkFlow.fxml", "Inbox", event);

        }else if(event.getSource() == createNewApplicationButton){
            work.pullUpScreen("NewApp.fxml","New Application", event);

        }else if(event.getSource() == logOutButton){
            work.pullUpScreen("Login.fxml","Login", event);

        }else{
            work.pullUpScreen("ErrorState.fxml","Error", event);
        }
    }
}