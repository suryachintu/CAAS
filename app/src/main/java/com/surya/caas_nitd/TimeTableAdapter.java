package com.surya.caas_nitd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Surya on 30-01-2017.
 */

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTable> {

    Context context;
    public TimeTableAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TimeTable onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_table_list,parent,false);

        return new TimeTable(itemView);
    }

    @Override
    public void onBindViewHolder(TimeTable holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class TimeTable extends RecyclerView.ViewHolder{

        public TimeTable(View itemView) {
            super(itemView);
        }
    }
}
