# SysY_Compiler_2023

北航2023年编译原理源代码

# SysY编译器设计文档

**姓名：杜金阳**

**学号：21373191**

## 编译器总体设计

> 完整SysY编译器代码见——[SysY_Compiler_2023: 北航2023年编译原理源代码](https://github.com/Charles2530/SysY_Compiler_2023)

编译器完整文法如下：

```markdown
编译单元    CompUnit → {Decl} {FuncDef} MainFuncDef  
声明  Decl → ConstDecl | VarDecl
常量声明    ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // i
基本类型    BType → 'int'
常数定义    ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal  // b k
常量初值    ConstInitVal → ConstExp
    | '{' [ ConstInitVal { ',' ConstInitVal } ] '}' 
变量声明    VarDecl → BType VarDef { ',' VarDef } ';' // i
变量定义    VarDef → Ident { '[' ConstExp ']' } // b
    | Ident { '[' ConstExp ']' } '=' InitVal // k
变量初值    InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
函数定义    FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // b g j
主函数定义   MainFuncDef → 'int' 'main' '(' ')' Block // g j
函数类型    FuncType → 'void' | 'int' 
函数形参表   FuncFParams → FuncFParam { ',' FuncFParam } 
函数形参    FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]  //   b k
语句块     Block → '{' { BlockItem } '}' 
语句块项    BlockItem → Decl | Stmt 
语句  Stmt → LVal '=' Exp ';' | [Exp] ';' | Block // h i
    | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    | 'for' '('[ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    | 'break' ';' | 'continue' ';' // i m
    | 'return' [Exp] ';' // f i
    | LVal '=' 'getint''('')'';' // h i j
    | 'printf''('FormatString{,Exp}')'';' // i j l
语句 ForStmt → LVal '=' Exp   //h
表达式 Exp → AddExp 注：SysY 表达式是int 型表达式 
条件表达式   Cond → LOrExp 
左值表达式   LVal → Ident {'[' Exp ']'} // c k
基本表达式   PrimaryExp → '(' Exp ')' | LVal | Number 
数值  Number → IntConst 
一元表达式   UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' // c d e j
        | UnaryOp UnaryExp 
单目运算符   UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中 
函数实参表   FuncRParams → Exp { ',' Exp } 
乘除模表达式  MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp 
加减表达式   AddExp → MulExp | AddExp ('+' | '−') MulExp 
关系表达式   RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
相等性表达式  EqExp → RelExp | EqExp ('==' | '!=') RelExp
逻辑与表达式  LAndExp → EqExp | LAndExp '&&' EqExp
逻辑或表达式  LOrExp → LAndExp | LOrExp '||' LAndExp 
常量表达式   ConstExp → AddExp 注：使用的Ident 必须是常量
格式字符串:  <FormatString> → '"'{<Char>}'"' // a
```

### 基于Java语言设计

本SysY编译器选择了采用**Java语言**进行编写，充分发挥了Java**面向对象编程**的特点，这样相比于C++更有利于大工程的设计，但也带来了代码冗长的问题，所以本编译器及其重视设计一个好的架构以**减少不必要的重构**，并使代码风格更加易读。

### 总体结构

本编译器的总体设计结构参考了教程中的编译顺序，即**词法分析**、**语法分析**、**错误处理**、**中间代码生成**、**生成mips代码**、**中端代码优化**、**后端代码优化**几个部分，文件夹具体结构如下：

```markdown
├─backend
│  ├─generation
│  │  ├─mips
│  │  │  └─asm
│  │  │      ├─datasegment
│  │  │      └─textsegment
│  │  │          ├─complex
│  │  │          ├─mipsinstr
│  │  │          └─structure
│  │  └─utils
│  └─simplify
│      └─method
├─frontend
│  ├─generation
│  │  ├─lexer
│  │  ├─semantic
│  │  │  ├─symtable
│  │  │  │  └─symbol
│  │  │  │      └─varsymbol
│  │  │  └─utils
│  │  └─syntax
│  │      ├─decl
│  │      ├─funcdef
│  │      ├─mainfuncdef
│  │      └─utils
│  └─simplify
│      └─method
├─iostream
│  ├─declare
│  └─structure
└─midend
    ├─generation
    │  ├─llvm
    │  ├─utils
    │  │  └─irtype
    │  └─value
    │      ├─construction
    │      │  ├─procedure
    │      │  └─user
    │      └─instr
    │          ├─basis
    │          └─optimizer
    └─simplify
        ├─controller
        │  └─datastruct
        └─method
```

## 词法分析

### 总体设计

词法分析算是整个编译器设计较为简单的部分，承担的任务就是 **通过扫描输入的源程序字符串，将其分割成一个个单词，同时记录这些单词的类别信息** 。于此同时，词法分析还需要去除那些对于编译没用的符号，例如各种空白符和注释等。经过词法分析器的解析，我们就可以从词法分析器依次获取每个单词的信息，包括 **单词值和单词类别** ，用于后续的编译。

对此，我在词法分析设计方面主要设计了三个类来辅助**词法分析处理**

- `LexicalAnalysis`(词法分析器)——用于**保存保留字**，并根据单词**生成对应的类别码**
- `LexicalWordCheck`(词法分类检查)——用于**分割语句**形成对应的词法单词，并负责词法分析过程的**错误处理**
- `SymToken`(标识符存储单位)——用于**存储词法分析形成的信息**，包括每个单词的类别码、值、所在行数等

具体而言，在`Compiler`主类中从文件中读取完整的源码，然后按行输入`LexicalAnalysis`进行分析，之后在`LexicalWordCheck`中由``if-else``分支语句进行类别判断，然后进入对应分支读取完整单词，得到一个`SymToken`实例，不断重复上述操作直到读到源码末尾。

* 对于保留字采用``HashMap``进行查寻，保留字为键，类别码为值，当得到一个保留字单词就查询``HashMap``获得类别码
* 对于注释空白符跳过不识别，即直接读到注释之后的第一个字符
* 对于行号记录，在`Compiler`每次循环前将行号加一

> 注：考虑到评测系统为``Linux``系统于是对于要跳过的空白符需要注意判断``\r``的情况，当然也可以直接调用方法直接跳过空白符。

### 词法错误处理

在词法分析部分，需要处理的词法分析错误只有一种—— **字符串中出现非法字符** 。除下面三类以外，均为非法字符——

- 十进制编码为32,33,40-126的ASCII字符
- （编码92）出现当且仅当为'\n'
- \<FormatChar>==>'%d'

判断使用正则表达式即可，注意要将%d和\n作为一个整体进行判断。

```java
private boolean checkIllegalSym(String word) {
    String legalSym = "\"([ !()*+,\\-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ\\[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~]|(%d)|(\\\\n))*\"";
    return !word.matches(legalSym);
}
```

## 语法分析

### 总体设计

语法分析部分的主要任务是让我们**按照课程组给定的文法构造相应的抽象语法树(`AST`)，并输出后序遍历`AST`树的结果**即可完成该部分任务。所以我采用在词法分析部分先进行**一遍的扫描**来获得词法分析分析出来的`token`集合，这样虽然理论上并没有边读词法边分析文法效率高，但如果综合考**虑由于文法的`FIRST`集合的交集存在导致的词法预读现象**，最终两种方式获得的分析效率其实相差无几，但预先分析出词法`token`集合可以很好地**减少编码出现错误的概率**，同时也可以使**整个编译过程更加结构清晰，架构完整,实现高内聚低耦合**。

而具体到语法分析部分使用的方法，我采用了**递归下降子程序**的方法来构建`AST`树，并通过设计`Definer`类和`Judge`类来**避免了大量的重复造轮子**的操作，具体操作细节请看下文。

#### 构造AST生成图辅助设计文法分析程序

在参加了编译大赛的同学那里了解到了一个神器——**`antlr4`**，这是一个`IDEA`的插件，通过它只要你给定设计好的词法和文法，再给定符合词法和文法的程序，你就可以**直观的看到相应代码建立`AST`树的结果**，这将对我们更加深入理解文法分析的过程和之后的`debug`工作做出巨大的贡献。

> 据说`antlr4`甚至可以直接帮你生成词法分析和语法分析程序，但这肯定是课程组不允许的，所以我就没有探索这部分的内容

下图为用`antlr4`分析`helloworld`程序构造的语法树(由于每年的文法可能有差异，可能会有细微的不同)。

![parseTree](https://charles2530.github.io/image/SysY-compiler-design-doc/parseTree.png)

#### 改写左递归文法

由于我采用的是递归下降分析法，所以**为了避免死循环的出现**，一个非常重要的步骤就是**去除文法中的左递归**(左递归会导致死循环应该编译理论课已经讲过了)，通过仔细分析课程组的文法，不难发现主要的左递归都出现在了**Exp表达式**那个部分中(如下图)，所以我们要找到合适的方法改写这部分左递归。

![image-20230926084756548](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20230926084756548.png)

关于如何修改左递归，课程组已经在教程和语法分析的专题讲座中给我们了一部分的思路，所以具体而言我采用了**改写为`BNF`范式并进行了少量修改以达到和课程组给定的`AST`树结构相同的目的**，下面以`AddExp`和`MulExp`举例。

原本存在左递归的文法是这样的。

```markdown
AddExp : MulExp | AddExp ('+' | '−') MulExp
MulExp : UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
```

所谓改写为`BNF`范式，具体而言通过观察不难发现，`AddExp`本质上是由若干个`MulExp`组成，`MulExp`本质上是由若干个`UnaryExp`组成，所以改写后的文法是这样的。

```markdown
AddExp : MulExp {('+' | '−') MulExp}
MulExp : UnaryExp {('*' | '/' | '%') UnaryExp}
```

这样我们就成功消除了左递归，但这就结束了吗，并不是，由于我们修改了文法，所以造成了我们的文法和课程组要求的`AST`树出现了结构上的差异，所以我们在实际编写代码时还需要进行一些小的修改，比如在生成`AddExp`时，可以在每次解析`('+' | '−') MulExp`之前，先将之前已经解析出的若干个`MulExp`合成一个`AddExp`，最后再将最终的`AddExp`加入调用该节点的父节点，**具体到代码中实际就是一个不停更换父节点的过程**，代码示例如下。

```java
private static void AddExp(AstNode constExpNode) {
    AstNode addExpNode = new AstNode("<AddExp>");
    MulExp(addExpNode);
    AstNode father = addExpNode;
    while (getPreSym().equals("PLUS") || getPreSym().equals("MINU")) {
        // Removed part unimportant code
        father = extraAddExpNode;
        if (getPreSym().equals("PLUS")) {
            // Removed part unimportant code
            MulExp(father);
        } else if (getPreSym().equals("MINU")) {
            // Removed part unimportant code
            MulExp(father);
        }
    }
    constExpNode.addChild(father);
}
```

这样左递归文法的问题就迎刃而解了。

#### 语法分析具体设计

在**语法分析**部分，我认为是编译过程一个非常重要的过程，它可以帮助我们**将词法分析获得的`token`集合进行进一步的分析，将其建立成结构化的语法树，以便于后续的编译**。

而在实际编写代码的过程中，首先我建立了`SyntaxAnalysis`(语法分析类)、`AstNode`(抽象语法树节点类)两个类来负责**语法树递归下降的调用和后序遍历的实现**，并为建立`AST`树打下基础。

具体到递归下降部分而言，我首先根据课程给定文法的开始符号`CompUnit`的给定文法将建立了`AstRecursion`类来模拟递归下降过程的开始，并根据起始文法(如下图)将`CompUnit`分为了`Decl`，`FuncDef`和`MainFuncDef`三个类，从而编写分别属于这几个部分不被共用的语法部分。

![image-20230926093020645](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20230926093020645.png)

之后我又编写了`Judge`类和`Definer`类来定义了**在递归下降过程中上述三个部分需要共用的语法成分的判断条件和具体操作**。这样在结构比较清晰的情况下我们就很好地实现了递归下降结构的设计，之后只需要按照给定的文法定义相关结构即可，在此不再具体赘述。

#### 处理预读操作

在我的实际编码过程中，在处理`Stmt`时，由于`FIRST`集合存在交集，所以我们需要进行**预读操作**来将这两个成分分开，具体而言我定义了下面这个函数来获得相对于当前`token`的位置前几个或后几个`token`的具体值。

```java
public static SymToken getNextSymToken(int pos) {
    if (symPos + pos > symTokens.size() - 1 || symPos + pos < 0) {
        return null;
    }
    return symTokens.get(symPos + pos);
}
```

这样我们就可以更加灵活的定义条件来避免FIRST集有交集的情况出现了。

### 语法错误处理

在编写语法分析部分时，随着错误处理种类的不断增加，为了更好地提高错误处理的效率，所以我设计了**`ErrorController`类并在类中提供了错误类别码来辅助我进行相关的错误不同种类的识别和精确处理**。而在语法分析方面，我们主要要处理三类错误——**缺少分号**、**缺少右小括号**和**缺少右中括号**，这三种错误处理要求如下表。(注意是报错行号是给定符号**前一个非终结符**所在的行号)

| 错误类型        | 错误类别码 | 解释                                           | 对应文法                                                     |
| --------------- | ---------- | ---------------------------------------------- | ------------------------------------------------------------ |
| 缺少分号        | i          | 报错行号为分号**前一个非终结符**所在行号。     | \<Stmt>,\<ConstDecl>及\<VarDecl>中的';’                      |
| 缺少右小括号    | j          | 报错行号为右小括号**前一个非终结符**所在行号。 | 函数调用(\<UnaryExp>)、函数定义(\<FuncDef>)及\<Stmt>中的')’  |
| 缺少右中括号’]’ | k          | 报错行号为右中括号**前一个非终结符**所在行号。 | 数组定义(\<ConstDef>,\<VarDef>,\<FuncFParam>)和使用(\<LVal>)中的']’ |

这三种错误处理还是比较简单的，只需要在文法读到相应的部分去申请一个`ErrorController`对象并填入相应的错误类别码即可，还是比较简单的。

## 错误处理

错误分析部分的主要任务是通过建立并**管理符号表**，从而实现对**语义分析的控制**，进而控制判断所建立的语法树中出现的语义错误。

> 语法错误部分已经在之前的词法分析和语法分析中处理完成

### 符号表建立

要想建立行之有效的语义分析模块，一个非常重要的先决条件就是建立符号表，本文设计的符号表建立在栈式符号表的基础上，**通过下标模拟入栈退栈来实现了实际为“类树状结构”的栈式符号表**。

#### 符号表的组成

符号表由`Symbol`、`StackSymbolTable`、`SymbolTable`三部分组成。

##### Symbol

本文创建了一个`Symbol`这一符号类，用于存储符号的基本信息。然后由该类派生出`VarSymbol`、`ConstSymbol`、`FuncSymbol`三个子类，用来存储不同类型符号的信息——

- **`VarSymbol`**：变量名，变量值类型（int），变量维度，变量各维度的长度，变量初始值。
- **`ConstSymbol`**：常量名，常量值类型（int），常量维度，常量各维度的长度，常量初始值。
- **`FuncSymbol`**：函数名，函数返回值类型（int/void），函数各形参类型，函数各形参维度。

> > 通过符号表可以算出**全局常量的具体值**，与此同时全局变量的初始值可以在编译阶段计算出，因此对于非全局的常量和变量，其“变量初始值”一项为null

```java
public class Symbol {
    public enum SymType {
        INT, VOID, CONST
    }
    private String symbolName;
    private SymType symbolType;
    private Integer symbolLevel;
}
```

- 这里设计了枚举类`SymType`来区分函数的类型（`VOID`，`INT`）和变量的类型（`INT`，`CONST`），由于有变量维度dim的存在，所以不需要额外设计`ARRAY`和`CONST_ARRAY`类型来区分变量类型和数组类型。

##### StackSymbolTable

栈式符号表类`StackSymbolTable`可以是一层栈式符号表，存储了**属于当前作用域的所有符号**。

```java
public class StackSymbolTable {
    private HashMap<String, Symbol> symbols;
}
```

- `symbols`的第一维存储的是这个作用域某个`symbol`的`name`，通过名字可以索引出这个对象。

##### SymbolTable

符号表类`SymbolTable`从全局角度管理了属于该程序的所有符号表，方便各层栈式符号表的**入栈出栈**及从全局的角度实现了各`symbol`的**插入删除**需求。

```java
public class SymbolTable {
    private static HashMap<Integer, ArrayList<StackSymbolTable>> symbolTables;
    private static boolean isGlobalArea;
    private static int loopLevel;
    private static FuncSymbol currentFunc;
    private static int curLevel;
}
```

- `symbolTables`第一维存储的是**某栈式符号表所在的维数信息**，第二维是一个数组存储了**在这一维所有的栈式符号表信息**。
- `isGlobalArea`记录了此时的`ConstDecl`和`VarDecl`是否为全局变量，从而决定是否适用`symCalc`调用符号表算出对应的值。
- `loopLevel`用于记录当前所处的循环维数，从而判断此时出现`break`和`continue`是否符合语义。
- `currentFunc`用于记录当前正在解析的函数所对应的符号，用与检查函数中是否出现不匹配的`return`语句。
- `curLevel`用于记录当前`symbolTable`所处的维数信息。

### 语义错误处理

#### 错误处理总览

| 错误类型                             | 错误类别码 | 解释                                                         | 对应文法及出错符号 ( … 表示省略该条规则后续部分)             |
| ------------------------------------ | ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 非法符号                             | a          | 格式字符串中出现非法字符报错行号为 **\<FormatString>** 所在行数。 | \<FormatString> → ‘“‘{\<Char>}’”                             |
| 名字重定义                           | b          | 函数名或者变量名在**当前作用域**下重复定义。注意，变量一定是同一级作用域下才会判定出错，不同级作用域下，内层会覆盖外层定义。报错行号为 **\<Ident>** 所在行数。 | \<ConstDef>→\<Ident> … \<VarDef>→\<Ident> … \<Ident> … \<FuncDef>→\<FuncType>\<Ident> … \<FuncFParam> → \<BType> \<Ident> … |
| 未定义的名字                         | c          | 使用了未定义的标识符报错行号为 **\<Ident>** 所在行数。       | \<LVal>→\<Ident> … \<UnaryExp>→\<Ident> …                    |
| 函数参数个数不匹配                   | d          | 函数调用语句中，参数个数与函数定义中的参数个数不匹配。报错行号为函数调用语句的**函数名**所在行数。 | \<UnaryExp>→\<Ident>‘(’[\<FuncRParams>]‘)’                   |
| 函数参数类型不匹配                   | e          | 函数调用语句中，参数类型与函数定义中对应位置的参数类型不匹配。报错行号为函数调用语句的**函数名**所在行数。 | \<UnaryExp>→\<Ident>‘(’[\<FuncRParams>]‘)’                   |
| 无返回值的函数存在不匹配的return语句 | f          | 报错行号为 **‘return’** 所在行号。                           | \<Stmt>→‘return’ {‘[’\<Exp>’]’}‘;’                           |
| 有返回值的函数缺少return语句         | g          | 只需要考虑函数末尾是否存在return语句，**无需考虑数据流**。报错行号为函数**结尾的’}’** 所在行号。 | \<FuncDef> → \<FuncType> \<Ident> ‘(’ [\<FuncFParams>] ‘)’ \<Block> \<MainFuncDef> → ‘int’ ‘main’ ‘(’ ‘)’ \<Block> |
| 不能改变常量的值                     | h          | \<LVal>为常量时，不能对其修改。报错行号为 **\<LVal>** 所在行号。 | \<Stmt>→\<LVal>‘=’ \<Exp>‘;’ \<Stmt>→\<LVal>‘=’ ‘getint’ ‘(’ ‘)’ ‘;’ |
| 缺少分号                             | i          | 报错行号为分号**前一个非终结符**所在行号。                   | \<Stmt>,\<ConstDecl>及\<VarDecl>中的’;’                      |
| 缺少右小括号’)’                      | j          | 报错行号为右小括号**前一个非终结符**所在行号。               | 函数调用(\<UnaryExp>)、函数定义(\<FuncDef>)及\<Stmt>中的’)’  |
| 缺少右中括号’]’                      | k          | 报错行号为右中括号**前一个非终结符**所在行号。               | 数组定义(\<ConstDef>,\<VarDef>,\<FuncFParam>)和使用(\<LVal>)中的’]’ |
| printf中格式字符与表达式个数不匹配   | l          | 报错行号为 **‘printf’** 所在行号。                           | \<Stmt> →‘printf’‘(’\<FormatString>{,\<Exp>}’)’‘;’           |
| 在非循环块中使用break和continue语句  | m          | 报错行号为 **‘break’** 与 **’continue’** 所在行号。          | \<Stmt>→‘break’‘;’ \<Stmt>→‘continue’‘;’                     |

