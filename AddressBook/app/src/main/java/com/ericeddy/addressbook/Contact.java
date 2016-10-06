package com.ericeddy.addressbook;

import java.util.ArrayList;

/**
 * Created by ericeddy on 2016-10-05.
 */

public class Contact implements Comparable<Contact>{

    Boolean fake = false;
    String id, name;
    ArrayList<String> numbers, emails;

    public Contact(String mId, String mName){
        id = mId;
        name = mName;
        numbers = new ArrayList<>();
        emails = new ArrayList<>();
    }

    public int compareTo(Contact anotherContact)
    {
        return name.compareTo(anotherContact.name);
    }
}
