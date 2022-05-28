@file:Suppress("NOTHING_TO_INLINE")
package com.github.augtons.orangemilk.utils

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream

/********************************实用拓展(语法糖)*********************************************/

/**
 * 获取slf4j的Logger
 * @author augtons
 */
inline fun <reified T> logger(): Logger = LoggerFactory.getLogger(T::class.java)

inline fun <T> Boolean.then(block: () -> T) = if (this) block() else null


/********************************JSON操作*********************************************/
/**
 * 转JSON
 * @author augtons
 */
inline fun <T> T.toJSONString() = Gson().toJson(this)

/**
 * JSON转对象
 * @author augtons
 */
inline fun <reified T> fromJSONString(json: String) = Gson().fromJson(json, T::class.java)



/********************************系统*********************************************/
/**
 * 获取当前的系统启动时间(毫秒)，等价于[System.currentTimeMillis()]
 * @author augtons
 */
fun nowMillis() = System.currentTimeMillis()



/********************************IO操作*********************************************/
/**
 * 一个流的数据粘贴到另外一个流，同时关闭两个流
 * @author augtons
 */
inline fun InputStream.pasteTo(target: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
    this.use { from ->
        target.use { to ->
            from.copyTo(to, bufferSize)
        }
    }
}