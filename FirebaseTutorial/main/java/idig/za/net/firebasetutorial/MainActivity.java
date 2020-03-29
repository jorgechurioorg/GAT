package idig.za.net.firebasetutorial;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*created by www.101apps.co.za*/
public class MainActivity extends AppCompatActivity {

    private List<Friend> friendsList;
    private ListView listView;
    private ArrayAdapter adapter;
    private FirebaseDatabase friendsDatabase;
    private DatabaseReference friendsDatabaseReference;
    private ChildEventListener childEventListener;


    private static final String TAG = "tut";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*get the list view*/
        listView = (ListView) findViewById(R.id.listView);

        /*for disk persistence*/
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        /*get instance of our friendsDatabase*/
        friendsDatabase = FirebaseDatabase.getInstance();
        /*get a reference to the friends node location*/
        friendsDatabaseReference = friendsDatabase.getReference("friends");

        /*add the Value event listener to update the data in real-time
        - displays the friendsDatabase items in a list*/
        addValueEventListener(friendsDatabaseReference);

        /*create a child event listener for changes on a child location to which its attached*/
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                Toast.makeText(MainActivity.this
                        , "Found item key: " + dataSnapshot.getKey()
                                + "  name: " + friend.getFriendName()
                                + "  tel: " + friend.getTelephoneNumber()
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                Toast.makeText(MainActivity.this
                        , "Friend changed: " + dataSnapshot.getKey()
                                + "  name: " + friend.getFriendName()
                                + "  tel: " + friend.getTelephoneNumber()
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                Toast.makeText(MainActivity.this
                        , "Friend removed: " + dataSnapshot.getKey()
                                + "  name: " + friend.getFriendName()
                                + "  tel: " + friend.getTelephoneNumber()
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "childEventListener, childEventListener()");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "childEventListener, onCancelled()");
            }
        };
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /*adds the dummy data to the friendsDatabase, overwrites what's already there*/
    private void loadDummyDataHashMap(DatabaseReference friendsReference) {
        friendsReference.setValue(DummyData.getDummyDataAsHashMap());
    }

    /*appends a new friend to the friends node
    * firebase supplies the key - (getKey())*/
    private void appendFriendToExistingFriendsTree(DatabaseReference friendsReference) {
        int number = 818181;
        String name = "Vicky Victoria";
        String key = friendsReference.push().getKey();
        Friend friend = new Friend(number, name);
        friendsReference.child(key).setValue(friend);
    }

    /*replaces the whole friends tree with this new data*/
    private void loadSingleItem(FirebaseDatabase database) {
        int number = 919191;
        String name = "William Wallace";
        String key = "0";
        Friend friend = new Friend();
        friend.setTelephoneNumber(number);
        friend.setFriendName(name);
        HashMap<String, Friend> hashMap = new HashMap<>();
        hashMap.put(key, friend);
        database.getReference("friends").setValue(hashMap);
    }

    /*updates data in realtime, displays the data in a list*/
    private void addValueEventListener(final DatabaseReference friendsReference) {
        /*add ValueEventListener to update data in realtime*/
        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList = new ArrayList<>();
                /*this is called when first passing the data and
                * then whenever the data is updated*/
                   /*get the data children*/
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    /*get the values as a Friend object*/
                    Friend value = iterator.next().getValue(Friend.class);
                    /*add the friend to the list for the adapter*/
                    friendsList.add(value);
                }
                /*set up the adapter*/
                ArrayAdapter adapter = setupAdapter(friendsList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });
    }

    /*update a friend given the id*/
    private void updateFriend(DatabaseReference friendsReference) {
        String name = "John Jones";
        int number = 654321;
        String key = "2";
        friendsReference.child(key).child("friendName").setValue(name);
        friendsReference.child(key).child("telephoneNumber").setValue(number);
    }

    /*update item using updateChildren()*/
    private void updateFriend_updateChildren(DatabaseReference friendsReference) {
        String name = "John Jones";
        int number = 654321;
        String key = "2";
        /*put new friend values in a map*/
        Map<String, Object> newFriend = new HashMap<>();
        newFriend.put("friendName", name);
        newFriend.put("telephoneNumber", number);
        /*creat a new map for the updateChildren() parameter*/
        Map<String, Object> update = new HashMap<>();
        /*put the child key plus the map containing the new friend values into the update map */
        update.put(key, newFriend);

        /*update without CompletionListener*/
        friendsReference.updateChildren(update);

        /*update the item and (optionally) including a CompletionListener
        * - onComplete() triggers and returns error or null if successful*/
       /* friendsReference.updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.i(TAG, "onComplete(), dataBaseError: " + databaseError.getMessage());
                }
            }
        });*/
    }


    /*sets up the list adapter*/
    private ArrayAdapter setupAdapter(final List<Friend> friendsList) {
        /*get the adapter*/
        ArrayAdapter adapter = new ArrayAdapter(this
                , android.R.layout.simple_list_item_2
                , android.R.id.text1, friendsList) {

            /*get the view*/
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(String.valueOf(friendsList.get(position).getTelephoneNumber()));
                text2.setText(friendsList.get(position).getFriendName());
                return view;
            }
        };
        return adapter;
    }

    /*button click - loads the given data into the friendsDatabase*/
    public void LoadDummyData(View view) {
        /*this overwrites everything in the friends tree by inserting hashmap of dummy items*/
        loadDummyDataHashMap(friendsDatabaseReference);
    }

    /*button click - appends an item to the end of the list*/
    public void AddItem(View view) {
        /*adds a new friend to the friends tree*/
        appendFriendToExistingFriendsTree(friendsDatabaseReference);
    }

    /*button click - updates a given item*/
    public void UpdateItem(View view) {
        /*update a friend record given the id*/
        updateFriend(friendsDatabaseReference);
        /*the alternative*/
//        updateFriend_updateChildren(friendsDatabaseReference);
    }

    /*button click - finds a matching item*/
    public void FindItem(View view) {
        String child = "friendName";
        String name = "Peter Pan";
        Query query = friendsDatabaseReference.orderByChild(child).equalTo(name);
        query.addChildEventListener(childEventListener);
    }

    /*button click - deletes a given item*/
    public void DeleteItem(View view) {
        deleteItem(friendsDatabaseReference);
    }

    /*button click - loads a single item by replacing the whole tree*/
    public void LoadSingleItemData(View view) {
        loadSingleItem(friendsDatabase);
    }

    /*delete a given item*/
    private void deleteItem(final DatabaseReference friendsReference) {
        /*look for the matching item*/
        Query deleteQuery = friendsReference.orderByChild("friendName")
                .equalTo("Vicky Victoria");
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                     /*get the data children*/
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    Friend friend = snapshot.getValue(Friend.class);
                        /*does it match what we're looking for?*/
                    if (friend.getFriendName().equals("Vicky Victoria")) {
                            /*we found the item, now get its id*/
                        String snapshotKey = snapshot.getKey();
                            /*now delete it*/
                        friendsReference.child(snapshotKey).removeValue();
                    } else {
                            /*no match*/
                        Log.i(TAG, "deleteItem(), no matching item to delete");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "deleteItem() - onCancelled(), error: "
                        + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://idig.za.net.firebasetutorial/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://idig.za.net.firebasetutorial/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
