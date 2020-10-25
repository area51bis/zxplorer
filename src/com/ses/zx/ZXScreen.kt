package com.ses.zx

import javafx.scene.image.Image
import javafx.scene.image.WritableImage

class ZXScreen {
    companion object {
        const val CH_WIDTH = 32
        const val CH_HEIGHT = 24
        const val PX_WIDTH = CH_WIDTH * 8
        const val PX_HEIGTH = CH_HEIGHT * 8
        const val PX_SIZE = CH_WIDTH * PX_HEIGTH
        const val ATTR_ADDR = PX_SIZE
        const val ATTR_SIZE = CH_WIDTH * CH_HEIGHT
        const val SIZE = PX_SIZE + ATTR_SIZE

        private val LINEAR_TO_ZX: IntArray
        private val ZX_TO_LINEAR: IntArray

        init {
            LINEAR_TO_ZX = IntArray(PX_HEIGTH)
            for (i in 0 until PX_HEIGTH) {
                val y7y6 = i and 0xC0 // 11000000
                val y2y0 = (i shl 3) and 0x38 // 00111000
                val y5y3 = (i shr 3) and 0x07 // 00000111
                val addr = y7y6 or y2y0 or y5y3
                LINEAR_TO_ZX[i] = (addr shl 5)
            }

            ZX_TO_LINEAR = IntArray(PX_HEIGTH)
            for (i in 0 until PX_HEIGTH) {
                ZX_TO_LINEAR[i] = LINEAR_TO_ZX[i] shr 5
            }
        }
    }

    val mem = ByteArray(SIZE)

    fun toImage(): Image {
        val image = WritableImage(256, 192)
        val writer = image.pixelWriter


        //TODO optimizar
        var i = 0
        for (zx_y in 0 until PX_HEIGTH) {
            val y = ZX_TO_LINEAR[zx_y]
            var attr_i = ATTR_ADDR + ((y shr 3) shl 5) // y / 8 * 32 = (y >> 3) << 5
            var x = 0
            while (x < PX_WIDTH) {
                val b: Int = mem[i++].toInt()

                val attr: Int = mem[attr_i++].toInt()
                val ink = ZXColor.getInk(attr)
                val paper = ZXColor.getPaper(attr)

                var mask = 0x80
                while (mask != 0) {
                    val color: Int = if ((b and mask) == 0) paper else ink
                    writer.setArgb(x++, y, color)
                    mask = mask shr 1
                }
            }
        }

        return image
    }
}