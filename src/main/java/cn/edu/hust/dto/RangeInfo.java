package cn.edu.hust.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RangeInfo {
    @NonNull
    private int parent;
    @NonNull
    private int start;
    @NonNull
    private int end;
    @NonNull
    private String description;


    public boolean inRange(int offset){
        return offset >= start && offset <= end;
    }
}
