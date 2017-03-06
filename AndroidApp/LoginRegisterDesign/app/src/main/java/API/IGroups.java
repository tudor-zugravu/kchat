package API;

import android.graphics.Bitmap;

/**
 * Created by Tudor Vasile on 2/10/2017.
 */

public interface IGroups {

    public String getDescription();

    public void setDescription(String description);

    public int getGroupId();

    public void setGroupId(int groupId);

    public int getType();

    public void setType(int type);

    public String getName();

    public void setName(String name);

    public int getImageId();

    public void setImageId(int imageId);

    public Bitmap getGroupImage();

    public void setGroupImage(Bitmap groupImage);

}
