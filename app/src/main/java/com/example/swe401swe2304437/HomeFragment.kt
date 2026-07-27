package com.example.swe401swe2304437

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.subjectRecyclerView)
        val dbHelper = DatabaseHelper(requireContext())


        val subjects = dbHelper.getDistinctSubjectsForHome()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        recyclerView.adapter = SubjectAdapter(subjects) { selectedSubject ->
            val intent = Intent(requireContext(), CategoryActivity::class.java).apply {
                putExtra("EXTRA_SUBJECT_NAME", selectedSubject.title) // e.g. "English", "Math"
            }
            startActivity(intent)
        }
    }
}