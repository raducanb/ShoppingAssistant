package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class ShoppingListAdapter extends BaseAdapter {
    public ArrayList<ShoppingItem> listData;
    private LayoutInflater layoutInflater;
    public ShoppingListAdapterProtocol delegate;

    public ShoppingListAdapter(Context aContext, ArrayList<ShoppingItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.shopping_list_row_layout, null);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.title);
            holder.categoryNameTextView = (TextView) convertView.findViewById(R.id.category_name);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final int pos = position;
        holder.titleTextView.setText(listData.get(position).name);
        holder.categoryNameTextView.setText(listData.get(position).categoryName);
        holder.checkBox.setSelected(false);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CheckBox)v).setSelected(false);
                didSelectPosition(pos);
            }
        });
        return convertView;
    }

    private void didSelectPosition(int position) {
        this.delegate.didCheckItem(this.listData.get(position));
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView categoryNameTextView;
        CheckBox checkBox;
    }
}
