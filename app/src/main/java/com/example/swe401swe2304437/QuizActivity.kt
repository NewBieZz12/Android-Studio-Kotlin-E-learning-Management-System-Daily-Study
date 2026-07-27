package com.example.swe401swe2304437

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private var correctAnswerLetter = "b"
    private var isUserCorrect = false
    private lateinit var dbHelper: DatabaseHelper
    private var subjectName = "English"
    private var exerciseTitle = "Exercise 1: Make & Do"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        dbHelper = DatabaseHelper(this)

        val backArrow: ImageView = findViewById(R.id.quizBackButton)
        val toolbarTitle: TextView = findViewById(R.id.quizToolbarTitle)

        val txtQuestion: TextView = findViewById(R.id.quizQuestionText)
        val optionsGroup: RadioGroup = findViewById(R.id.optionsRadioGroup)
        val rbA: RadioButton = findViewById(R.id.optionA)
        val rbB: RadioButton = findViewById(R.id.optionB)
        val rbC: RadioButton = findViewById(R.id.optionC)
        val txtStatusResult: TextView = findViewById(R.id.quizStatusResult)

        val btnSubmit: Button = findViewById(R.id.btnSubmitQuiz)
        val btnFinish: Button = findViewById(R.id.btnFinishQuiz)


        subjectName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: "English"
        exerciseTitle = intent.getStringExtra("EXTRA_EXERCISE_TITLE") ?: when {
            subjectName.equals("Math", ignoreCase = true) -> "Exercise 1: Addition"
            subjectName.equals("General Knowledge", ignoreCase = true) -> "Exercise 1: Geography"
            else -> "Exercise 1: Make & Do"
        }

        toolbarTitle.text = "$subjectName Exercise"

        val rawQuizContent = dbHelper.getExerciseContent(subjectName, exerciseTitle)

        if (!rawQuizContent.isNullOrEmpty() && rawQuizContent.contains("|")) {
            val parts = rawQuizContent.split("|")
            if (parts.size >= 5) {
                txtQuestion.text = parts[0]
                rbA.text = parts[1]
                rbB.text = parts[2]
                rbC.text = parts[3]
                correctAnswerLetter = parts[4].trim()
            }
        } else {
            Toast.makeText(this, "Data missing for: $exerciseTitle", Toast.LENGTH_LONG).show()
            txtQuestion.text = "No quiz data found for $exerciseTitle"
            btnSubmit.isEnabled = false
        }

        backArrow.setOnClickListener { finish() }

        btnSubmit.setOnClickListener {
            val checkedId = optionsGroup.checkedRadioButtonId

            if (checkedId == -1) {
                Toast.makeText(this, "Please pick an answer choice first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            rbA.isEnabled = false
            rbB.isEnabled = false
            rbC.isEnabled = false
            btnSubmit.isEnabled = false

            isUserCorrect = (rbA.isChecked && correctAnswerLetter == "a") ||
                    (rbB.isChecked && correctAnswerLetter == "b") ||
                    (rbC.isChecked && correctAnswerLetter == "c")

            if (isUserCorrect) {
                txtStatusResult.text = "⭐ Correct! Excellent job!"
                txtStatusResult.setTextColor(Color.parseColor("#4CAF50"))
                
                dbHelper.markExerciseAsCleared(subjectName, exerciseTitle)

            } else {
                txtStatusResult.text = "✗ Wrong! The correct answer is ($correctAnswerLetter)"
                txtStatusResult.setTextColor(Color.RED)
            }

            txtStatusResult.visibility = View.VISIBLE
        }

        btnFinish.setOnClickListener {
            finish()
        }
    }
}