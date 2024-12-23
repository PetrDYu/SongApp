package ru.petr.songapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import ru.petr.songapp.root.DefaultRootComponent
import ru.petr.songapp.root.RootContent
import ru.petr.songapp.ui.theme.SongAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root =
            DefaultRootComponent(
                componentContext = defaultComponentContext()
            )

        setContent {
            SongAppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    RootContent(component = root, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}