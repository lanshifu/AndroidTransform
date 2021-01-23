//import com.didiglobal.booster.kotlinx.file
//import com.didiglobal.booster.kotlinx.touch
//import com.didiglobal.booster.transform.TransformContext
//import com.didiglobal.booster.transform.asm.*
//import com.google.auto.service.AutoService
//import org.objectweb.asm.Opcodes
//import org.objectweb.asm.tree.*
//import java.io.PrintWriter
//
///**
// * @author lanxiaobin
// * @date 2020/11/28
// */
//@AutoService(ClassTransformer::class)
//class MethodConstTransform : ClassTransformer {
//
//    private lateinit var logger: PrintWriter
//
//    override fun onPreTransform(context: TransformContext) {
//        super.onPreTransform(context)
//        this.logger =
//            context.reportsDir.file("MethodConstTransform").file(context.name).file("report.txt")
//                .touch()
//                .printWriter()
//
//    }
//
//    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
//
//        klass.methods?.forEach { method ->
//            //遍历每个方法的指令
//
//            val firstMethodInsnNode = method.instructions.first
//            val lastMethodInsnNode = method.instructions.last
//            val maxLocals = method.maxLocals
//
//            //局部变量表+3
//            val indexStartTime = maxLocals
//            val indexEndTime = maxLocals + 1
//            val indexConstTime = maxLocals + 2
//            val indexResult = maxLocals + 3
//
//
//            //1.计算startTime，并保存到局部变量表
//            method.instructions.insertBefore(
//                firstMethodInsnNode, MethodInsnNode(
//                    Opcodes.INVOKESTATIC,
//                    "java/lang/System",
//                    "currentTimeMillis",
//                    "()J",
//                    false
//                )
//            )
//            method.instructions.insertBefore(firstMethodInsnNode, VarInsnNode(Opcodes.LSTORE, indexStartTime))
//
//            //2.计算endTime，并保存到局部变量表
//            method.instructions.insertBefore(
//                lastMethodInsnNode, MethodInsnNode(
//                    Opcodes.INVOKESTATIC,
//                    "java/lang/System",
//                    "currentTimeMilli" +
//                            "s",
//                    "()J", false
//                )
//            )
//            method.instructions.insertBefore(
//                lastMethodInsnNode, VarInsnNode(Opcodes.LSTORE, indexEndTime)
//            )
//
//            //3.减法，结果保存到局部变量表
//            method.instructions.insertBefore(
//                lastMethodInsnNode, VarInsnNode(Opcodes.LLOAD, indexEndTime)
//            )
//            method.instructions.insertBefore(
//                lastMethodInsnNode, VarInsnNode(Opcodes.LLOAD, indexStartTime)
//            )
//            method.instructions.insertBefore(lastMethodInsnNode, InsnNode(Opcodes.LSUB)) //LSUB 是减法指令
//            //结果保存到局部变量表
//            method.instructions.insertBefore(
//                firstMethodInsnNode,
//                VarInsnNode(Opcodes.LSTORE, indexConstTime)
//            )
//
//            //打印log，new StringBuilder
//            method.instructions.insertBefore(lastMethodInsnNode,
//                TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder")
//            )
//            method.instructions.insertBefore(lastMethodInsnNode, InsnNode(Opcodes.DUP)) //?
//
//            method.instructions.insertBefore(lastMethodInsnNode, MethodInsnNode(
//                    Opcodes.INVOKESPECIAL,
//                    "java/lang/StringBuilder", "<init>", "()V", false
//                )
//            )
//
//            method.instructions.insertBefore(lastMethodInsnNode, LdcInsnNode("constTime:"))
//            method.instructions.insertBefore(
//                lastMethodInsnNode, MethodInsnNode(
//                    Opcodes.INVOKESPECIAL,
//                    "java/lang/StringBuilder",
//                    "append",
//                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
//                    false
//                )
//            )
//            method.instructions.insertBefore(lastMethodInsnNode,
//                VarInsnNode(Opcodes.LLOAD, indexConstTime)
//            )
//            method.instructions.insertBefore(
//                lastMethodInsnNode, MethodInsnNode(
//                    Opcodes.INVOKESPECIAL,
//                    "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false
//                )
//            )
//
//            method.instructions.insertBefore(
//                lastMethodInsnNode, MethodInsnNode(
//                    Opcodes.INVOKESPECIAL,
//                    "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false
//                )
//            )
//
//            method.instructions.insertBefore(
//                lastMethodInsnNode,
//                VarInsnNode(Opcodes.ASTORE, indexResult)
//            )
//            method.instructions.insertBefore(lastMethodInsnNode, LdcInsnNode("TAG"))
//            method.instructions.insertBefore(
//                lastMethodInsnNode,
//                VarInsnNode(Opcodes.ALOAD, indexResult)
//            )
//
//            method.instructions.insertBefore(
//                lastMethodInsnNode,
//                MethodInsnNode(
//                    Opcodes.INVOKESTATIC,
//                    "android/util/Log",
//                    "d",
//                    "(Ljava/lang/String;Ljava/lang/String;)I"
//                )
//            )
//
//            method.instructions.insertBefore(lastMethodInsnNode, InsnNode(Opcodes.POP))
//            method.instructions.insertBefore(lastMethodInsnNode, InsnNode(Opcodes.RETURN))
//        }
//        return klass
//    }
//
//    override fun onPostTransform(context: TransformContext) {
//        super.onPostTransform(context)
//
//        logger.println("ThreadTransform onPostTransform")
//        this.logger.close()
//    }
//
//}