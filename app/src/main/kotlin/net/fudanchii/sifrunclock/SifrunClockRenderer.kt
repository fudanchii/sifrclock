package net.fudanchii.sifrunclock


import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.WatchFaceLayer
import java.time.ZonedDateTime

private const val CENTER_GAP_AND_CIRCLE_RADIUS = 4f

private const val FRAME_PERIOD_MS_DEFAULT: Long = 16L

class WatchFaceRenderer(
    context: Context,
    surfaceHolder: SurfaceHolder,
    currentUserStyleRepository: CurrentUserStyleRepository,
    watchState: WatchState,
    private val complicationSlotsManager: ComplicationSlotsManager,
    canvasType: Int
) : Renderer.CanvasRenderer2<WatchFaceAssets>(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    canvasType,
    FRAME_PERIOD_MS_DEFAULT,
    false,
) {
    private val assets = WatchFaceAssets(context)

    override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: WatchFaceAssets) {
        if (renderParameters.drawMode == DrawMode.INTERACTIVE &&
                renderParameters.watchFaceLayers.contains(WatchFaceLayer.BASE)) {
            drawBackground(canvas, zonedDateTime, sharedAssets)
            drawComplications(canvas, zonedDateTime)
            drawHourLabels(canvas, zonedDateTime, sharedAssets)
            drawHands(canvas, bounds, zonedDateTime, sharedAssets)
        }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: WatchFaceAssets) {
    }

    override suspend fun createSharedAssets(): WatchFaceAssets {
        return assets
    }

    private fun drawBackground(canvas: Canvas,dateTime: ZonedDateTime, assets: WatchFaceAssets) {
        val color = when (dateTime.hour) {
            in 0..3 -> assets.bgNight2Color
            5 -> assets.bgDawn5Color
            6 -> assets.bgDawn6Color
            in 7..10 -> assets.bgMorningColor
            in 11..14 -> assets.bgNoonColor
            in 15..16 -> assets.bgAfternoonColor
            17 -> assets.bgDusk5Color
            18 -> assets.bgDusk6Color
            else -> assets.bgNight1Color
        }
        assets.bgPaint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        canvas.drawBitmap(assets.bgBitmap, 0f, 0f, assets.bgPaint)
    }

    private fun currentHourIs(hour: Int, i: Int): Boolean =
        (12 - (hour % 12) == i) || (i == 0 && (hour == i || hour == 12))

    private fun drawHourLabels(
        canvas: Canvas,
        zonedDateTime: ZonedDateTime,
        assets: WatchFaceAssets
    ) {
        for (i in 0 until 12) {
            val paint = if (currentHourIs(zonedDateTime.hour, i)) {
                assets.currentHourLabelPaint
            } else {
                assets.hourLabelPaint
            }

            val (x, y) = assets.hourLocations[i]
            canvas.drawText(assets.hours[i], x, y, paint)
        }
    }

    private fun drawHands(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, assets: WatchFaceAssets) {
        val secondsRotation = 360 - (zonedDateTime.second * 6f)

        val minutesRotation = 360 - (zonedDateTime.minute * 6f)

        val hourHandOffset = zonedDateTime.minute / 2f
        val hoursRotation = 360 - (zonedDateTime.hour * 30 + hourHandOffset)

        val mCenterX = bounds.exactCenterX()
        val mCenterY = bounds.exactCenterY()

        val mSecondHandLength = (mCenterX * 0.875).toFloat()
        val sMinuteHandLength = (mCenterX * 0.75).toFloat()
        val sHourHandLength = (mCenterX * 0.5).toFloat()

        canvas.save()

        canvas.rotate(hoursRotation, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
            mCenterX,
            mCenterY - sHourHandLength,
            assets.hourPaint)

        canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
            mCenterX,
            mCenterY - sMinuteHandLength,
            assets.minutePaint)

        canvas.rotate(secondsRotation - minutesRotation, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
            mCenterX,
            mCenterY - mSecondHandLength,
            assets.secondPaint)

        canvas.drawCircle(
            mCenterX,
            mCenterY,
            CENTER_GAP_AND_CIRCLE_RADIUS,
            assets.secondPaint)

        /* Restore the canvas" original orientation. */
        canvas.restore()
    }

    private fun drawComplications(canvas: Canvas, zonedDateTime: ZonedDateTime) {
        for ((_, complication) in complicationSlotsManager.complicationSlots) {
            if (complication.enabled) {
                complication.render(canvas, zonedDateTime, renderParameters)
            }
        }
    }
}