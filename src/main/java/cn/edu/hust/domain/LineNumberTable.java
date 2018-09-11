package cn.edu.hust.domain;

import cn.edu.hust.domain.BlockInfo;
import lombok.Data;


@Data
public class LineNumberTable {
    private BlockInfo attributeNameIndex;
    private long attributeLength;
    private int lineNumberTableLength;
    private LineNumberInfo [] lineNumberInfos;
}

@Data
class LineNumberInfo{
    int startPc;
    int lineNumber;
}