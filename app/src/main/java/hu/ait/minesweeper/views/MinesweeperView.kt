package hu.ait.minesweeper.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.ait.minesweeper.MainActivity
import hu.ait.minesweeper.R
import hu.ait.minesweeper.models.MinesweeperModel

class MinesweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paintBg: Paint = Paint()
    private var paintLine: Paint = Paint()
    private var paintNum: Paint = Paint()

    private var mine: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.mine)
    private var flag: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.flag)

    init {
        paintBg.color = Color.parseColor("#c3ef63")

        paintLine.color = Color.WHITE
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintNum.color = Color.BLACK
        paintNum.style = Paint.Style.FILL
        paintNum.textSize = 100f

        // Generate mines and set number of nearby mines for each cell
        MinesweeperModel.generateMines()
        MinesweeperModel.setNumbers()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mine = Bitmap.createScaledBitmap(
            mine, width / 5, height / 5, false
        )

        flag = Bitmap.createScaledBitmap(
            flag, width / 5, height / 5, false
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)
        drawGameArea(canvas!!)
        drawCells(canvas!!)
    }

    private fun drawCells(canvas: Canvas) {
        for (i in 0..4) {
            for (j in 0..4) {
                // If the cell visibility is 1, then draw the number in the cell
                if (MinesweeperModel.getFieldContent(i, j, 1) == 1) {
                    canvas.drawText(
                        MinesweeperModel.getFieldContent(i, j, 0).toString(),
                        (i * (width / 5) + 70).toFloat(),
                        (j * (height / 5) + 135).toFloat(),
                        paintNum
                    )
                }

                // If the cell has been flagged, draw the flag
                else if (MinesweeperModel.getFieldContent(i, j, 0) == MinesweeperModel.FLAG) {
                    canvas.drawBitmap(
                        flag,
                        (i * (width / 5)).toFloat(),
                        (j * (height / 5)).toFloat(),
                        null
                    )
                }

                // If the cell contains a mine and we want to show it, then draw it
                else if (MinesweeperModel.getFieldContent(i, j, 0) == MinesweeperModel.MINE) {
                    if (MinesweeperModel.showMines) {
                        canvas.drawBitmap(
                            mine,
                            (i * (width / 5)).toFloat(),
                            (j * (height / 5)).toFloat(),
                            null
                        )
                    }
                }
            }
        }
    }

    private fun drawGameArea(canvas: Canvas) {
        // border
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)
        // four horizontal lines
        canvas.drawLine(
            0f, (height / 5).toFloat(), width.toFloat(), (height / 5).toFloat(),
            paintLine
        )
        canvas.drawLine(
            0f, (2 * height / 5).toFloat(), width.toFloat(), (2 * height / 5).toFloat(),
            paintLine
        )
        canvas.drawLine(
            0f, (3 * height / 5).toFloat(), width.toFloat(), (3 * height / 5).toFloat(),
            paintLine
        )
        canvas.drawLine(
            0f, (4 * height / 5).toFloat(), width.toFloat(), (4 * height / 5).toFloat(),
            paintLine
        )

        // four vertical lines
        canvas.drawLine(
            (width / 5).toFloat(), 0f, (width / 5).toFloat(), height.toFloat(),
            paintLine
        )
        canvas.drawLine(
            (2 * width / 5).toFloat(), 0f, (2 * width / 5).toFloat(), height.toFloat(),
            paintLine
        )
        canvas.drawLine(
            (3 * width / 5).toFloat(), 0f, (3 * width / 5).toFloat(), height.toFloat(),
            paintLine
        )
        canvas.drawLine(
            (4 * width / 5).toFloat(), 0f, (4 * width / 5).toFloat(), height.toFloat(),
            paintLine
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {

            val tX = event.x.toInt() / (width / 5)
            val tY = event.y.toInt() / (height / 5)

            // If we are in the game area
            if (tX < 5 && tY < 5) {
                // If we are in flag mode
                if ((context as MainActivity).binding.toggleBtn.isChecked) {
                    // If we flag a mine, decrease count of remaining mines
                    if (MinesweeperModel.getFieldContent(tX, tY, 0) == MinesweeperModel.MINE) {
                        MinesweeperModel.setFieldContent(tX, tY, 0, MinesweeperModel.FLAG)
                        if (--MinesweeperModel.mineCount == 0) {
                            MaterialAlertDialogBuilder(context as MainActivity)
                                .setMessage(R.string.game_won)
                                .setPositiveButton(R.string.ok) { dialog, which ->
                                    resetGame()
                                }.setCancelable(false)
                                .show()
                        }
                    }
                    // If we flag something other than a mine, we lose the game
                    else if (MinesweeperModel.getFieldContent(tX, tY, 1) == 0) {
                        MinesweeperModel.setFieldContent(tX, tY, 0, MinesweeperModel.FLAG)
                        MinesweeperModel.showMines = true
                        MaterialAlertDialogBuilder(context as MainActivity)
                            .setMessage(R.string.game_lost)
                            .setPositiveButton(R.string.ok) { dialog, which ->
                                resetGame()
                            }.setCancelable(false)
                            .show()
                    }

                }
                // If we are not in flag mode
                else {
                    // If we hit a mine, we lose the game
                    if (MinesweeperModel.getFieldContent(tX, tY, 0) == MinesweeperModel.MINE) {
                        MinesweeperModel.showMines = true
                        MaterialAlertDialogBuilder(context as MainActivity)
                            .setMessage(R.string.game_lost)
                            .setPositiveButton(R.string.ok) { dialog, which ->
                                resetGame()
                            }.setCancelable(false)
                            .show()
                    }
                    // If we don't hit a mine, turn the visibility on for the cell
                    else if (MinesweeperModel.getFieldContent(tX, tY, 1) == 0) {
                        MinesweeperModel.setFieldContent(tX, tY, 1, 1)
                    }
                }

                invalidate()
            }

        }
        return true
    }

    private fun resetGame() {
        MinesweeperModel.resetModel()
        (context as MainActivity).binding.toggleBtn.isChecked = false
        invalidate()
    }

}