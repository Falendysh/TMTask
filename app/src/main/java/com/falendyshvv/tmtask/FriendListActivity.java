package com.falendyshvv.tmtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendListActivity extends AppCompatActivity implements ResultCallback<People.LoadPeopleResult>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    public GoogleApiClient mGoogleApiClient = null;

    private static final int RC_SIGN_IN = 0;


    android.support.v4.widget.SwipeRefreshLayout friendSwiper;

    ListView friendListView;

    SocialPersonList myFriends;

    ArrayAdapter<SocialPerson> friendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friendSwiper = (android.support.v4.widget.SwipeRefreshLayout) findViewById(R.id.activity_friend_list_swipe_refresh_layout);
        friendListView = (ListView) findViewById(R.id.activity_friend_listview);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent friendDetailActivityIntent = new Intent(getApplicationContext(),FriendDetailActivity.class);
                friendDetailActivityIntent.putExtra(SocialPersonList.PERSON_NUMBER,i);
                startActivity(friendDetailActivityIntent);
            }
        });

        myFriends = SocialPersonList.getInstance();


        friendsAdapter = new ArrayAdapter<SocialPerson>(getApplicationContext(),R.layout.list_item);
        friendsAdapter.addAll(myFriends.socialPersons);
        friendListView.setAdapter(friendsAdapter);


        Intent snIntent = getIntent();

        myFriends.setNetwork(snIntent.getStringExtra(SocialPersonList.NETWORK));

         if (myFriends.getSocialNetworkType().equals(SocialPersonList.GOOGLE)){

             mGoogleApiClient = new GoogleApiClient.Builder(this)
                     .enableAutoManage(this,this)
                     .addApi(Plus.API)
                     .addScope(Plus.SCOPE_PLUS_LOGIN)
                     .build();
         }

        else
         {
             RefreshFacebookFriends();
         }


        friendSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (myFriends.getSocialNetworkType().equals(SocialPersonList.FACEBOOK)){

                    Toast.makeText(getApplicationContext(),"start refreshing from facebook",Toast.LENGTH_SHORT).show();
                    RefreshFacebookFriends();


            }

                else
                {
                    if (myFriends.getSocialNetworkType().equals(SocialPersonList.GOOGLE)){
                    RefreshGoogleFriends();
                }

                }

            }



        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onStart() {
        super.onStart();

        myFriends.setNetwork(getIntent().getStringExtra(SocialPersonList.NETWORK));

        if (myFriends.getSocialNetworkType().equals(SocialPersonList.GOOGLE)){
            friendSwiper.setRefreshing(true);
            RefreshGoogleFriends();
        }
        else if(myFriends.getSocialNetworkType().equals(SocialPersonList.FACEBOOK)){
            RefreshFacebookFriends();
        }

    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) { // Google+ request result
        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {

           PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            myFriends.socialPersons.clear();
            try {
                int count = personBuffer.getCount();

                notifyWhenNoFriendAvailable(count);

                for (int i = 0; i < count; i++) {
                    Person person = personBuffer.get(i);
                    String name = person.getDisplayName();
                    String information =  person.getId();

                    myFriends.socialPersons.add(new SocialPerson(name,information));

                }

                friendsAdapter.clear();
                friendsAdapter.addAll(myFriends.socialPersons);

                friendsAdapter.notifyDataSetChanged();
                friendSwiper.setRefreshing(false);

            } finally {
                personBuffer.release();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Error requesting visible circles: " + loadPeopleResult.getStatus(), Toast.LENGTH_SHORT).show();
            friendSwiper.setRefreshing(false);
        }




    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getApplicationContext(),"Connected to Google+",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Unfortunately, connection to Google+ failed",Toast.LENGTH_SHORT).show();
    }

    void RefreshGoogleFriends(){
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }

    void RefreshFacebookFriends(){

        GraphRequest friendListRequest = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {

                myFriends.socialPersons.clear();

                notifyWhenNoFriendAvailable(objects.length());

                for (int i = 0; i < objects.length(); i++) {

                    Integer l = new Integer(objects.length());
                    Log.i("tag_FACEBOOK", l.toString());

                    try {
                        JSONObject friendJSON = objects.getJSONObject(i);
                        SocialPerson sp = new SocialPerson(friendJSON.optString("name"), friendJSON.optString("id"));
                        myFriends.socialPersons.add(sp);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    friendsAdapter.clear();
                    friendsAdapter.addAll(myFriends.socialPersons);
                    friendsAdapter.notifyDataSetChanged();
                    friendSwiper.setRefreshing(false);
                }
            }
        });

        friendListRequest.executeAsync();
    }

    public void notifyWhenNoFriendAvailable(int count){
        if (count<1) {Toast.makeText(getApplicationContext(),"Sorry. No friends available",Toast.LENGTH_SHORT).show();}
    }


}
