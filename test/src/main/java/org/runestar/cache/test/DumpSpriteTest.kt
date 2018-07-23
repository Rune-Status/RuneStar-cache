package org.runestar.cache.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.runestar.cache.content.def.SpriteSheetDefinition
import org.runestar.cache.format.BackedStore
import org.runestar.cache.format.ReadableCache
import org.runestar.cache.format.fs.FileSystemStore
import org.runestar.cache.format.net.NetStore
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

private val mapper = jacksonObjectMapper()

fun main(args: Array<String>) {

    ReadableCache(
            BackedStore(
                    FileSystemStore.open(),
                    NetStore.open("oldschool1.runescape.com", 172)
            ),
            mapper.readValue(File("known-names.json"))
    ).use { rc ->

        val dir = Paths.get(".sprites")
        Files.createDirectories(dir)

        SpriteSheetDefinition.Loader(rc).getDefinitions().filterNotNull().forEach { ss ->
            val identifier = ss.archiveIdentifier
            val def = ss.getDefinition()

            val img = def.toImage()
            val fileName = identifier.name ?: identifier.nameHash!!.toString()
            val file = dir.resolve(fileName + ".png")
            ImageIO.write(img, "png", file.toFile())
        }

    }
}