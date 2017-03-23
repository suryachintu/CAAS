package com.surya.caas_nitd;

/**
 * Created by Surya on 24-03-2017.
 */
public class DeviceModel {
    private String deviceName;
    private String status;
    private String roomName;

    public DeviceModel() {
    }

    public DeviceModel(String deviceName, String status, String roomName) {
        this.deviceName = deviceName;
        this.status = status;
        this.roomName = roomName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String isState() {
        return status;
    }

    public String getRoomName() {
        return roomName;
    }

}
