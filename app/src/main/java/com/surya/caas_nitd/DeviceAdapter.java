package com.surya.caas_nitd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Surya on 06-04-2017.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{

    private final String TAG = DeviceAdapter.class.getSimpleName();
    private Context mContext;
    private HashMap<String,String> mDeviceMap;
    private ArrayList<String> data;
    private DatabaseReference reference;

    public DeviceAdapter() {
    }

    public DeviceAdapter(Context mContext, HashMap<String, String> mDeviceMap) {
        this.mContext = mContext;
        this.mDeviceMap = mDeviceMap;
        reference = FirebaseDatabase.getInstance().getReference().child("Rooms");
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item,parent,false);

        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, int position) {

        String[] p = data.get(position).split("=");
        final String[] devicename = p[0].split(",");
        holder.device_name.setText(devicename[0]);
        holder.device_state.setChecked(!p[1].equals("0"));

        holder.device_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = holder.device_state.isChecked() ? 1 : 0;
                reference.child(devicename[1]).child(devicename[0]).setValue(state);
            }
        });

    }

    @Override
    public int getItemCount() {

        data= new ArrayList<>();
        for (Object o : mDeviceMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getValue().equals("0") || pair.getValue().equals("1"))
                data.add(pair.getKey() + "=" + pair.getValue());
            Log.e(TAG, pair.getKey() + "*" + pair.getValue());
        }
        return data.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.device_name)
        TextView device_name;
        @BindView(R.id.switch_state)
        Switch device_state;
        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
