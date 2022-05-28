package com.github.augtons.orangemilk.media.music

sealed class MusicSearcher {
    abstract fun search(keyword: String): MusicResult?
}
