package net.fudanchii.sifrunclock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.scale
import androidx.wear.watchface.Renderer

private const val HOUR_STROKE_WIDTH = 5f
private const val MINUTE_STROKE_WIDTH = 3f
private const val SECOND_TICK_STROKE_WIDTH = 2f

private val SHADOW_RADIUS = 6f

class WatchFaceAssets(private val context: Context): Renderer.SharedAssets {
    val bgPaint: Paint = Paint().apply { color = Color.BLACK }
    val bgBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.watchface_service_bg)
        .scale(
            context.resources.displayMetrics.widthPixels,
            context.resources.displayMetrics.heightPixels,
            true
        )

    private val shadowColor: Int = Color.BLACK
    private val handColor: Int = Color.WHITE
    private val hiHandColor: Int = Color.RED

    val hourPaint: Paint = Paint().apply {
        color = handColor
        strokeWidth = HOUR_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(SHADOW_RADIUS, 0f, 0f, shadowColor)
    }

    val minutePaint: Paint = Paint().apply {
        color = handColor
        strokeWidth = MINUTE_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(SHADOW_RADIUS, 0f, 0f, shadowColor)
    }

    val secondPaint: Paint = Paint().apply {
        color = hiHandColor
        strokeWidth = SECOND_TICK_STROKE_WIDTH
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(SHADOW_RADIUS, 0f, 0f, shadowColor)
    }

    val hourLabelPaint: Paint = Paint().apply {
        color = handColor
        strokeWidth = SECOND_TICK_STROKE_WIDTH
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        textSize = 46f
    }

    var currentHourLabelPaint: Paint = Paint().apply {
        color = hiHandColor
        strokeWidth = SECOND_TICK_STROKE_WIDTH
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        textSize = 46f
    }

    val hours = arrayOf("١٢", "١١", "١٠", "٩", "٨", "٧", "٦", "٥", "٤", "٣", "٢", "١")

    override fun onDestroy() { }
}
