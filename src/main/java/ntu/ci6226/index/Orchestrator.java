package ntu.ci6226.index;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by alexto on 2/3/16.
 */
public class Orchestrator {

    private static final String input = "dblp.xml";
    private static final String newLine = System.getProperty("line.separator");

    public static void main(String[] args) throws Exception {
        indexCase1();
        indexCase2();
        indexCase3();
        indexCase4();
        //indexByYearVenue();
        //indexByYear();
        indexByYearBiGram();
        //showTopTopics();
        showTopTopicsBiGram();
    }


    private static void indexCase1() throws IOException {

        //Case 1. Index with no stemming, no stop words, case sensitive.

        File file = new File("case1_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case1_report.txt"), "utf-8"));
        writer.write("Index without stemming, no stop words and case sensitive." + newLine);
        System.out.println("1. Index  without stemming, no stop words and case sensitive.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index1", new CaseSensitiveStandardAnalyzer(CharArraySet.EMPTY_SET));
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index1", writer);
        writer.close();
        System.out.println("\tReport written to case1_report.txt");
    }

    private static void indexCase2() throws IOException {

        //Case 2. Index with no stemming, no stop words, case insensitive.

        File file = new File("case2_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case2_report.txt"), "utf-8"));
        writer.write("Index without stemming, no stop words and case insensitive." + newLine);
        System.out.println("2. Index without stemming, no stop words and case insensitive.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index2", new StandardAnalyzer(CharArraySet.EMPTY_SET));
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index2", writer);

        writer.close();
        System.out.println("\tReport written to case2_report.txt");
    }

    private static void indexCase3() throws IOException {

        //Case 3. Index with no stemming, using stop words. When using stop words, we also lowercase all terms.

        File file = new File("case3_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case3_report.txt"), "utf-8"));
        writer.write("Index without stemming, using default set of stop words." + newLine);
        System.out.println("3. Index without stemming, using default set of stop words.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index3", new StandardAnalyzer());
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index3", writer);

        writer.close();
        System.out.println("\tReport written to case3_report.txt");
    }

    private static void indexCase4() throws IOException {

        //Case 4. Index with Porter stemmer, using stop words.

        File file = new File("case4_report.txt");
        if (file.exists())
            return;


        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case4_report.txt"), "utf-8"));
        writer.write("Index with Porter stemmer, using default set of stop words." + newLine);
        System.out.println("4. Index with Porter stemmer, using default set of stop words.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index4", new PorterStemmerStandardAnalyzer());
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        writer.write(newLine);

        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index4", writer);
        writer.close();
        System.out.println("\tReport written to case4_report.txt");
    }

    /*private static void indexCase5() throws IOException {

        File file = new File("case5_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case5_report.txt"), "utf-8"));
        writer.write("Index by venue and year." + newLine);
        System.out.println("Index by venue and year.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index5", new PorterStemmerStandardAnalyzer(), IndexMode.BY_VENUE_YEAR);
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index5", writer);
        writer.close();
        System.out.println("\tReport written to case5_report.txt");
    }*/

    private static void indexByYearVenue() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("Index4"));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Directory dir2 = FSDirectory.open(Paths.get("Index5"));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new PorterStemmerStandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(dir2, indexWriterConfig);

        Terms yearTerms = MultiFields.getTerms(indexReader, "year");
        Terms venueTerms = MultiFields.getTerms(indexReader, "venue");
        TermsEnum iterator1 = yearTerms.iterator();
        BytesRef yearBytesRef;
        int count = 0;
        while ((yearBytesRef = iterator1.next()) != null) {
            TermsEnum iterator2 = venueTerms.iterator();
            BytesRef venueBytesRef;
            while ((venueBytesRef = iterator2.next()) != null) {
                String venue = venueBytesRef.utf8ToString();
                Integer year = NumericUtils.prefixCodedToInt(yearBytesRef);
                TermQuery venueQuery = new TermQuery(new Term("venue", venue));
                Query yearQuery = NumericRangeQuery.newIntRange("year", year, year, true, true);
                BooleanQuery.Builder builder = new BooleanQuery.Builder();
                builder.add(venueQuery, BooleanClause.Occur.MUST);
                builder.add(yearQuery, BooleanClause.Occur.MUST);
                BooleanQuery query = builder.build();
                TopDocs hits = indexSearcher.search(query, 100000);
                if (hits.totalHits > 0) {
                    Document doc = new Document();
                    doc.add(new StringField("venue", venue, Field.Store.YES));
                    doc.add(new IntField("year", year, Field.Store.YES));
                    for (ScoreDoc scoreDoc : hits.scoreDocs) {
                        Document d = indexSearcher.doc(scoreDoc.doc);
                        doc.add(new StringField("key", d.get("key"), Field.Store.YES));
                        doc.add(new TextField("title", d.get("title"), Field.Store.YES));
                        doc.add(new StringField("type", d.get("type"), Field.Store.YES));
                        String[] authors = d.getValues("author");
                        for (String author : authors) {
                            doc.add(new TextField("author", author, Field.Store.YES));
                        }
                    }
                    count++;
                    indexWriter.addDocument(doc);
                }
            }
        }
        System.out.println("Total number of documents: " + count);
        indexWriter.close();
        indexReader.close();
    }

    private static void writeTerms(String index, Writer writer) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(index));
        IndexReader indexReader = DirectoryReader.open(dir);
        Terms terms = MultiFields.getTerms(indexReader, "title");
        TermsEnum iterator = terms.iterator();
        BytesRef bytesRef = null;
        int count = 0;
        writer.write("Terms in Title field: " + newLine);
        writer.write(newLine);
        while ((bytesRef = iterator.next()) != null) {
            writer.write(bytesRef.utf8ToString() + newLine);
            count++;
        }
        writer.write(newLine);
        writer.write("Total number of terms: " + count);
        writer.write(newLine);
        writer.write("Total number of documents: " + indexReader.numDocs());
        System.out.println("\tTotal number of documents: " + indexReader.numDocs());
        System.out.println("\tTotal number of terms in Title field: " + count);
    }


    private static void indexByYear() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("Index4"));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);


        for (int i = 2000; i < 2017; i++) {
            Directory dir2 = FSDirectory.open(Paths.get("Index_" + i));
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new PorterStemmerStandardAnalyzer());
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(dir2, indexWriterConfig);


            Query yearQuery = NumericRangeQuery.newIntRange("year", i, i, true, true);
            TopDocs topDocs = indexSearcher.search(yearQuery, 100000);
            if (topDocs.totalHits > 0) {
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document doc = indexSearcher.doc(scoreDoc.doc);
                    indexWriter.addDocument(doc);
                }
            }

            indexWriter.close();
        }

        indexReader.close();
    }

    private static void indexByYearBiGram() throws IOException {
        Directory dir = FSDirectory.open(Paths.get("Index4"));
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);


        for (int i = 2000; i < 2017; i++) {
            Directory dir2 = FSDirectory.open(Paths.get("BiGram_Index_" + i));
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new BiGramAnalyzer());
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(dir2, indexWriterConfig);


            Query yearQuery = NumericRangeQuery.newIntRange("year", i, i, true, true);
            TopDocs topDocs = indexSearcher.search(yearQuery, 100000);
            if (topDocs.totalHits > 0) {
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document doc = indexSearcher.doc(scoreDoc.doc);
                    indexWriter.addDocument(doc);
                }
            }

            indexWriter.close();
        }

        indexReader.close();
    }

    private static void showTopTopics() throws Exception {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("2_1_report.txt"), "utf-8"));

        for (int i = 2000; i < 2017; i++) {
            Directory dir = FSDirectory.open(Paths.get("Index_" + i));
            IndexReader indexReader = DirectoryReader.open(dir);
            writer.write("Year " + i);
            writer.write(newLine);
            TermStats[] highFreqTerms = null;
            highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader, 10, "title", new TFIDFComparator(indexReader.numDocs()));
            for (TermStats ts : highFreqTerms) {
                double tf = Math.sqrt(ts.totalTermFreq);
                double idf = 1 + Math.log(indexReader.numDocs() / ts.docFreq);
                writer.write("\t" + ts.termtext.utf8ToString() + " | docFreq " + ts.docFreq + " | termFreq " + ts.totalTermFreq + " | tfidf " + tf * idf);
                writer.write(newLine);
            }
        }

        writer.close();
    }

    private static void showTopTopicsBiGram() throws Exception {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("2_1_report.txt"), "utf-8"));

        for (int i = 2000; i < 2017; i++) {
            Directory dir = FSDirectory.open(Paths.get("BiGram_Index_" + i));
            IndexReader indexReader = DirectoryReader.open(dir);
            writer.write("Year " + i);
            writer.write(newLine);
            TermStats[] highFreqTerms = null;
            highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader, 10, "title", new TFIDFComparator(indexReader.numDocs()));
            for (TermStats ts : highFreqTerms) {
                double tf = Math.sqrt(ts.totalTermFreq);
                double idf = 1 + Math.log(indexReader.numDocs() / ts.docFreq);
                writer.write("\t" + ts.termtext.utf8ToString() + " | docFreq " + ts.docFreq + " | termFreq " + ts.totalTermFreq + " | tfidf " + tf * idf);
                writer.write(newLine);
            }
        }

        writer.close();
    }
}
