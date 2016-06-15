package org.ontologyengineering.conceptdiagrams.web.shared.curvegeometry;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

/**
 * Author: Michael Compton<br>
 * Date: February 2016<br>
 * See license information in base directory.
 */
public class Point_CustomFieldSerializer extends CustomFieldSerializer<Point> {


    @Override
    public void deserializeInstance(SerializationStreamReader serializationStreamReader, Point point) throws SerializationException {
        point.setX(serializationStreamReader.readDouble());
        point.setY(serializationStreamReader.readDouble());
    }

    public static void deserialize(SerializationStreamReader serializationStreamReader, Point point) {
        try {
            point.setX(serializationStreamReader.readDouble());
            point.setY(serializationStreamReader.readDouble());
        } catch (Exception e) {
            // then barf
        }
    }

// other option for serialization
//    public static Point instantiate(SerializationStreamReader serializationStreamReader) throws SerializationException {
//        double x = serializationStreamReader.readDouble();
//        double y = serializationStreamReader.readDouble();
//        return new Point(x, y);
//    }

    @Override
    public void serializeInstance(SerializationStreamWriter serializationStreamWriter, Point point) throws SerializationException {
        serializationStreamWriter.writeDouble(point.getX());
        serializationStreamWriter.writeDouble(point.getY());
    }



    public static void serialize(SerializationStreamWriter serializationStreamWriter, Point point) {
        try {
            serializationStreamWriter.writeDouble(point.getX());
            serializationStreamWriter.writeDouble(point.getY());
        } catch (Exception e) {
            // help me
        }

    }
}
