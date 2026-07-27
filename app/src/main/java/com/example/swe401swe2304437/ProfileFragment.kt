package com.example.swe401swe2304437

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var txtProfileName: TextView
    private lateinit var txtStarCount: TextView
    private lateinit var txtCourseCount: TextView
    private lateinit var txtEnglishPercent: TextView
    private lateinit var txtMathPercent: TextView
    private lateinit var txtGeneralKnowledgePercent: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)

        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtStarCount = view.findViewById(R.id.txtProfileStarCount)
        txtCourseCount = view.findViewById(R.id.txtProfileCourseCount)
        txtEnglishPercent = view.findViewById(R.id.txtEnglishProgressPercent)
        txtMathPercent = view.findViewById(R.id.txtMathProgressPercent)
        txtGeneralKnowledgePercent = view.findViewById(R.id.txtGeneralKnowledgeProgressPercent)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.profileToolbar)
        toolbar.inflateMenu(R.menu.profile_menu)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_help -> {
                    Toast.makeText(context, "Tip: Watch lessons to unlock stars!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_logout -> {
                    val intent = Intent(activity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    activity?.finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfileStatistics()
    }

    private fun updateProfileStatistics() {
        val context = context ?: return
        val dbHelper = DatabaseHelper(context)

        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserName = sharedPreferences.getString("LOGGED_IN_USER", "Student")
        txtProfileName.text = currentUserName

        txtStarCount.text = dbHelper.getTotalStarsCount().toString()
        txtCourseCount.text = "3"

        val englishProgress = dbHelper.getSubjectProgressPercentage("English")
        val mathProgress = dbHelper.getSubjectProgressPercentage("Math")
        val gkProgress = dbHelper.getSubjectProgressPercentage("General Knowledge")

        txtEnglishPercent.text = "$englishProgress%"
        txtMathPercent.text = "$mathProgress%"
        txtGeneralKnowledgePercent.text = "$gkProgress%"
    }
}