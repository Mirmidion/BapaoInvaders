package com.mygdx.game.SaveSystem;

import java.io.*;

public class SerializeManager implements Serializable {
    private static final String FILE_NAME_PREFIX = "SaveGame_";

    public Object readByteStreamFromFileAndDeSerializeToObject(String saveGamePrefix, String whatToGet)
            throws IOException, ClassNotFoundException {
        Object object = null;
        //System.out.printf("\nDe-serialization bytestream from file: %s", saveGamePrefix + whatToGet);

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(saveGamePrefix + whatToGet))) {
            object = reader.readObject();
        }

        //System.out.println(READ_OBJECT + object.toString());
        return object;
    }

    public void serializeObjectAndSaveToFile(Object input, String saveGamePrefix, String whatToGet) throws IOException {
        String fileName = saveGamePrefix + whatToGet;
        //System.out.printf("\nSerialize object: %s \ninto a file: %s\n ", input.toString(), fileName);
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName))) {
            writer.writeObject(input);
        }
    }

}
