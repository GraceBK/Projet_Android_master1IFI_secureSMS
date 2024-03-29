package boukou.grace.projectm1ifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.RContact;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private final int PICK_CONTACT_REQUEST = 1; // Le code de reponse

    RecyclerView recyclerView;
    private RecentAdapter adapter;
    List<RContact> contacts;
    RecentViewModel viewModel;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (checkAndRequestPermissions()) {}

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS},
                1);

        db = AppDatabase.getDatabase(getApplicationContext());

        contacts = db.rContactDao().getAllRContacts();

        recyclerView = findViewById(R.id.main_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentAdapter(contacts);
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(RecentViewModel.class);
        viewModel.getContactList().observe(this, new Observer<List<RContact>>() {
            @Override
            public void onChanged(@Nullable List<RContact> rContacts) {
                adapter.update(rContacts);
            }
        });

    }

    /**
     * Start the Activity
     */
    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    /**
     * Receive the Result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            getContact(data);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void getContact(Intent data) {
        try {
            Uri contactUri = data.getData();
            @SuppressLint("Recycle") Cursor cursor = getContentResolver()
                    .query(Objects.requireNonNull(contactUri), null, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();

            int username_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
            int phone_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(username_id);
            String number = cursor.getString(phone_id);

            RContact rContact = new RContact();
            rContact.setUsername(name);
            rContact.setPhone(number);

            new AsyncTask<RContact, Void, Void>() {
                @Override
                protected Void doInBackground(RContact... contacts) {
                    for (RContact contact : contacts) {
                        db.rContactDao().insertRContact(contact);
                    }
                    return null;
                }
            }.execute(rContact);

            final Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("USERNAME", name);
            intent.putExtra("PHONE", number);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_inbox:
                Intent intent = new Intent(MainActivity.this, ReceiveSMSActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_contact:
                pickContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est garantie
                } else {
                    // La permission est refusee
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // TODO : faire appel a cette methode
    public void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // Cela signifie que la permission a deja ete demande et l'utilisateur l'a refuse
                // On peut aussi expliquer a l'utilisateur pourquoi
                // cette permission est necessaire et la redemander
            } else {
                // Sinon demander la permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
    }
}