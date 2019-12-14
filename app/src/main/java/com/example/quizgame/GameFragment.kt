package com.example.quizgame


import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.quizgame.Interfaces.TriviaApiListener
import com.example.quizgame.Models.QuizApiResponse
import com.example.quizgame.databinding.FragmentGameBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_game.*


/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {

    lateinit var binding: FragmentGameBinding
    lateinit var handler: Handler
    val TAG = "Game"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        binding.progressBar.isIndeterminate = true

        val args = GameFragmentArgs.fromBundle(arguments!!)

        val category = when (args.triviaCategory.toLowerCase()) {
            "arts" -> "&category=25"
            "general knowledge" -> "&category=9"
            "animals" -> "&category=27"
            "books" -> "&category=10"
            else -> ""
        }
        val url = "https://opentdb.com/api.php?amount=10${category}&type=multiple"

        loadQuestions(url, object : TriviaApiListener {
            override fun onCompleteListerner(response: QuizApiResponse) {
                binding.progressBar.isIndeterminate = false
                handler = Handler()

                val triviaGame = TriviaGame(response.results, context, args.isTimerOn)
                startTriviaGame(triviaGame)
            }

            override fun onErrorListener() {

            }
        }, context)

        return binding.root

    }

    private fun loadQuestions(
        url: String,
        listener: TriviaApiListener,
        context: Context?
    ) {
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided 9O8OL    URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val gson = Gson()

                val quizApiResponse: QuizApiResponse =
                    gson.fromJson(response, QuizApiResponse::class.java)

                listener.onCompleteListerner(quizApiResponse)
            },
            Response.ErrorListener {
                it.printStackTrace()
                listener.onErrorListener()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun startTriviaGame(triviaGame: TriviaGame) {
        updateQuestionDisplay(triviaGame)

        val timer: CountDownTimer = createTimer(triviaGame)
        if (triviaGame.isTimerInUse) {
            timer.start()
        } else {
            binding.progressBar.visibility = View.GONE
        }

        val listOfViews: List<View> = listOf(answer_one, answer_two, answer_three, answer_four)

        for (view in listOfViews) {
            view.setOnClickListener {
                val btn: RadioButton = view as RadioButton
                val btn2 = getRadioButtonReference(triviaGame.indexOfAnswerInShuffledList)

                disableRadioButtons(listOfViews)

                if (triviaGame.determineIsUserCorrect(btn.text.toString())) {
                    binding.question = "Correct"
                    btn.setBackgroundResource(R.drawable.correct_answer_background)
                } else {
                    binding.question = "Incorrect"
                    btn.setBackgroundResource(R.drawable.incorret_answer_background)
                    btn2.setBackgroundResource(R.drawable.correct_answer_background)
                }

                val nextQuestionRunnable = Runnable {
                    if (triviaGame.hasNextQuestion) {

                        triviaGame.getNextQuestion()

                        updateQuestionDisplay(triviaGame)

                        btn.isChecked = false
                        btn.setBackgroundResource(R.drawable.question_button)
                        btn2.setBackgroundResource(R.drawable.question_button)

                        enableRadioButtons(listOfViews)

                    } else {
                        if (triviaGame.isTimerInUse) {
                            timer.cancel()
                        }
                        triviaGame.gameOver()
                        navigateToGameFinished(triviaGame)
                    }
                }

                handler.postDelayed(nextQuestionRunnable, 500)
            }
        }

    }

    private fun enableRadioButtons(listOfViews: List<View>) {
        for (view2 in listOfViews) {
            val tempRadio = view2 as RadioButton
            tempRadio.isEnabled = true
        }
    }

    private fun disableRadioButtons(listOfViews: List<View>) {
        for (view in listOfViews) {
            var tempRadio = view as RadioButton
            tempRadio.isEnabled = false
        }
    }

    private fun createTimer(triviaGame: TriviaGame): CountDownTimer {
        val max: Long = 40000
        val interval: Long = 100
        var current = max

        binding.progressBar.max = current.toInt()
        return object : CountDownTimer(max, interval) {
            override fun onTick(millisUntilFinished: Long) {
                current -= interval
                binding.progressBar.progress = current.toInt()
            }

            override fun onFinish() {
                Log.d(TAG, "Countdown finished")
                binding.progressBar.progress = 0
                handler.removeCallbacksAndMessages(null)
                navigateToGameFinished(triviaGame)
            }
        }
    }

    private fun navigateToGameFinished(triviaGame: TriviaGame) {
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinished(
                triviaGame.numberCorrect,
                triviaGame.getArrayOfQuestionsReached(),
                triviaGame.getArrayOfCorrectAnswers(),
                triviaGame.getArrayOfUserAnswers()
            )

        )
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateQuestionDisplay(triviaGame: TriviaGame) {
        (activity as AppCompatActivity).supportActionBar?.title =
            "Question ${triviaGame.currentIndex + 1}/${triviaGame.listOfTriviaQuestions.size}"

        binding.triviaGame = triviaGame

    }

    fun getRadioButtonReference(index: Int): RadioButton {
        return when (index) {
            0 -> binding.answerOne
            1 -> binding.answerTwo
            2 -> binding.answerThree
            else -> binding.answerFour
        }
    }

}
