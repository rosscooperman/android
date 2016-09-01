package com.knowme.videoplayground.model

class Question(var textResId: Int, private var mAnswerTrue: Boolean) {

    fun isAnswerTrue(): Boolean {
        return mAnswerTrue
    }
}
