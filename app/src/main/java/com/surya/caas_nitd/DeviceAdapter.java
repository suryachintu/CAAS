package com.surya.caas_nitd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Surya on 30-01-2017.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.Devices> {

    Context context;
    public DeviceAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Devices onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item,parent,false);

        return new Devices(itemView);
    }

    @Override
    public void onBindViewHolder(Devices holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class Devices extends RecyclerView.ViewHolder{

        public Devices(View itemView) {
            super(itemView);
        }
    }
}
