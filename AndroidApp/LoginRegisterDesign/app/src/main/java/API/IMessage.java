package API;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Tudor Vasile on 2/10/2017.
 */

public interface IMessage {

    public int getMessageId();
    public void setMessageId(int id);
    public int getSenderId();
    public void setSenderId(int messageId);
    public int getReceiverId();
    public void setReceiverId(int receiverId);
    public int getGroupId();
    public void setGroupId(int GroupId);
    public String getMessage();
    public void setMessage(String message);
    public Date getTimestamp();
    public void setTimestamp(Date timestamp);
    public String getUsername();
    public void setUsername(String username);
    public int getImageId();
    public void setImageId(int imageId);
    public String getStrTimestamp();
    public void setStrTimestamp(String strTimestamp);
    public Bitmap getBitmap();
    public void setBitmap(Bitmap bitmap);
    public boolean isMe();
    public void setMe(boolean me);
}
