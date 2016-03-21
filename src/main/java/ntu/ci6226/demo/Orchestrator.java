package ntu.ci6226.demo;

import ntu.ci6226.index.PorterStemmerStandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by alex on 16/3/16.
 */
public class Orchestrator {
    public static void main(String[] args) throws IOException, ParseException {
        Indexer indexer = new Indexer("demo", new PorterStemmerStandardAnalyzer());
        ArrayList<Book> books = new ArrayList<Book>();
        String[] allFields = new String[]{"title", "content", "author", "year", "id"};

        books.add(new Book()
                .setId(1)
                .setTitle("Bowl: Vegetarian Recipes for Ramen, Pho, Bibimbap, Dumplings, and Other One-Dish Meals")
                .setContent("A restorative bowl of vegetarian ramen sent Lukas Volger on a quest to capture the full flavor of all the one-bowl meals that are the rage today—but in vegetarian form. With the bowl as organizer, the possibilities for improvisational meals full of seasonal produce and herbs are nearly endless")
                .setAuthor("Lukas Volger")
                .setYear(2011));

        books.add(new Book()
                .setId(2)
                .setTitle("Veggie Burgers Every Which Way: Fresh, Flavorful and Healthy Vegan and Vegetarian Burgers-Plus Toppings, Sides, Buns and More")
                .setContent("Whether you already subsist on veggie burgers, enjoy them occasionally, or ardently wish there was an alternative to the rubbery, over-processed frozen burgers sold in cardboard boxes, Veggie Burgers Every Which Way is the book for you–one you will want to cook from over and over again.")
                .setAuthor("Julia Wolfgang")
                .setYear(2012));

        books.add(new Book()
                .setId(3)
                .setTitle("Savor: Rustic Recipes Inspired by Forest, Field, and Farm")
                .setContent("Experiencing the bounty of nature is one of life’s great joys: foraging, gardening, fishing, and, ultimately, cooking casual meals, whether indoors or outside over an open fire. From her home in the mountains of Aspen, Colorado, Ilona Oppenheim devises recipes that make the best use of the abundance of her surroundings: foraged mushrooms and berries, fresh-caught fish, pasture-raised dairy, and home-milled flours.")
                .setAuthor("IIona Oppenheim")
                .setYear(2013));


        books.add(new Book()
                .setId(4)
                .setTitle("How to Make Everything Taste Better")
                .setContent("Like her mentor Julia Child, Sara Moulton believes that with the right guidance anyone can become a good cook. After all, great home cooking is in the details. Having the proper tools, understanding temperature and cooking time, and knowing how to balance flavors are simple skills that elevate everyday meals.")
                .setAuthor("Sara Volger")
                .setYear(2015));

        books.add(new Book()
                .setId(5)
                .setTitle("The Negative Calorie Diet: Lose Up to 10 Pounds in 10 Days with 10 All You Can Eat Foods")
                .setContent("The #1 New York Times bestselling author, chef, and healthy living expert Rocco DiSpirito returns with a revolutionary whole foods-based diet plan and cookbook featuring more than seventy-five delicious recipes and 100 color photographs.")
                .setAuthor("Rocco DiSprito")
                .setYear(2016));

        for (Book book : books) {
            indexer.Index(book);
        }

        indexer.Close();

        String keywords = "2013";

        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get("demo")));
        IndexReader indexReader = directoryReader;
        IndexSearcher searcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new PorterStemmerStandardAnalyzer();

        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(allFields, analyzer);


        Query multiFieldsQuery = multiFieldQueryParser.parse(keywords);
        TopDocs results = searcher.search(multiFieldsQuery, 5);

        ScoreDoc[] hits = results.scoreDocs;


        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            System.out.println(doc.get("id"));
        }

        System.out.println("------------------------------------------");

        Query query = new TermQuery(new Term("author","rocco DiSprito"));
        results = searcher.search(query, 5);

        hits = results.scoreDocs;

        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            System.out.println(doc.get("id"));
        }
        /*
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(keywords);
        TopDocs result = searcher.search(query, 5);
        ScoreDoc[] hit = result.scoreDocs;

        for (int i = 0; i < hit.length; i++) {
            Document doc = searcher.doc(hit[i].doc);
            System.out.println(doc.get("id"));
            //doc.add(new StringField("author", "Alex To", Field.Store.NO));
            //indexer.updateDocument(new Term("id", "5"), doc);
            //indexer.commit();
        }


        DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(directoryReader);
        if (newDirectoryReader != null)
        {
            directoryReader.close();
            indexReader = newDirectoryReader;
        }

        searcher = new IndexSearcher(indexReader);
        keywords = "author:alex";
        query = parser.parse(keywords);
        result = searcher.search(query, 5);
        hits = result.scoreDocs;

        System.out.println("------------------------------------------");

        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            System.out.println(doc.get("id"));
        }

        */
    }

}
