package cn.edu.hust.domain;

import cn.edu.hust.dto.RangeInfo;
import lombok.Data;

@Data
public class BlockInfo {
    RangeInfo rangeInfo;
    BlockInfo[] nexts;

    public BlockInfo(RangeInfo rangeInfo, BlockInfo[] nexts) {
        this.rangeInfo = rangeInfo;
        this.nexts = nexts;
    }

    public int getRangeStart() {
        return rangeInfo.getStart();
    }

    public int getRangeEnd() {
        return rangeInfo.getEnd();
    }

    public boolean inRange(int offset) {
        return rangeInfo.inRange(offset);
    }
}
