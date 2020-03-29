package idig.za.net.firebasetutorial;

import java.util.HashMap;

/**
 * Created by clive on 08-Jun-16.
 */
public class DummyData {

    static int[] numbers = {123456, 654321, 198765, 567890,375291,983276,182503};
    static String[] names = {"Jack Jones", "Mike Malone", "Peter Pan"
            , "Rodney Roper", "Harry Horne", "Casey Clarke", "Bernie Bunting"};

    public static HashMap<String, Friend> getDummyDataAsHashMap() {
        HashMap<String, Friend> friendsHashMap = new HashMap<>();
        for (int i = 0; i < numbers.length; i++) {
            Friend thisFriend = new Friend();
            thisFriend.setTelephoneNumber(numbers[i]);
            thisFriend.setFriendName(names[i]);
            friendsHashMap.put(String.valueOf(i), thisFriend);
        }
        return friendsHashMap;
    }
}
