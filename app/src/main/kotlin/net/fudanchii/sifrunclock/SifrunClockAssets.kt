package net.fudanchii.sifrunclock

import android.content.Context
import android.graphics.*
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.graphics.scale
import androidx.wear.watchface.Renderer
import kotlin.math.cos
import kotlin.math.sin


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
    var hourLocations: MutableList<Pair<Float, Float>> = mutableListOf()

    init {
        var textBounds = Rect()
        val displayMetrics = context.resources.displayMetrics
        val bounds: Rect = Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        for (i in 0 until 12) {
            hourLabelPaint.getTextBounds(hours[i], 0, hours[i].length, textBounds)

            val rotation = (i.toDouble() * Math.PI * 2f / 12f) - (Math.PI / 2f)
            val dx = cos(rotation).toFloat() * 0.29f * bounds.width().toFloat()
            val dy = sin(rotation).toFloat() * 0.28f * bounds.height().toFloat()

            hourLocations.add(
                Pair(
                    bounds.exactCenterX() + dx - (textBounds.width() / 2.0f) - 2,
                    bounds.exactCenterY() + dy + (textBounds.height() / 2.0f) + 6,
                )
            )
        }
    }

    override fun onDestroy() { }
}
