package net.fudanchii.sifrclock


import android.content.Context

import android.graphics.Canvas

import android.graphics.Rect

import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.WatchFaceLayer

import java.time.ZonedDateTime
import kotlin.math.cos
import kotlin.math.sin

private const val CENTER_GAP_AND_CIRCLE_RADIUS = 4f

private const val FRAME_PERIOD_MS_DEFAULT: Long = 16L

class WatchFaceRenderer(
    private val context: Context,
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
            drawBackground(canvas, sharedAssets)
            drawHourLabels(canvas, bounds, zonedDateTime, sharedAssets)
            drawHands(canvas, bounds, zonedDateTime, sharedAssets)
        }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: WatchFaceAssets) {
    }

    override suspend fun createSharedAssets(): WatchFaceAssets {
        return assets
    }

    private fun drawBackground(canvas: Canvas, assets: WatchFaceAssets) {
        canvas.drawBitmap(assets.bgBitmap, 0f, 0f, assets.bgPaint)
    }

    private fun drawHourLabels(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, assets: WatchFaceAssets) {
        val textBounds = Rect()
        for (i in 0 until 12) {
            assets.hourLabelPaint.getTextBounds(assets.hours[i], 0, assets.hours[i].length, textBounds)

            val rotation = (i.toDouble() * Math.PI * 2f / 12f) - (Math.PI / 2f)
            val dx = cos(rotation).toFloat() * 0.29f * bounds.width().toFloat()
            val dy = sin(rotation).toFloat() * 0.28f * bounds.height().toFloat()
            val paint = if (12 - (zonedDateTime.hour % 12) == i) {
                assets.currentHourLabelPaint
            } else {
                assets.hourLabelPaint
            }

            canvas.drawText(
                assets.hours[i],
                bounds.exactCenterX() + dx - textBounds.width() / 2.0f - 2,
                bounds.exactCenterY() + dy + textBounds.height() / 2.0f + 6,
                paint
            )
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
}