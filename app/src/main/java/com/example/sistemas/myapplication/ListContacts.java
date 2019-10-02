package com.example.sistemas.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class ListContacts extends AppCompatActivity {

    private String[] mProjection;
    Cursor mCursor;
    ListView list;
    ContactsAdapter mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);
        list = (ListView) findViewById(R.id.list);
        mProjection = new String[]{ ContactsContract.Profile._ID,
                ContactsContract.Profile.DISPLAY_NAME_PRIMARY};
        mContactsAdapter = new ContactsAdapter(this, null, 0);
        list.setAdapter(mContactsAdapter);
        loadListContacts();
    }

    private void loadListContacts(){
        mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null);
        mContactsAdapter.changeCursor(mCursor);
    }
}
