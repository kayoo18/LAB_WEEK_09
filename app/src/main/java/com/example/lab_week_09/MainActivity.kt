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
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

//==================================================
// PART 2 — DATA MODEL
//==================================================
data class Student(
    var name: String
)

//==================================================
// UPDATED HOME() — NOW ACTS AS THE PARENT COMPOSABLE
//==================================================
@Composable
fun Home() {

    // State list untuk menampung data Student
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    // State object untuk input TextField
    var inputField = remember { mutableStateOf(Student("")) }

    // Panggil HomeContent sambil passing state
    HomeContent(
        listData = listData,
        inputField = inputField.value,

        // Update nilai input text
        onInputValueChange = { input ->
            inputField.value = inputField.value.copy(name = input)
        },

        // Aksi tombol Add
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        }
    )
}

//==================================================
// CHILD COMPOSABLE — MENAMPILKAN UI
//==================================================
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) {

    LazyColumn {

        // Bagian input
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Title pakai UI Element
                OnBackgroundTitleText(
                    text = stringResource(id = R.string.enter_item)
                )

                // TextField input
                TextField(
                    value = inputField.name,
                    onValueChange = { onInputValueChange(it) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )

                // Button pakai UI Element
                PrimaryTextButton(
                    text = stringResource(id = R.string.button_click)
                ) {
                    onButtonClick()
                }
            }
        }

        // Daftar item
        items(listData) { student ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Item text pakai UI Element
                OnBackgroundItemText(
                    text = student.name
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    Home()
}
