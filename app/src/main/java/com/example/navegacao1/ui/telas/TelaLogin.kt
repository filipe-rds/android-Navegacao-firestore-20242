package com.example.navegacao1.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.navegacao1.model.dados.Usuario
import com.example.navegacao1.model.dados.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaLogin(
    modifier: Modifier = Modifier,
    onSigninClick: (Usuario) -> Unit,
    onCadastroClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuarioDAO = UsuarioRepository.usuarioDAO

    var login by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Bem-vindo!",
            style = MaterialTheme.typography.headlineLarge
        )

        // Campo de Login
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text(text = "Usuário") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Usuário") },
            isError = mensagemErro != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de Senha
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text(text = "Senha") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = mensagemErro != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Mensagem de erro (se houver)
        if (mensagemErro != null) {
            Text(
                text = mensagemErro ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Botão de Login
        Button(
            onClick = {
                isLoading = true
                scope.launch(Dispatchers.IO) {
                    usuarioDAO.buscarPorNome(login) { usuario ->
                        isLoading = false
                        if (usuario != null && usuario.senha == senha) {
                            onSigninClick(usuario)
                        } else {
                            mensagemErro = "Login ou senha inválidos!"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Entrar")
            }
        }

        // Botão de Cadastro
        OutlinedButton(
            onClick = onCadastroClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }

        mensagemErro?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                mensagemErro = null
            }
        }
    }
}
