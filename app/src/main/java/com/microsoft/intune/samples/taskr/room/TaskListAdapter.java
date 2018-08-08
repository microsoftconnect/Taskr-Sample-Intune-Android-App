/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.intune.samples.taskr.room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.microsoft.intune.samples.taskr.R;

import java.util.List;

/**
 * A ListAdapter that updates a ListView to show Tasks.
 */
public class TaskListAdapter extends BaseAdapter implements ListAdapter {

    private List<Task> mList;
    private final Context mContext;

    public TaskListAdapter(final Context context) {
        this.mContext = context;
    }

    /**
     * Sets the list that this adapter displays to be list, then updates the view to show it.
     *
     * @param list the new list to display
     */
    public void setList(final List<Task> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(final int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return mList == null ? -1 : mList.get(position).getId();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        // convertView may be null the first time this method is called
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null) {
                return null;
            }
            view = inflater.inflate(R.layout.task_list_item, parent, false);
        }

        // The mList may not have been set yet, so if it hasn't been exit
        if (mList == null) {
            return view;
        }

        // Get the fields to fill in
        TextView liDescription = view.findViewById(R.id.task_list_item_description);

        // Fill them in
        final Task task = mList.get(position);
        liDescription.setText(task.getDescription());

        // Set the check button listener. It will just call the complete method on this task.
        ImageButton completeButton = view.findViewById(R.id.task_list_complete_button);
        completeButton.setOnClickListener((final View v) -> RoomManager.completeTask(task));

        return view;
    }
}
