package de.sambalmueslie.boardbuddy.gateway.portal

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import jakarta.inject.Singleton
import java.io.ByteArrayOutputStream

/** Renders QR codes locally, so join links (which contain the session key) never leave the server. */
@Singleton
class QrCodeRenderer {
    companion object {
        private const val SIZE = 256
    }

    fun render(content: String): ByteArray {
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE, mapOf(EncodeHintType.MARGIN to 1))
        return ByteArrayOutputStream().use { out ->
            MatrixToImageWriter.writeToStream(matrix, "PNG", out)
            out.toByteArray()
        }
    }
}
