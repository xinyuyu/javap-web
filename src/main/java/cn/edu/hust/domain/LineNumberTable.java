package cn.edu.hust.domain;

import cn.edu.hust.constants.Description;
import cn.edu.hust.domain.BlockInfo;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import lombok.Data;


@Data
public class LineNumberTable {
    private BlockInfo attributeNameIndex; // u2
    private RangeInfo attributeLength; //u4
    private RangeInfo lineNumberTableLength; //u2
    private LineNumberInfo[] lineNumberArr; //u2

    private void init(BlockInfo attributeNameIndex, RangeInfo attributeLength, RangeInfo lineNumberTableLength, LineNumberInfo[] lineNumberArr) {
        this.attributeNameIndex = attributeNameIndex;
        this.attributeLength = attributeLength;
        this.lineNumberTableLength = lineNumberTableLength;
        this.lineNumberArr = lineNumberArr;
    }

    public LineNumberTable(byte[] data, int offset) {
        RangeInfo attributeNameIndexRangeInfo = RangeInfoFactory.getRangeInfo(offset, Description.LNT_ATT_NAME_INDEX);
        BlockInfo attributeNameIndex = BlockInfoFactory.getBlockInfo(attributeNameIndexRangeInfo);
        RangeInfo attributeLength = RangeInfoFactory.getRangeInfo(offset + 2, Description.LNT_ATT_LENGTH);
        RangeInfo lineNumberTableLength = RangeInfoFactory.getRangeInfo(offset + 6, Description.LNT_NUMBER_TABLE_LENGTH);
        int arrlength = Bytes.toInt(data, lineNumberTableLength.getStart(), lineNumberTableLength.getEnd());
        LineNumberInfo[] lineNumberArr = new LineNumberInfo[arrlength];
        for (int i = 0; i < arrlength; i++) {
            lineNumberArr[i] = new LineNumberInfo(data, offset);
            offset += 4;
        }
        init(attributeNameIndex, attributeLength, lineNumberTableLength, lineNumberArr);
    }

    public int getLineNumberTableLength(byte[] data) {
        return Bytes.toInt(data, attributeLength.getStart(), attributeLength.getEnd()) + 2;
    }
}

@Data
class LineNumberInfo {
    RangeInfo startPc; // u2
    RangeInfo lineNumber; // u2

    private void init(RangeInfo startPc, RangeInfo lineNumber) {
        this.startPc = startPc;
        this.lineNumber = lineNumber;
    }

    public LineNumberInfo(byte[] data, int offset) {
        RangeInfo startPc = RangeInfoFactory.getRangeInfo(offset, Description.LNI_START_PC);
        RangeInfo lineNumber = RangeInfoFactory.getRangeInfo(offset, Description.LNI_LINE_NUMBER);
        init(startPc, lineNumber);
    }
}