package cn.edu.hust.factory;

import cn.edu.hust.dto.RangeInfo;

public class RangeInfoFactory {
    public static RangeInfo getRangeInfo(int start, int end, String description){
        return new RangeInfo(-1, start, end, description);
    }
    public static RangeInfo getRangeInfo(int parent, int start, int end, String description){
        return new RangeInfo(parent, start, end, description);
    }
}
