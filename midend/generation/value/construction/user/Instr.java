package midend.generation.value.construction.user;

import backend.generation.mips.asm.textsegment.structure.Comment;
import midend.simplify.controller.datastruct.FunctionClone;
import midend.simplify.method.FunctionInlineUnit;
import iostream.declare.GetIntDeclare;
import iostream.structure.IoStreamGeneration;
import iostream.structure.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.User;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.IcmpInstr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.ZextInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.method.Mem2RegUnit;

/**
 * Instruction 是 LLVM IR 中的指令成分，
 * 继承于User，主要用于生成指令
 */
public class Instr extends User {
    /**
     * instrType 是该 Instr 的类型
     * parent 是该 Instr 所属的基本块
     */
    protected String instrType;
    private BasicBlock parent;

    public Instr(IrType type, String name, String instrType) {
        super(type, name);
        this.instrType = instrType;
        if (!OptimizerUnit.isOptimizer()) {
            IrNameController.addInstr(this);
        }
    }

    public String getInstrType() {
        return instrType;
    }

    public void setBelongingBlock(BasicBlock currentBasicBlock) {
        this.parent = currentBasicBlock;
    }

    public BasicBlock getBelongingBlock() {
        return parent;
    }

    @Override
    public void generateAssembly() {
        new Comment(this.toString());
    }

    /**
     * insertPhiProcess 方法用于在 Mem2Reg 优化中插入 Phi 指令
     * 该函数首先初始化和该alloca指令相关的数据结构
     * 之后找出需要添加phi指令的基本块，并添加phiInstr
     */
    public void insertPhiProcess() {
        Mem2RegUnit.reConfig(this);
        Mem2RegUnit.insertPhi();
    }

    /**
     * isDead 方法用于判断该 Instr 是否为死代码
     * 主要用于中间代码的死代码优化
     */
    public boolean isDead() {
        return this.isValid() && !(this instanceof CallInstr) &&
                !(this instanceof IoStreamGeneration) && this.getUseDefChain().isEmpty();
    }

    /**
     * isValid 方法用于判断该 Instr 是否为有效指令
     * isValid 的指令有 alloca，alu，call，gep，io，getint，load，phi，zext
     * 其中call指令调用的函数将指针作为形参、修改全局变量、调用了其他函数，
     * 因此不能直接删除
     * io中的getint指令获得的数字即使没有用到也应该完成io操作，也不能删除
     */
    public boolean isValid() {
        boolean valid = this instanceof AllocaInstr || this instanceof CalcInstr ||
                (this instanceof CallInstr callInstr && !callInstr.getType().isVoid()) ||
                this instanceof GetIntDeclare;
        return valid || this instanceof PhiInstr ||
                this instanceof GetEleInstr || this instanceof IcmpInstr ||
                this instanceof LoadInstr || this instanceof ZextInstr;
    }

    /**
     * getGlobalVariableNumberingHash 方法用于获取该 Instr 的全局变量编号哈希值
     * 主要用于全局变量编号优化(GVN)
     */
    public String getGlobalVariableNumberingHash() {
        return null;
    }

    /**
     * addPhiToUse 方法用于将 Phi 指令的操作数添加到 Use 集合中
     * 主要用于活跃变量分析
     */
    public void addPhiToUse() {
        if (this instanceof PhiInstr phiInstr) {
            for (Value operand : phiInstr.getOperands()) {
                if (operand instanceof Instr || operand instanceof GlobalVar
                        || operand instanceof Param) {
                    this.getBelongingBlock().getUseBasicBlockHashSet().add(operand);
                }
            }
        }
    }

    /**
     * genUseDefAnalysis 方法用于生成 Use-Def 链和 Def-Use 链
     * 先使用后定义的变量放在use中
     * 先定义后使用的变量放在def中
     */
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

    /**
     * buildFuncCallGraph 方法用于构建函数调用图
     * 主要用于函数内联
     */
    public void buildFuncCallGraph() {
        if (this instanceof CallInstr callInstr) {
            Function response = callInstr.getBelongingBlock().getBelongingFunc();
            Function curFunc = this.getBelongingBlock().getBelongingFunc();
            if (!curFunc.isBuildin()) {
                if (!FunctionInlineUnit.getCaller(curFunc).contains(response)) {
                    FunctionInlineUnit.addCaller(curFunc, response);
                }
                if (!FunctionInlineUnit.getResponse(response).contains(curFunc)) {
                    FunctionInlineUnit.addResponse(response, curFunc);
                }
            }
        }
    }

    /**
     * copy 方法用于复制该 Instr
     * 主要用于函数内联
     */
    public Value copy(FunctionClone functionClone) {
        return null;
    }
}
