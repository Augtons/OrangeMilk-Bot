package com.github.augtons.orangemilk.user.game.feiHuaLing.database.entities

import javax.persistence.*

@Entity
@Table(name = "poetry")
class PoetryEntity @JvmOverloads constructor (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = 0,

    @Column(name = "sub_sentence", length = 24)
    var subSentence: String = "",

    @Column(name = "sentence", length = 24)
    var sentence: String = "",

    @Column(name = "title", length = 24)
    var title: String = "",

    @Column(name = "author", length = 24)
    var author: String = "",

    @Column(name = "book", length = 24)
    var book: String = ""
) {
    override fun toString(): String {
        return "Poetry(id=$id, subSentence='$subSentence', sentence='$sentence', title='$title', author='$author', book='$book')"
    }
}