package excelsior.input;

import excelsior.panel.Narration;
import excelsior.panel.TextBubble;

public class StringPreparer {
    /**
     * Sorts through fonts for text bubble
     * @param s the text to prepare
     * @param tBub the bubble
     * @return the output String or null
     */
    public String prepareTBub(String s, TextBubble tBub) {
        String output;

        output = prepareString(s, 3, 17);
        if (output != null) {
            tBub.setTextSize(20);
            return output;
        }
        output = prepareString(s, 4, 18);
        if (output != null) {
            tBub.setTextSize(16);
            return output;
        }
        output = prepareString(s, 5, 25);
        if (output != null) {
            tBub.setTextSize(13);
            return output;
        }

        return null;
    }

    /**
     * Dynamically change font size for narration depending on input length
     * @param s the text to prepare
     * @param narration the narration
     * @return the output String or null
     */
    public String prepareNarration(String s, Narration narration) {
        String output;

        output = prepareString(s, 2, 50);
        if (output != null) {
            narration.setTextSize(20);
            return output;
        }
        output = prepareString(s, 2, 63);
        if (output != null) {
            narration.setTextSize(16);
            return output;
        }

        output = prepareString(s, 3, 77);
        if (output != null) {
            narration.setTextSize(13);
            return output;
        }

        return null;
    }

    /**
     * Prepares String for text bubbles and returns null if exceeds acceptable length
     * @param s the text to prepare
     * @param numLines the number of lines
     * @param charPerLine the number of characters per line
     * @return output String
     */
    private String prepareString(String s, int numLines, int charPerLine) {
        String output;
        int lastSpace = 0;
        int lineLength = 0;
        int index = 0;
        char curr;
        StringBuilder outputBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            curr = s.charAt(i);
            lineLength++;
            if (curr == ' ') {
                lastSpace = i;
            }
            if (lineLength == charPerLine && i != (s.length() - 1)) {
                if (i == lastSpace || (i - lastSpace >= charPerLine - 1)) {
                    outputBuilder.append(curr).append("\n");
                } else {
                    outputBuilder = new StringBuilder(outputBuilder.substring(0, lastSpace + index + 1) + "\n" + outputBuilder.substring(lastSpace + index + 1) + curr);
                }
                lineLength = (i - lastSpace < charPerLine - 1) ? (i - lastSpace) : 0;
                index++;
            } else outputBuilder.append(curr);
        }
        output = outputBuilder.toString();
        if (index + 1 > numLines) output = null;
        return output;
    }
}
