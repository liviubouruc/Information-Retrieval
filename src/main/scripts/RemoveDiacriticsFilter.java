import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class RemoveDiacriticsFilter extends TokenFilter {
    private final CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

    protected RemoveDiacriticsFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        char[] word = this.charTermAttribute.buffer();
        char[] noDiacriticsWord = removeDiacritics(this.charTermAttribute.buffer());
        System.arraycopy(noDiacriticsWord, 0, word, 0, word.length);
        return true;
    }

    public static char[] removeDiacritics(char[] word) {
        char[] noDiacriticsWord = new char[word.length];
        for (int i = 0; i < word.length; i++) {
            char chr = word[i];
            if (chr == 'ă' || chr == 'â') {
                chr = 'a';
            }
            else if (chr == 'î') {
                chr = 'i';
            }
            else if (chr == 'ș') {
                chr = 's';
            }
            else if (chr == 'ț') {
                chr = 't';
            }
            noDiacriticsWord[i] = chr;
        }
        return noDiacriticsWord;
    }
}
