package com.github.augtons.orangemilk.utils

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File

internal class HttputilsKtTest {

    @Test
    fun `test downloading file`() {
        val url = "https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png"
        val targetPath = "C:\\Users\\Augtons\\Desktop\\sss.png"
        httpDownloadFileBlock(url, File(targetPath))
    }

    @Test
    fun `test downloading file suspend`() {
        val url = "https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png"
        val targetPath = "C:\\Users\\Augtons\\Desktop\\sss.png"

        runBlocking {
            httpDownloadFileSuspend(url, File(targetPath))
        }
    }

    @Test
    fun `sdsd`() {
        val url = "http://81.68.161.75:3000/login/status"
        runBlocking { httpGetString(url) }.let {
            println(it)
        }
    }
}