package idig.za.net.firebasetutorial;

/**
 * Created by clive on 08-Jun-16.
 */
public class Friend {


    private int telephoneNumber;
    private String friendName;

    public Friend(int telephoneNumber, String friendName) {
        this.telephoneNumber = telephoneNumber;
        this.friendName = friendName;
    }

    public Friend() {
        /*default, no arguments constructor*/
    }

    public int getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setTelephoneNumber(int telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
