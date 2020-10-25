package com.ses.zx

object ZXColor {
    // Colors in ZX Spectrum:
    // FLASH BRIGHT PAPER(GRB) INK(GRB)
    val INK_BLUE: Int = 0x01    // bit 0
    val INK_RED: Int = 0x02     // bit 1
    val INK_GREEN: Int = 0x04   // bit 2
    val PAPER_BLUE: Int = 0x08  // bit 3
    val PAPER_RED: Int = 0x10   // bit 4
    val PAPER_GREEN: Int = 0x20 // bit 5
    val BRIGHT: Int = 0x40      // bit 6
    val FLASH: Int = 0x80       // bit 7

    val INK_BLACK: Int = 0
    val INK_MAGENTA: Int = INK_RED or INK_BLUE
    val INK_CYAN: Int = INK_GREEN or INK_BLUE
    val INK_YELLOW: Int = INK_RED or INK_GREEN
    val INK_WHITE: Int = INK_RED or INK_GREEN or INK_BLUE

    val PAPER_BLACK: Int = 0
    val PAPER_MAGENTA: Int = PAPER_RED or PAPER_BLUE
    val PAPER_CYAN: Int = PAPER_GREEN or PAPER_BLUE
    val PAPER_YELLOW: Int = PAPER_RED or PAPER_GREEN
    val PAPER_WHITE: Int = PAPER_RED or PAPER_GREEN or PAPER_BLUE

    val INK_MASK: Int = 0x07
    val PAPER_MASK: Int = 0x38
    val BRIGHT_MASK: Int = BRIGHT
    val FLASH_MASK: Int = FLASH

    // normal colors
    val PALETTE: IntArray = intArrayOf(
            0xFF000000.toInt(), // 000 - black
            0xFF0000CA.toInt(), // 001 - blue
            0xFFCA0000.toInt(), // 010 - red
            0xFFCA00CA.toInt(), // 011 - magenta
            0xFF00CA00.toInt(), // 100 - green
            0xFF00CACA.toInt(), // 101 - cyan
            0xFFCACA00.toInt(), // 110 - yellow
            0xFFC5C5C5.toInt(), // 111 - white
            // bright colors
            0xFF000000.toInt(), // 000 - black
            0xFF0000FF.toInt(), // 001 - blue
            0xFFFF0000.toInt(), // 010 - red
            0xFFFF00FF.toInt(), // 011 - magenta
            0xFF00FF00.toInt(), // 100 - green
            0xFF00FFFF.toInt(), // 101 - cyan
            0xFFFFFF00.toInt(), // 110 - yellow
            0xFFFFFFFF.toInt(), // 111 - white
    )

    operator fun get(i: Int): Int = PALETTE[i]

    fun getInk(attr: Int): Int {
        val index: Int = attr and INK_MASK or ((attr and BRIGHT_MASK) shr 3)
        return PALETTE[index]
    }

    fun getPaper(attr: Int): Int {
        val index: Int = (attr and (PAPER_MASK or BRIGHT_MASK)) shr 3
        return PALETTE[index]
    }
}