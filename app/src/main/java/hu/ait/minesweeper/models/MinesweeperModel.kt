package hu.ait.minesweeper.models

import java.lang.IndexOutOfBoundsException
import kotlin.random.Random

object MinesweeperModel {

    const val EMPTY = 0
    const val FLAG = 99
    const val MINE = -1
    var mineCount = 3
    var showMines: Boolean = false

    // 3D Array -- 2D for game grid, and one dimension to store one array of 2 elements for each cell
    // Each cell is an array of 2 integers, the first integer is whether the cell should store a number
    // or a mine, and the second integer controls whether it should be visible/drawn
    private val model = Array(5) { Array(5) {Array(2) { EMPTY }}}

    fun generateMines() {
        for (i in 0..2) {
            var rand = Random.nextInt(24)
            while (model[rand / 5][rand % 5][0] == MINE) {
                rand = Random.nextInt(24)
            }
            model[rand / 5][rand % 5][0] = MINE
        }
    }

    private fun countNearbyMines(x: Int, y: Int): Int{
        var mines = 0

        for (i in (x-1)..(x+1)){
            for (j in (y-1)..(y+1)){
                if (i == x && j == y){
                    continue
                }
                try {
                    if (model[i][j][0] == MINE){
                        mines++
                    }
                } catch (e: IndexOutOfBoundsException){}
            }
        }

        return mines
    }

    fun setNumbers() {
        for (i in 0..4) {
            for (j in 0..4) {
                if (model[i][j][0] != MINE) {
                    model[i][j][0] = countNearbyMines(i,j)
                }
            }
        }
    }

    fun resetModel() {
        for (i in 0..4) {
            for (j in 0..4) {
                for (k in 0..1) {
                    model[i][j][k] = EMPTY
                }
            }
        }
        generateMines()
        setNumbers()
        mineCount = 3
        showMines = false
    }

    fun getFieldContent(x: Int, y: Int, z: Int) = model[x][y][z]

    fun setFieldContent(x: Int, y: Int, z: Int, content: Int) {
        model[x][y][z] = content
    }


}