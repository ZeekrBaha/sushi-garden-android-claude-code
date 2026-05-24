package com.baha.sushigarden

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream

fun ComposeContentTestRule.captureAndSaveScreenshot(name: String) {
    val bitmap = onRoot().captureToImage().asAndroidBitmap()
    val ctx = InstrumentationRegistry.getInstrumentation().targetContext
    val dir = File(ctx.getExternalFilesDir(null), "screenshots")
    dir.mkdirs()
    val file = File(dir, "$name.png")
    FileOutputStream(file).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    println("SCREENSHOT_SAVED: ${file.absolutePath}")
}
