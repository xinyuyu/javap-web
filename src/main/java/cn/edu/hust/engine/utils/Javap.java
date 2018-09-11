/*
 *	输出*.class文件解析结果
 *	类似执行JDK中javap.exe命令(javap -verbose name(类全限定名))的结果
 *
 *	Copyright (c) 2014.2.18, All rights reserved.
 */

package cn.edu.hust.engine.utils;



import cn.edu.hust.engine.api.*;

import java.util.Map;

public class Javap {
	/**
	 * 输出版本号
	 */
	public static void printVersion(ClassParser parser) {
		System.out.printf("=======" + parser.getMagic() + "========");
		//System.out.printf("Compiled from %s\n", parser.get_SourceFile());
		System.out.printf("\nversion:\n");
		System.out.printf("minor version: %d\n", parser.getMinorVersion());
		System.out.printf("major version: %d\n", parser.getMajorVersion());
	}

	/**
	 * 输出常量池
	 */
	public static void printConstantPool(ClassParser parser) {
		parser.printConstantPool();
	}

	/**
	 * 输出类定义
	 */
	public static void printClass(ClassParser parser) {
		String flags_className = parser.getAccessFlags() + " " + parser.getClassName();
		String superName = parser.getSuperClassName();
		System.out.println();
		if (parser.isInterface())
			System.out.print(flags_className);
		else {
			System.out.print(flags_className + " extends " + superName);
			String[] interfaces = parser.getInterfaceNames();
			String interfaceNames = "";
			for (String name: interfaces)
				interfaceNames += "," + name;
			if (!interfaceNames.isEmpty()) {
				interfaceNames = interfaceNames.substring(1);
				System.out.print(" implements " + interfaceNames);
			}
		}
		System.out.println(" {");
	}

	/**
	 * 输出字段区域
	 */
	public static void printFieldsZone(ClassParser parser) {
		Field[] fields = parser.getFields();
		for (Field field: fields) {
			System.out.println("   " + field.getFieldSignature());
		}
	}

	/**
	 * 输出方法区域
	 */
	public static void printMethodZone(ClassParser parser) {
		Method[] methods = parser.getMethods();
		for (Method method: methods) {
			if (parser.isInterface())
				System.out.println("   " + method.getMethodSignature() + ";");
			else {
				System.out.println();
				System.out.println("   " + method.getMethodSignature() + " {");
				Map<String, Attribute> map1 = method.getAttributeMap();
				for (String name: map1.keySet()) {
					if (name.equals("Code")) {
						Code code = (Code)map1.get(name);
						code.print();
						Map<String, Attribute> map2 = code.getAttributeMap();
						LineNumberTable line_table = (LineNumberTable)map2.get("LineNumberTable");
						if (line_table != null)
							line_table.print();
						LocalVariableTable local_table = (LocalVariableTable)map2.get("LocalVariableTable");
						if (local_table != null)
							local_table.print();
					}
				}
				System.out.println("   }");
			}
		}
		System.out.println("}");
	}

	/**
	 * 输出*.class文件解析结果
	 *
	 * @param file-*.class文件名
	 */
	public static void print(String file) {
		try {
			ClassParser parser = new ClassParser(file);
			printVersion(parser);
			printConstantPool(parser);
			printClass(parser);
			printFieldsZone(parser);
			printMethodZone(parser);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//print(args[0]);
		//print("E:\\User\\Prog\\Java\\ClassParsers\\bin\\Interface1.class");
//		print("/Users/yuyu/Desktop/MyFreeDemo/ClassParsers/test/C.class");
		print("/Users/yuyu/Desktop/space/my/ClassParserRefactor/out/production/ClassParserRefactor/cn/edu/hust/classparser/api/ClassParser.class");
		//print("E:\\User\\Prog\\Java\\ClassParsers\\bin\\Test.class");
		//print("E:\\User\\Prog\\Java\\ClassParsers\\bin\\app\\utils\\parsers\\ClassParser.class");
	}
}