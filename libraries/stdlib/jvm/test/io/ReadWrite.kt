/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test.io

import test.collections.behaviors.sequenceBehavior
import test.collections.compare
import kotlin.test.*
import java.io.File
import java.io.Reader
import java.io.StringReader
import java.net.URL
import java.util.ArrayList

private fun sample(): Reader = StringReader("Hello\nWorld");

class ReadWriteTest {
    @Test fun testAppendText() {
        val file = File.createTempFile("temp", System.nanoTime().toString())
        file.writeText("Hello\n")
        file.appendText("World\n")
        file.appendText("Again")

        assertEquals("Hello\nWorld\nAgain", file.readText())
        assertEquals(listOf("Hello", "World", "Again"), file.readLines(Charsets.UTF_8))
        file.deleteOnExit()
    }

    @Test fun reader() {
        val list = ArrayList<String>()

        /* TODO would be nicer maybe to write this as
            reader.lines.forEach { ... }

          as we could one day maybe write that as
            for (line in reader.lines)

          if the for(elem in thing) {...} statement could act as syntax sugar for
            thing.forEach{ elem -> ... }

          if thing is not an Iterable/array/Iterator but has a suitable forEach method
        */
        sample().forEachLine {
            list.add(it)
        }
        assertEquals(listOf("Hello", "World"), list)

        assertEquals(listOf("Hello", "World"), sample().readLines())

        val lines: List<String>
        val linesResult = sample().useLines {
            lines = it.toList()
            lines
        }
        assertEquals(listOf("Hello", "World"), lines)
        assertEquals(lines, linesResult)


        var reader = StringReader("")
        var c = 0
        reader.forEachLine { c++ }
        assertEquals(0, c)

        reader = StringReader(" ")
        reader.forEachLine { c++ }
        assertEquals(1, c)

        reader = StringReader(" \n")
        c = 0
        reader.forEachLine { c++ }
        assertEquals(1, c)

        reader = StringReader(" \n ")
        c = 0
        reader.forEachLine { c++ }
        assertEquals(2, c)
    }

    @Test fun file() {
        val file = File.createTempFile("temp", System.nanoTime().toString())
        val writer = file.outputStream().writer().buffered()

        writer.write("Hello")
        writer.newLine()
        writer.write("World")
        writer.close()

        //file.replaceText("Hello\nWorld")
        file.forEachBlock { arr: ByteArray, size: Int ->
            assertTrue(size >= 11 && size <= 12, size.toString())
            assertTrue(arr.contains('W'.code.toByte()))
        }
        val list = ArrayList<String>()
        file.forEachLine(Charsets.UTF_8, {
            list.add(it)
        })
        assertEquals(arrayListOf("Hello", "World"), list)

        assertEquals(arrayListOf("Hello", "World"), file.readLines())

        val lines: List<String>
        val linesResult = file.useLines {
            lines = it.toList()
            lines
        }
        assertEquals(listOf("Hello", "World"), lines)
        assertEquals(lines, linesResult)

        val text = file.inputStream().reader().readText()
        assertTrue(text.contains("Hello"))
        assertTrue(text.contains("World"))

        file.writeText("")
        var c = 0
        file.forEachLine { c++ }
        assertEquals(0, c)

        file.writeText(" ")
        file.forEachLine { c++ }
        assertEquals(1, c)

        file.writeText(" \n")
        c = 0
        file.forEachLine { c++ }
        assertEquals(1, c)

        file.writeText(" \n ")
        c = 0
        file.forEachLine { c++ }
        assertEquals(2, c)

        file.deleteOnExit()
    }

    @Test fun testURL() {
        val file = File.createTempFile("temp", System.nanoTime().toString())
        file.deleteOnExit()
        val fileText = "Test Text"
        file.writeText(fileText)

        val url: URL = file.toURI().toURL()
        val textDefault = url.readText()
        assertEquals(fileText, textDefault)
        val textUTF8 = url.readText(charset("UTF8"))
        assertEquals(fileText, textUTF8)
    }
}


class LineIteratorTest {
    @Test fun useLines() {
        // TODO we should maybe zap the useLines approach as it encourages
        // use of iterators which don't close the underlying stream
        val list1 = sample().useLines { it.toList() }
        val list2 = sample().useLines<ArrayList<String>>{ it.toCollection(arrayListOf()) }

        assertEquals(listOf("Hello", "World"), list1)
        assertEquals(listOf("Hello", "World"), list2)
    }

    @Test fun manualClose() {
        val reader = sample().buffered()
        try {
            val list = reader.lineSequence().toList()
            assertEquals(arrayListOf("Hello", "World"), list)
        } finally {
            reader.close()
        }
    }

    @Test fun boundaryConditions() {
        var reader = StringReader("").buffered()
        assertEquals(emptyList(), reader.lineSequence().toList())
        reader.close()

        reader = StringReader(" ").buffered()
        assertEquals(listOf(" "), reader.lineSequence().toList())
        reader.close()

        reader = StringReader(" \n").buffered()
        assertEquals(listOf(" "), reader.lineSequence().toList())
        reader.close()

        reader = StringReader(" \n ").buffered()
        assertEquals(listOf(" ", " "), reader.lineSequence().toList())
        reader.close()

        reader = StringReader("a\nb\nc").buffered()
        compare(listOf("a", "b", "c").asSequence().constrainOnce(), reader.lineSequence()) {
            sequenceBehavior(isConstrainOnce = true)
        }
    }
}
