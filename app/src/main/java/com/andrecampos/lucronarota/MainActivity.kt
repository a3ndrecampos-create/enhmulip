package com.andrecampos.lucronarota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.andrecampos.lucronarota.data.AppDatabase
import com.andrecampos.lucronarota.data.CorridaRepository
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.navigation.LucroNaRotaApp
import com.andrecampos.lucronarota.ui.theme.LucroNaRotaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.obterInstancia(applicationContext)
        val repository = CorridaRepository(database.corridaDao(), database.configDao())
        val factory = ViewModelFactory(repository)

        setContent {
            LucroNaRotaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LucroNaRotaApp(factory)
                }
            }
        }
    }
}
