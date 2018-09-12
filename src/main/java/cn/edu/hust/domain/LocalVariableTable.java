package cn.edu.hust.domain;

import cn.edu.hust.constants.Description;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.utils.Bytes;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import lombok.Data;

@Data
public class LocalVariableTable extends Attribute {
    private RangeInfo localVariableTableLength;
    private LocalVariableInfo[] localVariableInfoArr;

    public LocalVariableTable(byte[] data, int[] pool, int offset) {
        super(data, pool, offset);
        RangeInfo localVariableTableLength = RangeInfoFactory.getRangeInfo(offset + 6, Description.LVT_ATT_TABLE_LENGTH);
        int arrayLength = Bytes.toInt(data, localVariableTableLength.getStart(), localVariableTableLength.getEnd());
        LocalVariableInfo[] localVariableInfoArr = new LocalVariableInfo[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            localVariableInfoArr[i] = new LocalVariableInfo(data, pool, offset + 8);
            offset += localVariableInfoArr[i].getLocalVariableInfoLength(data);
        }
        this.localVariableTableLength = localVariableTableLength; // u2
        this.localVariableInfoArr = localVariableInfoArr;
    }

    @Override
    public long getLength(byte[] data) {
        return Bytes.toInt(data, attributeLength.getStart(), attributeLength.getEnd()) + 2 + 4;
    }
}

@Data
class LocalVariableInfo {
    private RangeInfo startPc;
    private RangeInfo length;
    private BlockInfo nameIndex;
    private BlockInfo descriptorIndex;
    private RangeInfo index;

    private void init(RangeInfo startPc, RangeInfo length, BlockInfo nameIndex, BlockInfo descriptorIndex, RangeInfo index) {
        this.startPc = startPc;//u2
        this.length = length; // u2
        this.nameIndex = nameIndex; // u2
        this.descriptorIndex = descriptorIndex; // u2
        this.index = index; // u2
    }

    /**
     * @param data
     * @param pool
     * @param offset 指向start_pc开始的位置
     */
    public LocalVariableInfo(byte[] data, int[] pool, int offset) {
        // start_pc代表局部变量的生命周期开始的字节码偏移量
        RangeInfo startPc = RangeInfoFactory.getRangeInfo(offset, Description.LVI_START_PC);
        // length代表局部变量作用范围覆盖的长度
        RangeInfo length = RangeInfoFactory.getRangeInfo(offset + 2, Description.LVI_LENGTH);
        // 局部变量名字->CONSTANT_UTF8
        RangeInfo nameIndexRangeInfo = RangeInfoFactory.getRangeInfo(offset + 4, Description.LV_NAME_INDEX);
        BlockInfo nameIndex = BlockInfoFactory.getBlockInfo(nameIndexRangeInfo);
        // 局部变量描述符->CONSTANT_UTF8
        RangeInfo descriptorIndexRangeInfo = RangeInfoFactory.getRangeInfo(offset + 6, Description.LV_DESCRIPTOR_INDEX);
        BlockInfo descriptorIndex = BlockInfoFactory.getBlockInfo(descriptorIndexRangeInfo);
        // 这个局部变量在栈帧局部变量表中slot的位置
        RangeInfo index = RangeInfoFactory.getRangeInfo(offset + 8, Description.LVI_INDEX);
        this.init(startPc, length, nameIndex, descriptorIndex, index);
    }

    public int getLocalVariableInfoLength(byte[] data) {
        return Bytes.toInt(data, length.getStart(), length.getEnd()) + 2;
    }
}