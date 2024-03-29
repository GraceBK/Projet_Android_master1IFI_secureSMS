package boukou.grace.projectm1ifi.db.room_db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 26/04/2018.
 */
@Database(entities = {RContact.class, Msg.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RContactDao rContactDao();
    public abstract MsgDao msgDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "grace_bk_db")
                            .allowMainThreadQueries()
                            //.fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }
}
