package API;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Tudor Vasile on 2/10/2017.
 */

public interface IGroups {

    public String getDescription();

    public void setDescription(String description);

    public String getDescription2();

    public void setDescription2(String description2);

    public int getOwnerId();

    public void setOwnerId(int ownerId);

    public String getName();

    public void setName(String name);

    public String getImageLocation();

    public void setImageLocation(String pictureLocation);

    public Bitmap getGroupImage();

    public void setGroupImage(Bitmap groupImage);

    public ArrayList<Integer> getUsersAsID();

    public int getActualOwnerId();

    public void setActualOwnerId(int actualOwnerId);




}