- 在上文所示的表格中，a,i,j,k属于**语法错误**，已经在之前**词法分析**和**语法分析**部分完成了处理

#### 语义分析单元

##### ErrorController和ErrorToken

和之前处理语法错误类似，各种语义错误也需要在`ErrorController`中设置相关的错误处理函数，由于题目要求输出错误需要按照行号顺序依次输出，所以我们需要先用容器将相关的错误信息存储下来，之后再按照行号大小进行排序后统一输出，所以我重新建立了错误符号类`ErrorToken`来存储每个错误的错误码及行号等信息，并在`ErrorController`中统一输出。

```java
public class ErrorToken {
    private String errorCategoryCode;
    private int lineNum;
}
```

- `errorCategoryCode`为当前的错误码，`lineNum`为出现该错误时的行号

##### Span

语义分析中有许多部分要求我们输出某个非终结符的行号，但在之前的设计中只有词法分析部分记录了所有终结符的行号信息，于此同时记录各个部分所处的行号范围对后续的`debug`过程也有着巨大的帮助，所有我设计了范围类`Span`来记录符号集中各符号所处的范围起始行数和终结行数信息。

```java
public class Span {
    private int startLine;
    private int endLine;
}
```

##### 语义分析处理

在语义分析处理部分我写了四个工具类`symChecker`，`symDefiner`,`symType`,`symCalc`来辅助我建立符号表并输出错误信息。

