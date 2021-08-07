package com.jxd.jetpacktetris.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * @author : Alfred
 * 游戏结束
 * 重置游戏或退出
 */
@Composable
fun GameOverScreen(gameViewModel: GameViewModel = viewModel(), onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0x88888888))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Game Over",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Red
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "重新开始", fontSize = 32.sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { gameViewModel.gameRestart() }
                        .border(BorderStroke(3.dp, Color.Red))
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "退出游戏", fontSize = 32.sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { onBack() }
                        .border(BorderStroke(3.dp, Color.Red))
                )
            }
        }
    }
}