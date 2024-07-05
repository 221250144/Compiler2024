lexer grammar SysYLexer;

CONST : 'const';

INT : 'int';

VOID : 'void';

IF : 'if';

ELSE : 'else';

WHILE : 'while';

BREAK : 'break';

CONTINUE : 'continue';

RETURN : 'return';

PLUS : '+';

MINUS : '-';

MUL : '*';

DIV : '/';

MOD : '%';

ASSIGN : '=';

EQ : '==';

NEQ : '!=';

LT : '<';

GT : '>';

LE : '<=';

GE : '>=';

NOT : '!';

AND : '&&';

OR : '||';

L_PAREN : '(';

R_PAREN : ')';

L_BRACE : '{';

R_BRACE : '}';

L_BRACKT : '[';

R_BRACKT : ']';

COMMA : ',';

SEMICOLON : ';';

IDENT : ('_' | LETTER) WORD* ;//以下划线或字母开头，仅包含下划线、英文字母大小写、阿拉伯数字

INTEGER_CONST : [1-9]NUMBER* | '0' // 十进制
    | '0' [xX] [0-9a-fA-F]+ // 十六进制
    | '0' [0-7]+ // 八进制
    ;

WS : [ \r\n\t]+ -> skip ;

LINE_COMMENT : '//' .*? '\n' -> skip;

MULTILINE_COMMENT : '/*' .*? '*/' -> skip ;

fragment NUMBER : [0-9];
fragment LETTER : [a-zA-Z];
fragment WORD : [a-zA-Z0-9_];
fragment TYPE : INT | VOID;
fragment OP1 : MUL | DIV | MOD;
fragment OP2 : PLUS | MINUS;
fragment OP3 : LT | GT | LE | GE;
fragment OP4 : EQ | NEQ;
fragment OP5 : AND | OR;