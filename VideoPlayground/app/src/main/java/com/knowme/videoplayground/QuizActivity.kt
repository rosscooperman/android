package com.knowme.videoplayground

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.knowme.videoplayground.model.Question
import org.jetbrains.anko.*

class QuizActivity : AppCompatActivity() {
    private var currentIndexKey = "index"

    private val questions = arrayOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true)
    )
    private var currentIndex = 0
    private var cheated = Array(questions.size, { i -> false })

    private fun updateText() {
        val question = questions[currentIndex].textResId
        (findViewById(R.id.question_text_view) as TextView).setText(question)
    }

    private fun chooseAnswer(answer: Boolean) {
        val id = if (cheated[currentIndex]) {
            R.string.judgement_toast
        } else if (questions[currentIndex].isAnswerTrue() == answer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this@QuizActivity, id, Toast.LENGTH_SHORT).show()
    }

    private fun next()  {
       currentIndex = (currentIndex + 1) % questions.size
       updateText()
    }

    private fun prev() {
        currentIndex -= 1
        if (currentIndex < 0) currentIndex = questions.size - 1
        updateText()
    }

    private fun cheat() {
        startActivityForResult<CheatActivity>(0, "answer" to questions[currentIndex].isAnswerTrue())
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        currentIndex = state?.getInt(currentIndexKey) ?: 0
        setContentView(R.layout.activity_quiz)

        (findViewById(R.id.true_button) as Button).setOnClickListener { chooseAnswer(true) }
        (findViewById(R.id.false_button) as Button).setOnClickListener { chooseAnswer(false) }

        (findViewById(R.id.question_text_view) as TextView).setOnClickListener { next() }
        (findViewById(R.id.next_button) as ImageButton).setOnClickListener { next() }
        (findViewById(R.id.prev_button) as ImageButton).setOnClickListener { prev() }

        (findViewById(R.id.cheat_button) as Button).setOnClickListener { cheat() }

        updateText()
     }

    override fun onSaveInstanceState(state: Bundle?) {
        super.onSaveInstanceState(state)
        state?.putInt(currentIndexKey, currentIndex)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, responseCode: Int, data: Intent?) {
        println(requestCode)
        println(responseCode)
        println(data)

        if (requestCode == 0 && responseCode == Activity.RESULT_OK && !cheated[currentIndex]) {
            cheated[currentIndex] = data?.getBooleanExtra("cheated", false) ?: false
        }
    }
}
