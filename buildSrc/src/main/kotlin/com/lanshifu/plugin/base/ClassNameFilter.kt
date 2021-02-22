package com.lanshifu.plugin.base

interface ClassNameFilter {
    fun filter(className: String): Boolean
}