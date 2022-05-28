package com.github.augtons.orangemilk.utils

import kotlinx.coroutines.internal.resumeCancellableWith
import okhttp3.*
import org.slf4j.LoggerFactory
import org.springframework.web.util.UriComponentsBuilder
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.reflect

fun buildUri(baseString: String, block: UriComponentsBuilder.() -> Any): String {

    return when(val r = UriComponentsBuilder.fromUriString(baseString).block()) {
        Unit -> baseString
        is UriComponentsBuilder -> r.toUriString()
        else -> throw IllegalStateException("block返回值错误")
    }
}

/**
 * 挂起函数
 * 用于通过HTTP，Get一个字符串。
 * @return 成功返回获取到的字符串，失败返回空字符串""
 */
suspend fun httpGetString(url: String): String {
    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    return suspendCoroutine {
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resume("")
            }

            override fun onResponse(call: Call, response: Response) {
                it.resume(response.body?.string() ?: "")
                response.close()
            }
        })
    }
}

/**
 * 通过Http下载一个文件，存放到指定文件
 * @return 函数将阻塞，直至成功或出错
 * @throws IOException 请求失败或文件写入失败
 * @see httpDownloadFileSuspend 此函数的挂起版本
 */
@Throws(IOException::class)
fun httpDownloadFileBlock(url: String, targetFile: File) {
    val request = Request.Builder()
        .url(url)
        .build()

    OkHttpClient().newCall(request).execute().use { response ->
        response.body?.byteStream()?.run {
            targetFile.outputStream().use { fileStream ->
                copyTo(fileStream)
            }
        }
        response.close()
    }

}

/**
 * 通过Http下载一个文件，存放到指定文件
 * @return 函数将挂起，直至成功或出错
 * @throws IOException 请求失败或文件写入失败
 * @see httpDownloadFileBlock 此函数的阻塞版本
 */
@Throws(IOException::class)
suspend fun httpDownloadFileSuspend(url: String, targetFile: File) {
    val request = Request.Builder()
        .url(url)
        .build()

    suspendCoroutine<Unit> {
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.byteStream()?.run {
                    targetFile.outputStream().use { fileStream ->
                        copyTo(fileStream)
                    }
                }
                response.close()
                it.resume(Unit)
            }
        })
    }
}