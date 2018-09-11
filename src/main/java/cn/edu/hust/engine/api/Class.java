package cn.edu.hust.engine.api;

public class Class {
	public String innerClassInfo;
	public String outerClassInfo;
	public String innerName;
	public String innerClassAccessFlags;

	public Class( String inner, String outer, String name, String flags) {
		innerClassInfo = inner;
		outerClassInfo = outer;
		innerName = name;
		innerClassAccessFlags = flags;
	}
}
