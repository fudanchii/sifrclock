package net.fudanchii.sifrunclock

import android.content.Context
import android.graphics.*
import androidx.core.graphics.scale
import androidx.wear.watchface.Renderer
import kotlin.math.cos
import kotlin.math.sin


private const val HOUR_STROKE_WIDTH = 5f
private const val MINUTE_STROKE_WIDTH = 3f
private const val SECOND_TICK_STROKE_WIDTH = 2f

private val SHADOW_RADIUS = 6f

class WatchFaceAssets(context: Context): Renderer.SharedAssets {
    val bgPaint: Paint = Paint().apply { color = Color.BLACK }

    private val displayWidth = context.resources.displayMetrics.widthPixels
    private val displayHeight = context.resources.displayMetrics.heightPixels

    val bgBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.watchface_service_bg_v2)
        .scale(displayWidth, displayHeight, false)

    private val shadowColor: Int = Color.BLACK
    private val handColor: Int = Color.WHITE
    private val hiHandColor: Int = Color.RED

    val bgNight1Color: Int = context.resources.getColor(R.color.night_1, context.theme)
    val bgNight2Color: Int = context.resources.getColor(R.color.night_2, context.theme)
    val bgDawn5Color: Int = context.resources.getColor(R.color.dawn5, context.theme)
    val bgDawn6Color: Int = context.resources.getColor(R.color.dawn6, context.theme)
    val bgMorningColor: Int = context.resources.getColor(R.color.morning, context.theme)
    val bgNoonColor: Int = context.resources.getColor(R.color.noon, context.theme)
    val bgAfternoonColor: Int = context.resources.getColor(R.color.afternoon, context.theme)
    val bgDusk5Color: Int = context.resources.getColor(R.color.dusk5, context.theme)
    val bgDusk6Color: Int = context.resources.getColor(R.color.dusk6, context.theme)

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
        val textBounds = Rect()
        val bounds = Rect(0, 0, displayWidth, displayHeight)

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
