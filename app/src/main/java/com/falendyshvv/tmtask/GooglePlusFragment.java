package com.falendyshvv.tmtask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link GooglePlusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GooglePlusFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public GoogleApiClient mGoogleApiClient = null;

    GoogleSignInOptions gso;

    private static final int RC_SIGN_IN = 0;


    public GooglePlusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GooglePlusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GooglePlusFragment newInstance() {
        GooglePlusFragment fragment = new GooglePlusFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext(), this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_google_plus, container, false);

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.login_button_google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(this);


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
    public void onConnected(Bundle bundle) {



    }



    @Override
    public void onConnectionSuspended(int i) {




    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button_google )
        {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount acct = result.getSignInAccount();
                Toast.makeText(getContext().getApplicationContext(),"Signed In via Google+ Successfully",Toast.LENGTH_SHORT).show();

                Intent friendListActivityIntent = new Intent(getContext().getApplicationContext(),FriendListActivity.class);
                friendListActivityIntent.putExtra(SocialPersonList.NETWORK, SocialPersonList.GOOGLE);

                startActivity(friendListActivityIntent);

            }
            else
            {
                Toast.makeText(getContext().getApplicationContext(),"Google+ Sign-In Error",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