> 下面给出各个工具类的部分代码

- `symChecker`类：检查类，用于对各个`AstNode`进行错误检查并输出对应的`Symbol`变量。

```java
public void check(AstNode rootAst) throws IOException {
    switch (rootAst.getGrammarType()) {
        case "<CompUnit>":
            checkCompUnitChecker(rootAst);
            break;
        case "<ConstDef>":
            // Error b
            checkConstDefChecker(rootAst);
            break;
        case "<VarDef>":
            // Error b
            checkVarDefChecker(rootAst);
            break;
        case "<Block>":
            checkBlockChecker(rootAst);
            break;
        case "FORTK":
            checkForStmtChecker(rootAst);
            break;
        case "BREAKTK":
            // Error m
            checkBreakStmtChecker(rootAst);
            break;
        case "CONTINUETK":
            // Error m
            checkContinueStmtChecker(rootAst);
            break;
        case "RETURNTK":
            // Error f
            checkReturnStmtChecker(rootAst);
            break;
        case "<LVal>":
            // Error c
            checkLValChecker(rootAst);
            break;
        case "<FuncDef>":
            // Error b,g
            checkFuncDefChecker(rootAst);
            break;
        case "<FuncFParam>":
            // Error b
            checkFuncFParamChecker(rootAst);
            break;
        case "<MainFuncDef>":
            // Error b,g
            checkMainFuncDefChecker(rootAst);
            break;
        case "ASSIGN":
            // Error h
            checkASSIGNChecker(rootAst);
            break;
        case "<UnaryExp>":
            // Error b,c,d,e
            checkUnaryExpChecker(rootAst);
            break;
        case "PRINTFTK":
            // Error l
            checkPRINTFTKChecker(rootAst);
            break;
        default:
            SemanticAnalysis.preTraverse(rootAst);
            break;
    }
}
```

- `symDefiner`类：工具类，用于对`AstNode`进行一些辅助操作，如给函数引入形式参数，求解某个`Exp`对应的类型维数信息等。

```java
public static void setParamInfo(AstNode astNode, FuncSymbol symbol) {
    ... ...
}
public static Integer getExpDim(AstNode astNode) {
    switch (astNode.getGrammarType()) {
        case "<Number>":
            return getExpDimNumber(astNode);
        case "<LVal>":
            return getExpDimLVal(astNode);
        case "<UnaryExp>":
            return getExpDimUnaryExp(astNode);
        case "<PrimaryExp>":
            return getExpDimPrimaryExp(astNode);
        default:
            return getExpDim(astNode.getChildList().get(0));
    }
}
```

- `symType`类：类别类，利用递归下降求解某变量或函数对应的参数类型。

```java
public static Symbol.SymType getExpType(AstNode astNode) {
    switch (astNode.getGrammarType()) {
        case "<Number>":
            return getExpTypeNumber(astNode);
        case "<LVal>":
            return getExpTypeLVal(astNode);
        case "<UnaryExp>":
            return getExpTypeUnaryExp(astNode);
        case "<PrimaryExp>":
            return getExpTypePrimaryExp(astNode);
        default:
            return getExpType(astNode.getChildList().get(0));
    }
}
```

- `symCalc`类：计算类，书写了大量计算函数，用于计算可以立即被符号表计算出结果的信息，如全局变量区的`VarDef`和`ConstDef`的初始值。

```java
public static int calc(AstNode astNode) {
    switch (astNode.getGrammarType()) {
        case "<Exp>", "<ConstExp>":
            return calc(astNode.getChildList().get(0));
        case "<AddExp>":
            return calcAddExp(astNode);
        case "<MulExp>":
            return calcMulExp(astNode);
        case "<UnaryExp>":
            return calcUnaryExp(astNode);
        case "<PrimaryExp>":
            return calcPrimaryExp(astNode);
        case "<Number>":
            return calcNumber(astNode);
        case "<LVal>":
            return calcLValExp(astNode);
        default:
            return 0;
    }
}
```

通过上面定义的这些工具类，我们就可以很有效的处理各种语义错误了。具体调用方法就是和语法分析过程类似，通过一个后续遍历让每个`astNode`调用`check`函数来按照顺序检查每个变量即可。

##### 函数参数匹配问题

在这次作业中，相比下比较困难的部分时我们需要处理函数参数个数和维度匹配的问题。函数参数个数匹配比较简单，难的是函数维度的匹配（维度匹配包括维度个数的匹配、各维度长度的比较）。有些变量在定义时，纬度的长度不是简单的常数，而是用常量表达式（即`ConstInitVal`）来表示长度。因此，为了能够进行匹配，我们需要进行在编译时将该长度计算出来。本文采用的方法是，给`Exp`、`MulExp`、`AddExp`等类都编写一个`evaluate`方法。在需要计算`Exp`的值时，即调用`Exp`的`evaluate`方法，在这个方法中又调用`AddExp`的`evaluate`……以此类推，最终`Exp`的`evaluate`返回的就是整个表达式的值。

```java
for (int i = 0; i < paramNum; i++) {
    Symbol.SymType paramType = funcSymbol.getFparamTypes().get(i);
    Symbol.SymType argType = SymTypeJudge.getExpType(childList.get(i));
    Integer paramdim = funcSymbol.getFparamDims().get(i);
    Integer argdim = SymDefiner.getExpDim(childList.get(i));
    if (!paramType.equals(argType) || !paramdim.equals(argdim)) {
        ErrorController.addError(new ErrorToken("e",rootAst.getSpan().getStartLine()));
    }
}
```

## 中间代码生成

中间代码生成主要分为**两个重要部分**，第一个是重新进行语义分析**重建栈式符号表**从而将各个部分**转化为`llvm_ir`**的格式，另一个重要任务是利用`llvm`中一切皆value的思想**重新建立起一棵由不同种类的value组成的新语法树**，最后生成中间代码其实就是对新语法树的一个**后序遍历**过程。

### `llvm_ir`转化

要想实现`llvm_ir`的转化，我们首先需要**清空之前错误处理时建立的符号表**，并重新开始`AST`的遍历，并将遍历到的各种不同成分的`AstNode`进行处理即可，遍历及处理的代码如下：

```java
//GenerationMain.java
public static void preTraverse(AstNode rootAst) {
    if (rootAst.isLeaf()) {
        return;
    }
    for (AstNode astNode : rootAst.getChildList()) {
        GenerationMain.llvmGenIR.genIrAnalysis(astNode);
        if (astNode.getGrammarType().matches(
            "IFTK|FORTK|BREAKTK|CONTINUETK|RETURNTK|PRINTFTK|ASSIGN")) {
            break;
        }
    }
}
//LLvmGenIr.java
public Value genIrAnalysis(AstNode rootAst) {
    return switch (rootAst.getGrammarType()) {
        case "<CompUnit>" -> genIrCompUnitChecker(rootAst);
            //Definer.java
        case "<ConstDef>" -> genIrConstDefChecker(rootAst);
        case "<VarDef>" -> genIrVarDefChecker(rootAst);
        case "<Block>" -> genIrBlockChecker(rootAst);
        case "IFTK" -> genIrIfStmtChecker(rootAst);
        case "FORTK" -> genIrForStmtChecker(rootAst);
        case "BREAKTK" -> genIrBreakStmtChecker();
        case "CONTINUETK" -> genIrContinueStmtChecker();
        case "RETURNTK" -> genIrReturnStmtChecker(rootAst);
        case "PRINTFTK" -> genIrPrintStmtChecker(rootAst);
        case "<Exp>","<ConstExp>" -> genIrExpChecker(rootAst);
        case "<PrimaryExp>" -> genIrPrimaryExpChecker(rootAst);
        case "<Number>" -> genIrNumberCallChecker(rootAst);
        case "<UnaryExp>" -> genIrUnaryExpChecker(rootAst);
        case "<MulExp>" -> genIrMulExpChecker(rootAst);
        case "<AddExp>" -> genIrAddExpChecker(rootAst);
        case "<RelExp>" -> genIrRelExpChecker(rootAst);
        case "<EqExp>" -> genIrEqExpChecker(rootAst);
            //FuncDef.java
        case "<FuncDef>" -> genIrFuncDefChecker(rootAst);
        case "<FuncFParam>" -> genIrFuncFParamChecker(rootAst);
            //MainFuncDef.java
        case "<MainFuncDef>" -> genIrMainFuncDefChecker(rootAst);
            //Lexer_part
        case "ASSIGN" -> genIrAssignChecker(rootAst);
        default -> {
            GenerationMain.preTraverse(rootAst);
            yield null;
        }
    };
}
```

这里需要注意，由于文法分析的限制，所以我们在**判断`stmt`的具体种类**时不可避免的需要用到叶子节点，而叶子节点访问后需要回溯到其父节点进行对应的处理，这样就会**造成二次访问**，所以我们需要在其叶节点进行过一次访问后跳出循环。

由于`llvm`中有着**一切皆`value`的思想**，所以我们可以选择将该函数的**返回值**改为`Value`，这样就可以在处理之后归于统一，从而维护更好的代码风格也更有利于管理。除此之外，在实际进行处理时，我发现有许多方法可以重复复用，所以我建立了`LLvmGenUtils`类来**处理这些可以重用的轮子**。

#### `For`循环的处理

在实际操作时，`for`循环给我带来了不少的麻烦，主要相比于`while`循环，`for`循环的`forstmt`和`cond`均可以**缺省**，这也就导致在实际转化为`llvm`时产生的`BasicBlock`的数量会动态变化，可以说是一大难点了，需要重点注意,下面为我处理`for`循环转化部分的代码。

```java
private Value genIrForStmtChecker(AstNode sonAst) {
    AstNode rootAst = sonAst.getParent();
    AstNode forStmtVal1 = null;
    AstNode condAst = null;
    AstNode forStmtVal2 = null;
    for (int i = 0; i < rootAst.getChildList().size(); i++) {
        if (rootAst.getChildList().get(i).getGrammarType().equals("<ForStmt>")) {
            if (i == 2) {
                forStmtVal1 = rootAst.getChildList().get(i);
            } else {
                forStmtVal2 = rootAst.getChildList().get(i);
            }
        } else if (rootAst.getChildList().get(i).getGrammarType().equals("<Cond>")) {
            condAst = rootAst.getChildList().get(i);
        }
    }
    if (forStmtVal1 != null) {
        genIrAnalysis(forStmtVal1);
    }
    SymbolTable.enterLoop();
    BasicBlock condBlock = null;
    if (condAst != null) {
        condBlock = new BasicBlock(IrNameController.getBlockName());
    }
    BasicBlock currentLoopBlock = new BasicBlock(IrNameController.getBlockName());
    BasicBlock followBlock = new BasicBlock(IrNameController.getBlockName());
    IrNameController.pushLoop(new Loop(forStmtVal1, condBlock,
                                       forStmtVal2, currentLoopBlock, followBlock));
    if (condAst != null) {
        new JumpInstr(condBlock);
        IrNameController.setCurrentBlock(condBlock);
        llvmGenUtils.genCondIr(condAst, currentLoopBlock, followBlock);
    } else {
        new JumpInstr(currentLoopBlock);
    }
    IrNameController.setCurrentBlock(currentLoopBlock);
    genIrAnalysis(rootAst.getChildList().get(rootAst.getChildList().size() - 1));
    if (forStmtVal2 != null) {
        genIrAnalysis(forStmtVal2);
    }
    if (condAst != null) {
        new JumpInstr(condBlock);
    } else {
        new JumpInstr(currentLoopBlock);
    }
    IrNameController.setCurrentBlock(followBlock);
    IrNameController.popLoop();
    SymbolTable.leaveLoop();
    return null;
}
```

