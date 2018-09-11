package cn.edu.hust.engine.api;

public class ExceptionTable {
	public int start_pc; 
	public int end_pc; 
	public int handler_pc; 
	public int catch_type; 
	
	public ExceptionTable( int start_pc, int end_pc, int handler_pc, int catch_type) {
		this.start_pc = start_pc;
		this.end_pc = end_pc;
		this.handler_pc = handler_pc;
		this.catch_type = catch_type;
	}
}
