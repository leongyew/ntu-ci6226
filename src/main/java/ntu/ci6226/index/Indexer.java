package ntu.ci6226.index;

import ntu.ci6226.models.Publication;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
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
        this.mode = BY_PUBLICATION;
    }

    public void Index(Publication publication) throws IOException, ParseException {
        if (!"article".equals(publication.getType()) && !"inproceedings".equals(publication.getType())) {
            return;
        }
        IndexByPublication(publication);
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
        doc.add(new TextField("venue", publication.getVenue(), Field.Store.YES));
        doc.add(new TextField("type", publication.getType(), Field.Store.YES));
        String[] authors = publication.getAuthors();
        for (String author : authors) {
            doc.add(new TextField("author", author, Field.Store.YES));
        }
        this.indexWriter.addDocument(doc);
    }

    /*private void IndexByVenueYear(Publication publication) throws IOException, ParseException {
        count++;
        Document doc;
        ScoreDoc[] hits = null;
        boolean newDoc = true;
        String yearvenue = String.format("%1s_%2s", publication.getYear(), publication.getVenue());

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

            Query query = new TermQuery(new Term("yearvenue", yearvenue));
            TopDocs result = indexSearcher.search(query, 5);
            hits = result.scoreDocs;
            if (hits != null && hits.length > 0) {
                doc = indexSearcher.doc(hits[0].doc);
                newDoc = false;
                System.out.println("Updating existing entry for " + yearvenue);
            } else {
                doc = new Document();
                doc.add(new StringField("yearvenue", yearvenue, Field.Store.YES));
                System.out.println("Adding new entry for " + yearvenue);
            }
        } else {
            doc = new Document();
            doc.add(new StringField("yearvenue", yearvenue, Field.Store.YES));
            System.out.println("Adding new entry for " + yearvenue);
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
            this.indexWriter.updateDocument(new Term("yearvenue", yearvenue), doc);

        this.recordExist = true;
        this.indexWriter.commit();
    }*/


}
