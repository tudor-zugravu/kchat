package IMPL;

import API.IGroups;

/**
 * Created by Tudor Vasile on 2/15/2017.
 */

public class Groups implements IGroups {

   private String description;
    private int groupId;
    private int type;
    private String name;

    public Groups (int groupId, String name, String description, int type){
    this.description = description;
        this.groupId = groupId;
        this.type=type;
        this.name=name;
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
        return getGroupId();
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
}
