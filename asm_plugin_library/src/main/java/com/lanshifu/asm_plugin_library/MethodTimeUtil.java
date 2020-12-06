package com.lanshifu.asm_plugin_library;

import android.os.Looper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 检测快速点击
 */
public class MethodTimeUtil {

    private static List<Entity> methodList = new LinkedList<>();

    public static void start(String name) {
        if (isOpenTraceMethod()) {
            synchronized (methodList) {
                methodList.add(new Entity(name, System.currentTimeMillis(), true, isInMainThread()));
            }
        }
    }

    public static void end(String name) {
        if (isOpenTraceMethod()) {
            MethodTimeLog.d("执行了方法:" + name);
            synchronized (methodList) {
                methodList.add(new Entity(name, System.currentTimeMillis(), false, isInMainThread()));
            }
        }
    }


    /**
     * 处理插桩数据，按顺序获取所有方法耗时
     */
    public static List<MethodInfo> obtainMethodCostData() {
        synchronized (methodList) {
            List<MethodInfo> resultList = new ArrayList();
            for (int i = 0; i < methodList.size(); i++) {
                Entity startEntity = methodList.get(i);
                if (!startEntity.isStart) {
                    continue;
                }

                //找到start
                startEntity.pos = i;
                Entity endEntity = findEndEntity(startEntity.name, i + 1);

                if (startEntity != null && endEntity != null && endEntity.time - startEntity.time > 0) {
                    resultList.add(createMethodInfo(startEntity, endEntity));
                }
            }
            return resultList;
        }
    }


    private static MethodInfo createMethodInfo(Entity startEntity, Entity endEntity) {
        return new MethodInfo(startEntity.name,
                endEntity.time - startEntity.time, startEntity.pos, endEntity.pos, startEntity.isMainThread);
    }


    /**
     * 找到方法对应的结束点
     *
     * @param name
     * @param startPos
     * @return
     */
    private static Entity findEndEntity(String name, int startPos) {
        int sameCount = 1;
        for (int i = startPos; i < methodList.size(); i++) {
            Entity endEntity = methodList.get(i);
            if (endEntity.name.equals(name)) {
                if (endEntity.isStart) {
                    sameCount++;
                } else {
                    sameCount--;
                }
                if (sameCount == 0 && !endEntity.isStart) {
                    endEntity.pos = i;
                    return endEntity;
                }
            }
        }
        return null;
    }


    public static boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    private static boolean isOpenTraceMethod() {
        return true;
    }

    static class Entity {
        public String name;
        public Long time;
        public boolean isStart;
        public int pos;
        public boolean isMainThread;

        public Entity(String name, Long time, boolean isStart, boolean isMainThread) {
            this.name = name;
            this.time = time;
            this.isStart = isStart;
            this.isMainThread = isMainThread;
        }
    }

    static class MethodInfo {
        public String name;
        public Long costTime;
        public int startPos;
        public int endPos;
        public boolean isMainThread;

        public MethodInfo(String name,
                          Long costTime,
                          int startPos,
                          int endPos,
                          boolean isMainThread) {
            this.name = name;
            this.costTime = costTime;
            this.startPos = startPos;
            this.endPos = endPos;
            this.isMainThread = isMainThread;
        }
    }
}

