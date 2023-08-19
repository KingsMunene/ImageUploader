package com.example.pdfuploader.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.pdfuploader.models.OnlineNotes
import com.example.pdfuploader.sealed.DataState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OnlineNotesViewModel : ViewModel() {
    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)


    init {
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        val tempList = mutableListOf<OnlineNotes>()

        response.value = DataState.Loading

        FirebaseDatabase.getInstance().getReference("Units")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Clear the list before adding data into it
                    tempList.clear()
                    for (course in snapshot.children){
                        // get data
                        val courseItem = course.getValue(OnlineNotes::class.java)

                        if (courseItem !=null)
                            tempList.add(courseItem)
                    }

                    response.value = DataState.Success(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                   response.value = DataState.Failure(error.message)
                }

            })
    }
}