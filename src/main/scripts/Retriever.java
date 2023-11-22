import com.sun.source.doctree.ReferenceTree;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.nio.file.Paths;

public class Retriever implements AutoCloseable {
    private final IndexSearcher searcher;

    public Retriever(String dir) throws IOException {
        Directory indexedDirectory = FSDirectory.open(Paths.get(dir));
        IndexReader reader = DirectoryReader.open(indexedDirectory);
        searcher = new IndexSearcher(reader);
    }

    public void search(String query) throws Exception {
        Query q = new QueryParser("content", new MyAnalyzer()).parse(query);
        TopDocs docs = searcher.search(q, 10);

        System.out.println("For query " + query + " found " + docs.scoreDocs.length + " appearances:");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = searcher.doc(docId);
            System.out.println(document.get("path"));
        }
    }

    @Override
    public void close() throws Exception {
    }
}
