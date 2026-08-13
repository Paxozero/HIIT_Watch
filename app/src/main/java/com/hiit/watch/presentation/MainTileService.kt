package com.hiit.watch.presentation

import android.content.ComponentName
import androidx.wear.protolayout.ActionBuilders.launchAction
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material3.Typography
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures

class MainTileService : TileService() {
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) =
        Futures.immediateFuture(
            TileBuilders.Tile.Builder()
                .setResourcesVersion("1")
                .setTileTimeline(
                    TimelineBuilders.Timeline.fromLayoutElement(
                        materialScope(this, requestParams.deviceConfiguration) {
                            val launchAction = clickable(
                                action = launchAction(
                                    ComponentName(
                                        this@MainTileService.packageName,
                                        MainActivity::class.java.name
                                    )
                                )
                            )

                            primaryLayout(
                                titleSlot = {
                                    text(
                                        "HIIT Watch".layoutString,
                                        typography = Typography.TITLE_MEDIUM
                                    )
                                },
                                mainSlot = {
                                    text(
                                        "Listo para entrenar".layoutString,
                                        typography = Typography.BODY_LARGE
                                    )
                                },
                                bottomSlot = {
                                    textEdgeButton(
                                        onClick = launchAction,
                                        labelContent = {
                                            text("ENTRENAR".layoutString)
                                        }
                                    )
                                },
                                onClick = launchAction
                            )
                        }
                    )
                ).build()
        )

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest) =
        Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion("1")
                .build()
        )
}