#### 变量名重定义

在实际生成`llvm`中间代码的过程中，对**虚拟寄存器的命名**获得是一个非常重要的步骤，我们知道虚拟寄存器的命名有两种方式——**使用数字递增顺序**和**使用字符串变量命名**，前者难度较大且容易出错，可读性也较差，所以笔者选用了使用字符串变量命名的方法来获得虚拟寄存器的名称。

具体而言，笔者建立了`IrNameController`类和`IrPrefix`类来进行名称获取的管理。

- `IrNameController`类用于定义了一些静态方法来使每次调用时都可以**动态的产生新的名字**。
- `IrPrefix`类按照虚拟寄存器的不同作用为这些命名加上对应的前缀使代码的可读性得以增强，是一个**枚举类**。

```java
//IrNameController.java
private static HashMap<Function, Integer> blockNameIndexHashMap;
private static Integer paramNameIndex;
private static Integer stringLiteralNameIndex;
private static HashMap<Function, Integer> localVarNameIndexHashMap;
public static String getLocalVarName() {
    int localVarNameIndex = localVarNameIndexHashMap.get(currentFunction);
    localVarNameIndexHashMap.put(currentFunction, localVarNameIndex + 1);
    return IrPrefix.LOCAL_VAR_NAME.toString() + localVarNameIndex;
}

public static String getBlockName() {
    int blockNameIndex = blockNameIndexHashMap.get(currentFunction);
    blockNameIndexHashMap.put(currentFunction, blockNameIndex + 1);
    String funcName = currentFunction.getName().equals("@main") ? "main" :
    currentFunction.getName().substring(3);
    return funcName + "_" + IrPrefix.BB_NAME + blockNameIndex;
}

public static String getGlobalVarName(String varName) {
    return IrPrefix.GLOBAL_VAR_NAME + varName;
}

public static String getParamName() {
    return IrPrefix.PARAM_NAME.toString() + paramNameIndex++;
}

public static String getStringLiteralName() {
    return IrPrefix.STRING_LITERAL_NAME.toString() + stringLiteralNameIndex++;
}

public static String getFuncName(String funcName) {
    return (funcName.equals("main")) ? "@main" : IrPrefix.FUNC_NAME + funcName;
}
//IrPrefix.java
public enum IrPrefix {
    BB_NAME("block_label_"),//基本块名
    FUNC_NAME("@f_"),//函数名
    GLOBAL_VAR_NAME("@g_"),//全局变量名
    LOCAL_VAR_NAME("%v_"),//局部变量名
    PARAM_NAME("%a_"),//参数名
    STRING_LITERAL_NAME("@s_");//字符串字面量名

    private final String prefix;

    IrPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
```

### `Value`语法树建立

#### 一切皆`value`

`LLVM`中的核心观点就是“一切皆`Value`”，所以我选择建立的`value`类作为所有`value`语法树的公共父节点，最终建立的继承结构如下图

![image-20231025204943210](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231025204943210.png)

具体而言，对于我们所生成的中间代码，可以理解为就是一个以`module`为根节点的语法树，而`module`中由**字符串字面值列表**、**全局变量列表**和**函数列表**三个部分组成，而这三个部分又可以依次向下展开，也就形成了树结构的基本样式。对于该结构而言，字符串列表和全局变量都比较简单，所以我这里重点介绍一下**函数列表**的结构。

##### `Function`结构

`Function`函数主要分为**参数列表**和**基本块列表**两个部分，参数列表较为容易实现，而基本块则是一个相对陌生的概念，所以我在正式展开介绍前先介绍一下基本块的概念。

所谓基本块，就是**代码中可以连续执行的最大序列**，简而言之就是对于一个基本块而言，程序的执行（控制流）只能从基本块的第一条语句进入，只能从基本块的最后一条语句离开，符合这样的条件的代码段就是一个基本块，在我们完成基本块的划分后，后续的中间代码优化都是建立在**基本块的块内逻辑和块间逻辑**上的，所以将一个函数划分成多个基本块是非常重要的。

```java
public class Function extends User {
    private final IrType returnType;
    private final ArrayList<BasicBlock> basicBlocks;
    private final ArrayList<Param> params;
}
```

那我们应该如何划分基本块呢，有一个非常重要的部分就是**跳转语句**，因为只有遇到跳转语句后才会使得我们的代码段不再连续，这样也就自然形成的基本块的划分。

而在`BasicBlock`内部是由一系列指令`Instr`和所属函数的指针组成的，所以我们在之前转化`llvm_ir`的过程中其实本质就是不断将`AST`中建立的各种**函数变量**和**符号变量**转化成一条条指令装入`BasicBlock`的过程。

##### Instr类型

`Instr`指令部分我分成了`basis`和`optimizer`两部分分别表示**基本指令**和**优化指令**，其中优化指令是后续代码优化需要用到的如phi等指令，而基本指令我分为了`AllocaInstr`、`BrInstr`、`CalcInstr`、`CallInstr`、`GetEleInstr`、`IcmpInstr`、`JumpInstr`、`LoadInstr`、`RetInstr`、`StoreInstr`、`TruncInstr`、`ZextInstr`等，之后按照不同指令需要的操作数不同各自重写相应的`toString`方法即可。

值得一提的是，这里我们终于使用到了上述结构中定义的`User`类，这个类是所有需要用到**操作数**的类的公共父类，在他其中定义了一个`protected`属性的`operands`操作数集合，并定义了各种操作数可以使用的方法，从而使其子类调用方法时更加便利。

```java
public class User extends Value {
    protected ArrayList<Value> operands;
}
```

#### 类型管理

为了更好的区分各种变量的类型，所以我们不可避免的需要**建立类型类**并加以管理，具体而言我建立了`IrType`类并在其下方继承了四个子类`ArrayType`，`PointerType`，`StructType`和`VarType`分别表示**数组类型**、**指针类型**、**结构类型**和**变量类型**，其中结构类型是语法树中一些非叶子节点所属的类型，如`BasicBlock`。建立好这些类型类之后，我们就可以在其中重写`toString`方法，这样可以避免大量重复造轮子的需求。

#### 二维数组

在实际操作中，二维数组的处理毫无疑问是一大难点，仅仅它的`toString`编写难度就异常巨大，你需要根据它定义初值的情况动态处理字符串，代码复杂程度还是很高的,具体体现在为**变量初始化**（`Initial`）和**取数**（`GetEleInstr`）两个部分，需要我们格外细心。

```java
//Initial.java
@Override
public String toString() {
    if (initValue == null || initValue.isEmpty()) {
        return (type.isInt32()) ? type + " 0" : type + " zeroinitializer";
    } else {
        if (type.isInt32()) {
            return type + " " + initValue.get(0);
        } else {
            if (space.size() == 1) {
                return type + " [" + initValue.stream().map(number -> "i32 " + number)
                    .collect(Collectors.joining(", ")) + "]";
            } else {
                StringBuilder sb = new StringBuilder();
                if (offset == 0) {
                    sb.append(type).append(" zeroinitializer");
                } else {
                    sb.append(type).append(" [");
                    for (int i = 0; i < space.get(0); i++) {
                        if (i * space.get(1) < offset) {
                            sb.append("[").append(space.get(1)).append(" x i32]").append("[");
                            for (int j = 0; j < space.get(1); j++) {
                                sb.append("i32 ").append(initValue.get(i * space.get(1) + j));
                                if (j != space.get(1) - 1) {
                                    sb.append(", ");
                                }
                            }
                            sb.append("]");
                        } else {
                            sb.append("[").append(space.get(1)).
                                append(" x i32] ").append("zeroinitializer");
                        }
                        if (i != space.get(0) - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("]");
                }
                return sb.toString();
            }
        }
    }
}
//GetEleInstr.java
@Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    PointerType ptrType = (PointerType) (operands.get(0).getType());
    IrType type = ptrType.getTarget();
    sb.append(name).append(" = getelementptr inbounds ")
        .append(type).append(", ")
        .append(ptrType).append(" ")
        .append(operands.get(0).getName())
        .append((type.isArray() ? ", i32 0, i32 " : ", i32 "));
    if (type.isArray() && ((ArrayType) type).getEleSpace().size() == 2) {
        if (operands.get(1).getName().matches("[0-9]+")) {
            ArrayList<Integer> spaces = ((ArrayType) type).getEleSpace();
            Integer offset = Integer.parseInt(operands.get(1).getName());
            sb.append(offset / spaces.get(1)).append(", i32 ").append(offset % spaces.get(1));
        } else {
            sb.append("0, i32 ").append(operands.get(1).getName());
        }
    } else {
        sb.append(operands.get(1).getName());
    }
    return sb.toString();
}
```

当我们完成到这里后，最后一个步骤就是将所有继承了`Value`类的节点全部**重写`toString`方法**然后调用`modele.toString()`就可以获得最终的中间代码了，`module`类的`toString`方法如下，其他类可以类似效仿：

```java
@Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(GetIntDeclare.getDeclare()).append("\n");
    sb.append(PutIntDeclare.getDeclare()).append("\n");
    sb.append(PutStrDeclare.getDeclare()).append("\n\n");
    for (FormatString stringLiteral : stringLiterals) {
        sb.append(stringLiteral).append("\n");
    }
    sb.append("\n");
    for (GlobalVar globalVar : globalVars) {
        sb.append(globalVar.toString()).append("\n");
    }
    sb.append("\n");
    for (Function function : functions) {
        sb.append(function.toString()).append("\n\n");
    }
    return sb.toString();
}
```

## 生成`mips`代码

在进行**代码优化**之前，笔者先进行了`mips`代码生成的编写，这样可以在中间代码相对简单时生成`mips`，后续优化编写之后**指令集变化也较小**，这样可以得到更高的编译器编写效率。言归正传，我们要想由中间代码生成`mips`汇编，我们需要**遍历之前生成的`Value`语法树**，并以`Instr`为单位**将每条指令转化为几条汇编指令**，转化的整体而言难度还是比较小的，主要的难点在于对**栈指针的移动**和**内存管理**。

#### 汇编指令转化

汇编指令转化部分我将`mips`汇编分为了`.data`和`.text`段，并分别用一个数组存储对应的`Assembly`类,之后编写各类不同的指令继承`Assembly`这个父类即可。

![image-20231025214942032](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231025214942032.png)

而具体到汇编指令的转化，我们需要在`Instr`中编写`generateAssembly()`方法并在各个子类中**重写该方法**，即可完成转化。

除此之外，我们还需要实现`.word`和`.asciiz`两个预处理指令，下方我以`.word`指令举例，`.asciiz`类似实现即可。

