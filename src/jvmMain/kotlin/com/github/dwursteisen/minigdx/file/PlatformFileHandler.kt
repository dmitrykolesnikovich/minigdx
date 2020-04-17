package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.GL
import de.matthiasmann.twl.utils.PNGDecoder
import java.io.File
import java.nio.ByteBuffer

actual class PlatformFileHandler {

    actual fun read(filename: String): Content<String> {
        val content = Content<String>(filename)
        content.load(File(filename).readText())
        return content
    }

    actual fun readData(filename: String): Content<ByteArray> {
        val content = Content<ByteArray>(filename)
        content.load(File(filename).readBytes())
        return content
    }

    actual fun readTextureImage(filename: String): Content<TextureImage> {

        val content = Content<TextureImage>(filename)
        val file = File(filename)

        val decoder = PNGDecoder(file.inputStream())

        // create a byte buffer big enough to store RGBA values
        val buffer =
            ByteBuffer.allocateDirect(4 * decoder.width * decoder.height)

        // decode
        decoder.decode(buffer, decoder.width * 4, PNGDecoder.Format.RGBA)

        // flip the buffer so its ready to read
        buffer.flip()

        content.load(TextureImage(
            width = decoder.width,
            height = decoder.height,
            glFormat = GL.RGBA,
            glType = GL.UNSIGNED_BYTE,
            pixels = buffer
        ))
        return content
    }
}
