package com.jxd.jetpacktetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.jxd.jetpacktetris.game.GameOverScreen
import com.jxd.jetpacktetris.game.GameScreen
import com.jxd.jetpacktetris.game.GameState
import com.jxd.jetpacktetris.game.GameViewModel
import com.jxd.jetpacktetris.ui.theme.JetpackTetrisTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel by viewModels<GameViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackTetrisTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val gState by gameViewModel.gameState.observeAsState()
                    GameScreen(gameViewModel)
                    when (gState) {
                        GameState.Over -> GameOverScreen(gameViewModel) { onBackPressed() }
                        GameState.Pause -> gameViewModel.gamePause()
                        GameState.Running -> gameViewModel.gameRunning()
                        null -> {
                        }
                    }
                }
            }
        }
        gameViewModel.gameStart()
    }
}