package cn.edu.hust.domain;

import cn.edu.hust.constants.Description;
import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.factory.BlockInfoFactory;
import cn.edu.hust.factory.RangeInfoFactory;
import lombok.Data;

@Data
public class ConstantValue extends Attribute {
    private BlockInfo constantValueIndex;

    public ConstantValue(byte[] data, int[] pool, int offset) {
        super(data, pool, offset);
        RangeInfo constantValueIndexRange = RangeInfoFactory.getRangeInfo(offset + 6, Description.CV_CONSTANT_VALUE_INDEX);
        BlockInfo constantValueIndex = BlockInfoFactory.getBlockInfo(constantValueIndexRange, data, pool);
        this.constantValueIndex = constantValueIndex;
    }

    @Override
    public long getLength(byte [] data){
        return 8;
    }
}
