package com.lanshifu.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;


/**
 * MethodVisitor 的子类,
 * 提供了 onMethodEnter 和 onMethodExit 方法，方便我们使用
 */
public class MethodTimeAdviceAdapter extends AdviceAdapter {

    private int access;
    private String methodName = "";
    private String className = "";
    private String desc = "";
    private int maxSectionNameLength = 254;


    protected MethodTimeAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String className, String methodName, String descriptor) {
        super(api, methodVisitor, access, methodName, descriptor);

        this.access = access;
        this.className = className;
        this.desc = descriptor;
        this.methodName = getMethodNameText(methodName);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        //常量入栈
        mv.visitLdcInsn(generatorMethodName());
        //调用静态方法
        mv.visitMethodInsn(
                INVOKESTATIC,
                CLASS_METHOD_TIME_UTIL,
                "start",
                "(Ljava/lang/String;)V",
                false
        );

    }

    @Override
    protected void onMethodExit(int opcode) {
        //常量入栈
        mv.visitLdcInsn(generatorMethodName());
        //调用静态方法
        mv.visitMethodInsn(
                INVOKESTATIC,
                CLASS_METHOD_TIME_UTIL,
                "end",
                "(Ljava/lang/String;)V",
                false
        );
    }

    private String generatorMethodName(){
        String sectionName = methodName;
        int length = sectionName.length();
        if (length > maxSectionNameLength && !sectionName.isEmpty()) {
            // 先去掉参数
            int parmIndex = sectionName.indexOf('(');;
            sectionName = sectionName.substring(0, parmIndex);
            // 如果依然更大，直接裁剪
            length = sectionName.length();
            if (length > 127) {
                sectionName = sectionName.substring(length - maxSectionNameLength);
            }
        }
        return sectionName;
    }

    String getMethodNameText(String methodName) {
         if (desc == null) {
             return this.className + "." + methodName;
        } else {
            return this.className + "." + methodName + "." + desc;
        }
    }


    public static String CLASS_METHOD_TIME_UTIL = "com/lanshifu/asm_plugin_library/MethodTimeUtil";

}