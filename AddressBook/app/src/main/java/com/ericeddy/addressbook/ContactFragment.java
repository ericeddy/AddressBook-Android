package com.ericeddy.addressbook;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by ericeddy on 2016-10-05.
 */

public class ContactFragment extends Fragment {

    private Context context;
    private Contact contact;

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final MainActivity a = (MainActivity) getActivity();
        context = view.getContext();
        final Resources r = a.getResources();
        view.findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.closeContactFragment();
            }
        });
        view.findViewById(R.id.frag_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ((TextView)view.findViewById(R.id.name)).setText(contact.name);
        ((TextView)view.findViewById(R.id.phone)).setText( contact.numbers.get(0) );
        TextView email = (TextView)view.findViewById(R.id.email);
        if(contact.emails.size() > 0){
            email.setText( contact.emails.get(0) );
        } else {
            email.setText( r.getString(R.string.resource_unavailable) );
            email.setTextColor( ContextCompat.getColor(view.getContext(), R.color.unavailable) );
        }

        if(contact.fake){
            view.findViewById(R.id.btn_goto_contacts).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.btn_goto_contacts).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.id));
                    intent.setData(uri);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    public Uri getPhotoUri() {
        try {
            Cursor cur = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contact.id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(contact.id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
