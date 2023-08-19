package com.example.pdfuploader

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel(){
    data class OnlineNoteUrl(val url: String = "")

    private val onlineUiState = MutableStateFlow(OnlineNoteUrl())

    //backing property
    val _onlineUiState = onlineUiState.asStateFlow()


    fun setCurrentUrl(url: String){
        onlineUiState.update { it.copy(url = url)}
    }
}