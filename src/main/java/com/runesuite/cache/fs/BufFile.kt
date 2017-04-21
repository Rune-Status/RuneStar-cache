package com.runesuite.cache.fs

import com.runesuite.cache.extensions.freeDirect
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.Closeable
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class BufFile(file: Path, maxSize: Int) : AutoCloseable, Closeable {

    private val fileChannel = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    private val originalSize = fileChannel.size()

    private val mappedByteBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, maxSize.toLong())

    val buffer: ByteBuf = Unpooled.wrappedBuffer(mappedByteBuffer).writerIndex(originalSize.toInt())

    override fun close() {
        val writtenSize = buffer.writerIndex()
        buffer.release()
        mappedByteBuffer.force()
        mappedByteBuffer.freeDirect()
        fileChannel.truncate(writtenSize.toLong())
        fileChannel.close()
    }
}