package com.ses.app.zxplorer.model

import com.ses.app.zxplorer.I
import javafx.scene.control.TableCell
import javafx.scene.image.ImageView

class FileDownloadTableCell : TableCell<ModelDownload, String>() {
    private val cloudImage = I("cloud")
    private val downloadedImage = I("file")
    private val webImage = I("web")

    private val iconView = ImageView()

    init {
        graphic = iconView
    }

    override fun updateItem(value: String?, empty: Boolean) {
        val download = tableRow?.item

        if (empty || (download == null)) {
            //text = null
            iconView.image = null
        } else {
            //text = download.fileName
            val model = download.model
            iconView.image = when {
                download.getType() == ModelDownload.Type.Web -> webImage
                model.isDownloaded(download) -> downloadedImage
                else -> cloudImage
            }
        }
    }
}
