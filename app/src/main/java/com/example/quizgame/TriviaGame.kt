package com.example.quizgame

import android.content.Context
import android.media.MediaPlayer
import androidx.core.text.HtmlCompat
import com.example.quizgame.Models.TriviaQuestion

class TriviaGame(
    triviaQuestionList: List<TriviaQuestion>,
    context: Context?,
    val isTimerInUse : Boolean
) {

    private val correctSoundRef = R.raw.correct_choice_sound
    private val incorrectSoundRef = R.raw.incorrect_choice_sound

    private val correctAnswerPlayer: MediaPlayer = MediaPlayer.create(context, correctSoundRef)
    private val incorrectAnswerPlayer: MediaPlayer = MediaPlayer.create(context, incorrectSoundRef)

    // Game variables
    var listOfTriviaQuestions: List<TriviaQuestion> = triviaQuestionList
    private var answersUserInputted: ArrayList<String> = ArrayList()
    var shuffledPossibleAnswers: List<String> =
        shuffleAndFormat(getCurrentTriviaQuestion().incorrect_answers, getCurrentAnswer())

    var indexOfAnswerInShuffledList: Int = findIndexOfCorrectAnswer()
    var numberAnswered = 0
    var numberCorrect = 0
    var currentIndex = 0
    var hasNextQuestion = false
        get() {
            field = currentIndex + 1 < listOfTriviaQuestions.size
            return field
        }

    fun getNextQuestion(): TriviaQuestion {
        if (hasNextQuestion) {
            currentIndex++
            shuffledPossibleAnswers =
                shuffleAndFormat(getCurrentTriviaQuestion().incorrect_answers, getCurrentAnswer())
            indexOfAnswerInShuffledList = findIndexOfCorrectAnswer()
            return listOfTriviaQuestions[currentIndex]
        }
        return getCurrentTriviaQuestion()
    }

    private fun getCurrentTriviaQuestion(): TriviaQuestion {
        return listOfTriviaQuestions[currentIndex]
    }

    fun getListOfShuffledPossibleAnswers(): List<String> {
        return shuffledPossibleAnswers
    }

    private fun getCurrentAnswer(): String {
        return formatString(getCurrentTriviaQuestion().correct_answer)
    }

    fun getCurrentQuestionString(): String {
        return formatString(getCurrentTriviaQuestion().question)
    }

    private fun playerAnsweredCorrectly() {
        numberCorrect++

        if (correctAnswerPlayer.isPlaying) {
            correctAnswerPlayer.stop()
            correctAnswerPlayer.prepare()
        }
        correctAnswerPlayer.start()
    }

    private fun playerAnsweredIncorrect() {

        if (incorrectAnswerPlayer.isPlaying) {
            incorrectAnswerPlayer.stop()
            incorrectAnswerPlayer.reset()
        }
        incorrectAnswerPlayer.start()
    }

    fun determineIsUserCorrect(userSelection: String): Boolean {

        val isPlayerCorrect = userSelection == getCurrentAnswer()

        answersUserInputted.add(userSelection)

        if (numberAnswered + 1 < listOfTriviaQuestions.size) {
            numberAnswered++
        }

        if (isPlayerCorrect) {
            playerAnsweredCorrectly()
        } else {
            playerAnsweredIncorrect()
        }

        return isPlayerCorrect
    }

    fun getArrayOfUserAnswers(): Array<String> {
        return answersUserInputted.toTypedArray()
    }

    fun getArrayOfQuestionsReached(): Array<String> {
        val list = arrayListOf<String>()
        for (i in 0 until numberAnswered) {
            list.add(formatString(listOfTriviaQuestions.get(i).question))
        }

        return list.toTypedArray()
    }

    fun getArrayOfCorrectAnswers(): Array<String> {
        val list = arrayListOf<String>()

        for (i in 0 until numberAnswered) {
            list.add(formatString(listOfTriviaQuestions.get(i).correct_answer))
        }

        return list.toTypedArray()
    }

    private fun shuffleAndFormat(
        incorrectAnswers: List<String>,
        correctAnswer: String
    ): List<String> {
        val list: MutableList<String> = mutableListOf(formatString(correctAnswer))

        for (item in incorrectAnswers) {
            list.add(formatString(item))
        }
        list.shuffle()

        return list
    }

    private fun formatString(response: String): String {
        return HtmlCompat.fromHtml(response, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun findIndexOfCorrectAnswer(): Int {
        for (i in shuffledPossibleAnswers.indices) {
            if (shuffledPossibleAnswers[i] == getCurrentAnswer()) {
                return i
            }
        }
        return -1

    }
    fun gameOver(){
        correctAnswerPlayer.stop()
        correctAnswerPlayer.reset()
        correctAnswerPlayer.release()

        incorrectAnswerPlayer.stop()
        incorrectAnswerPlayer.reset()
        incorrectAnswerPlayer.release()

    }
}