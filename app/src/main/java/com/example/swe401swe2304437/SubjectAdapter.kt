package com.example.swe401swe2304437

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectAdapter(
    private val subjectList: List<Subject>,
    private val onSubjectClicked: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currentSubject = subjectList[position]
        holder.titleText.text = currentSubject.title
        holder.illustrationImage.setImageResource(currentSubject.imageRes)


        holder.itemView.setOnClickListener {
            onSubjectClicked(currentSubject)
        }
    }

    override fun getItemCount(): Int = subjectList.size

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.subjectTitle)
        val illustrationImage: ImageView = itemView.findViewById(R.id.subjectIllustration)
    }
}