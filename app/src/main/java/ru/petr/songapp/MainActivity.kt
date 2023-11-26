package ru.petr.songapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import ru.petr.songapp.root.DefaultRootComponent
import ru.petr.songapp.root.RootContent
import ru.petr.songapp.ui.theme.SongAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root =
            DefaultRootComponent(
                    componentContext = defaultComponentContext(),
                    context = this
            )

        setContent {
            SongAppTheme {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background) {
                    RootContent(component = root, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}