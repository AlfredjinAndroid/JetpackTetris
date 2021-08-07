package com.jxd.jetpacktetris.game

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jxd.jetpacktetris.R

const val BORDER_WIDTH = 4

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                // 游戏区
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(2f)
                        .padding(top = BORDER_WIDTH.dp, start = BORDER_WIDTH.dp)
                        .border(border = BorderStroke(BORDER_WIDTH.dp, Color.Gray))
                ) {
                    GameArea(gameViewModel = gameViewModel)
                }
                //功能区
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(
                            top = BORDER_WIDTH.dp,
                            end = BORDER_WIDTH.dp,
                            start = BORDER_WIDTH.dp
                        )
                        .border(border = BorderStroke(BORDER_WIDTH.dp, Color.Gray))
                ) {
                    GameFunctional(gameViewModel = gameViewModel)
                }
            }
            //控制区
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .fillMaxWidth()
                    .border(border = BorderStroke(BORDER_WIDTH.dp, Color.Gray))
            ) {
                GameController(gameViewModel = gameViewModel)
            }
        }
    }
}

@Composable
fun GameArea(gameViewModel: GameViewModel = viewModel()) {
    val cellList by gameViewModel.cellList.observeAsState(initial = emptyArray())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { pSize ->
                gameViewModel.resetCellList(pSize)
            }, verticalArrangement = Arrangement.Center
    ) {
        cellList.forEach { rows ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rows.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(BorderStroke(0.2.dp, Color.DarkGray))
                    ) {
                        if (cell > 0) {
                            ColorationCell()
                        } else {
                            NormalCell()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameFunctional(gameViewModel: GameViewModel = viewModel()) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // 下一个状态
        FeatureArea(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            gameViewModel = gameViewModel
        )
        // 游戏分数区域
        GameScore(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            gameViewModel = gameViewModel
        )

        // 等级区域
        GameLevel(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            gameViewModel = gameViewModel
        )
    }
}

/**
 * @author : Alfred
 * 游戏等级
 */
@Composable
fun GameLevel(modifier: Modifier = Modifier, gameViewModel: GameViewModel = viewModel()) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "游戏级别:",
            color = Color.LightGray,
            fontWeight = FontWeight.W700,
            fontSize = 22.sp
        )
        Text(text = "1", color = Color.LightGray, fontWeight = FontWeight.W900, fontSize = 24.sp)
    }
}

/**
 * @author : Alfred
 * 分数
 */
@Composable
fun GameScore(modifier: Modifier = Modifier, gameViewModel: GameViewModel = viewModel()) {
    val score by gameViewModel.score.observeAsState(initial = 0)
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "游戏得分:",
            color = Color.LightGray,
            fontWeight = FontWeight.W700,
            fontSize = 22.sp
        )
        Text(
            text = "$score",
            color = Color.LightGray,
            fontWeight = FontWeight.W900,
            fontSize = 24.sp
        )
    }
}

/**
 * @author : Alfred
 * 下一个样式
 */
@Composable
fun FeatureArea(modifier: Modifier = Modifier, gameViewModel: GameViewModel = viewModel()) {
    val nextVariant by gameViewModel.featureCell.observeAsState()
    val nextCell = when (nextVariant) {
        CellVariant.I -> arrayOf(
            arrayOf(0, 1, 0, 0),
            arrayOf(0, 1, 0, 0),
            arrayOf(0, 1, 0, 0),
            arrayOf(0, 1, 0, 0)
        )
        CellVariant.O -> arrayOf(
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 1, 1, 0),
            arrayOf(0, 1, 1, 0),
            arrayOf(0, 0, 0, 0)
        )
        CellVariant.T -> arrayOf(
            arrayOf(1, 1, 1, 0),
            arrayOf(0, 1, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
        CellVariant.L -> arrayOf(
            arrayOf(0, 1, 0, 0),
            arrayOf(0, 1, 0, 0),
            arrayOf(0, 1, 1, 0),
            arrayOf(0, 0, 0, 0)
        )
        CellVariant.J -> arrayOf(
            arrayOf(0, 0, 1, 0),
            arrayOf(0, 0, 1, 0),
            arrayOf(0, 1, 1, 0),
            arrayOf(0, 0, 0, 0)
        )
        CellVariant.S -> arrayOf(
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 1, 1),
            arrayOf(0, 1, 1, 0),
            arrayOf(0, 0, 0, 0)
        )
        CellVariant.Z -> arrayOf(
            arrayOf(0, 0, 0, 0),
            arrayOf(1, 1, 0, 0),
            arrayOf(0, 1, 1, 0),
            arrayOf(0, 0, 0, 0)
        )
        else -> arrayOf(
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
    }
    Box(
        modifier = modifier
            .border(BorderStroke(2.dp, Color.Gray))
            .aspectRatio(1f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            nextCell.forEach { rows ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    rows.forEach { c ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            if (c > 0) {
                                ColorationCell()
                            } else {
                                NormalCell()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * @author : Alfred
 * 默认格子
 */
@Composable
fun NormalCell() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(0.2.dp, Color.DarkGray))
    )
}

/**
 * @author : Alfred
 * 着色格子
 */
@Composable
fun ColorationCell() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .border(1.dp, Color.Green, RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(.2f)
                .background(Color.Green)
                .align(
                    Alignment.Center
                )
        )
    }
}

/**
 * @author : Alfred
 * 控制手柄
 */
@Composable
fun GameController(gameViewModel: GameViewModel = viewModel()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        GameControllerButton(
            modifier = Modifier
                .weight(1f),
            ArrowButton.UP
        ) { gameViewModel.up() }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            GameControllerButton(
                modifier = Modifier
                    .fillMaxHeight(), ArrowButton.LEFT
            ) { gameViewModel.left() }
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
            GameControllerButton(
                modifier = Modifier
                    .fillMaxHeight(),
                ArrowButton.RIGHT
            ) { gameViewModel.right() }
        }
        GameControllerButton(
            modifier = Modifier
                .weight(1f),
            ArrowButton.DOWN
        ) {
            gameViewModel.down()
        }
    }
}

/**
 * @author : Alfred
 * 控制按钮
 */
@Composable
fun GameControllerButton(
    modifier: Modifier = Modifier,
    arrowButton: ArrowButton,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(width = BORDER_WIDTH.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        val arrowIcon = when (arrowButton) {
            ArrowButton.LEFT -> R.drawable.ic_baseline_keyboard_arrow_left_24
            ArrowButton.RIGHT -> R.drawable.ic_baseline_keyboard_arrow_right_24
            ArrowButton.UP -> R.drawable.ic_baseline_keyboard_arrow_up_24
            ArrowButton.DOWN -> R.drawable.ic_baseline_keyboard_arrow_down_24
        }
        Image(
            painter = painterResource(id = arrowIcon),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(.8f)
        )
    }
}

// 游戏手柄方向
enum class ArrowButton {
    LEFT, RIGHT, UP, DOWN
}


@Preview(widthDp = 1080, heightDp = 1920)
@Composable
fun GamePreview() {
    GameScreen()
}