package com.example.pair3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class InclinedSeparatorView(context: Context) : View(context) {

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
    }
    private val angle = 45f // Angle de l'inclinaison

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Dessiner le trait incliné
        canvas.save()
        canvas.rotate(angle, width / 2, height / 2)
        canvas.drawLine(0f, 0f, width, height, paint)
        canvas.restore()
    }
}
