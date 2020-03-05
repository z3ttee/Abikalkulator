package de.zitzmanncedric.abicalc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class AppSerializer {
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
