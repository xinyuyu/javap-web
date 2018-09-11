package cn.edu.hust.engine.api;/*
 *	类、字段及方法访问信息(Access_flags)常量数组定义
 *
 *	Copyright (c) 2014.2.18, All rights reserved.
 */


public interface ItemName {
    public String MAGIC           = "magic";
    public String MINOR_VERSION   = "minor_version";
    public String MAJOR_VERSION   = "major_version";
    public String CONSTANT_COUNT  = "constant_count";
    public String CP_INFO         = "cp_info";
    public String ACCESS_FLAGS    = "access_flags";
    public String THIS_CLASS      = "this_class";
    public String SUPER_CLASS     = "super_class";
    public String INTERFACE_COUNT = "interface_count";
    public String INTERFACES      = "interfaces";

    public String FIELDS_COUNT     = "fields_count";
    public String FIELDS           = "fields";
    public String METHODS_COUNT    = "methods_count";
    public String METHODS          = "methods";
    public String ATTRIBUTES_COUNT = "attributes_count";
    public String ATTRIBUTES       = "attributes";

}
