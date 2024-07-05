public enum ErrorType {
    RESERVE, // 0
    UNDEF_VAR, // 1、未定义的变量
    UNDEF_FUNC, // 2、未定义的函数
    REDEF_VAR, // 3、重复定义的变量
    REDEF_FUNC, // 4、重复定义的函数
    TYPEERR_ASSIGN, // 5、赋值号两边的表达式类型不匹配
    TYPEERR_OP, // 6、运算符两边的表达式类型不匹配
    TYPEERR_RETURN, // 7、返回值类型不匹配
    PARAMSERR, // 8、函数调用参数不匹配
    USEINDEX_NOTARRAY, // 9、对非数组变量使用下标
    CALLFUNC_VAR, // 10、对非函数变量使用函数调用
    ASSIGN_FUNC // 11、对函数进行赋值
}
