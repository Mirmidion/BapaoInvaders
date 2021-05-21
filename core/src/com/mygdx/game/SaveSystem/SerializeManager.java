package com.mygdx.game.SaveSystem;

import java.io.*;

public class SerializeManager implements Serializable {

    public Object load(String saveGamePrefix, String whatToGet)
            throws IOException, ClassNotFoundException {
        Object object = null;

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(saveGamePrefix + whatToGet))) {
            object = reader.readObject();
        }

        return object;
    }

    public void save(Object input, String saveGamePrefix, String whatToGet) throws IOException {
        String fileName = saveGamePrefix + whatToGet;
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName))) {
            writer.writeObject(input);
        }
    }

}
