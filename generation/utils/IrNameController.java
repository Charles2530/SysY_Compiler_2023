package generation.utils;

import java.util.HashMap;

public class IrNameController {
    private static HashMap<IrPrefix, String> irPrefixHashMap;
    private Integer bbNameIndex = 0;
    private Integer paramNameIndex = 0;
    private Integer stringLiteralNameIndex = 0;

    public static void init() {
        IrNameController.irPrefixHashMap = new HashMap<>();
        IrNameController.irPrefixHashMap.put(IrPrefix.BB_NAME, "block_label_");
        IrNameController.irPrefixHashMap.put(IrPrefix.FUNC_NAME, "@f_");
        IrNameController.irPrefixHashMap.put(IrPrefix.GLOBAL_VAR_NAME, "@");
        IrNameController.irPrefixHashMap.put(IrPrefix.LOCAL_VAR_NAME, "%v");
        IrNameController.irPrefixHashMap.put(IrPrefix.PARAM_NAME, "%a");
        IrNameController.irPrefixHashMap.put(IrPrefix.STRING_LITERAL_NAME, "@s");
    }

    public static String getPrefix(IrPrefix prefix) {
        return irPrefixHashMap.get(prefix);
    }

    public String getBbName() {
        return IrNameController.getPrefix(IrPrefix.BB_NAME) + bbNameIndex++;
    }

    public String getGlobalVarName(String varName) {
        return IrNameController.getPrefix(IrPrefix.GLOBAL_VAR_NAME) + varName;
    }

    public String getParamName() {
        return IrNameController.getPrefix(IrPrefix.PARAM_NAME) + paramNameIndex++;
    }

    public String getStringLiteralName() {
        return IrNameController.getPrefix(IrPrefix.STRING_LITERAL_NAME) + stringLiteralNameIndex++;
    }
}
