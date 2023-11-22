import com.healthmarketscience.jackcess.Index;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Indexer implements AutoCloseable {
    private final IndexWriter writer;

    public Indexer(String dir) throws IOException {
        Directory indexDir = FSDirectory.open(Paths.get(dir));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new MyAnalyzer());
        writer = new IndexWriter(indexDir, indexWriterConfig);
    }

    public void indexDocuments(String dir) throws IOException {
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                Tika tika = new Tika();
                Document document = new Document();

                document.add(new StringField("path", filePath.toString(), Field.Store.YES));
                try {
                    document.add(new TextField("content", tika.parseToString(filePath), Field.Store.YES));
                } catch (TikaException e) {
                    throw new RuntimeException(e);
                }

                writer.updateDocument(new Term("path", filePath.toString()), document);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void close() throws IOException {
        writer.close();
    }
}
