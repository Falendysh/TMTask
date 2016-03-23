package com.falendyshvv.tmtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    TextView friendNameTextView = null;
    TextView friendDetailsTextView = null;

    public SocialPersonList socialPersonList;
    public static int personNumber = - 1;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 //       setSupportActionBar(toolbar);

        socialPersonList = SocialPersonList.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        friendDetailsTextView = (TextView) findViewById(R.id.friend_details_textview);
        friendNameTextView = (TextView) findViewById(R.id.friend_name_textview);



    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent detailIntent = getIntent();
        personNumber = detailIntent.getIntExtra(SocialPersonList.PERSON_NUMBER, personNumber);

        if (personNumber > -1) {

            String id = socialPersonList.socialPersons.get(personNumber).information;
            friendNameTextView.setText(socialPersonList.socialPersons.get(personNumber).name);

        if (socialPersonList.getSocialNetworkType().equals(SocialPersonList.FACEBOOK)) {

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+id,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                            JSONObject jsonObject = response.getJSONObject();

                            String about = "";
                            String bio = "";
                            String birthday = "";
                            String email = "";
                            String oid = "";

                                about = jsonObject.optString("about");
                                bio = jsonObject.optString("bio");
                                birthday = jsonObject.optString("birthday");
                                email = jsonObject.optString("email");
                                oid = jsonObject.optString("id");

                            String allInfo = "About : \n" + about + "\n" +
                                    "bio : \n" + bio + "\n" +
                                    "birthday : " + birthday + "\n" +
                                    "id : "+oid+"\n"+
                                    "email : " + email;


                            friendDetailsTextView.setText(allInfo);

                        }
                    }
            ).executeAsync();


        }
            else // if Google+
        {
            List<String> userIds = new ArrayList<String>();
            userIds.add(id);
            Plus.PeopleApi.load(mGoogleApiClient, userIds).setResultCallback(this);
        }

    }

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

        String about = "";
        String birthday = "";
        String language = "";
        String nickName = "";
        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Person person = personBuffer.get(i);
                    about = person.getAboutMe();
                    birthday = person.getBirthday();
                    language = person.getLanguage();
                    nickName = person.getNickname();
                }
            } finally {
                personBuffer.release();
            }
        } else {

            about = "Error obtaining information";

        }

        String allInfo = "About : \n"+about+"\n"+
                "Birthday : "+birthday+"\n"+
                "Language : "+language+"\n"+
                "NickName : "+nickName;

        friendDetailsTextView.setText(allInfo);

    }


}
