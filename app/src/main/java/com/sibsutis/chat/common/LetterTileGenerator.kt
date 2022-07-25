package com.sibsutis.chat.common

import android.content.Context
import android.graphics.*
import androidx.core.content.res.ResourcesCompat
import com.sibsutis.chat.R
import kotlin.math.abs

class LetterTileGenerator(context: Context) {

    private val darkColors by lazy {
        val darkColors = context.resources.obtainTypedArray(R.array.dark_colors)
        (0 until darkColors.length()).map { i ->
            darkColors.getColor(i, Color.BLACK)
        }.also {
            darkColors.recycle()
        }
    }

    private val lightColors by lazy {
        val lightColors = context.resources.obtainTypedArray(R.array.light_colors)
        (0 until lightColors.length()).map { i ->
            lightColors.getColor(i, Color.GRAY)
        }.also {
            lightColors.recycle()
        }
    }

    fun generate(
        context: Context,
        userId: String,
        letters: String,
        width: Int,
        height: Int,
        dark: Boolean
    ): Bitmap {
        val colors = if (dark) darkColors else lightColors
        val hashCode = userId.hashCode()
        val index = abs(hashCode) % colors.size

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = colors[index]
        paint.style = Paint.Style.FILL

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawPaint(paint)

        val text = letters.uppercase()
        val typeface = ResourcesCompat.getFont(context, R.font.kanit_regular)
        val bounds = Rect()

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = typeface
        paint.textSize = width * 0.4f
        paint.getTextBounds(text, 0, text.length, bounds)

        val x = width / 2.0f
        val y = height / 2.0f + bounds.height() / 2.0f - bounds.bottom

        canvas.drawText(text, x, y, paint)

        return bitmap
    }
}