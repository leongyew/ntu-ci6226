package ntu.ci6226.query;

import ntu.ci6226.index.PorterStemmerStandardAnalyzer;
import ntu.ci6226.models.PublicationSearchHit;
import ntu.ci6226.models.YearVenueSearchHit;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by alex on 26/3/16.
 */

@Service
public class SearcherImpl implements Searcher {

    private static String publicationIndex = "/home/alex/Projects/NTU/CI6226/Project1/Index4";
    private static String venueYearIndex = "/home/alex/Projects/NTU/CI6226/Project1/Index5";
    private static Integer maxSearchResults = 100;
    private static String[] allFields = new String[]{"title", "venue", "author", "year"};

    public ArrayList<PublicationSearchHit> searchByPublication(String query) throws IOException, ParseException {
        Directory dir = FSDirectory.open(Paths.get(SearcherImpl.publicationIndex));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(allFields, new PorterStemmerStandardAnalyzer());
        Query q = multiFieldQueryParser.parse(query);
        TopDocs docs = indexSearcher.search(q, SearcherImpl.maxSearchResults);
        ArrayList<PublicationSearchHit> publications = new ArrayList<PublicationSearchHit>();
        for (ScoreDoc d : docs.scoreDocs) {
            Document doc = indexSearcher.doc(d.doc);
            PublicationSearchHit p = (new PublicationSearchHit())
                    .setKey(doc.get("key"))
                    .setTitle(doc.get("title"))
                    .setType(doc.get("type"))
                    .setVenue(doc.get("venue"))
                    .setYear(Integer.parseInt(doc.get("year")))
                    .setAuthors(doc.getValues("author"))
                    .setScore(d.score);

            publications.add(p);
        }
        indexReader.close();
        return publications;
    }

    public ArrayList<YearVenueSearchHit> searchByVenueYear(String query) throws IOException, ParseException {

        Directory dir = FSDirectory.open(Paths.get(SearcherImpl.venueYearIndex));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser queryParser = new QueryParser("title", new PorterStemmerStandardAnalyzer());
        Query q = queryParser.parse(query);
        TopDocs docs = indexSearcher.search(q, SearcherImpl.maxSearchResults);
        ArrayList<YearVenueSearchHit> yearVenueSearchHits = new ArrayList<YearVenueSearchHit>();
        for (ScoreDoc d : docs.scoreDocs) {
            Document doc = indexSearcher.doc(d.doc);
            YearVenueSearchHit p = (new YearVenueSearchHit())
                    .setYear(Integer.parseInt(doc.get("year")))
                    .setVenue(doc.get("venue"))
                    .setTitles(doc.getValues("title"))
                    .setScore(d.score);
            yearVenueSearchHits.add(p);
        }
        indexReader.close();
        return yearVenueSearchHits;
    }
}