```java
public class WordAsm extends DataAssembly {
    private final ArrayList<Integer> initValue;

    public WordAsm(String label, String value, ArrayList<Integer> initValue) {
        super(label, value);
        this.initValue = initValue;
    }

    @Override
    public String toString() {
        if (initValue == null) {
            return label + ": .word " + value;
        } else {
            int offsetTot = Integer.parseInt(value) - initValue.size();
            StringBuilder res = new StringBuilder(label + ": .word ");
            for (int i = 0; i <= initValue.size() - 1; i++) {
                res.append(initValue.get(i));
                if (i != initValue.size() - 1) {
                    res.append(", ");
                }
            }
            for (int i = 0; i < offsetTot; i++) {
                res.append("0");
                if (i != offsetTot - 1) {
                    res.append(", ");
                }
            }
            return res.toString();
        }
    }
}
```

#### 栈指针移动

为了更好实现**栈指针的移动和相关的取数操作**，我编写了`RegisterUtils`方法类，里面编写了包括**移动栈指针**，**申请寄存器**，**取寄存器的值**，**取内存的值**，**取指针对应值**等多种内存环境下的常用方法的封装，这样可以使代码复用性更强，适应高内聚低耦合的需求。

```java
public class RegisterUtils {
    public static Integer moveValueOffset(Value value) {
        ... ...
    }

    public static void allocReg(Value value, Register target) {
        ... ...
    }

    public static void reAllocReg(Value value, Register target) {
       ... ...
    }

    public static Register loadRegisterValue(Value operand, Register instead, Register reg) {
        ... ...
    }

    public static Register loadVariableValue(Value operand, Register reg, Register instead) {
        ... ...
    }

    public static Register loadPointerValue(Value operand, Register pointerReg, Register instead) {
        ... ...
    }

    public static Register loadMemoryOffset(Value operand, Register instead, Register target,
                                            Register pointerReg, Register offsetReg) {
        ... ...
    }

    public static Register allocParamReg(Value para, Register paraReg,
                                         int currentOffset, ArrayList<Register> allocatedRegs) {
        ... ...
    }

    public static Register allocParamMem(Value para, Register paraReg, int currentOffset,
                                         ArrayList<Register> allocatedRegs, int paraNum) {
        ... ...
    }
}
```

#### 寄存器控制

生成汇编还有一个非常重要的步骤是**为每个虚拟寄存器分配对应的实际寄存器**，这里需要我们创建`Register`寄存器类和`RegisterController`控制器类来**控制寄存器的分配**。

`Register`类是一个枚举类，相比于直接使用字符串访问寄存器，使用枚举类的好处在于可以调用**枚举类的`ordinal()`方法**，这个可以找到每个枚举常量的索引，就像数组索引一样，这样有利于我们**动态访问所需的寄存器**。

而`RegisterController`类是一个建立了一组`Value`和`Register`的对应关系，从而及时更新各个`Value`实际分配的寄存器，这样也就实现了对寄存器的动态管理的需求。

```java
public class RegisterController {
    private static HashMap<Value, Register> valueRegisterHashMap;
    public Register getRegister(Value value) {
        return (valueRegisterHashMap == null) ? null :
                valueRegisterHashMap.get(value);
    }
    public void allocRegister(Value value, Register register) {
        if (valueRegisterHashMap == null) {
            return;
        }
        valueRegisterHashMap.put(value, register);
    }
}
```

#### 内存管理策略

对于已经分配好寄存器的变量，我们可以**直接将其翻译成对应的寄存器**，而对于其他变量，我们只能将其**放到内存中**，使用的时候从内存中取,这就要涉及到内存管理策略了，而本编译器的内存管理策略如下。

调用新函数前，`$sp`的值会设置为**新函数的栈底**，在当前函数的作用域中， `$sp`的值不会随内存分配而变化。函数前3个形参保存在`$a1, $a2, $a3`中，`$a0`只用于IO，剩余的形参存入内存方便后续取用。在调用某个函数时，我们需要先保存当前已经分配的寄存器，然后将函数参数、`$sp`和`$ra`寄存器依次保存栈顶。最后，**将`$sp`的值加上目前的总偏移量，作为被调用函数的栈底**。接下来，直接`jump`到目标函数对应的`label`即可。**当函数返回时将存储于内存的变量恢复**。

在函数内部使用到的**局部变量依次存放**，并在`AssemblyUnit`记录每个局部变量的存放位置相对于栈底的偏移，以及当前栈顶到栈底的总偏移量。每定义一个需要分配空间的变量时，栈指针偏移，然后在`AssemblyUnit`中**记录好该变量到该偏移量的映射关系**即可。

到此一个简单的、没有进行任何优化的编译器正式完工,后续可以开始开展**优化工作**了。

