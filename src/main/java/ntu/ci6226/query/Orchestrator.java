package ntu.ci6226.query;

import ntu.ci6226.index.PorterStemmerStandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by alex on 16/3/16.
 */
public class Orchestrator {
    public static void main(String[] args) throws IOException {
        String index = "index4";
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new PorterStemmerStandardAnalyzer();

    }
}
