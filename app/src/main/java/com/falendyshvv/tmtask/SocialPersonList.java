package com.falendyshvv.tmtask;

import java.util.ArrayList;

/**
 * Created by Falendysh on 16.03.2016.
 */
public class SocialPersonList {

    public static final String NETWORK = "Network";
    public static final String FACEBOOK = "Facebook";
    public static final String GOOGLE = "Google+";
    public static final String PERSON_NUMBER = "person_number";

    private String socialNetworkType;
    public ArrayList<SocialPerson> socialPersons = null;

    private static final SocialPersonList INSTANCE = new SocialPersonList();

    private SocialPersonList(){
        this.socialPersons = new ArrayList<SocialPerson>();
        this.socialNetworkType = "";
    }

    public static SocialPersonList getInstance(){
        return INSTANCE;
    }

    public void setNetwork(String socialNetworkType){
        this.socialNetworkType = socialNetworkType;
    }

    public String getSocialNetworkType(){
        return this.socialNetworkType;
    }





}
