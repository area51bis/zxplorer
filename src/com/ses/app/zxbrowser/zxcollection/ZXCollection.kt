package com.ses.app.zxbrowser.zxcollection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

class ZXCollection(file: File? = null) {
    val info = ZXCollectionInfo()

    // tabla principal
    val entries = ArrayList<Entry>()

    // tablas secundarias (opcionales en el JSON)
    var genres = IdList<Genre>()
    var machines = IdList<Machine>()
    var languages = IdList<Language>()
    var fileTypes = IdList<FileType>()
    var availabilityTypes = IdList<Availability>()

    init {
        if (file != null) load(file)
    }

    /**
     * Carga la biblioteca de un JSON.
     */
    fun load(file: File) {
    }

    fun save(file: File) {
    }

    companion object {
        private val gson: Gson

        // tablas secundarias globales (incluidas en ZXplorer)
        private val genres: IdList<Genre>
        private val machines: IdList<Machine>
        private val languages: IdList<Language>
        private val fileTypes: IdList<FileType>
        private val availabilityTypes: IdList<Availability>

        init {
            genres = readInternalList("genretypes", Genre::class)
            machines = readInternalList("machinetypes", Machine::class)
            languages = readInternalList("languages", Language::class)
            fileTypes = readInternalList("filetypes", FileType::class)
            availabilityTypes = readInternalList("availabletypes", Availability::class)

            gson = GsonBuilder()
                    .registerTypeAdapter(ReleaseDate::class.java, ReleaseDateTypeAdapter())
                    .registerTypeAdapter(Genre::class.java, IntReferenceTypeAdapter(genres))
                    .registerTypeAdapter(Machine::class.java, IntReferenceTypeAdapter(machines))
                    .registerTypeAdapter(Language::class.java, StringReferenceTypeAdapter(languages))
                    .registerTypeAdapter(FileType::class.java, IntReferenceTypeAdapter(fileTypes))
                    .registerTypeAdapter(Availability::class.java, StringReferenceTypeAdapter(availabilityTypes))
                    .create()
        }

        fun loadCollection(file: File): ZXCollection {
            return gson.fromJson(FileReader(file), ZXCollection::class.java)
        }

        private fun <T : IdText<*>> readInternalList(name: String, cls: KClass<T>): IdList<T> {
            return readList(ZXCollection::class.java.getResourceAsStream("data/$name.json"), cls)
        }

        private fun <T : IdText<*>> readList(stream: InputStream, cls: KClass<T>): IdList<T> {
            val list = IdList<T>()
            val type = TypeToken.getParameterized(ArrayList::class.java, cls.javaObjectType).type
            val arr: ArrayList<T> = Gson().fromJson(InputStreamReader(stream), type)
            arr.forEach(list::addRow)
            return list
        }
    }
}
