package com.ses.app.zxbrowser

import java.io.File

object KnownPrograms {
    private val zxspin = Program("ZXSpin", "", "\${filePath}", arrayOf("tzx", "tap", "z80", "sna"))
    private val zesarux = Program("ZEsarUX", "", "--noconfigfile --realloadfast --realtape \${filePath}", arrayOf("tzx", "tap", "z80", "sna"), true)
    private val fuse = Program("Fuse", "", "--auto-load --tape \${filePath}", arrayOf("tzx", "tap", "z80", "sna"), true)
    private val rvm = Program("Retro Virtual Machine", "", "-b=zx48k -w -p -c=j\"\"\\n -i \${filePath}", arrayOf("tzx", "tap"), true)

    //TODO meter en JSON
    private val map = hashMapOf(
            "zxspin" to zxspin,
            "zesarux" to zesarux,
            "fuse" to fuse,
            "retrovirtualmachine" to rvm,
            "retro virtual machine 2" to rvm
    )

    private val genericProgram = Program("?", "", "\${filePath}", arrayOf("tzx", "tap", "z80", "sna"))

    fun get(file: File): Program {
        var prog: Program? = map[file.nameWithoutExtension.toLowerCase()]
        if (prog == null) {
            prog = genericProgram
            prog.name = file.nameWithoutExtension.capitalize()
        }
        prog.path = file.absolutePath

        return prog.clone()
    }
}