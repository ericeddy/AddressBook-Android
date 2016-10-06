package com.ericeddy.addressbook;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST_ID = 1;

    private ArrayList<Contact> contacts;
    private ArrayList<Contact> fakeContacts;
    private ContactAdapter cAdapter;
    private RecyclerView recyclerView = null;

    private View spinner;

    private ContactFragment contactInfoView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);

        //Needed for Contact Permission Update
        if (Build.VERSION.SDK_INT < 23) {
            getContacts();
        } else {
            getReadUserContactsPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getReadUserContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST_ID);

        } else {
            getContacts();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST_ID) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getContacts(){
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        contacts = new ArrayList<>();
        fakeContacts = null;

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Contact contact = null;
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    contact = new Contact(id, name);

                    Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                    while (phoneCursor.moveToNext()) {
                        String phone = phoneCursor.getString( phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) );
                        contact.numbers.add(phone);
                    }
                    phoneCursor.close();

                    Cursor emailCursor = cr.query( ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String email = emailCursor.getString( emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA) );
                        String emailType = emailCursor.getString( emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE) );
                        contact.emails.add(email);
                    }
                    emailCursor.close();
                }
                if(contact != null)contacts.add(contact);
            }
        }

        setupRecycler();
    }

    private void setupRecycler(){
        if(recyclerView == null){
            recyclerView = (RecyclerView)findViewById(R.id.contact_list);
        }
        if(recyclerView != null){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            callForFakeContacts();
        }
    }
    private void updateRecyclerData(){
        cAdapter = new ContactAdapter(contacts, fakeContacts, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                if(contactInfoView != null) closeContactFragment();
                contactInfoView = new ContactFragment();
                contactInfoView.setContact(contact);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add( android.R.id.content, contactInfoView).commit();
            }
        });
        recyclerView.setAdapter(cAdapter);
        spinner.setVisibility(View.GONE);
    }

    private void callForFakeContacts(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String randomUserCall ="http://api.randomuser.me/?results=50&inc=name,phone,email&nat=us,dk,fr,gb";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, randomUserCall,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fakeContacts = new ArrayList<>();
                        try{
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray jsonArray = responseObject.optJSONArray("results");
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String id = (i+1)+"";
                                JSONObject nameObject = jsonObject.getJSONObject("name");
                                String name = uppercaseFirstLetter(nameObject.getString("first")) + " " + uppercaseFirstLetter(nameObject.getString("last"));

                                Contact contact = new Contact(id, name);
                                contact.fake = true;
                                contact.numbers.add( jsonObject.getString("phone") );
                                contact.emails.add( jsonObject.getString("email") );
                                fakeContacts.add(contact);
                            }
                            updateRecyclerData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            updateRecyclerData();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "There was a problem reaching RandomUser.me", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    public void closeContactFragment(){
        if(contactInfoView != null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(contactInfoView).commit();
            contactInfoView = null;
        }

    }

    private String uppercaseFirstLetter(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
