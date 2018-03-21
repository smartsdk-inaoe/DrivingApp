package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

import www.fiware.org.ngsi.datamodel.datatypes.LocationPointObject;
import www.fiware.org.ngsi.datamodel.datatypes.NumberObject;
import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 9/12/2017.
 * Entidad Device
 */

public class Device implements Serializable {
    private String id;
    private String type = "Device";
    private TextObject category;
    private TextObject osVersion;
    private NumberObject batteryLevel;
    private TextObject dateCreated;
    private TextObject dateModified;
    private TextObject ipAddress;
    private LocationPointObject location;
    private NumberObject latitude;
    private NumberObject longitude;
    private TextObject mnc;
    private TextObject mcc;
    private TextObject macAddress;
    private TextObject refDeviceModel;
    private TextObject serialNumber;
    private TextObject owner;

    public Device(){
        category = new TextObject();
        osVersion = new TextObject();
        batteryLevel = new NumberObject();
        dateCreated = new TextObject();
        dateModified = new TextObject();
        ipAddress = new TextObject();
        location = new LocationPointObject();
        latitude = new NumberObject();
        longitude = new NumberObject();
        mnc = new TextObject();
        mcc = new TextObject();
        macAddress = new TextObject();
        refDeviceModel = new TextObject();
        serialNumber = new TextObject();
        owner = new TextObject();
    }

    public Device(String id, String type, TextObject category, TextObject osVersion, NumberObject batteryLevel, TextObject dateCreated, TextObject dateModified, TextObject ipAddress, LocationPointObject location, NumberObject latitude, NumberObject longitude, TextObject mnc, TextObject mcc, TextObject macAddress, TextObject refDeviceModel, TextObject serialNumber, TextObject owner) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.osVersion = osVersion;
        this.batteryLevel = batteryLevel;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.ipAddress = ipAddress;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mnc = mnc;
        this.mcc = mcc;
        this.macAddress = macAddress;
        this.refDeviceModel = refDeviceModel;
        this.serialNumber = serialNumber;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextObject getCategory() {
        return category;
    }

    public void setCategory(TextObject category) {
        this.category = category;
    }

    public TextObject getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(TextObject osVersion) {
        this.osVersion = osVersion;
    }

    public NumberObject getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(NumberObject batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public TextObject getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(TextObject dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TextObject getDateModified() {
        return dateModified;
    }

    public void setDateModified(TextObject dateModified) {
        this.dateModified = dateModified;
    }

    public TextObject getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(TextObject ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocationPointObject getLocation() {
        return location;
    }

    public void setLocation(LocationPointObject location) {
        this.location = location;
    }

    public NumberObject getLatitude() {
        return latitude;
    }

    public void setLatitude(NumberObject latitude) {
        this.latitude = latitude;
    }

    public NumberObject getLongitude() {
        return longitude;
    }

    public void setLongitude(NumberObject longitude) {
        this.longitude = longitude;
    }

    public TextObject getMnc() {
        return mnc;
    }

    public void setMnc(TextObject mnc) {
        this.mnc = mnc;
    }

    public TextObject getMcc() {
        return mcc;
    }

    public void setMcc(TextObject mcc) {
        this.mcc = mcc;
    }

    public TextObject getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(TextObject macAddress) {
        this.macAddress = macAddress;
    }

    public TextObject getRefDeviceModel() {
        return refDeviceModel;
    }

    public void setRefDeviceModel(TextObject refDeviceModel) {
        this.refDeviceModel = refDeviceModel;
    }

    public TextObject getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(TextObject serialNumber) {
        this.serialNumber = serialNumber;
    }

    public TextObject getOwner() {
        return owner;
    }

    public void setOwner(TextObject owner) {
        this.owner = owner;
    }
}
