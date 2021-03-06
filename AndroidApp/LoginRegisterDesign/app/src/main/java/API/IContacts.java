package API;

import android.graphics.Bitmap;

/**
 * Created by Tudor Vasile on 2/10/2017.
 */

public interface IContacts {

    public int getContactId();

    public void setContactId(int contactId);

    public String getTimestamp();

    public void setTimestamp(String timestamp);

    public String getUserId();

    public void setUserId(String userId);

    public String getContactName();

    public void setContactName(String contactName);

    public String getEmail();

    public void setEmail(String email);

    public String getUsername();

    public void setUsername(String username);

    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);

    public String getContactProfile();

    public void setContactPicture(String contactPicture);

    public Bitmap getBitmap();

    public void setBitmap(Bitmap contactsBitmap);

    public void setBiography(String biography);

    public String getBiography();

}
