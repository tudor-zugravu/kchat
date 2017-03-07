package IMPL;

import android.graphics.Bitmap;

import com.example.user.kchat01.R;

import java.util.ArrayList;

import API.IGroups;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Groups implements IGroups {

   private String description;
    private int groupId;
    private int type;
    private String name;
    private int imageId;
    private ArrayList<Integer> usersAsID;
    private Bitmap groupImage;

    public static ArrayList<IGroups> groupList = new ArrayList<>();

    public Groups (int groupId, String name, String description, int type){
    this.description = description;
        this.groupId = groupId;
        this.type=type;
        this.name=name;
    }

    public Groups (int groupId, String name, String description, int type,ArrayList<Integer> usersAsID){
        this.description = description;
        this.groupId = groupId;
        this.type=type;
        this.name=name;
        this.usersAsID = usersAsID;
    }

    public Groups (String name, String description, int type, int imageId){
        this.description = description;
        this.groupId = groupId;
        this.type=type;
        this.name=name;
    }

    public Groups (int groupId, String name, String description, int type,ArrayList<Integer> usersAsID, Bitmap groupImage){
        this.description = description;
        this.groupId = groupId;
        this.type=type;
        this.name=name;
        this.usersAsID = usersAsID;
        this.groupImage = groupImage;
    }


    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getGroupId() {
        return this.groupId;
    }

    @Override
    public void setGroupId(int groupId) {
    this.groupId=groupId;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public void setType(int type) {
        this.type=type;
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
    public int getImageId() {
        return imageId;
    }

    @Override
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static ArrayList<IGroups> getObjectList() {
        ArrayList<IGroups> dataList = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            IGroups group = new Groups("name of group" +i,"Description message of group" + i,0,R.drawable.human);
            group.setImageId(R.drawable.human);
            dataList.add(group);
        }
        return dataList;
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
}