> 注：在编写llvm生成和mips生成时我编写了一台评测机，链接为——[Charles2530/compiler_autotest: 北航2023年编译原理自动化测试 (github.com)](https://github.com/Charles2530/compiler_autotest)，可供参考。

## 中端代码优化

### 全局变量局部化

全局变量局部化是指将函数出于全局区的变量转化到局部使用，由于全局变量(非数组)是可以在编译阶段计算出具体的值的，而在GVN有常数折叠的行为，所以将可以直接转化为常数的全局变量进行转化后可以更好的推进后续的优化。

具体而言，全局变量局部化主要对两种情况的全局变量进行了优化:
1. 如果全局变量没有使用者，那么就可以局部化
2. 如果全局变量只有一个使用者，那么就可以局部化

为了实现全局变量局部化，我们首先需要设计一个HashMap存储每个全局变量和调用它的函数列表，之后我们需要创建函数调用表，之后按照上述逻辑将符合条件的函数局部化即可,下面是局部化的代码逻辑。

```java
	/**
     * localize 是局部化全局变量的函数
     * 该函数的执行逻辑如下:
     * 1. 如果全局变量没有使用者，那么就可以局部化
     * 2. 如果全局变量只有一个使用者，那么就可以局部化
     */
private static boolean localize(GlobalVar globalVar) {
    if (globalVarUsers.get(globalVar).isEmpty()) {
        return true;
    }
    if (globalVarUsers.get(globalVar).size() == 1) {
        Function target = globalVarUsers.get(globalVar).iterator().next();
        if (!FunctionInlineUnit.getResponse(target).isEmpty()) {
            return false;
        }
        BasicBlock entry = target.getBasicBlocks().get(0);
        if (globalVar.getType().isInt32()) {
           	... ...
            return true;
        }
    }
    return false;
}
```

### 基本块简化

在生成中间代码后，我们首先需要一些不必要的跳转指令，虽然这部分并不会优化我们中间代码的效率，但这可以很好的简化中间代码，而且去除重复跳转语句后可以让我们在后续写优化时可以通过访问基本块的最后一条指令即可获得所需的跳转块，大大提高优化效率。具体而言主要分为删除重复的分支语句和删除死代码块两部分。

#### 删除重复分支语句

所谓删除基本块中重复的分支，对于每个基本块而言，仅需保留第一个分支即可，该分支以下的代码不会被执行，函数代码如下:

```java
public static void deleteDuplicateBranch(BasicBlock basicBlock) {
    for (int i = 0; i < basicBlock.getInstrArrayList().size() - 1; i++) {
        Instr instr = basicBlock.getInstrArrayList().get(i);
        if (instr instanceof BrInstr || instr instanceof JumpInstr
            || instr instanceof RetInstr) {
            basicBlock.setInstrArrayList(new ArrayList<>(
                basicBlock.getInstrArrayList().subList(0, i + 1)));
            break;
        }
    }
}
```

#### 删除死代码块

删除死代码块而言，如果该基本块不可达，则该基本块为死代码块，应当被删除，函数代码如下:

```java
public static void deleteDeadBlock(Function function) {
    HashSet<BasicBlock> vis = new HashSet<>();
    BasicBlock entry = function.getBasicBlocks().get(0);
    dfs(entry, vis);
    function.getBasicBlocks().removeIf(bb -> !vis.contains(bb) && bb.setDeleted(true));
}
```

这里通过深度优先搜索进行访问，并用vis数组进行记录即可。

### Mem2Reg

Mem2Reg将一个非SSA的形式转化到含有phi函数的SSA形式，具体分为插入phi指令和变量重命名两个步骤。具体而言，在实现Mem2Reg之前，我们需要创建一些数据结构来辅助完成Mem2Reg的内容。

#### CFG流图构建

控制流程图(Control Flow Graph)简称CFG，记录了每个块之间的跳转关系，具体而言即记录了每个块的前序基本块和后继基本块。

要想构建控制流图，需要寻找函数各个块的直接跳转指令和间接跳转指令，并为期添加跳转的记录即可。具体函数逻辑如下:

```java
	/**
     * buildControlFlowGraph 方法用于构建控制流图
     * 这里求解出了IndBasicBlock和OutBasicBlock
     */
private static void buildControlFlowGraph(Function function) {
    for (BasicBlock basicBlock : function.getBasicBlocks()) {
        Instr lastInstr = basicBlock.getLastInstr();
        if (lastInstr instanceof JumpInstr jumpInstr) {
            ControlFlowGraph.addDoubleEdge(basicBlock, jumpInstr.getTarget());
        } else if (lastInstr instanceof BrInstr brInstr) {
            ControlFlowGraph.addDoubleEdge(basicBlock, brInstr.getThenBlock());
            ControlFlowGraph.addDoubleEdge(basicBlock, brInstr.getElseBlock());
        }
    }
}
```

#### 支配树及DF构建

DominatorTree 是支配树，在这个阶段我们需要形成四个数据结构，即每个基本块的支配集合，支配边界集合，直接支配父节点和直接支配子节点集合。除此之外，我们还可以在这个过程中算出每个基本块所在支配树的深度，用于后续GCM优化提供服务。

这几个定义如下:

- **支配**(dominate)：如果CFG中从起始节点到基本块y的所有路径都经过了基本块x，我们说**x支配y**
- **严格支配**(strict dominate)：显然每个基本块都支配它自己。如果x支配y，且x不等于y，那么**x严格支配y**
- **直接支配者**（immediate dominator, idom）：严格支配n，且不严格支配任何严格支配 n 的节点的节点(直观理解就是所有严格支配n的节点中离n最近的那一个)，我们称其为n的直接支配者
- **支配边界**（**D**ominance **F**rontier）：节点 n 的支配边界是 CFG 中刚好**不**被 n 支配到的节点集合

##### 支配集合

要想构建支配集合，对于这个函数而言，函数的逻辑是在for循环中找到所有不被 basicBlock支配的block，放入reachSet中，这样所有不在reachedSet中的Block就都是被basicBlock，支配的Block，然后将所有不在reachSet中的block放入domList中(包括basicBlock本身)

> 注:这里与教程中的支配集合定义有区别，应该理解为该支配集合里的节点都是被该节点支配的。这样省略了求不动点的过程，效率更高。

```java
public void searchBlockDominateSet() {
    BasicBlock entry = basicBlocks.get(0);
    for (BasicBlock basicBlock : basicBlocks) {
        HashSet<BasicBlock> reachedSet = new HashSet<>();
        DominatorTree.dfsDominate(entry, basicBlock, reachedSet);
        ArrayList<BasicBlock> domList = new ArrayList<>();
        for (BasicBlock bb : basicBlocks) {
            if (!reachedSet.contains(bb)) {
                domList.add(bb);
            }
        }
        DominatorTree.addBlockDominateSet(basicBlock, domList);
    }
}
```

##### 构建支配树

求解支配树换言之就是求解直接支配关系，即A直接支配B——A严格支配B，且不严格支配任何严格支配B的节点。所以我们需要借助之前生成的集合，判断该节点和他的支配集合是否为直接支配关系，如果是直接支配就直接生成直接支配父节点和直接支配子节点集合。

判断是否为直接支配关系的逻辑如下:

```java
private static boolean isImmediateDominator(BasicBlock basicBlock, BasicBlock dominateBlock) {
    boolean flag = basicBlock.getBlockDominateSet().contains(dominateBlock) &&
        !dominateBlock.equals(basicBlock);
    for (BasicBlock midBlock : basicBlock.getBlockDominateSet()) {
        if (!midBlock.equals(dominateBlock) && !midBlock.equals(basicBlock)
            && midBlock.getBlockDominateSet().contains(dominateBlock)) {
            flag = false;
            break;
        }
    }
    return flag;
}
```

##### 构建支配边界集合

求解支配边界是一个相对固定的算法，直接借助教程所给的伪代码即可。

![image-20231119215943218](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231119215943218.png)

![image-20231119220001178](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231119220001178.png)

> 上述伪代码是插入phi函数的伪代码，我们的迭代支配边界也将在这个过程被计算出来。

```java
public void searchBlockDominanceFrontier() {
    for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> entry :
         ControlFlowGraph.getFunctionOutBasicBlock(this).entrySet()) {
        BasicBlock from = entry.getKey();
        ArrayList<BasicBlock> outBasicBlocks = entry.getValue();
        for (BasicBlock to : outBasicBlocks) {
            BasicBlock runner = from;
            while (!runner.getBlockDominateSet().contains(to)
                   || runner.equals(to)) {
                DominatorTree.addBlockDominanceFrontierEdge(runner, to);
                runner = runner.getBlockDominateParent();
            }
        }
    }
}
```

##### 计算支配树深度集

支配树深度集合计算相对比较简单，实际就是一个深度优先搜索的过程，代码如下:

```java
public static void dfsDominateLevel(BasicBlock basicBlock, Integer depth) {
    DominatorTree.addBlockDominateTreeDepth(basicBlock, depth);
    for (BasicBlock child : basicBlock.getBlockDominateChildList()) {
        DominatorTree.dfsDominateLevel(child, depth + 1);
    }
}
```

#### 插入Phi指令

在有了上述功能作为基础后，我们就可以开始我们Mem2Reg的过程了。这里主要分为Phi指令位置选择和变量重命名两个步骤。

##### Phi指令位置选择

这部分可以参考之前的伪代码，本算法首先将defBasicBlockArrayList中的基本块推入栈中，然后在while循环执行固定算法即可，这段代码形成的代码如下:

```java
public static void insertPhi() {
    HashSet<BasicBlock> f = new HashSet<>();
    Stack<BasicBlock> w = new Stack<>();
    Mem2RegUnit.defBasicBlockArrayList.forEach(w::push);
    while (!w.isEmpty()) {
        BasicBlock x = w.pop();
        for (BasicBlock y : x.getBlockDominanceFrontier()) {
            if (!f.contains(y)) {
                f.add(y);
                Instr phiInstr = new PhiInstr(IrNameController.getLocalVarName(
                    y.getBelongingFunc()), y.getBlockIndBasicBlock());
                y.insertInstr(0, phiInstr);
                useInstrArrayList.add(phiInstr);
                defInstrArrayList.add(phiInstr);
                if (!defBasicBlockArrayList.contains(y)) {
                    w.push(y);
                }
            }
        }
    }
}
```

这里f 为需要添加phi的基本块的集合，w 为定义变量的基本块的集合。

> 注：phi既是useInstr,又是defInstr

##### 变量重命名

变量重命名主要依托于深度优先搜索，来实现对基本块的变量进行重命名，文档中的伪代码如下:

![image-20231119223355130](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231119223355130.png)

而变量重命名的主要代码如下:

```java
public static void dfsVarRename(BasicBlock presentBlock) {
    int cnt = removeUnnecessaryInstr(presentBlock);
    for (BasicBlock basicBlock : presentBlock.getBlockOutBasicBlock()) {
        Instr instr = basicBlock.getInstrArrayList().get(0);
        if (instr instanceof PhiInstr phiInstr && useInstrArrayList.contains(phiInstr)) {
            phiInstr.modifyValue(((stack.isEmpty()) ?
                                  new Constant("0", new VarType(32), false) : stack.peek()), presentBlock);
        }
    }
    presentBlock.getBlockDominateChildList().forEach(Mem2RegUnit::dfsVarRename);
    for (int i = 0; i < cnt; i++) {
        stack.pop();
    }
}
```

在这里，cnt 记录遍历presentBlock的过程中，stack的push次数，而该函数的执行逻辑如下:

1. 在移除了非必要的Instr后，遍历presentBlock的后继集合，将最新的define（stack.peek）填充进每个后继块的第一个phi指令中(有可能某个后继块没有和当前alloc指令相关的phi，需要进行特判(符合条件的Phi应该在useInstrList中))
2. 对presentBlock支配的基本块使用dfsVarRename方法，实现DFS
3. 将该次dfs时压入stack的数据全部弹出

### 死代码删除

程序包含的一些代码可能并不会被运行或者不会对结果产生影响，那么我们称这种代码为死代码。我们将不会被运行到的称为**不可达代码**，将不会对结果产生影响的代码成为**无用代码**。删除无用或不可达代码可以缩减IR代码，可使程序更小、编译更快、执行也更快。

其实之前的基本块简化也可以算是死代码删除，但那里删除的都是真的没有用的代码，对于优化没有帮助，所以这部分主要介绍删除不会对结果产生影响的代码。这里我主要定义了isDead()函数来判断某个指令是否可以被死代码优化。

```java
public boolean isDead() {
    return this.isValid() && !(this instanceof CallInstr) &&
        !(this instanceof IoStreamGeneration) && this.getUseDefChain().isEmpty();
}
```

这里我们首先意识到IO指令和函数调用指令是肯定不能优化的，除此之外我们我们需要判断指令是否优先以及是否有其他指令使用了该指令，如果该指令无效或者没有其他指令使用该指令，这样我们就可以删除这条指令。

这里isValid 的指令有 alloca，alu，call，gep，io，getint，load，phi，zext，其中call指令调用的函数将指针作为形参、修改全局变量、调用了其他函数，因此不能直接删除，io中的 getint 指令获得的数字即使没有用到也应该完成io操作，也不能删除。

### 函数内联

> 关于函数内联这个操作，其实正常是属于中端代码优化的，但我受到参考编译器的启发，发现这个操作其实可以在生成`AST`树后进行节点调整完成，这样可以很好的完成后续的操作，但在尝试后发现这个功能在Mem2Reg后修改效率更高，所以最终这部分还是放在了中端代码优化部分。

程序中的一次函数调用，除去函数内部执行造成的开销，会产生很多的额外开销，如保存和恢复现场，参数传递和跳转到被调用函数，以及完成执行后返回等，而对于非递归，且没有调用与递归相关函数的函数，我们可以将其内部的执行指令嵌入到调用它的函数中，这种优化，我们叫做函数内联。

函数内联时首先要判断这个函数是否可以被内联，评价标准是内联函数不能递归，不能调用其他函数。而函数内联分析主要分为以下几个步骤:

1. 建立函数调用图
2. 深度优先搜索函数调用图，判断是否有递归
3. 内联函数并更新函数调用图
4. 删除无用函数
5. 重复1-4步骤，直到不动点稳定

而具体代码如下:

```java
private static void inlineAnalysis() {
    FunctionInlineUnit.fixedPoint = true;
    while (FunctionInlineUnit.fixedPoint) {
        FunctionInlineUnit.fixedPoint = false;
        FunctionInlineUnit.buildFuncCallGraph();
        module.getFunctions().forEach(Function::dfsCaller);
        FunctionInlineUnit.inlineFunctionsList.forEach(Function::inlineFunction);
        FunctionInlineUnit.inlineFunctionsList.clear();
        FunctionInlineUnit.buildFuncCallGraph();
        FunctionInlineUnit.removeUselessFunction();
    }
}
```

在函数调用图构建时和上述全局变量局部化的函数调用图构建过程相同，其余部分也较为简单，这里我主要介绍内联函数那一步。

#### 内联函数操作

内联函数操作的执行逻辑如下:

1. 通过遍历，获得所有调用 inlineFunc 的 callInstr
2. 对于每一个 callInstr，将其替换为 inlineFunc 的所有基本块
3. 去除调用关系

显而易见，这里最难的一步是将callInstr替换为基本块集合的操作，这里我们需要将函数调用指令所在的基本块分割成两半，之后将函数调用指令所在的基本块之后再建一个块，用于存放函数调用指令之后的指令，最后我们需要克隆函数调用指令所在的基本块，将克隆的基本块插入到函数调用指令所在的基本块之后即可。

```java
public static void replaceFunctions(CallInstr callInstr) {
    Function response = callInstr.getTarget();
    BasicBlock basicBlock = callInstr.getBelongingBlock();
    Function function = basicBlock.getBelongingFunc();
    BasicBlock inlineBlock = new BasicBlock(IrNameController.getBlockName(function));
    function.addBasicBlock(inlineBlock, function.getBasicBlocks().indexOf(basicBlock) + 1);
    FunctionInlineUnit.splitBlock(callInstr, basicBlock, inlineBlock);
    FunctionInlineUnit.cloneBlock(callInstr, basicBlock, inlineBlock, response);
}
```

除此之外需要强调，在函数内联完成后，由于内联函数属于块间优化，所以我们在执行后需要重建流图和支配树，再继续后续的操作。

### GVM

GVN(Global Variable Numbering) 全局值编号：为全局的变量进行编号，实现全局的消除公共表达式。

要想实现这些操作，我们需要构建全局变量编号哈希表，之后在分析时我们可以将各个指令的hash值存入，如果在该hash表中有之前的记录就可以直接替换，这样就实现了全局的消除公共表达式。

#### 常数折叠

由于常数相同值但计算表达式不同的情况的存在，所以我们需要先将可以合并的表达式进行合并，这样我们就可以获得更多的公共表达式。

进行常数折叠时我们需要根据每个calcInstr的两个操作数中常数的数量进行分类讨论:

1. 如果没有常数，则可以将前后两个操作数相同且操作位减法、取模或除法时进行相应的操作。

   ```java
   private static Value simplifyNoConstant(CalcInstr calcInstr) {
       if (calcInstr.getOperands().get(0).equals(calcInstr.getOperands().get(1))) {
           if (calcInstr.getInstrType().matches("sub|srem")) {
               return new Constant("0", new VarType(32));
           } else if (calcInstr.getInstrType().matches("sdiv")) {
               return new Constant("1", new VarType(32));
           }
       }
       return null;
   }
   ```

   

2. 如果有一个常数，我们需要按照常数所处的位置进行相应的优化。

   ```java
   private static Value simplifySingle(CalcInstr calcInstr) {
       if (calcInstr.getOperands().get(0) instanceof Constant constant) {
           int val = constant.getVal();
           if (val == 0) {
               if (calcInstr.getInstrType().matches("add")) {
                   return calcInstr.getOperands().get(1);
               } else if (calcInstr.getInstrType().matches("mul")) {
                   return new Constant("0", new VarType(32));
               }
           } else if (val == 1) {
               if (calcInstr.getInstrType().matches("mul")) {
                   return calcInstr.getOperands().get(1);
               }
           }
       } else {
           int val = ((Constant) calcInstr.getOperands().get(1)).getVal();
           if (val == 0) {
               if (calcInstr.getInstrType().matches("add|sub")) {
                   return calcInstr.getOperands().get(0);
               } else if (calcInstr.getInstrType().matches("mul")) {
                   return new Constant("0", new VarType(32));
               }
           } else if (val == 1) {
               if (calcInstr.getInstrType().matches("mul|sdiv")) {
                   return calcInstr.getOperands().get(0);
               } else if (calcInstr.getInstrType().matches("srem")) {
                   return new Constant("0", new VarType(32));
               }
           }
       }
       return null;
   }
   ```

   

3. 如果有两个常数，我们可以直接提前进行计算。

   ```java
   private static Value simplifyDouble(CalcInstr calcInstr) {
       int op1 = ((Constant) calcInstr.getOperands().get(0)).getVal();
       int op2 = ((Constant) calcInstr.getOperands().get(1)).getVal();
       op2 = (calcInstr.getInstrType().matches("sdiv|srem") && op2 == 0) ? 1 : op2;
       return switch (calcInstr.getInstrType()) {
           case "add" -> new Constant(String.valueOf(op1 + op2), new VarType(32));
           case "sub" -> new Constant(String.valueOf(op1 - op2), new VarType(32));
           case "mul" -> new Constant(String.valueOf(op1 * op2), new VarType(32));
           case "sdiv" -> new Constant(String.valueOf(op1 / op2), new VarType(32));
           case "srem" -> new Constant(String.valueOf(op1 % op2), new VarType(32));
           case "and" -> new Constant(String.valueOf(op1 & op2), new VarType(32));
           case "or" -> new Constant(String.valueOf(op1 | op2), new VarType(32));
           default -> null;
       };
   }
   ```

这样我们就完成了常数折叠步骤。

#### 消除公共表达式

遍历一遍Instr，找出所有可优化的alu, gep, icmp和func，如果GVN中已存在，直接用该值即可, 否则插入到GVN中，之后对其直接支配的子节点调用该函数实现递归，最后需要将该基本块插入GVN的instr全部删去, 避免影响兄弟子树的遍历。实现的代码如下:

```java
public static void uniqueInstr(BasicBlock entry, HashMap<String, Instr> hashMap) {
    ConstantFoldingController.foldingCalcInstr(entry);
    HashSet<Instr> vis = new HashSet<>();
    Iterator<Instr> iterator = entry.getInstrArrayList().iterator();
    while (iterator.hasNext()) {
        Instr instr = iterator.next();
        if (instr instanceof CalcInstr || instr instanceof IcmpInstr ||
            instr instanceof GetEleInstr || (instr instanceof CallInstr callInstr &&
                                             ((Function) callInstr.getOperands().get(0)).isImprovable())) {
            String hash = instr.getGlobalVariableNumberingHash();
            if (hashMap.containsKey(hash)) {
                instr.replaceAllUse(hashMap.get(hash));
                iterator.remove();
            } else {
                hashMap.put(hash, instr);
                vis.add(instr);
            }
        }
    }
    entry.getBlockDominateChildList()
        .forEach(child -> uniqueInstr(child, hashMap));
    DebugDetailController.printGlobalVariableNumbering(entry.getBelongingFunc(), hashMap);
    for (Instr instr : vis) {
        hashMap.remove(instr.getGlobalVariableNumberingHash());
    }
}
```

> 在上述优化过程中，有一个非常重要的部分是优化callInstr，但并不是每个调用函数指令都是可以被优化的，简而言之，只有函数满足参数没有指针类型, 每个指令没有读写全局变量，不能调用其他函数

### GCN

GCM(Global Code Motion) 全局代码移动：根据Value之间的依赖关系，将代码的位置重新安排，从而使得一些不必要（不会影响结果）的代码尽可能少执行。

在实现GCM之前我们需要进行循环分析和副作用分析，除此之外我们还需要获得支配树的深度(这一步之前已经实现过了)。

#### 循环分析

所谓循环分析，也就是获得各个基本块所处的循环层数。而如何判断一个基本块进入循环了呢，这就需要用到我们理论课的知识了，即判断循环的依据是如果某个块支配了它的某个前序块，那么这个块就是循环的头这样我们将循环层数添加即可。

```java
public void loopAnalysis() {
    ... ...
    ArrayList<BasicBlock> latchBlocks = new ArrayList<>();
    ArrayList<BasicBlock> posOrderBlocks =
        DominatorTree.computeDominanceTreePostOrder(this);
    for (BasicBlock basicBlock : posOrderBlocks) {
        for (BasicBlock indBasicBlock : basicBlock.getBlockIndBasicBlock()) {
            if (basicBlock.getBlockDominateSet().contains(indBasicBlock)) {
                latchBlocks.add(indBasicBlock);
            }
        }
        if (!latchBlocks.isEmpty()) {
            LoopVal loop = new LoopVal(basicBlock, latchBlocks);
            LoopAnalysisController.addLoopIntoGraph(latchBlocks, loop);
            latchBlocks.clear();
        }
    }
    LoopAnalysisController.addLoopSons(basicBlocks.get(0));
}
```

#### 副作用分析

在副作用分析之前，我们需要构建函数调用图，然后借助这个图完成分析。之后我们需要看该函数调用的所有函数，如果它调用的函数本身存在副作用或者它调用的函数没有被访问过且该函数调用的函数存在副作用，则该函数存在副作用。主要代码逻辑如下:

```java
private static boolean processAnalysis(Function function) {
    boolean sideEffect = false;
    addFunctionVisited(function, true);
    if (getFunctionProcessed(function)) {
        sideEffect = function.getSideEffect();
        for (Function response : function.getResponses()) {
            if (!getFunctionVisited(response) && !getFunctionProcessed(response)) {
                processAnalysis(response);
            }
        }
    } else {
        for (Function response : function.getResponses()) {
            if (response.getSideEffect() ||
                (!getFunctionVisited(response) && !getFunctionProcessed(response)
                 && processAnalysis(response))) {
                sideEffect = true;
            }
        }
    }
    addFunctionVisited(function, false);
    function.setSideEffect(sideEffect);
    addFunctionProcessed(function, true);
    return sideEffect;
}
```

#### 全局代码移动

在完成两个前序步骤后，我们就可以正式展开优化了。全局代码移动算法的功能是调度那些“浮动”的指令，把它们归位到一个个基本块中。这个过程必须保留已有的控制依赖和数据依赖。在这个大前提下，算法可以自由发挥，我们的目标就是尽可能把代码移动到循环外面，尽可能让代码的执行路径变少。

而如果我们想要调度指令，需要遵守以下几个流程:find_Pinned_Insts，schedule_Early，schedule_Late，select_block

##### find_Pinned_Insts

有些指令是无法被灵活调度的，如phi，br/jump，ret等指令，这些指令我们叫做pinnedInst。pinnedInst受到控制依赖的牵制，无法被调度到其他基本块，也就是说这些指令和它们的基本块是绑定的。这一部分，我们需要先遍历指令将pinnedInst进行标记。具体代码如下:

```java
public boolean isPinned() {
    return !(this instanceof CalcInstr) && !(this instanceof IcmpInstr) &&
        !(this instanceof ZextInstr) && !(this instanceof GetEleInstr) &&
        !(this instanceof CallInstr callInstr && callInstr.isPure());
}
```

##### schedule_Early

这一步骤我们会尽可能的把指令前移，**确定每个指令能被调度到的最早的基本块**，同时不影响指令间的依赖关系。这里我们可以直接参考教程提供的伪代码即可。

![image-20231120000001515](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231120000001515.png)

该函数对应的代码如下:

```java
	/**
     * scheduleEarly 用于在GCM中实现尽可能的把指令前移，
     * 确定每个指令能被调度到的最早的基本块，同时不影响指令间的依赖关系。
     * 当我们把指令向前提时，限制它前移的是它的输入，
     * 即每条指令最早要在它的所有输入定义后的位置。
     * 该函数执行逻辑如下:
     * 1.如果已经处理过了，或者是无法移动，那么就结束处理。
     * 2.如果未处理，将这条指令从当前块移除，然后插入到入口块的最后一条指令之前。
     * 3.遍历该指令用到的操作数，尝试前移。
     */
public static void scheduleEarly(Instr instr, Function function) {
    if (!visited.contains(instr) && !instr.isPinned()) {
        visited.add(instr);
        BasicBlock root = function.getBasicBlocks().get(0);
        instr.getBelongingBlock().getInstrArrayList().remove(instr);
        root.addInstr(instr, root.getInstrArrayList().size() - 1);
        GlobalCodeMovementUnit.addPath(instr, root);
        instr.getOperands().forEach(v -> scheduleEarlyAnalysis(v, instr, function));
    }
}
```

##### schedule_Late

这部分我们会尽可能的把指令后移，**确定每个指令能被调度到的最晚的基本块**。每个指令也会被使用它们的指令限制，限制其不能无限向后移。这里我们同样可以直接参考教程提供的伪代码即可。

![image-20231120000151951](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231120000151951.png)

该函数对应的代码如下:

```java
	/**
     * scheduleLate 用于在GCM中尽可能的把指令后移，
     * 确定每个指令能被调度到的最晚的基本块。
     * 每个指令也会被使用它们的指令限制，限制其不能无限向后移。
     * 该函数执行逻辑如下:
     * 1.如果已经处理过了，或者是无法移动，那么就结束处理。
     * 2.如果未处理，遍历该指令的使用者，寻找LCA尝试后移。
     * 3.如果该指令的使用者是Phi指令，那么遍历Phi指令的每个操作数，寻找LCA尝试后移。
     */
public static void scheduleLate(Instr instr) {
    if (!visited.contains(instr) && !instr.isPinned()) {
        visited.add(instr);
        BasicBlock lcaBlock = null;
        for (User user : instr.getUsers()) {
            lcaBlock = scheduleLateAnalysis(user, instr, lcaBlock);
        }
        GlobalCodeMovementUnit.pickFinalPos(lcaBlock, instr);
        BasicBlock bestBlock = instr.getBelongingBlock();
        for (Instr instInst : bestBlock.getInstrArrayList()) {
            if (!instInst.equals(instr) && !(instInst instanceof PhiInstr) &&
                instInst.getOperands().contains(instr)) {
                instr.getBelongingBlock().getInstrArrayList().remove(instr);
                bestBlock.addInstr(instr, bestBlock.getInstrArrayList().indexOf(instInst));
                GlobalCodeMovementUnit.addPath(instr, bestBlock);
                break;
            }
        }
    }
}
```

##### select_block

在确定每个指令可以被灵活调度的空间后，我们将进行最关键的一步，为指令选择它最终的基本块。这里选择的依据是循环深度尽可能浅且尽可能的靠前，这里循环分析就起作用了。这里对应代码如下:

```java
	/**
     * pickFinalPos 用于在GCM中寻找指令最终所处的位置
     * 该函数的执行逻辑如下：
     * 1.如果该指令没有使用者，那么直接返回
     * 2.如果该指令有使用者，那么遍历该指令的使用者，找到最佳位置
     * 3.找到他们的LCA，如果他们是有共同祖先的，那么就将该指令插入到共同祖先的位置
     * 4.如果共同祖先不是该指令的所在块，则尽量让循环深度变小
     */
private static void pickFinalPos(BasicBlock lcaBlock, Instr instr) {
    BasicBlock posBlock = lcaBlock;
    if (!instr.getUsers().isEmpty()) {
        BasicBlock bestBlock = posBlock;
        while ((posBlock.getBlockDominateParent() != null) &&
               !posBlock.equals(instr.getBelongingBlock())) {
            posBlock = posBlock.getBlockDominateParent();
            if (posBlock.getLoopDepth() < bestBlock.getLoopDepth()) {
                bestBlock = posBlock;
            }
        }
        instr.getBelongingBlock().getInstrArrayList().remove(instr);
        bestBlock.addInstr(instr, bestBlock.getInstrArrayList().size() - 1);
        GlobalCodeMovementUnit.addPath(instr, bestBlock);
    }
}
```

## 后端代码优化

### 后端消除Phi

由于phi指令的存在，所以我们不能直接将phi指令转化为汇编，消PHI最简单的一个思路是在源基本块跳转之前将PHI指令拆成多条move指令，之后再将move指令转化为汇编即可完成相关工作。

#### Phi指令转化

要想将Phi指令转化为move指令，我们需要先将其转化为ParallelCopy指令并将其插入到跳转语句之前，而转化为ParallelCopy的方法就是将phiInstr的每个option都放到对应的ParallelCopy中，这里注意需要options按照前驱的顺序排列，然后删除Phi指令即可。这里可以参考课程教程伪代码如下:

![image-20231120095602990](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231120095602990.png)

而将ParallelCopy指令转化为Move指令的过程，我们首先需要将其直接转化为一个move数组的集合，之后需要通过添加临时变量的方法解决循环赋值以及共享寄存器等问题，这样就可以形成所需的move指令，相关伪代码如下:

![image-20231120095644956](https://charles2530.github.io/image/SysY-compiler-design-doc/image-20231120095644956.png)

而move指令的转汇编代码如下:

```java
@Override
public void generateAssembly() {
    super.generateAssembly();
    Register dstReg = AssemblyUnit.getRegisterController().getRegister(to);
    Register srcReg = AssemblyUnit.getRegisterController().getRegister(from);
    if (dstReg != null && dstReg.equals(srcReg)) {
        return;
    }
    dstReg = (dstReg == null) ? Register.K0 : dstReg;
    srcReg = RegisterUtils.loadVariableValue(from, srcReg, dstReg);
    if (!srcReg.equals(dstReg)) {
        new MoveAsm(dstReg, srcReg);
    }
    RegisterUtils.memAllocReg(to, dstReg);
}
```

而这里在转move指令部分我们就需要用到分配寄存器，这些需要我们在后续使用图着色算法进行设计。

#### 图着色寄存器分配

由于mips只有32个寄存器，所以如何进行寄存器分配是编译优化当中一个重要的问题。这里的基本思路是保留目前处于活跃状态的寄存器，如果当前寄存器全部分配完成，则需要依靠堆栈进行分配(不过这种情况基本不会出现)。

这里我先用枚举类Register来表示表示MIPS中的寄存器，代码如下:

```java
public enum Register {
    // MIPS registers
    ZERO("$zero"),
    V0("$v0"), V1("$v1"),
    A0("$a0"), A1("$a1"), A2("$a2"), A3("$a3"),
    T0("$t0"), T1("$t1"), T2("$t2"), T3("$t3"), T4("$t4"), T5("$t5"), T6("$t6"), T7("$t7"),
    S0("$s0"), S1("$s1"), S2("$s2"), S3("$s3"), S4("$s4"), S5("$s5"), S6("$s6"), S7("$s7"),
    T8("$t8"), T9("$t9"),
    K0("$k0"), K1("$k1"), GP("$gp"), SP("$sp"), FP("$fp"), RA("$ra");
    /**
     * regName 是寄存器的名称，便于使用enum的值
     */
    private final String regName;

    Register(String regName) {
        this.regName = regName;
    }

    /**
     * regTransform 方法用于将int类型的index转换为Register类型，
     * 类似于enum的valueOf方法
     */
    public static Register regTransform(int index) {
        return values()[index];
    }

    @Override
    public String toString() {
        return regName;
    }
}
```

之后我们在寄存器分配控制器中装入所有可以使用的寄存器，然后完成相关分配工作即可，具体而言是我们需要设计var2reg和reg2var这两个实现value和寄存器的映射即可。

当我们需要分配寄存器给基本块时，首先遍历一边所有指令，记录每个变量在该基本块里最后一次被使用的位置，之后再遍历一遍所有指令,执行releaseReg方法，然后遍历其直接支配的节点,调用映射函数reflection并将该基本块定义的变量对应的寄存器释放，最后将“后继不再使用但是是从indBasicBlock传过来”的变量对应的寄存器映射恢复回来，即可完成申请寄存器操作。简要代码如下:

```java
public static void blockAllocate(BasicBlock entry) {
    HashMap<Value, Value> lastUseMap = new HashMap<>();
    HashSet<Value> defined = new HashSet<>();
    HashSet<Value> used = new HashSet<>();
    entry.getInstrArrayList().forEach(instr -> instr.getOperands().forEach(
        operand -> lastUseMap.put(operand, instr)));
    entry.getInstrArrayList().forEach(instr -> releaseReg(entry, instr, lastUseMap,
                                                          var2reg, reg2var, defined, used));
    entry.getBlockDominateChildList().forEach(RegisterAllocator::reflection);
    for (Value value : defined) {
        if (var2reg.containsKey(value)) {
            reg2var.remove(var2reg.get(value));
        }
    }
    for (Value value : used) {
        if (var2reg.containsKey(value) && !defined.contains(value)) {
            reg2var.put(var2reg.get(value), value);
        }
    }
}
```

而释放寄存器方面，我们分为两种情况讨论。如果该指令的某个operand是该基本块内的最后一次使用，并且该基本块的out中没有这个operand,那么我们可以暂时释放这个变量所占用的寄存器（释放reg2var，但不改变var2reg）。而如果该指令属于定义语句，并且不是创建数组的alloc指令，我们需要为该变量分配寄存器，代码逻辑如下:

```java
private static void releaseReg(BasicBlock entry, Instr instr, HashMap<Value, Value> lastUseMap,
                               HashMap<Value, Register> var2reg,
                               HashMap<Register, Value> reg2var,
                               HashSet<Value> defined, HashSet<Value> used) {
    if (!(instr instanceof PhiInstr)) {
        for (Value operand : instr.getOperands()) {
            if (lastUseMap.get(operand).equals(instr) && var2reg.containsKey(operand) &&
                !entry.getOutBasicBlockHashSet().contains(operand)) {
                reg2var.remove(var2reg.get(operand));
                used.add(operand);
            }
        }
    }
    if (instr.isValid() && !(instr instanceof ZextInstr)) {
        defined.add(instr);
        Register reg = allocRegFor();
        if (reg != null) {
            if (reg2var.containsKey(reg)) {
                var2reg.remove(reg2var.get(reg));
            }
            reg2var.put(reg, instr);
            var2reg.put(instr, reg);
        }
    }
}
```

这样我们就完成了寄存器的分配，但还有一个重要的问题没有解决，哪些寄存器可以释放呢，这就需要活跃性分析决定哪些变量需要保留。

##### 活跃性变量分析

为了实现活跃性变量分析，首先我们需要生成Use-Def链，先使用后定义的变量放在use中，先定义后使用的变量放在def中，代码逻辑如下:

```java
public void genUseDefAnalysis() {
    for (Value operand : this.getOperands()) {
        if (!this.getBelongingBlock().getDefBasicBlockHashSet().contains(operand) &&
            (operand instanceof Instr || operand instanceof GlobalVar
             || operand instanceof Param)) {
            this.getBelongingBlock().getUseBasicBlockHashSet().add(operand);
        }
    }
    if (!this.getBelongingBlock().getUseBasicBlockHashSet().contains(this) && this.isValid()) {
        this.getBelongingBlock().getDefBasicBlockHashSet().add(this);
    }
}
```

之后需要根据之前获得的use和def的情况计算每个基本块的in集合和out集合，即根据后继的in，求出当前block的out，再根据公式in = (out - def) + use，求出当前基本块的in，如果in集合发生变化，则继续执行while循环，否则结束，代码逻辑如下:

```java
public static void calculateInOut(Function function) {
    ArrayList<BasicBlock> basicBlocks = function.getBasicBlocks();
    boolean change = true;
    while (change) {
        change = false;
        for (int i = basicBlocks.size() - 1; i >= 0; i--) {
            BasicBlock basicBlock = basicBlocks.get(i);
            HashSet<Value> out = new HashSet<>();
            basicBlock.getBlockOutBasicBlock().forEach(
                successor -> out.addAll(successor.getInBasicBlockHashSet()));
            addOutBlockHashSet(basicBlock, out);
            HashSet<Value> in = new HashSet<>(out);
            in.removeAll(basicBlock.getDefBasicBlockHashSet());
            in.addAll(basicBlock.getUseBasicBlockHashSet());
            HashSet<Value> originIn = basicBlock.getInBasicBlockHashSet();
            addInBlockHashSet(basicBlock, in);
            if (!in.equals(originIn)) {
                change = true;
            }
        }
    }
}
```

### 跳转指令删除

由于在llvm中每个基本块的最后一定要有跳转语句，而mips如果最后没有跳转则可以自动顺延到下一个块，所以当llvm如果跳转的就是下一个块，则可以省略一条跳转语句，除此之外如果两个块的前序后继均为1则可以直接合并这两个块，这部分比较简单，在此不再详细说明。

> 至此编译器设计完整结束!!!

## 参考编译器

[1].[Hyggge/Petrichor: Java 实现的 SysY - LLVM IR 编译器 (github.com)](https://github.com/Hyggge/Petrichor)

[2].[echo17666/BUAA2022-SysY-Compiler: A SysY Compiler written by Java for the Compiler Technology Course in BUAA (github.com)](https://github.com/echo17666/BUAA2022-SysY-Compiler)

[3].[Chenrt-ggx/MipsCompiler: BUAA 1906 Mips Compiler (github.com)](https://github.com/Chenrt-ggx/MipsCompiler)

[4].[Thysrael/Pansy: 一个简单的编译 SysY 语言（C 语言子集）到 Mips 的编译器，采用 Java 实现。 (github.com)](https://github.com/Thysrael/Pansy)
