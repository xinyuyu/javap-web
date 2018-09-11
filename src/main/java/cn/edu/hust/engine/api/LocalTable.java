package cn.edu.hust.engine.api;

public class LocalTable {
	public int start_pc;
	public int length;
	public int name_index;
	public int descriptor_index;
	public int index;

	public LocalTable( int start_pc, int length, int name_index, int descriptor_index, int index) {
		this.start_pc = start_pc;
		this.length = length;
		this.name_index = name_index;
		this.descriptor_index = descriptor_index;
		this.index = index;
	}
}