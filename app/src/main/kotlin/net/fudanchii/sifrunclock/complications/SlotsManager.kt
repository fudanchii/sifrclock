package net.fudanchii.sifrunclock.complications

import android.content.Context
import android.graphics.RectF
import androidx.wear.watchface.CanvasComplicationFactory
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.complications.ComplicationSlotBounds
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import net.fudanchii.sifrunclock.R

private const val LEFT_AND_RIGHT_COMPLICATIONS_TOP_BOUND = 0.4125f
private const val LEFT_AND_RIGHT_COMPLICATIONS_BOTTOM_BOUND = 0.5875f

private const val LEFT_COMPLICATION_LEFT_BOUND = 0.275f
private const val LEFT_COMPLICATION_RIGHT_BOUND = 0.45f

// Unique IDs for each complication. The settings activity that supports allowing users
// to select their complication data provider requires numbers to be >= 0.
internal const val LEFT_COMPLICATION_ID = 100

class SlotsManager {
    /**
     * Represents the unique id associated with a complication and the complication types it supports.
     */
    sealed class ComplicationConfig(val id: Int, val supportedTypes: List<ComplicationType>) {
        object Left : ComplicationConfig(
            LEFT_COMPLICATION_ID,
            listOf(
                ComplicationType.RANGED_VALUE,
                ComplicationType.MONOCHROMATIC_IMAGE,
                ComplicationType.SHORT_TEXT,
                ComplicationType.SMALL_IMAGE
            )
        )
    }

    companion object {
        fun create(context: Context, currentUserStyleRepository: CurrentUserStyleRepository): ComplicationSlotsManager {
            val defaultCanvasComplicationFactory =
                CanvasComplicationFactory { watchState, listener ->
                    CanvasComplicationDrawable(
                        ComplicationDrawable.getDrawable(context, R.drawable.complication_default_style)!!,
                        watchState,
                        listener
                    )
                }

            val leftComplication = ComplicationSlot.createRoundRectComplicationSlotBuilder(
                id = ComplicationConfig.Left.id,
                canvasComplicationFactory = defaultCanvasComplicationFactory,
                supportedTypes = ComplicationConfig.Left.supportedTypes,
                defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                    SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
                    ComplicationType.RANGED_VALUE
                ),
                bounds = ComplicationSlotBounds(
                    RectF(
                        LEFT_COMPLICATION_LEFT_BOUND,
                        LEFT_AND_RIGHT_COMPLICATIONS_TOP_BOUND,
                        LEFT_COMPLICATION_RIGHT_BOUND,
                        LEFT_AND_RIGHT_COMPLICATIONS_BOTTOM_BOUND
                    )
                )
            ).build()

            return ComplicationSlotsManager(listOf(leftComplication), currentUserStyleRepository)
        }
    }}