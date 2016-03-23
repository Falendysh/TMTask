package com.falendyshvv.tmtask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FaceBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceBookFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    public static CallbackManager callbackManager;

   volatile ArrayList<SocialPerson> friends = new ArrayList<SocialPerson>();



    public FaceBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment FaceBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceBookFragment newInstance() {
        FaceBookFragment fragment = new FaceBookFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            callbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_face_book, container, false);

        com.facebook.login.widget.LoginButton myLoginButton = (LoginButton) rootView.findViewById(R.id.login_button_facebook);

        myLoginButton.setReadPermissions("user_friends");
        myLoginButton.setFragment(this);
        myLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Toast.makeText(getContext().getApplicationContext(), "Signed In via Facebook Successfully", Toast.LENGTH_SHORT).show();

                Intent friendListActivityIntent = new Intent(getContext().getApplicationContext(), FriendListActivity.class);
                friendListActivityIntent.putExtra(SocialPersonList.NETWORK,SocialPersonList.FACEBOOK);
                startActivity(friendListActivityIntent);


            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext().getApplicationContext(), "Facebook Connection Cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getContext().getApplicationContext(), "FaceBook Connection Error", Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
