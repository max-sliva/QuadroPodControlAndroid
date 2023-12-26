package com.example.quadropodcontrol

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quadropodcontrol.ui.theme.QuadroPodControlTheme

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuadroPodControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Выберите режим:")
                }
            }
        }
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(text: String="Выберите режим:", modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$text!",
            modifier = modifier
        )
        Button(
            onClick = {
                val newAct = Intent(mContext, MainActivity::class.java) //описан ниже
//                newAct.putExtra("angle", degsForLegs[number])
//                newAct.putExtra("legNumber", number)
                mContext.startActivity(newAct)
            },
            modifier = Modifier
//                .padding(8.dp)
//                .border(2.dp, Color.Blue,
//                    shape = MaterialTheme.shapes.small)
        ) {
            Text(text = "Управление любой конечностью")
        }
        Button(
            onClick = { },
            modifier = Modifier
//                .padding(8.dp)
//                .border(2.dp, Color.Blue,
//                    shape = MaterialTheme.shapes.small)
        ) {
            Text(text = "Управление перемещением")
        }
    }}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuadroPodControlTheme {
        Greeting("Android")
    }
}