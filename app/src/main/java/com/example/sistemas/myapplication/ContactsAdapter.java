package com.example.sistemas.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.sistemas.myapplication.R;

public class ContactsAdapter extends CursorAdapter {
    private static final int CONTACT_ID_INDEX = 0;
    private static final int DISPLAY_NAME_INDEX = 1;

    public ContactsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listviewcontacts, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvidContact = (TextView) view.findViewById(R.id.idContact);
        TextView tvnameContact = (TextView) view.findViewById(R.id.nomContact);
        int idcursor = cursor.getInt(CONTACT_ID_INDEX);
        String nomcursor = cursor.getString(DISPLAY_NAME_INDEX);
        tvidContact.setText(String.valueOf(idcursor));
        tvnameContact.setText(nomcursor);


    }
}
