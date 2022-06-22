package com.example.epic.data


data class Music(
    var id: String? = null,
    var title: String? = null,
    val artist: String? = null,
    val cover: String? = null,
    var lyrics: String? = null,
    var chords: String? = null,
    var rhythms: String? = null,
    val tones: String? = null,
    val youtubeLink: String? = null,
    var isGlobalMusic: String? = "true"
)
