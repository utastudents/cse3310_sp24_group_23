package uta.group23.wurdle.models;

import java.util.ArrayList;

public class MessageBoard{

  private ArrayList<Message> messages = new ArrayList<>();

  public void addMessage(Player sender, String message, ChatScope scope) {
    //Message message = new Message(sender, message, scope);  //figuring out the array list thing but conflict 
    //messages.add(message);  //
    //notifyListeners();
  }

  //Trying to make sense of this to see if we can filter Global vs Local 

  // public ArrayList<Message> getMessages(ChatScope scope) {
      // ArrayList<Message> filteredMessages = new ArrayList<>();
      // for (Message message : messages) {
      //   if(Message.getScope() == 'Local') {
      //     filteredMessages.add(message);
      //   }
      // }
      // return filteredMessages;
  // }
      
  //Filter then print?

  public void displayMessage(String user, String[]message){  

   }
}
