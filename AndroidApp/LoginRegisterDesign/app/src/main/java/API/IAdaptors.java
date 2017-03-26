package API;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Tudor Vasile on 2/10/2017.
 * This is a Interface used to:
 * 1)Setup the local phone database and tables, will also check for duplicate table creation
 * 2)Add to contacts table will update the contacts from a REST request -- Server
 * 3)Add to Groups table will update the groups from a REST request -- Server
 * 4)Add to Message table will update the messages from a REST request -- Server
 * 5) Flush data upon logout
 */

public interface IAdaptors {

    public void initialDatabaseSetup();

    public boolean addToContactsTable(int contactId, int requestNum, String timestamp,String userId,String contactName,String email,
                                      String username,String phonenumber,int blocked,int session,String contactPicture,Bitmap profilePicture);

    public boolean addToGroupsTable(int id, String name, String description, int type);

    public boolean addToMessagesTable(int messageId, int senderId, int groupId, String message, Date timestamp);

    public boolean flushData();

}
