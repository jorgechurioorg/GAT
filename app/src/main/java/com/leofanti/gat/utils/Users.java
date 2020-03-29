package com.leofanti.gat.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leofanti.gat.model.UserPin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Users {
    private static final Users ourInstance = new Users();

    public static Users getInstance() {
        return ourInstance;
    }

    private Users() {
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbUser =  database.getReference("users");
    private Map<String, UserPin> usersList = new HashMap<>();
    private ArrayList<UserPin> canalArrayList = new ArrayList<>();
    private ArrayList<UserPin> userArrayList = new ArrayList<>();
    private UserPin currentUser = new UserPin();

    public void setUsersList(final String installMode, final DbGetDataListener<ArrayList<UserPin>> listener ) {

        listener.onStart();

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                canalArrayList.clear();
                userArrayList.clear();
                for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    UserPin userPin = userSnap.getValue(UserPin.class);
                    if( userPin.getRole().equalsIgnoreCase("CANAL") ) {
                        canalArrayList.add(userPin);
                    } else {
                        userPin.setThisKey(userSnap.getKey());
                        usersList.put( userSnap.getKey(), userPin);
                        userArrayList.add(userPin);
                        if( userPin.getRole().equalsIgnoreCase("ROOT")){
                            canalArrayList.add(userPin);
                        }
                    }
                }
               listener.onSuccess(userArrayList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCHUSERS", "Users cancelled error :" + databaseError.toException());
                listener.onFailed(databaseError);
            }

        });
    }

    public Map<String, UserPin> getUsersList() {
        return this.usersList;
    }

    public ArrayList<UserPin> getUsersAsList() {
        ArrayList<UserPin> userPinList = new ArrayList<>();
        for (Map.Entry<String,UserPin> entry : usersList.entrySet()) {
            UserPin userPin = new UserPin();
            String key = entry.getKey();
            userPin.setThisKey(key);
            userPinList.add(userPin);
        }
        return userPinList;

    }
    public void setCurrentUser(String userKey){
        currentUser = usersList.get(userKey);
    }

    public void destroyCurrentUSer(){
        if( currentUser != null ){
            currentUser = null ;
        }
    }

}
