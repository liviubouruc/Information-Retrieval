import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class MyAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String s) {
        CharArraySet stopWords = null;
        try {
            stopWords = ExtendLuceneStopWords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StandardTokenizer standardTokenizer = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(standardTokenizer);
        tokenStream = new RemoveDiacriticsFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, stopWords);
        tokenStream = new SnowballFilter(tokenStream, new RomanianStemmer());
        tokenStream = new RemoveDiacriticsFilter(tokenStream);
        tokenStream = new RemoveDuplicatesTokenFilter(tokenStream);
        return new TokenStreamComponents(standardTokenizer, tokenStream);
    }

    private CharArraySet ExtendLuceneStopWords() throws IOException {
        HashSet<char[]> stopWords = new HashSet<>();
        FileReader stopWordsFile = new FileReader("stopwords.txt");
        BufferedReader reader = new BufferedReader(stopWordsFile);

        String stopWord;
        while ((stopWord = reader.readLine()) != null) {
            stopWords.add(RemoveDiacriticsFilter.removeDiacritics(stopWord.toCharArray()));
        }
        for (Object stopWordLucene : new RomanianAnalyzer().getStopwordSet()) {
            char[] processedStopWord = RemoveDiacriticsFilter.removeDiacritics((char[]) stopWordLucene);
            stopWords.add(processedStopWord);
        }

        return new CharArraySet(stopWords, true);
    }
}
