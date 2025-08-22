package com.example.quickdraw.game.components

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quickdraw.TAG
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrCodeScanner


@Composable
fun QrCodeImage(text: String, width: Dp =200.dp, height: Dp =200.dp,modifier:Modifier = Modifier) {
    val whiteReplace = MaterialTheme.colorScheme.background.toArgb()
    val qrCodeBitmap = remember {
        QRCodeWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            512,
            512,
            mapOf(EncodeHintType.MARGIN to 1)
        ).let { matrix ->
            val bitmaps = IntArray(matrix.width * matrix.height)
            for (x in matrix.width - 1 downTo 0) {
                for (y in matrix.height - 1 downTo 0) {
                    bitmaps[(matrix.height - y - 1) * matrix.width + x] =
                        if (matrix[x, y]) Color.Black.toArgb() else whiteReplace  // Black or background
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

@Composable
fun QrScanner(onScan:(res:String)->Unit){
    var scanning by remember { mutableStateOf(true) }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            QrCodeScanner(modifier = Modifier.padding(20.dp),
                flashlightOn = false,
                onCompletion = {result -> onScan(result)},
                cameraLens = CameraLens.Back,
                overlayShape = OverlayShape.Square,
                overlayColor = MaterialTheme.colorScheme.background,
                overlayBorderColor = Color.White,
                zoomLevel = 0.5f,
                maxZoomLevel = 1.0f,
                customOverlay = {}) {

            }
        }

}