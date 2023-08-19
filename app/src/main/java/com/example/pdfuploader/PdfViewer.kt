package com.example.pdfuploader

import android.annotation.SuppressLint


import android.view.View
import android.widget.ProgressBar

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewmodel.compose.viewModel



import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


// We expect every document to start at first page (0)
const val DEFAULTPAGENO = 0


class OnlinePdfViewer: ViewModel(){

    //Data class to hold the document page number
    data class DocPage(val pageNumber: Int = DEFAULTPAGENO)

    // State for the current page
    var uiState = MutableStateFlow(DocPage())


    data class CurrentDocBytes(val currentBytes: ByteArray? = null )

    val currentBytesArray = MutableStateFlow(CurrentDocBytes())


    fun setCurrentBytes(bytes: ByteArray){
        currentBytesArray.update { it.copy(currentBytes = bytes) }
    }


}
@Composable
fun PdfViewer(selectedFileUrl: String) {

    //Instantiate OnlinePdfViewer
    val onlineViewModel: OnlinePdfViewer = viewModel()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }

    AndroidView(
        factory = {
            View.inflate(it, R.layout.main_layout, null)
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            val pdfView = it.findViewById<PDFView>(R.id.pdfviewer)
            // Here we are calling our async
            // task to load our pdf file from url.
            // we are also passing our pdf view to
            // it along with pdf view url.

            val progressBar = it.findViewById<ProgressBar>(R.id.progress)
            val reference = FirebaseStorage.getInstance().getReferenceFromUrl(selectedFileUrl)
            reference.getBytes(Constants.MAX_BYTTES_PDF)
                .addOnSuccessListener { bytes ->

                    onlineViewModel.setCurrentBytes(bytes)

                    pdfView.fromBytes(onlineViewModel.currentBytesArray.value.currentBytes)
                        .swipeHorizontal(false)
                        .defaultPage(onlineViewModel.uiState.value.pageNumber)
                        .scrollHandle(DefaultScrollHandle(it.context))
                        .onPageChange { page, _ ->
                            if (page >= 1){
                                onlineViewModel.uiState.update {
                                    it.copy(pageNumber = page)
                                }
                                }
                            }
                        .load()
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener {

                }

        }
    )
}
