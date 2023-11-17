package midend.simplify.method;

import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;

import java.util.HashSet;

/**
 * GlobalCodeMovementUnit 是全局代码移动单元，
 * 主要用于全局代码移动
 * 一个指令的位置是由他使用的指令和使用他的指令决定的
 * 我们找到的是一个区间，这个区间上指令可以自由的移动
 * 我们要挑选尽可能靠近支配树根节点和尽可能循环深度比较深的点
 */
public class GlobalCodeMovementUnit {
    /**
     * visited 是该 GlobalCodeMovementUnit 的已访问指令集合
     */
    private static HashSet<Instr> visited;

    public static void run(Module module) {
        GlobalCodeMovementUnit.init();
        module.getFunctions().forEach(Function::globalCodeMovementAnalysis);
    }

    /**
     * init 方法用于初始化该 GlobalCodeMovementUnit
     */
    private static void init() {
        GlobalCodeMovementUnit.visited = new HashSet<>();
    }

    public static HashSet<Instr> getVisited() {
        return visited;
    }
}
