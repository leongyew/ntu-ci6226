package ntu.ci6226.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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
    private boolean recordExist;
    private String indexPath;

    public Indexer(String indexPath, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        this.indexWriter = new IndexWriter(dir, indexWriterConfig);
        this.recordExist = false;
        this.indexPath = indexPath;
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
        doc.add(new TextField("title", publication.getTitle(), Field.Store.YES));
        doc.add(new IntField("year", publication.getYear(), Field.Store.YES));
        doc.add(new StringField("venue", publication.getVenue(), Field.Store.YES));
        doc.add(new StringField("type", publication.getType(), Field.Store.YES));
        Person[] authors = publication.getAuthors();
        for (Person author : authors) {
            doc.add(new TextField("author", author.getName(), Field.Store.YES));
        }
        this.indexWriter.addDocument(doc);
    }

    private void IndexByVenueYear(Publication publication) throws IOException, ParseException {
        count++;
        Document doc;
        ScoreDoc[] hits = null;
        boolean newDoc = true;
        String id = String.format("%1s_%2s", publication.getYear(), publication.getVenue());

        if (recordExist) {
            if (this.indexReader == null) {
                directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(this.indexPath)));
                this.indexReader = directoryReader;
            } else {
                DirectoryReader newReader = DirectoryReader.openIfChanged(directoryReader);
                if (newReader != null) {
                    this.indexReader = newReader;
                }
            }
            IndexSearcher indexSearcher = new IndexSearcher(this.indexReader);

            Query query = new TermQuery(new Term("id", id));
            TopDocs result = indexSearcher.search(query, 5);
            hits = result.scoreDocs;
            if (hits != null && hits.length > 0) {
                doc = indexSearcher.doc(hits[0].doc);
                newDoc = false;
                System.out.println("Updating existing entry for " + id);
            } else {
                doc = new Document();
                doc.add(new StringField("id", id, Field.Store.YES));
                System.out.println("Adding new entry for " + id);
            }
        } else {
            doc = new Document();
            doc.add(new StringField("id", id, Field.Store.YES));
            System.out.println("Adding new entry for " + id);
        }


        doc.add(new StringField("key", publication.getKey(), Field.Store.YES));
        doc.add(new TextField("title", publication.getTitle(), Field.Store.YES));
        doc.add(new StringField("type", publication.getType(), Field.Store.YES));
        Person[] authors = publication.getAuthors();
        for (Person author : authors) {
            doc.add(new TextField("author", author.getName(), Field.Store.YES));
        }

        if (newDoc)
            this.indexWriter.addDocument(doc);
        else
            this.indexWriter.updateDocument(new Term("id", id), doc);

        this.recordExist = true;
        this.indexWriter.commit();
    }
}
