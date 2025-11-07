package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lab_week_09.ui.theme.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LAB_WEEK_09Theme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    App(
                        navController = navController
                    )
                }
            }
        }
    }
}

// DATA CLASS
data class Student(
    var name: String
)

// ROOT NAVIGATION
@Composable
fun App(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            Home { jsonString ->
                navController.navigate("resultContent/?listData=$jsonString")
            }
        }

        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(
                navArgument("listData") {
                    type = NavType.StringType
                }
            )
        ) {
            ResultContent(it.arguments?.getString("listData").orEmpty())
        }
    }
}

// HOME
@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {

    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    var inputField by remember { mutableStateOf(Student("")) }

    // Create Moshi
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)

    HomeContent(
        listData = listData,
        inputField = inputField,
        onInputValueChange = {
            inputField = inputField.copy(name = it)
        },
        onButtonClick = {
            if (inputField.name.isNotBlank()) {
                listData.add(inputField)
                inputField = Student("")
            }
        },
        navigateFromHomeToResult = {
            if (listData.isNotEmpty()) {
                val json = adapter.toJson(listData)
                navigateFromHomeToResult(json)
            }
        }
    )
}

// HOME CONTENT
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {

    LazyColumn {

        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = {
                        onInputValueChange(it)
                    }
                )

                Row {

                    // ✅ Prevent add when empty
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click)
                    ) {
                        if (inputField.name.isNotBlank()) onButtonClick()
                    }

                    // ✅ Prevent navigate when list empty
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_navigate)
                    ) {
                        if (listData.isNotEmpty()) navigateFromHomeToResult()
                    }
                }
            }
        }

        // List display
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// RESULT CONTENT — BONUS: JSON list → LazyColumn
@Composable
fun ResultContent(listData: String) {

    // Parse JSON back to list<Student>
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)

    val studentList = adapter.fromJson(listData) ?: emptyList()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText("Result Screen")

        LazyColumn {
            items(studentList) { student ->
                OnBackgroundItemText(student.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        val navController = rememberNavController()
        App(navController)
    }
}
