package cn.edu.hust.engine.api;

public class LineTable {
	public int start_pc, line_number;
	
	public LineTable( int start_pc, int line_number) {
		this.start_pc = start_pc;
		this.line_number = line_number;
	}
}
