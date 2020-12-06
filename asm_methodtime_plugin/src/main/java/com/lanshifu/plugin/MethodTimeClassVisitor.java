package com.lanshifu.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * ClassVisitor，负责访问.class文件中各个元素，还记得上一课时我们介绍的.class文件结构吗？
 * ClassVisitor就是用来解析这些文件结构的，当解析到某些特定结构时（比如类变量、方法），
 * 它会自动调用内部相应的 FieldVisitor 或者 MethodVisitor 的方法，进一步解析或者修改 .class 文件内容
 */
public class MethodTimeClassVisitor extends ClassVisitor {
    private String className;
    private String superName;
    private boolean isABSClass;


    public MethodTimeClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.superName = superName;


//        String resultClassName = name.replace(".", "/");
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        boolean isSystemApi = className.startsWith("android") || superName.startsWith("java") || superName.startsWith("kotlin");
        boolean isConstruction = name.equals("<init>");
        if (isSystemApi || isConstruction) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        } else {
            System.out.println("MethodTimeClassVisitor visitMethod name=" + name + ", superName=" + superName + "," +
                    "desc=" + desc + ",isConstruction=" + isConstruction + ",className="+className);
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            return new MethodTimeAdviceAdapter(Opcodes.ASM5, mv, access, className, name, desc);
        }

    }
}
