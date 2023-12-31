package com.example.pdfuploader

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pdfuploader.models.OnlineNotes
import com.example.pdfuploader.sealed.DataState
import com.example.pdfuploader.ui.theme.PdfUploaderTheme
import com.example.pdfuploader.viewmodel.OnlineNotesViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : ComponentActivity() {

    private var imageReference = Firebase.storage.reference
    private var dataBaseReference = Firebase.database.reference

    private var currentFile: Uri? = null

    private lateinit var progressDialog: ProgressDialog


    // Activity result launcher
    /**
    * This variable is used to get a file from the instance opened
     * */
    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == RESULT_OK) {
            result?.data?.data?.let {
                currentFile = it
                Toast.makeText(
                    this,
                    "File Selected Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else {
            Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        }
    }

    val viewModel: OnlineNotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)



        // Start an intent to select an image
        fun startIntent(){
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "application/pdf"
                imageLauncher.launch(it)
            }
        }


        fun uploadPdfIntoDb(uploadedPdfUrl: Uri, fileName: String, courseSelection: String) {
            progressDialog.setMessage("Uploading PDF info...")
            progressDialog.show()

            val timeStamp = System.currentTimeMillis()
            val pdfData = HashMap<String, Any>()

            pdfData["name"] = fileName
            pdfData["unit"] = "$uploadedPdfUrl.pdf"
            pdfData["category"] = courseSelection

            dataBaseReference.child("Units").
            child(timeStamp.toString())
                .setValue(pdfData).addOnSuccessListener{
                    progressDialog.dismiss()
                    // Saved successfully
                    Toast.makeText(this, "Saved In database", Toast.LENGTH_SHORT).show()
                    currentFile = null
                    pdfData.clear()
                }.addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(this, "failed to Saved In database", Toast.LENGTH_SHORT).show()
                }

        }

        // Function to upload a file
        fun uploadImageToStorage(fileName: String, courseSelected: String) {
            progressDialog.setMessage("Uploading PDF...")
            progressDialog.show()
            try {
                currentFile?.let {
                    imageReference.child("images/$fileName")
                        .putFile(it).addOnSuccessListener { taskSnapshot  ->

                            // Get url of uploaded pdf
                            val urlTask = taskSnapshot.storage.downloadUrl
                            while (!urlTask.isSuccessful);
                            val uploadedPdfUrl = urlTask.result

                            // Call the function to save the pdf information to the database
                            uploadPdfIntoDb(uploadedPdfUrl, fileName, courseSelected)

                            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener {
                            progressDialog.dismiss()
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
                    var fileName by remember {mutableStateOf("")}

                    var isExpanded by remember {
                        mutableStateOf( false)
                    }

                    var courseName by remember {
                        mutableStateOf("")
                    }


                    fun validateData(){
                       if (courseName.isEmpty()){
                           Toast.makeText(this, "Enter course name", Toast.LENGTH_SHORT).show()
                       }else if(fileName.isEmpty()){
                           Toast.makeText(this, "Enter file name", Toast.LENGTH_SHORT).show()
                       }else{
                           uploadImageToStorage(fileName, courseName)
                       }
                    }

                    val navController: NavHostController = rememberNavController()

                    var selectedFileUrl: String? = null

                    NavHost(
                        navController = navController,
                        startDestination = "Start"
                    ){


                        composable(route = "Start"){
                            AppUi(
                                startIntent = { startIntent() },

                                uploadImage = {
                                    validateData()
                                },
                                onNewFileName = {fileName = it},
                                fileName = fileName,
                                isExpanded = isExpanded,
                                courseName =courseName,
                                onExpanded = {isExpanded = it},
                                onItemClick = {
                                        selectedName, state -> courseName = selectedName
                                    isExpanded = state
                                },
                                onDismissRequest = {currentState -> isExpanded = !currentState},
                                fetchData = {
                                    navController.navigate("fetchData")
                                }
                            )
                        }

                        composable("fetchData"){
                            setData(viewModel = viewModel,
                                openUnit = {url -> selectedFileUrl = url
                                    navController.navigate("pdfViewer")}
                                )
                        }

                        composable("pdfViewer"){
                            PdfViewer(selectedFileUrl = selectedFileUrl!!)
                        }
                    }


                }
            }
        }

    
    }
}

@Composable
fun CourseItem(
    course: OnlineNotes,
    openUnit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(
                onClick = openUnit
            )
    ){
        Box(modifier = Modifier
            .fillMaxSize()
        ){
            Text(
                text = course.name!!,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
fun ShowCourseList(
    data: MutableList<OnlineNotes>,
    openUnit: (String) -> Unit
) {
    LazyColumn{
        items(data){ course ->
            CourseItem(course, openUnit = {openUnit(course.unit!!)})

        }
    }
}

@Composable
fun setData(
    viewModel: OnlineNotesViewModel,
    openUnit: (String) -> Unit
){
    when (val result = viewModel.response.value){
        DataState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }

        }
        is DataState.Success ->  {
            ShowCourseList(
                result.data,
                openUnit = {
                        unitUrl -> openUnit(unitUrl)
                }
            )

        }
        is DataState.Failure -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = result.message,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }

        }
        else -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Error Fetching data",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUi(
    startIntent: () -> Unit,
    uploadImage: () -> Unit,
    onNewFileName: (String) -> Unit,
    fileName: String,
    isExpanded: Boolean,
    courseName: String,
    onExpanded: (Boolean) -> Unit,
    onItemClick: (String, Boolean) -> Unit,
    onDismissRequest: (Boolean) -> Unit,
    fetchData: () -> Unit
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

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = onExpanded
        ){
            TextField(
                value = courseName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onDismissRequest(isExpanded) }
            ) {
                DataManager.courses.forEach { course ->
                    DropdownMenuItem(
                        text = {
                            Text(course.courseName)
                        },
                        onClick = {

                            onItemClick(course.courseCode, false)

                        }
                    )
                }
            }
        }

        TextField(
            value = fileName,
            onValueChange = onNewFileName
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

            Button(
                onClick = fetchData
            ){
                Text(text = "Get Data")
            }




        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun AppPreview() {
//    PdfUploaderTheme {
//        AppUi(startIntent = {}, uploadImage = {}, onNewFileName = {}, "Download"
//        )
//    }
//}