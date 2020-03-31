package de.zitzmanncedric.abicalc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Klasse beinhaltet Funktionen, die genutzt werden, um Objekte in Bytes zu übersetzen oder Bytes in ein Objekt zu transformieren.
 * @author Fremdcode (Quelle unbekannt)
 */
public class AppSerializer {

    /**
     * Verwandelt ein Datenobjekt in ein Byte-Array
     * @param object Datenobjekt, das verwandelt werden soll
     * @return Byte-Array des Datenobjekts
     */
    public static byte[] serialize(Object object){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out;

            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();

            return bos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Verwandelt ein Byte-Array in ein Datenobjekt
     * @param bytes Byte-Array des Datenobjekts
     * @return Datenobjekt
     */
    public static Object deserialize(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
