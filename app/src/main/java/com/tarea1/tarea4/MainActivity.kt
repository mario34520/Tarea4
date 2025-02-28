package com.tarea1.tarea4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random


data class GameState(
    val diceValue: Int = 1,
    val totalThrows: Int = 0,
    val remainingTurns: Int = 3
)

// ViewModel con MutableStateFlow
class DiceViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    fun rollDice() {
        _gameState.value.let { state ->
            if (state.remainingTurns > 0) {
                _gameState.value = state.copy(
                    diceValue = Random.nextInt(1, 7),
                    totalThrows = state.totalThrows + 1,
                    remainingTurns = state.remainingTurns - 1
                )
            }
        }
    }

    fun resetGame() {
        _gameState.value = GameState()
    }
}

// Composable principal
@Composable
fun DiceGameScreen(viewModel: DiceViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dado: ${state.diceValue}", style = MaterialTheme.typography.titleLarge)
        Text("Tiradas: ${state.totalThrows}/3")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.rollDice()
                if (state.remainingTurns == 1) showDialog = true
            },
            enabled = state.remainingTurns > 0
        ) {
            Text("Lanzar Dado")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Fin del juego") },
                text = { Text("Total de lanzamientos: ${state.totalThrows}") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.resetGame()
                        showDialog = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// Activity principal
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DiceGameScreen()
            }
        }
    }
}
