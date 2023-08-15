package com.example.pdfuploader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pdfuploader.ui.theme.PdfUploaderTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.net.URL

class MainActivity : ComponentActivity() {

    private var imageReference = Firebase.storage.reference
    private var currentFile: Uri? = null

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == RESULT_OK) {
            result?.data?.data?.let {
                currentFile = it
                Toast.makeText(
                    this,
                    "Image Selected Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else {
            Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start an intent to select an image
        fun startIntent(){
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                imageLauncher.launch(it)
            }
        }

        // Function to upload a file
        fun uploadImageToStorage(fileName: String){
            try {
                currentFile?.let {
                    imageReference.child("images/$fileName")
                        .putFile(it).addOnSuccessListener {
                            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Toast.makeText(this, "upload Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }catch (e: Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            PdfUploaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppUi(
                        startIntent = { startIntent() },
                        uploadImage = { uploadImageToStorage("2")}
                    )
                }
            }
        }
    }
}

@Composable
fun AppUi(
    startIntent: () -> Unit,
    uploadImage: () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){

        Text("PFD UPLOADER",
            style = TextStyle(
                fontSize = 18.sp
            )
            )

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){

            Button(
                // Open the image gallery
                onClick = startIntent
            ){
                Text(text = "Select File")
            }

            Button(
                onClick = uploadImage
            ){
                Text(text = "Upload File")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    PdfUploaderTheme {
        AppUi(startIntent = {}, uploadImage = {})
    }
}