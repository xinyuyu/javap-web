package cn.edu.hust.utils;

import cn.edu.hust.domain.Attribute;
import cn.edu.hust.domain.BlockInfo;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.factory.BlockInfoFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TransUtils {

    public static List<BlockInfo> fieldTransToBlockInfo(Object attribute) throws IllegalAccessException {
        List<BlockInfo> list = new ArrayList<>();
        Field[] fields = attribute.getClass().getFields();
        for (Field field : fields) {
            if (field.getType().equals(RangeInfo.class)) {
                list.add(BlockInfoFactory.getBlockInfo((RangeInfo) field.get(attribute)));
            } else if (field.getType().isArray()) {
                for (Object o : (Object[]) field.get(attribute)) {
                    list.addAll(fieldTransToBlockInfo(o));
                }
            } else {
                list.add((BlockInfo) attribute);
            }
        }
        return list;
    }

    public static void main(String [] args){
        
    }
}
