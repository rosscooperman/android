package com.knowme.videoplayground

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor

class CheatActivity : AppCompatActivity() {

    var answerView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        find<TextView>(R.id.android_version_number).text = "API level ${Build.VERSION.SDK_INT}"

        answerView = findViewById(R.id.answer_text_view) as TextView
        if (intent.getBooleanExtra("answer", true)) {
            answerView?.setText(R.string.true_button)
        } else {
            answerView?.setText(R.string.false_button)
        }

        (findViewById(R.id.show_answer_button) as Button).setOnClickListener { showAnswer() }
    }

    private fun showAnswer() {
        setResult(Activity.RESULT_OK, intentFor<QuizActivity>("cheated" to true))

        val imAnswerView = answerView
        if (imAnswerView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx = imAnswerView.width / 2
                val cy = imAnswerView.height / 2
                val radius: Float = imAnswerView.width.toFloat()

                val anim = ViewAnimationUtils.createCircularReveal(imAnswerView, cx, cy, radius, 0f)
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        imAnswerView.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        (findViewById(R.id.show_answer_button) as Button).visibility = View.INVISIBLE
                    }
                })
                anim.start()
            }
        } else {
            answerView?.visibility = View.VISIBLE
            (findViewById(R.id.show_answer_button) as Button).visibility = View.INVISIBLE
        }
    }
}
