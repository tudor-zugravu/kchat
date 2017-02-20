package IMPL;

import API.IContacts;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Contacts implements IContacts {
    private int contactId;
    private int globalId;
    private String name;
    private String username;

    public Contacts(int contactId, int globalId, String name, String username){
    this.contactId = contactId;
        this.globalId = globalId;
        this.name = name;
        this.username = username;
    }

    @Override
    public int getContactId() {
        return this.contactId;
    }

    @Override
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    @Override
    public int getGlobalId() {
        return this.globalId;
    }

    @Override
    public void setGlobalId(int globalId) {
        this.globalId=globalId;
    }

    @Override
    public String getContactName() {
        return this.name;
    }

    @Override
    public void setContactName(String contactName) {
        this.name = contactName;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }
}
