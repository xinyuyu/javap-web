package cn.edu.hust.service.upload;

import cn.edu.hust.engine.utils.Bytes;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
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

    public static List<String> createPageContent(String path) throws IOException {
        List<String> result = new ArrayList<>();
        byte[] data = getFileByteData(path);
        for (Byte item : data){
            byte [] bytes = new byte[1];
            bytes[0] = item;
            result.add(DatatypeConverter.printHexBinary(bytes));
        }
        return result;
    }

    public static String createHtmlContent(String path) throws IOException {
        byte[] data = getFileByteData(path);
        int rowSize = 15;
        int row = data.length / rowSize + 1;

        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 16; i++) {
            sb.append("<label style=\"display:inline-block;width:40px; text-align: center\">" + "&nbsp;" + Integer.toHexString(i) + "&nbsp;" + " </label>");
        }
        sb.append("<br />");
        for (int i = 0, j = 1; i < data.length; i++) {
            byte[] item = new byte[1];
            item[0] = data[i];
            String itemStr = DatatypeConverter.printHexBinary(item);
            itemStr = "<label id=" + i + " onclick=\"show(this)\" style=\"display:inline-block;width:40px; text-align: center\">" + "&nbsp;" + itemStr + "&nbsp;" + " </label>";
            if ((i + 1) % rowSize == 0) {
                itemStr = itemStr + "<br /><br />";
                j++;
            }
            sb.append(itemStr);
        }
        return sb.toString();
    }
}
