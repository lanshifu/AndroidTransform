package com.lanshifu.asm_plugin_library

/**
 * @author lanxiaobin
 * @date 2020/12/7
 */
class MethodInfo(
    var name: String,
    var costTime: Long,
    var startPos: Int,
    var endPos: Int,
    var isMainThread: Boolean
) {
    override fun toString(): String {
        return "MethodInfo{" +
                "name='" + name + '\'' +
                ", costTime=" + costTime +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", isMainThread=" + isMainThread +
                '}'
    }

}