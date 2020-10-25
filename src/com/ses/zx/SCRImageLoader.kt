package com.ses.zx

import javafx.scene.image.Image
import java.io.InputStream


class SCRImageLoader {
    fun load(stream: InputStream): Image {
        val screen = ZXScreen()
        stream.read(screen.mem)
        return screen.toImage()
    }
}