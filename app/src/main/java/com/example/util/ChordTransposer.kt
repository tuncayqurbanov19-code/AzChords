package com.example.util

import java.util.regex.Pattern

object ChordTransposer {
    private val NOTES_SHARP = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val NOTES_FLAT = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

    // RegEx to find the root note of a chord (e.g. C#, Db, A, F)
    // Supports matching sharps (#) and flats (b)
    private val CHORD_ROOT_RE = Pattern.compile("^([A-G][#b]?)")

    /**
     * Transposes a full text containing chords in brackets like "[Am] Saçın ucun [Dm] hörməzlər".
     */
    fun transposeText(text: String, semitones: Int): String {
        if (semitones == 0) return text
        
        val bracketPattern = Pattern.compile("\\[([^\\]]+)\\]")
        val matcher = bracketPattern.matcher(text)
        val sb = StringBuffer()

        while (matcher.find()) {
            val chord = matcher.group(1) ?: ""
            val transposedChord = transposeChord(chord, semitones)
            matcher.appendReplacement(sb, "[" + java.util.regex.Matcher.quoteReplacement(transposedChord) + "]")
        }
        matcher.appendTail(sb)
        return sb.toString()
    }

    /**
     * Transposes a single block representing a chord, e.g. "Am7" or "C#/F"
     */
    private fun transposeChord(chord: String, semitones: Int): String {
        if (chord.contains("/")) {
            val parts = chord.split("/")
            if (parts.size == 2) {
                return "${transposeSingleChord(parts[0], semitones)}/${transposeSingleChord(parts[1], semitones)}"
            }
        }
        return transposeSingleChord(chord, semitones)
    }

    private fun transposeSingleChord(chord: String, semitones: Int): String {
        val matcher = CHORD_ROOT_RE.matcher(chord)
        if (matcher.find()) {
            val root = matcher.group(1) ?: return chord
            val remainder = chord.substring(root.length)
            val transposedRoot = transposeNote(root, semitones)
            return transposedRoot + remainder
        }
        return chord
    }

    private fun transposeNote(note: String, semitones: Int): String {
        // Find index in sharp list or flat list
        var idx = NOTES_SHARP.indexOf(note)
        if (idx == -1) {
            idx = NOTES_FLAT.indexOf(note)
        }
        if (idx == -1) return note // If not recognized, return original note

        // Shift index
        val newIdx = (idx + semitones).mod(12)

        // If the original note was flat (contains 'b') or we want flats, we can use flats. Let's match original style!
        return if (note.contains("b")) {
            NOTES_FLAT[newIdx]
        } else {
            NOTES_SHARP[newIdx]
        }
    }
}
