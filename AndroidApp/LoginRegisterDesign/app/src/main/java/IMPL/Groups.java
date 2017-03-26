package IMPL;

import android.graphics.Bitmap;

import java.util.ArrayList;

import API.IGroups;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Groups implements IGroups {

   private String description;
    private int ownerId;
    private String name;
    private String pictureLocation;
    private ArrayList<Integer> usersAsID;
    private Bitmap groupImage;
    private int actualOwner;
    public static ArrayList<IGroups> groupList = new ArrayList<>();

    public Groups (String groupName, String description, int ownerId,  String pictureLocation, ArrayList<Integer> usersId){
        this.name = groupName;
        this.description = description;
        this.ownerId = ownerId;
        this.pictureLocation=pictureLocation;
        this.usersAsID = usersId;
    }

    public Groups (String groupName, String description, int ownerId,  String pictureLocation, ArrayList<Integer> usersId,Bitmap groupImage){
        this.name = groupName;
        this.description = description;
        this.ownerId = ownerId;
        this.pictureLocation=pictureLocation;
        this.usersAsID = usersId;
        groupImage = groupImage;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public String getImageLocation() {
        return this.pictureLocation;
    }

    @Override
    public void setImageLocation(String pictureLocation) {
        this.pictureLocation = pictureLocation;
    }

    public static ArrayList<IGroups> getGroupList() {
        return groupList;
    }

    public static void setGroupList(ArrayList<IGroups> groupList) {
        Groups.groupList = groupList;
    }

    public ArrayList<Integer> getUsersAsID() {
        return usersAsID;
    }

    public void setUsersAsID(ArrayList<Integer> usersAsID) {
        this.usersAsID = usersAsID;
    }

    public Bitmap getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(Bitmap groupImage) {
        this.groupImage = groupImage;
    }

    @Override
    public int getActualOwnerId() {
        return actualOwner;
    }

    @Override
    public void setActualOwnerId(int actualOwnerId) {
        this.actualOwner=actualOwnerId;

    }
}
