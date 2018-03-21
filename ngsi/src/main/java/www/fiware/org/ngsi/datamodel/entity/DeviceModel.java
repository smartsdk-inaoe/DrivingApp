package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 9/12/2017.
 * Entidad DeviceModel
 */

public class DeviceModel  implements Serializable{
    private String id;
    private String type = "DeviceModel";
    private TextObject category;
    private TextObject brandName;
    private TextObject modelName;
    private TextObject manufacturerName;
    private TextObject dateCreated;



    public DeviceModel(){
        category = new TextObject();
        brandName = new TextObject();
        modelName = new TextObject();
        manufacturerName = new TextObject();
        dateCreated = new TextObject();
    }

    public DeviceModel(String id, String type, TextObject category, TextObject brandName, TextObject modelName, TextObject manufacturerName, TextObject dateCreated) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.brandName = brandName;
        this.modelName = modelName;
        this.manufacturerName = manufacturerName;
        this.dateCreated = dateCreated;
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

    public TextObject getBrandName() {
        return brandName;
    }

    public void setBrandName(TextObject brandName) {
        this.brandName = brandName;
    }

    public TextObject getModelName() {
        return modelName;
    }

    public void setModelName(TextObject modelName) {
        this.modelName = modelName;
    }

    public TextObject getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(TextObject manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public TextObject getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(TextObject dateCreated) {
        this.dateCreated = dateCreated;
    }
}
