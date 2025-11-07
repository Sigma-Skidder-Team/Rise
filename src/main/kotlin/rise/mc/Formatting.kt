package rise.mc

import java.util.Locale
import java.util.regex.Pattern

enum class Formatting(
    /**
     * The name of this color/formatting
     */
    private val formattingName: String,
    /**
     * The formatting code that produces this format.
     */
    formattingCode: Char,
    /**
     * False if this is just changing the color or resetting; true otherwise.
     */
    val isFancyStyling: Boolean,
    /**
     * The numerical index that represents this color
     */
    val colorIndex: Int = -1
) {
    BLACK("BLACK", '0', 0),
    DARK_BLUE("DARK_BLUE", '1', 1),
    DARK_GREEN("DARK_GREEN", '2', 2),
    DARK_AQUA("DARK_AQUA", '3', 3),
    DARK_RED("DARK_RED", '4', 4),
    DARK_PURPLE("DARK_PURPLE", '5', 5),
    GOLD("GOLD", '6', 6),
    GRAY("GRAY", '7', 7),
    DARK_GRAY("DARK_GRAY", '8', 8),
    BLUE("BLUE", '9', 9),
    GREEN("GREEN", 'a', 10),
    AQUA("AQUA", 'b', 11),
    RED("RED", 'c', 12),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
    YELLOW("YELLOW", 'e', 14),
    WHITE("WHITE", 'f', 15),
    NONE("NONE", 'r', -1),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1);

    /**
     * The control string (section sign + formatting code) that can be inserted into client-side text to display
     * subsequent text in this format.
     */
    private val controlString: String = "§$formattingCode"

    /**
     * Returns the numerical color index that represents this formatting
     */

    constructor(formattingName: String, formattingCodeIn: Char, colorIndex: Int) : this(
        formattingName,
        formattingCodeIn,
        false,
        colorIndex
    )

    val isColor: Boolean
        /**
         * Checks if this is a color code.
         */
        get() = !this.isFancyStyling && this != RESET

    val friendlyName: String
        /**
         * Gets the friendly name of this value.
         */
        get() = this.formattingName.lowercase(Locale.getDefault())

    override fun toString(): String {
        return this.controlString
    }

    companion object {
        private val nameMapping = hashMapOf<String, Formatting>()

        /**
         * Matches formatting codes that indicate that the client should treat the following text as bold, recolored,
         * obfuscated, etc.
         */
        private val formattingCodePattern: Pattern = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]")

        private fun normalize(txt: String): String {
            return txt.lowercase(Locale.getDefault()).replace("[^a-z]".toRegex(), "")
        }

        /**
         * Returns a copy of the given string, with formatting codes stripped away.
         *
         * @param text The text to strip formatting codes from
         */
        fun getTextWithoutFormattingCodes(text: String?): String? {
            return if (text == null) null else formattingCodePattern.matcher(text).replaceAll("")
        }

        /**
         * Gets a value by its friendly name; null if the given name does not map to a defined value.
         *
         * @param friendlyName The friendly name
         */
        fun getValueByName(friendlyName: String?): Formatting? {
            return if (friendlyName == null) null else nameMapping[normalize(friendlyName)]
        }

        fun getFromIndex(colorIndex: Int): Formatting? {
            if (colorIndex < 0) {
                return RESET
            } else {
                for (fmt in entries) {
                    if (fmt.colorIndex == colorIndex) {
                        return fmt
                    }
                }

                return null
            }
        }

        fun getValidValues(color: Boolean, fancyStyling: Boolean): Collection<String> {
            val list = mutableListOf<String>()

            for (formatting in entries) {
                if ((!formatting.isColor || color) && (!formatting.isFancyStyling || fancyStyling)) {
                    list.add(formatting.friendlyName)
                }
            }

            return list
        }

        init {
            for (fmt in entries) {
                val a = normalize(fmt.formattingName)
                nameMapping[a] = fmt
            }
        }
    }
}
