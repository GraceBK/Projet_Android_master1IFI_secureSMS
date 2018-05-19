package boukou.grace.projectm1ifi.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 19/05/2018.
 */
@Dao
public interface Msg2Dao {
    @Query("SELECT * FROM Msg2")
    List<Msg> getAllMsg2();

    @Query("SELECT * FROM Msg2 WHERE numero_receiver = :phone")
    List<Msg> getAllMsg2ByNumber(String phone);

    @Query("SELECT cle FROM Msg2 WHERE name_receiver = :id_sms")
    String getKey(String id_sms);

    @Query("SELECT * FROM Msg2")
    LiveData<List<Msg>> getAll();

    @Query("UPDATE Msg2 SET status_sms = :statusSms WHERE name_receiver = :id_sms")
    int updateStatusSms(String id_sms, String statusSms);

    @Query("UPDATE Msg2 SET cle = :cle WHERE name_receiver = :id_sms")
    int updateKeySms(String id_sms, String cle);

    //@Query("SELECT sms.name_receiver, sms.numero_receiver FROM sms WHERE uid IN (:smsIds)")

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSms(Msg2... msgs);

    @Update
    void updateStatusSms(Msg2... msgs);
}
