package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 4/9/2018.
 */

public class Zone implements Serializable {
    private String idZone;
    private String type;
    private TextObject refBuildingType;
    private TextObject name;
    private TextObject address;
    private TextObject description;


}
