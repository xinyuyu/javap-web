package cn.edu.hust.domain;

import cn.edu.hust.constants.Description;
import cn.edu.hust.domain.BlockInfo;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import lombok.Data;


@Data
public class LineNumberTable extends Attribute {
    private RangeInfo lineNumberTableLength; //u2
    private LineNumberInfo[] lineNumberArr; //u2

    public LineNumberTable(byte[] data, int[] pool, int offset) {
        super(data,pool, offset);
        RangeInfo lineNumberTableLength = RangeInfoFactory.getRangeInfo(offset + 6, Description.LNT_NUMBER_TABLE_LENGTH);
        int arrlength = Bytes.toInt(data, lineNumberTableLength.getStart(), lineNumberTableLength.getEnd());
        LineNumberInfo[] lineNumberArr = new LineNumberInfo[arrlength];
        for (int i = 0; i < arrlength; i++) {
            lineNumberArr[i] = new LineNumberInfo(data, offset);
            offset += 4;
        }
        this.lineNumberTableLength = lineNumberTableLength;
        this.lineNumberArr = lineNumberArr;
    }

    @Override
    public long getLength(byte[] data) {
        return Bytes.toInt(data, attributeLength.getStart(), attributeLength.getEnd()) + 2 + 4;
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

    public int getLineNumberInfoLength() {
        return 4;
    }
}