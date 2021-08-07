package com.jxd.jetpacktetris.game

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class GameViewModel : ViewModel() {

    private val _cellList = MutableLiveData<Array<Array<Int>>>()
    val cellList: LiveData<Array<Array<Int>>> = _cellList

    private val _gameSize = MutableLiveData<IntSize>()
    val gameSize: LiveData<IntSize> = _gameSize

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    fun resetCellList(gSize: IntSize) {
        if (gameSize.value == gSize) return
        _gameSize.postValue(gSize)
        val h = gSize.height
        val w = gSize.width
        val cWidth = w / 10
        val columns = h / cWidth
        val cells = Array(columns, init = { Array(10, init = { 0 }) })
        _cellList.postValue(cells)
        nextCell()
    }

    private val _featureCell = MutableLiveData<CellVariant>()
    val featureCell: LiveData<CellVariant> = _featureCell

    private fun nextCell() {
        val next = CellVariant.values().random() //CellVariant.I//
        _featureCell.postValue(next)
    }

    private var _currentCell: CellVariant? = null

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState
    fun gameStart() {
        viewModelScope.launch(Dispatchers.IO) {
            _gameState.postValue(GameState.Running)
        }
    }

    fun gameRestart() {
        viewModelScope.launch(Dispatchers.IO) {
            clearCells()
            _score.postValue(0)
            _gameState.postValue(GameState.Running)
        }
    }

    private fun clearCells() {
        val cList = _cellList.value?.clone() ?: return

        for (i in cList.indices) {
            for (j in cList[i].indices) {
                cList[i][j] = 0
            }
        }
        _cellList.postValue(cList)
    }

    private fun renew() {
        val cVariant = _featureCell.value ?: CellVariant.values().random()
        _currentCell = cVariant
        nextCell()
        resetCurrent()
    }

    private fun resetCurrent() {
        for (i in currentIndex.indices) {
            currentIndex[i] = 0 to 0
        }
    }

    val currentIndex = Array(4, init = { 0 to 0 })

    fun gamePause() {
        _gameState.value = GameState.Pause
    }

    fun gameRunning() {
        viewModelScope.launch(Dispatchers.IO) {
            while (gameState.value is GameState.Running) {
                delay(1000)
                if (moveCollided(y = 1)) {
                    mergeCell()
                    calcScore()
                    renew()
                    calcCells()
                    calcCurrent()
                } else {
                    down()
                }
            }
        }
    }

    private fun calcScore() {
        if (isReset()) return
        val cList = _cellList.value?.clone() ?: return
        val deathLine = cList.count { c -> c.none { n -> n <= 0 } }
        var index = cList.size - 1
        while (index > 0) {
            val column = cList[index]
            if (column.any { a -> a > 0 } && column.any { a -> a <= 0 }) {
                index -= 1
                continue
            }
            var topIndex = index
            for (dIndex in index downTo 0) {
                if (cList[dIndex].any { a -> a > 0 }) {
                    topIndex = dIndex
                    break
                }
            }
            val top = cList[topIndex]
            for (j in column.indices) {
                column[j] = top[j]
                top[j] = 0
            }

            if (cList.slice(0 until index).none { n -> !n.none { c -> c > 0 } })
                break
        }
        if (deathLine > 0) {
            val old = _score.value ?: 0
            val new = old + (2.0.pow((deathLine - 1).toDouble())).toInt()
            _score.postValue(new)
        }
    }

    private fun mergeCell() {
        if (isReset()) return
        val cList = _cellList.value?.clone() ?: return
        currentIndex.forEach { (x, y) ->
            cList[x][y] = 2
        }
        _cellList.postValue(cList)
    }


    private var variantIndex = 0
    private fun calcCurrent() {
        variantIndex = 0
        when (_currentCell) {
            CellVariant.I -> {
                currentIndex[0] = 0 to 4
                currentIndex[1] = 1 to 4
                currentIndex[2] = 2 to 4
                currentIndex[3] = 3 to 4
            }
            CellVariant.O -> {
                currentIndex[0] = 0 to 4
                currentIndex[1] = 0 to 5
                currentIndex[2] = 1 to 4
                currentIndex[3] = 1 to 5
            }
            CellVariant.T -> {
                currentIndex[0] = 0 to 3
                currentIndex[1] = 0 to 4
                currentIndex[2] = 0 to 5
                currentIndex[3] = 1 to 4
            }
            CellVariant.L -> {
                currentIndex[0] = 0 to 4
                currentIndex[1] = 1 to 4
                currentIndex[2] = 2 to 4
                currentIndex[3] = 2 to 5
            }
            CellVariant.J -> {
                currentIndex[0] = 0 to 5
                currentIndex[1] = 1 to 5
                currentIndex[2] = 2 to 5
                currentIndex[3] = 2 to 4
            }
            CellVariant.S -> {
                currentIndex[0] = 0 to 4
                currentIndex[1] = 0 to 5
                currentIndex[2] = 1 to 4
                currentIndex[3] = 1 to 3
            }
            CellVariant.Z -> {
                currentIndex[0] = 0 to 3
                currentIndex[1] = 0 to 4
                currentIndex[2] = 1 to 4
                currentIndex[3] = 1 to 5
            }
            null -> {
            }
        }
        if (isGameOver()) {
            _gameState.postValue(GameState.Over)
        } else {
            calcCells()
        }
    }

    private fun calcCellList() {
        if (isReset()) return
        if (isGameOver()) return
        calcCells()
    }

    private fun calcCells() {
        if (isReset()) return
        val cList = _cellList.value?.clone() ?: return
        for (i in cList.indices) {
            for (j in cList[i].indices) {
                if (cList[i][j] > 1) {
                    cList[i][j] = 2
                } else {
                    cList[i][j] = 0
                }
            }
        }
        currentIndex.forEach { (x, y) ->
            cList[x][y] = 1
        }
        _cellList.postValue(cList)
    }

    private fun isGameOver(): Boolean {
        val cells = _cellList.value ?: return false
        val cIndex = currentIndex.clone()
        cIndex.forEach { (fx, fy) ->
            if (fx < 0) return true
            if (fx >= cells.size) return true
            val row = cells[fx]
            if (fy >= row.size) return true
            if (fy < 0) return true
            if (cells[fx][fy] > 1) return true
        }
        return false
    }

    private fun moveCollided(x: Int = 0, y: Int = 0): Boolean {
        if (isReset()) return true
        val cells = _cellList.value ?: return true
        val cIndex = currentIndex.clone()
        for (i in 0 until 4) {
            val (fx, fy) = cIndex[i]
            cIndex[i] = fx + y to fy + x
        }
        cIndex.forEach { (fx, fy) ->
            if (fx < 0) return true
            if (fx >= cells.size) return true
            val row = cells[fx]
            if (fy >= row.size) return true
            if (fy < 0) return true
            if (cells[fx][fy] > 1) return true
        }
        return false
    }

    fun isReset(): Boolean {
        currentIndex.forEach { (x, y) ->
            if (x > 0 || y > 0) return false
        }
        return true
    }

    fun left() {
        if (moveCollided(x = -1)) return
        currentMove(x = -1)
    }

    fun right() {
        if (moveCollided(x = 1)) return
        currentMove(x = 1)
    }

    fun up() {
        variantIndex += 1
        val reversal = when (_currentCell) {
            CellVariant.I -> calcI()
            CellVariant.T -> calcT()
            CellVariant.L -> calcL()
            CellVariant.J -> calcJ()
            CellVariant.S -> calcS()
            CellVariant.Z -> calcZ()
            else -> null
        }
        if (reversal != null) {
            for (i in reversal.indices) {
                currentIndex[i] = reversal[i]
            }
        }
        currentMove()
    }

    fun down() {
        if (moveCollided(y = 1)) return
        currentMove(y = 1)
    }

    private fun currentMove(x: Int = 0, y: Int = 0) {
        for (i in currentIndex.indices) {
            val (fx, fy) = currentIndex[i]
            currentIndex[i] = fx + y to fy + x
        }
        calcCellList()
    }

    private fun calcI() =
        if (variantIndex % 2 == 0) {
            tryToReversal(arrayOf(-1 to 1, 0 to 0, 1 to -1, 2 to -2))
        } else {
            tryToReversal(arrayOf(1 to -1, 0 to 0, -1 to 1, -2 to 2))
        }

    private fun calcZ() =
        if (variantIndex % 2 == 0) {
            tryToReversal(arrayOf(0 to -1, -1 to 1, 0 to 0, -1 to 2))
        } else {
            tryToReversal(arrayOf(0 to 1, 1 to -1, 0 to 0, 1 to -2))
        }

    private fun calcS() =
        if (variantIndex % 2 == 0) {
            tryToReversal(arrayOf(0 to 0, -1 to 1, 0 to -1, -1 to -2))
        } else {
            tryToReversal(arrayOf(0 to 0, 1 to -1, 0 to 1, 1 to 2))
        }

    private fun calcL() =
        when (variantIndex % 4) {
            0 -> tryToReversal(arrayOf(-1 to 1, 0 to 0, 1 to -1, 2 to 0))
            1 -> tryToReversal(arrayOf(1 to 1, 0 to 0, -1 to -1, 0 to -2))
            2 -> tryToReversal(arrayOf(1 to -1, 0 to 0, -1 to 1, -2 to 0))
            3 -> tryToReversal(arrayOf(-1 to -1, 0 to 0, 1 to 1, 0 to 2))
            else -> null
        }

    private fun calcJ() =
        when (variantIndex % 4) {
            0 -> tryToReversal(arrayOf(-1 to 1, 0 to 0, 1 to -1, 0 to -2))
            1 -> tryToReversal(arrayOf(1 to 1, 0 to 0, -1 to -1, -2 to 0))
            2 -> tryToReversal(arrayOf(1 to -1, 0 to 0, -1 to 1, 0 to 2))
            3 -> tryToReversal(arrayOf(-1 to -1, 0 to 0, 1 to 1, 2 to 0))
            else -> null
        }

    private fun calcT() =
        when (variantIndex % 4) {
            1 -> tryToReversal(arrayOf(-1 to 1, 0 to 0, 1 to -1, -1 to -1))
            2 -> tryToReversal(arrayOf(1 to 1, 0 to 0, -1 to -1, -1 to 1))
            3 -> tryToReversal(arrayOf(1 to -1, 0 to 0, -1 to 1, 1 to 1))
            0 -> tryToReversal(arrayOf(-1 to -1, 0 to 0, 1 to 1, 1 to -1))
            else -> null
        }


    private fun tryToReversal(
        offset: Array<Pair<Int, Int>>
    ): Array<Pair<Int, Int>> {
        val cIndex = currentIndex.clone()
        for (i in cIndex.indices) {
            val (fx, fy) = cIndex[i]
            val (ox, oy) = offset[i]
            cIndex[i] = fx + ox to fy + oy
        }
        val canRe = canReversal(cIndex)
        if (canRe) return cIndex
        variantIndex -= 1
        if (variantIndex < 0) {
            variantIndex = 0
        }
        return currentIndex
    }

    private fun canReversal(reversal: Array<Pair<Int, Int>>): Boolean {
        if (isReset()) return false
        val cells = _cellList.value ?: return false
        reversal.forEach { (fx, fy) ->
            if (fx < 0) return false
            if (fx >= cells.size) return false
            val row = cells[fx]
            if (fy >= row.size) return false
            if (fy < 0) return false
            if (cells[fx][fy] > 1) return false
        }
        return true
    }
}