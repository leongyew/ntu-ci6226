package ntu.ci6226.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

import static ntu.ci6226.index.IndexMode.BY_PUBLICATION;

/**
 * Created by alexto on 2/3/16.
 */
public class Indexer {

    private IndexWriter indexWriter;
    private IndexReader indexReader;
    private DirectoryReader directoryReader;
    private int count = 0;
    private IndexMode mode;

    public Indexer(String indexPath, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        this.indexWriter = new IndexWriter(dir, indexWriterConfig);

        this.directoryReader = DirectoryReader.open(dir);
        this.indexReader = directoryReader;

        this.mode = BY_PUBLICATION;
    }

    public Indexer(String indexPath, Analyzer analyzer, IndexMode indexMode) throws IOException {
        this(indexPath, analyzer);
        this.mode = indexMode;
    }

    public void Index(Publication publication) throws IOException, ParseException {
        if (!"article".equals(publication.getType()) && !"inproceedings".equals(publication.getType())) {
            return;
        }

        switch (this.mode) {
            case BY_PUBLICATION:
                IndexByPublication(publication);
                break;
            case BY_VENUE_YEAR:
                IndexByVenueYear(publication);
                break;
        }
    }

    public void Close() throws IOException {
        if (this.indexWriter != null && this.indexWriter.isOpen())
            this.indexWriter.close();

        if (this.indexReader != null)
            this.indexReader.close();
    }

    public int getCount() {
        return count;
    }

    private void IndexByPublication(Publication publication) throws IOException {

        count++;
        Document doc = new Document();
        doc.add(new StringField("key", publication.getKey(), Field.Store.YES));
        doc.add(new TextField("title", publication.getTitle(), Field.Store.NO));
        doc.add(new IntField("year", publication.getYear(), Field.Store.NO));
        doc.add(new StringField("venue", publication.getVenue(), Field.Store.NO));
        doc.add(new StringField("type", publication.getType(), Field.Store.NO));
        Person[] authors = publication.getAuthors();
        for (Person author : authors) {
            doc.add(new TextField("author", author.getName(), Field.Store.NO));
        }
        this.indexWriter.addDocument(doc);
    }

    private void IndexByVenueYear(Publication publication) throws IOException, ParseException {
        count++;
        this.indexReader = DirectoryReader.openIfChanged(directoryReader);
        IndexSearcher indexSearcher = new IndexSearcher(this.indexReader);

        String keywords = String.format("venue:{} year:{}", publication);
        QueryParser parser = new QueryParser("content", new PorterStemmerStandardAnalyzer());
        Query query = parser.parse(keywords);
        TopDocs result = indexSearcher.search(query, 5);
        ScoreDoc[] hit = result.scoreDocs;
    }
}
