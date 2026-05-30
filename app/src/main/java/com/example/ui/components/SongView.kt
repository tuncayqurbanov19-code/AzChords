package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.ChordTransposer

data class ChordPart(val chord: String?, val lyrics: String)

fun parseLine(line: String): List<ChordPart> {
    if (!line.contains("[")) {
        return listOf(ChordPart(null, line))
    }
    val parts = mutableListOf<ChordPart>()
    val builder = StringBuilder()
    var currentChord: String? = null
    var i = 0
    while (i < line.length) {
        if (line[i] == '[') {
            // Flush current text if it exists
            if (builder.isNotEmpty() || currentChord != null) {
                parts.add(ChordPart(currentChord, builder.toString()))
                builder.clear()
                currentChord = null
            }
            val start = i + 1
            val end = line.indexOf(']', start)
            if (end != -1) {
                currentChord = line.substring(start, end)
                i = end + 1
                continue
            }
        }
        builder.append(line[i])
        i++
    }
    if (builder.isNotEmpty() || currentChord != null) {
        parts.add(ChordPart(currentChord, builder.toString()))
    }
    return parts
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransposedSongContent(
    rawText: String,
    transposition: Int,
    modifier: Modifier = Modifier
) {
    val transposedText = ChordTransposer.transposeText(rawText, transposition)
    val lines = transposedText.split("\n")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        lines.forEach { line ->
            if (line.trim().isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                val lineParts = parseLine(line)
                
                // FlowRow prevents overflow and wraps chords + lyrics correctly
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    lineParts.forEach { part ->
                        if (part.chord != null) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(end = 6.dp, bottom = 4.dp)
                            ) {
                                // Modern Glowing Chord Gold Badge
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = part.chord,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 11.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(2.dp))
                                
                                // Lyric text segment associated with this chord
                                Text(
                                    text = if (part.lyrics.isEmpty()) " " else part.lyrics,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = 18.sp,
                                    letterSpacing = 0.2.sp
                                )
                            }
                        } else {
                            // Plain lyric without an explicit chord
                            Box(
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .align(Alignment.Bottom)
                            ) {
                                Text(
                                    text = part.lyrics,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                    lineHeight = 18.sp,
                                    letterSpacing = 0.2.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
