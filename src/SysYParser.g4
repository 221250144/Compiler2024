parser grammar SysYParser;

options {
    tokenVocab = SysYLexer;//注意使用该语句指定词法分析器；请不要修改词法分析器或语法分析器的文件名，否则Makefile可能无法正常工作，影响评测结果
}

program : compUnit ;
compUnit : (funcDef | decl)+ EOF;
decl : constDecl | varDecl ;                      // 声明：常量声明或变量声明

constDecl : CONST bType constDef ( ',' constDef )* ';' ;    // 常量声明：常量 类型 常量定义 (',' 常量定义)* ';'
bType : INT ;                                    // 类型：整型
constDef : IDENT ( '[' constExp ']' )* '=' constInitVal ;    // 常量定义：标识符 ('[' 常量表达式 ']')* '=' 常量初始值
constInitVal : constExp | '{' (constInitVal (',' constInitVal)* )?'}' ;    // 常量初始值：常量表达式 | '{' (常量初始值 (',' 常量初始值)* )? '}'

varDecl : bType varDef (',' varDef)* ';' ;      // 变量声明：类型 变量定义 (',' 变量定义)* ';'
varDef : IDENT ('[' constExp ']')*
        | IDENT ('[' constExp ']')* '=' initVal ;    // 变量定义：标识符 ('[' 常量表达式 ']')* | 标识符 ('[' 常量表达式 ']')* '=' 初始化值
initVal : exp | '{'  (initVal (',' initVal)* )? '}' ;    // 初始化值：表达式 | '{'  (初始化值 (',' 初始化值)* )? '}'

funcDef : funcType funcName '(' funcFParams? ')' block ;    // 函数定义：函数类型 标识符 '(' 函数形参? ')' 代码块
funcName : IDENT ;                                // 函数名：标识符
funcType : 'void' | 'int' ;                      // 函数类型：void 或 int
funcFParams : funcFParam (',' funcFParam)* ;      // 函数形参：函数形参 (',' 函数形参)*
funcFParam : bType IDENT ('[' ']' ('[' exp ']')* )? ;    // 函数形参：类型 标识符 ('[' ']' ('[' 表达式 ']')* )?
block : '{' (blockItem)* '}' ;                    // 代码块：'{' (代码块项)* '}'
blockItem : decl | stmt ;                        // 代码块项：声明 | 语句
stmt : lVal '=' exp ';'        # ASSIGN                   // 语句：左值 '=' 表达式 ';'
            | exp? ';'         #  NOT_BLOCK                 //       | 表达式? ';'
            | block          # BLOCK                         //       | 代码块
            | 'if' '(' cond ')' stmt ('else' stmt)?  #  IF_ELSE //       | 'if' '(' 条件 ')' 语句 ('else' 语句)?
            | 'while' '(' cond ')' stmt          # WHILE  //       | 'while' '(' 条件 ')' 语句
            | 'break' ';'                          # BREAK  //       | 'break' ';'
            | 'continue' ';'                      #   CONTINUE//       | 'continue' ';'
            | 'return' exp ';'          #  RETURN        //       | 'return' 表达式? ';'
            | 'return' ';'              #  RETURN          //       | 'return' ';'
            ;
//elseStmt : 'else' stmt ;                        // else语句：'else' 语句
exp
   : '(' exp ')'                 # REEXP         // 表达式：'(' 表达式 ')'
   | lVal                                 # RESERVE         //        | 左值
   | number                             # NUMBER          //        | 数字
   | funcName '(' funcRParams? ')'        # FUNCCALL   //        | 标识符 '(' 函数实参? ')'
   | unaryOp exp                       # SINGLE           //        | 一元操作符 表达式
   | exp (MUL | DIV | MOD) exp          # MULEXP          //        | 表达式 (乘 | 除 | 求余) 表达式
   | exp (PLUS | MINUS) exp             # PLUSEXP          //        | 表达式 (加 | 减) 表达式
   ;

cond
   : exp                               # ONLY_EXP            // 条件：表达式
   | cond (LT | GT | LE | GE) cond     # ONE          //       | 条件 (小于 | 大于 | 小于等于 | 大于等于) 条件
   | cond (EQ | NEQ) cond              # ONE           //       | 条件 (等于 | 不等于) 条件
   | cond AND cond                     # AND_COND           //       | 条件 AND 条件
   | cond OR cond                      # OR_COND           //       | 条件 OR 条件
   ;

lVal: IDENT ('[' exp ']')* ;              // 左值：标识符 ('[' 表达式 ']')*;

number : INTEGER_CONST ;                               // 数字：整数常量

unaryOp
   : PLUS                                          // 一元操作符：正号
   | MINUS                                         //           | 负号
   | NOT                                           //           | 非
   ;

funcRParams
   : param (',' param)*                         // 函数实参：参数 (',' 参数)*
   ;

param : exp ;                                         // 参数：表达式

constExp : exp ;                                         // 常量表达式：表达式