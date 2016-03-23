package com.falendyshvv.tmtask;

/**
 * Created by Falendysh on 07.03.2016.
 */
public class SocialPerson {

    public String name;
    public String information;

    public SocialPerson(String name, String information){
        this.name = name;
        this.information = information;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
