package ru.ifmo.ctddev.igushkin.regexGrammar

import com.sun.deploy.util.StringUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.HashMap

/**
 * Created by Sergey on 02.05.2015.
 */

private class Visualizer {
    public companion object {
        val nodeStyle: java.util.HashMap<String, String> = java.util.HashMap()

        init {
            nodeStyle["S"] = "[label = \"S\", shape = box]"
            nodeStyle["T"] = "[label = \"T\"]"
            nodeStyle["N"] = "[label = \"N\"]"
            nodeStyle["X"] = "[label = \"X\"]"
        }

        fun literalStyle(name: String) = "[label = \"$name\", shape = plaintext]"
    }
}

public fun vizualizeToDot(root: Node, filename: String): Unit = vizualizeToDot(root, java.io.File(filename))
public fun vizualizeToDot(root: Node, file: java.io.File): Unit = vizualizeToDot(root, java.io.FileOutputStream(file))
public fun vizualizeToDot(root: Node, output: java.io.OutputStream) {
    var nodeId = 0
    val nodeMapping = java.util.HashMap<Node, Int>()

    fun traverse(n: Node) {
        val id = nodeId++
        nodeMapping[n] = id
        for (c in n.children) {
            traverse(c)
        }
    }

    traverse(root)

    val writer = output.writer("UTF-8").buffered()
    fun writeln(s: String, indent: Int = 0) {
        for (i in 1..indent)
            writer.write("\t")
        writer.write(s); writer.newLine()
    }

    writer.use {
        writeln("digraph S {")
        for ((n, id) in nodeMapping.entrySet().sortBy { it.value } ) {
            val style = if (n.literal)
                Visualizer.literalStyle(n.name) else
                Visualizer.nodeStyle[n.name]
            writeln("node$id $style;", 1)
            if (n.children.size() > 0) {
                val childrenIds = splitted(n.children.map { "node" + nodeMapping[it] }, ", ")
                writeln("node$id -> {$childrenIds}", 1)
            }
        }
        writeln("}")
    }
}

fun printUsage() {
    println("Usage:")
    println("RegexGrammar REGEX [FILENAME]")
    println("\tREGEX is a regular expression, see the grammar")
    println("\tFILENAME is output dot-file name. Default: out.dot")
}

fun splitted(items: Iterable<Any>, separator: String): String =
        StringBuilder {
            append(items.first())
            for (i in items.drop(1)) {
                append(separator+i)
            }
        }.toString()

public fun main(args: Array<String>) {
    if (args.size() < 1) {
        printUsage()
        return
    }

    val regex = args[0]
    val filename = if (1 in args.indices) args[1] else "out.dot"

    try {
        val parsed = Parser.Companion.parse(regex)
        vizualizeToDot(parsed, filename)
    } catch (e: ParserException) {
        println(e.getMessage())
        return
    }
}