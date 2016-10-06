package com.ericeddy.addressbook;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ericeddy on 2016-10-05.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    ArrayList<Contact> data = null;


    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    private final OnItemClickListener listener;

    public ContactAdapter(ArrayList<Contact> contacts, ArrayList<Contact> fake_contacts, OnItemClickListener listener){
        data = new ArrayList<>();
        data.addAll(contacts);
        data.addAll(fake_contacts);
        Collections.sort(data);

        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if( data.get(position).fake ){
            type = 1;
        } else {
            type = 0;
        }
        return type;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item_layout, parent, false);

        return new ContactViewHolder(itemView, viewType);
    }

    //Layout Item
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = data.get(position);
        holder.name.setText(contact.name);
        holder.addClickListener(contact, listener);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        View background;
        TextView name;

        public ContactViewHolder(View view, int viewType) {
            super(view);
            background = view.findViewById(R.id.listitem_bg);
            name = (TextView) view.findViewById(R.id.listitem_name);
            background.setBackgroundColor(ContextCompat.getColor( view.getContext(), viewType == 0 ? R.color.lineitem_bg_real : R.color.lineitem_bg_fake ));
        }

        public void addClickListener(final Contact contact, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(contact);

                }
            });
        }

    }
}
