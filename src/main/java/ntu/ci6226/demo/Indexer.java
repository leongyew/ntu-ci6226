package ntu.ci6226.demo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by alexto on 2/3/16.
 */
public class Indexer {

    private IndexWriter indexWriter;

    public Indexer(String indexPath, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        indexWriter = new IndexWriter(dir, indexWriterConfig);
    }

    public void Index(Book book) throws IOException {
        Document doc = new Document();
        doc.add(new IntField("id", book.getId(), Field.Store.YES));
        doc.add(new TextField("title", book.getTitle(), Field.Store.NO));
        doc.add(new IntField("year", book.getYear(), Field.Store.NO));
        doc.add(new StringField("author", book.getAuthor(), Field.Store.NO));
        doc.add(new TextField("content", book.getContent(), Field.Store.NO));
        indexWriter.addDocument(doc);
    }

    public void Close() throws IOException {
        if (indexWriter != null && indexWriter.isOpen())
            indexWriter.close();
    }

    public void updateDocument(Term term, Document doc) throws IOException {
        indexWriter.updateDocument(term, doc);
    }

    public void commit() throws IOException {
        indexWriter.commit();
    }
}
