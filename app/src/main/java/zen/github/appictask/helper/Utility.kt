package zen.github.appictask.helper

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import java.io.IOException
import java.io.InputStream

object Utility {
    fun readXMLinString(fileName: String, c: Context): String {
        return try {
            val `is`: InputStream = c.assets.open(fileName)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun toCamelCase(value: String): String {
        var whiteSpace = true
        val builder = StringBuilder(value.replace("_", " "))
        val builderLength = builder.length
        for (i in 0 until builderLength) {
            val c = builder[i]
            if (whiteSpace) {
                if (!Character.isWhitespace(c)) {
                    builder.setCharAt(i, c.titlecaseChar())
                    whiteSpace = false
                }
            } else if (Character.isWhitespace(c)) {
                whiteSpace = true
            } else {
                builder.setCharAt(i, c.lowercaseChar())
            }
        }
        return builder.toString()
    }

    fun boldText(initial: String, boldText: String): String {
        return SpannableStringBuilder()
            .append(initial)
            .bold { append(boldText) }.toString()
    }
}