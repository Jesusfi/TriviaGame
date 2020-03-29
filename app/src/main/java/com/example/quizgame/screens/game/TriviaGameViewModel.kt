package com.example.quizgame.screens.game

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizgame.models.QuestionResult
import com.example.quizgame.models.TriviaQuestion
import com.example.quizgame.utils.GameUtils
import com.example.quizgame.utils.GameUtils.formatHtmlStringForDisplay
import timber.log.Timber

class TriviaGameViewModel(
    listOfTriviaQuestions: List<TriviaQuestion>, hasTimerEnabled: Boolean
) :
    ViewModel() {

    // Fields
    private val triviaQuestionList: List<TriviaQuestion> = listOfTriviaQuestions
    private var currentIndex: Int = 0

    private var _numberUserAnsweredCorrectly: Int = 0
    val numberPlayerAnsweredCorrectly: Int
        get() = _numberUserAnsweredCorrectly

    private val _listOfAnswersPlayerReached: ArrayList<QuestionResult> = arrayListOf()
    val listOfAnswersPlayerReached: Array<QuestionResult>
        get() = _listOfAnswersPlayerReached.toTypedArray()

    private lateinit var timer: CountDownTimer

    // Properties
//    private val hasMoreQuestions: Boolean
//        get() = currentIndex < triviaQuestionList.size

    // Live Data Objects
    private val _currentQuestion = MutableLiveData<TriviaQuestion>()
    val currentQuestion: LiveData<TriviaQuestion>
        get() = _currentQuestion

    private val _currentShuffledList = MutableLiveData<List<String>>()
    val currentShuffledList: LiveData<List<String>>
        get() = _currentShuffledList

    private val _onGameOverEvent = MutableLiveData<Boolean>()
    val onGameOverEvent: LiveData<Boolean>
        get() = _onGameOverEvent

    private val _playerDecisionResult = MutableLiveData<Pair<Boolean, Int>>()
    val playerDecisionResult: LiveData<Pair<Boolean, Int>>
        get() = _playerDecisionResult

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long>
        get() = _timeLeft

    init {
        _onGameOverEvent.value = false
        nextQuestion()
//        triviaQuestionList.forEach {
//            Timber.d(it.question)
//        }

        Timber.d("Timer Enabled: ${hasTimerEnabled}")

        if (hasTimerEnabled) {
            _timeLeft.value = 30000
            timer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _timeLeft.value = _timeLeft.value!!.minus(1000)
                    Timber.d("time left is ${timeLeft.value}")
                }

                override fun onFinish() {
                    _onGameOverEvent.value = true
                }
            }.start()
        }
    }

    fun nextQuestion() {
        if (currentIndex < triviaQuestionList.size) {
            _currentQuestion.value = triviaQuestionList[currentIndex]
            shuffledList()
            currentIndex++

        } else {
            gameOver()
        }
    }

    fun onUserAnswer(userAnswer: String) {
        val correctAnswer = GameUtils.formatHtmlStringForDisplay(_currentQuestion.value?.correct_answer)
        if (userAnswer.equals(correctAnswer)) {
            _numberUserAnsweredCorrectly++
            _playerDecisionResult.value = Pair(true, findIndexWithCorrectAnswer())
            Timber.d("Player answered correctly")
        } else {
            _playerDecisionResult.value = Pair(false, findIndexWithCorrectAnswer())
            Timber.d("Player answered incorrectly")
        }

        updateQuestionsPlayerAnswered(userAnswer)
    }

    private fun updateQuestionsPlayerAnswered(userAnswer: String) {
        val questionResult = QuestionResult(
            formatHtmlStringForDisplay(_currentQuestion.value?.question!!),
            formatHtmlStringForDisplay(_currentQuestion.value?.correct_answer!!),
            userAnswer
        )
        _listOfAnswersPlayerReached.add(questionResult)
    }

    private fun findIndexWithCorrectAnswer(): Int {
        for (i in _currentShuffledList.value?.indices!!) {
            if (_currentQuestion.value?.correct_answer.equals(_currentShuffledList.value?.get(i))) {
                return i;
            }
        }
        return -1
    }

    private fun shuffledList() {
        val possibleAnswers: MutableList<String> =
            _currentQuestion.value?.incorrect_answers!!.toMutableList()

        possibleAnswers.add((_currentQuestion.value)?.correct_answer.toString())
        possibleAnswers.shuffle()

        _currentShuffledList.value = possibleAnswers
    }

    private fun gameOver() {
        _onGameOverEvent.value = true
        //Timber.d("Game is over")
    }

    override fun onCleared() {
        super.onCleared()
    }
}