package com.example.quickdraw.game.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter


@Composable
fun QrCodeImage(text: String, width: Dp =200.dp, height: Dp =200.dp,modifier:Modifier = Modifier) {
    val whiteReplace = MaterialTheme.colorScheme.background.toArgb()
    val qrCodeBitmap = remember {
        QRCodeWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            512, // Width
            512, // Height
            mapOf(EncodeHintType.MARGIN to 1) // Margin
        ).let { matrix ->
            val bitmaps = IntArray(matrix.width * matrix.height)
            for (x in matrix.width - 1 downTo 0) {
                for (y in matrix.height - 1 downTo 0) {
                    bitmaps[(matrix.height - y - 1) * matrix.width + x] =
                        if (matrix[x, y]) Color.Black.toArgb() else whiteReplace  // Black or White
                }
            }
            Bitmap.createBitmap(bitmaps, matrix.width, matrix.height, Bitmap.Config.RGB_565)
        }
    }

    Image(
        bitmap = qrCodeBitmap.asImageBitmap(),
        contentDescription = "QR Code for $text",
        modifier = modifier.size(width = width, height = height)
    )
}