package com.example.navegacao1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navegacao1.model.dados.Usuario
import com.example.navegacao1.ui.telas.TelaCadastro
import com.example.navegacao1.ui.telas.TelaLogin
import com.example.navegacao1.ui.telas.TelaPrincipal
import com.example.navegacao1.ui.theme.Navegacao1Theme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            Navegacao1Theme {
                val navController = rememberNavController()
                var usuarioLogado by remember { mutableStateOf<Usuario?>(null) }

                Scaffold(
                    topBar = {
                        val navBackStackEntry = navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry.value?.destination?.route ?: "login"
                        val topBarTitle = when (currentRoute) {
                            "login" -> "Login"
                            "cadastro" -> "Cadastro"
                            "principal" -> "Principal"
                            else -> "App"
                        }
                        TopAppBar(
                            title = { Text(topBarTitle) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            TelaLogin(
                                modifier = Modifier.padding(innerPadding),
                                onSigninClick = { usuario ->
                                    usuarioLogado = usuario
                                    navController.navigate("principal")
                                },
                                onCadastroClick = { navController.navigate("cadastro") }
                            )
                        }
                        composable("cadastro") {
                            TelaCadastro(
                                modifier = Modifier.padding(innerPadding),
                                onCadastroSuccess = { navController.navigate("login") }
                            )
                        }
                        composable("principal") {
                            usuarioLogado?.let { usuario ->
                                TelaPrincipal(
                                    modifier = Modifier.padding(innerPadding),
                                    onLogoffClick = {
                                        // Aqui você pode também limpar o estado do usuário logado se necessário
                                        usuarioLogado = null
                                        navController.navigate("login")
                                    },
                                    usuarioLogado = usuario
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
