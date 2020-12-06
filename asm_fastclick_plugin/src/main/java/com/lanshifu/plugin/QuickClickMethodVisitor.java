package com.lanshifu.plugin;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

public class QuickClickMethodVisitor extends MethodVisitor {
    private String className;
    private String methodName;

    public QuickClickMethodVisitor(MethodVisitor methodVisitor, String className, String methodName) {
        super(Opcodes.ASM5, methodVisitor);
        this.className = className;
        this.methodName = methodName;
    }

    //方法执行前插入
    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("QuickClickMethodVisitor visitCode------");
/**
 // access flags 0x1
 public onClick(Landroid/view/View;)V
 L0
 LINENUMBER 23 L0  //增加执行if语句
 ALOAD 1
 INVOKESTATIC com/lanshifu/asmapplication/FastClickUtil.canClick (Landroid/view/View;)Z
 IFEQ L1         //满足条件，跳转到 L1
 L2
 LINENUMBER 24 L2   //这个是原来逻辑
 LDC "lxb"
 LDC "onClick"
 INVOKESTATIC android/util/Log.d (Ljava/lang/String;Ljava/lang/String;)I
 POP
 L1
 LINENUMBER 27 L1
 FRAME SAME
 RETURN
 L3
 LOCALVARIABLE this Lcom/lanshifu/asmapplication/TestActivity$1; L0 L3 0
 LOCALVARIABLE v Landroid/view/View; L0 L3 1
 MAXSTACK = 2
 MAXLOCALS = 2
 */

//字节码操作参考文章 https://www.jianshu.com/p/92a75a18cbc1

/**
 ifeq:若栈顶int类型为0则跳转；
 ifne:若栈顶int类型不为0则跳转；
 iflt:若栈顶int类型小于0则跳转；
 ifle: 若栈顶int类型小于等于0则跳转；
 ifgt:若栈顶int类型大于0则跳转；
 ifge:若栈顶int类型大于等于0则跳转；
 if_icmpeq:若栈顶两int类型相等则跳转；
 if_icmpne: 若栈顶两int类型相等则跳转；
 if_icmplt:若栈顶int前小于后则跳转;
 if_icpmle:若栈顶int前小于等于后则跳转;
 if_icpmgt: 若栈顶int前大于后则跳转;
 if_icpmge: 若栈顶int前大于等于后则跳转;
 ifnull: 如栈顶引用为空则跳转;
 ifnonnull:若栈顶引用不为空则跳转;
 if_acmpeq:若栈顶两引用相等则跳转；
 if_acmpne: 若栈顶两引用不相等则跳转;

 */

        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/lanshifu/asm_plugin_library/FastClickUtil",
                "canClick", "(Landroid/view/View;)Z", false);
        Label label = new Label();
        mv.visitJumpInsn(IFNE, label); //ifne 跳转指令，如果栈顶元素不为0，就跳转，即canClick为true就跳转

        /**打印log*/
        //压栈
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn("fast click...");
        //访问方法指令
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
//        mv.visitInsn(Opcodes.POP);//出栈
        mv.visitInsn(RETURN); //return，不继续走了

        //canClick为true就跳转到这里
        mv.visitLabel(label);

    }
}