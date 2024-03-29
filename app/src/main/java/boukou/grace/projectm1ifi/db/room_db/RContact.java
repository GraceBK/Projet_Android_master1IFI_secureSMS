package boukou.grace.projectm1ifi.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 26/04/2018.
 */
@Entity(indices = {@Index(value = "phone_number", unique = true)})
public class RContact {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "user")
    private String username;

    @ColumnInfo(name = "phone_number")
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
