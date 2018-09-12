package cn.edu.hust.utils;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static byte[] getFileByteData(String path) throws IOException {
        List<Byte> dataList = new ArrayList<>();
        InputStream in = new FileInputStream(new File(path));
        byte[] buffer = new byte[1];
        while (in.read(buffer) != -1) {
            dataList.add(buffer[0]);
        }
        return listToArray(dataList);
    }

    private static byte[] listToArray(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }
}
