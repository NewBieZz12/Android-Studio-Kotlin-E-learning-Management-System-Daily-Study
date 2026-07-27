package com.example.swe401swe2304437

object GameMemory {

    var isMathExerciseCleared: Boolean = false
    var isEnglishExerciseCleared: Boolean = false
    var isGeneralKnowledgeCleared: Boolean = false


    const val TOTAL_COURSES = 3


    fun getTotalStarsCount(): Int {
        var count = 0
        if (isMathExerciseCleared) count++
        if (isEnglishExerciseCleared) count++
        if (isGeneralKnowledgeCleared) count++
        return count
    }


    fun getMathProgressPercent(): Int = if (isMathExerciseCleared) 100 else 0
    fun getEnglishProgressPercent(): Int = if (isEnglishExerciseCleared) 100 else 0
    fun getGeneralKnowledgeProgressPercent(): Int = if (isGeneralKnowledgeCleared) 100 else 0
}