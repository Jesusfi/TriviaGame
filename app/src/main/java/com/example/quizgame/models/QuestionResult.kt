package com.example.quizgame.models

import android.os.Parcel
import android.os.Parcelable

data class QuestionResult(
    val question: String,
    val correctAnswer: String,
    val userAnswer: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString().toString(),
        source.readString().toString(),
        source.readString().toString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(question)
        writeString(correctAnswer)
        writeString(userAnswer)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<QuestionResult> =
            object : Parcelable.Creator<QuestionResult> {
                override fun createFromParcel(source: Parcel): QuestionResult =
                    QuestionResult(source)

                override fun newArray(size: Int): Array<QuestionResult?> = arrayOfNulls(size)
            }
    }
}