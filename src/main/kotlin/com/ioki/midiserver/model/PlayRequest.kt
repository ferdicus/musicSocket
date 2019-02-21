package com.ioki.midiserver.model

import com.google.gson.annotations.SerializedName

data class PlayRequest(
    @SerializedName("pressed")
    val shouldPlay: Boolean,
    @SerializedName("note")
    val note: Int
)