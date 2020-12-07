package com.lanshifu.asm_plugin_library

import android.os.Looper
import java.util.*

/**
 * 检测快速点击
 */
object MethodTimeUtil {
    private val methodList: MutableList<Entity> =
        LinkedList()

    @JvmStatic
    fun start(name: String) {
        if (isOpenTraceMethod) {
            synchronized(
                methodList
            ) {
                methodList.add(
                    Entity(
                        name,
                        System.currentTimeMillis(),
                        true,
                        isInMainThread
                    )
                )
            }
        }
    }

    @JvmStatic
    fun end(name: String) {
        if (isOpenTraceMethod) {
            MethodTimeLog.d("执行了方法:$name")
            synchronized(
                methodList
            ) {
                methodList.add(
                    Entity(
                        name,
                        System.currentTimeMillis(),
                        false,
                        isInMainThread
                    )
                )
            }
        }
    }

    /**
     * 处理插桩数据，按顺序获取所有方法耗时
     */
    @JvmStatic
    fun obtainMethodCostData(): MutableList<MethodInfo> {
        synchronized(methodList) {
            val resultList: MutableList<MethodInfo> = mutableListOf()
            for (i in methodList.indices) {
                val startEntity = methodList[i]
                if (!startEntity.isStart) {
                    continue
                }

                //找到start
                startEntity.pos = i
                val endEntity =
                    findEndEntity(startEntity.name, i + 1)
                if (startEntity != null && endEntity != null && endEntity.time - startEntity.time > 0) {
                    resultList.add(createMethodInfo(startEntity, endEntity))
                }
            }
            return resultList
        }
    }

    private fun createMethodInfo(
        startEntity: Entity,
        endEntity: Entity
    ): MethodInfo {
        return MethodInfo(
            startEntity.name,
            endEntity.time - startEntity.time,
            startEntity.pos,
            endEntity.pos,
            startEntity.isMainThread
        )
    }

    /**
     * 找到方法对应的结束点
     *
     * @param name
     * @param startPos
     * @return
     */
    private fun findEndEntity(name: String, startPos: Int): Entity? {
        var sameCount = 1
        for (i in startPos until methodList.size) {
            val endEntity = methodList[i]
            if (endEntity.name == name) {
                if (endEntity.isStart) {
                    sameCount++
                } else {
                    sameCount--
                }
                if (sameCount == 0 && !endEntity.isStart) {
                    endEntity.pos = i
                    return endEntity
                }
            }
        }
        return null
    }

    val isInMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    private val isOpenTraceMethod: Boolean
        private get() = true

    internal class Entity(
        var name: String,
        var time: Long,
        var isStart: Boolean,
        var isMainThread: Boolean
    ) {
        var pos = 0

    }
}