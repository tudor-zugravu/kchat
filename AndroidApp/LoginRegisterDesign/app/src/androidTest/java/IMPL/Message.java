package IMPL;

import java.util.Date;

import API.IMessage;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Message implements IMessage {

    public Message(){

    }

    @Override
    public int getMessageId() {
        return 0;
    }

    @Override
    public void setMessageId(int id) {

    }

    @Override
    public int getSenderId() {
        return 0;
    }

    @Override
    public void setSenderId(int messageId) {

    }

    @Override
    public int getGroupId() {
        return 0;
    }

    @Override
    public void setGroupId(int GroupId) {

    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public Date getTimestamp() {
        return null;
    }

    @Override
    public void setTimestamp(Date timestamp) {

    }
}
