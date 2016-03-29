package ntu.ci6226.query;

import ntu.ci6226.index.PorterStemmerStandardAnalyzer;
import ntu.ci6226.models.PublicationSearchHit;
import ntu.ci6226.models.YearVenueSearchHit;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by alex on 26/3/16.
 */

@Service
public class SearcherImpl implements Searcher {


    private static String publicationIndex = "/Volumes/Data/Users/alexto/Projects/NTU/CI6226/IndexByPublication";
    private static String venueYearIndex = "/Volumes/Data/Users/alexto/Projects/NTU/CI6226/IndexByYearVenue";
    private static Integer maxSearchResults = 100;

    private static String[] allFields = new String[]{"title", "venue", "author"};

    public ArrayList<PublicationSearchHit> searchByPublication(String query) throws IOException, ParseException {
        Directory dir = FSDirectory.open(Paths.get(SearcherImpl.publicationIndex));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(allFields,
                new PorterStemmerStandardAnalyzer());
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
        //MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(allFields, new PorterStemmerStandardAnalyzer());
        //Query q = multiFieldQueryParser.parse(query);
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

    public ArrayList<YearVenueSearchHit> searchByVenueYear2(String query) throws IOException, ParseException {

        Integer year = Integer.parseInt(query.substring(query.length() - 4));
        String venue = query.substring(0, query.length() - 4).trim();

        Directory dir = FSDirectory.open(Paths.get(SearcherImpl.venueYearIndex));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        TermQuery venueQuery = new TermQuery(new Term("venue", venue));
        Query yearQuery = NumericRangeQuery.newIntRange("year", year, year, true, true);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(venueQuery, BooleanClause.Occur.MUST);
        builder.add(yearQuery, BooleanClause.Occur.MUST);
        BooleanQuery q = builder.build();
        BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
        TopDocs topDocs = indexSearcher.search(q, SearcherImpl.maxSearchResults);
        ArrayList<YearVenueSearchHit> hits = new ArrayList<YearVenueSearchHit>();
        for (ScoreDoc d : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(d.doc);
            String[] titles = doc.getValues("title");
            QueryParser queryParser = new QueryParser("title", new PorterStemmerStandardAnalyzer());
            Query q2 = queryParser.parse(join(titles));
            TopDocs topDocs2 = indexSearcher.search(q2, 10);
            for (ScoreDoc d2 : topDocs2.scoreDocs) {
                Document doc2 = indexSearcher.doc(d2.doc);
                hits.add((new YearVenueSearchHit()
                        .setVenue(doc2.get("venue"))
                        .setYear(Integer.parseInt(doc2.get("year")))
                        .setTitles(doc2.getValues("title")))
                        .setScore(d2.score));
            }

        }

        return hits;
    }

    private String join(String[] titles) {
        String result = "";
        for (String title : titles) {
            result += " " + title.replace(':', ' ');
        }
        return result;
    }
}
