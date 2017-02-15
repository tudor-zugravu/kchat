package API;

/**
 * Created by Tudor Vasile on 2/10/2017.
 */

public interface IContacts {

    public int getContactId();

    public void setContactId(int contactId);

    public int getGlobalId();

    public void setGlobalId(int globalId);

    public String getContactName();

    public void setContactName(String contactName);

    public String getUsername();

    public void setUsername(String username);
}
