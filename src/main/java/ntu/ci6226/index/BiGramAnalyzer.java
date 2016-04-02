package ntu.ci6226.index;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class BiGramAnalyzer extends StopwordAnalyzerBase {
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;

    public BiGramAnalyzer(CharArraySet stopWords) {
        super(stopWords);
        this.maxTokenLength = 255;
    }

    public BiGramAnalyzer() {
        this(STOP_WORDS_SET);
    }

    public BiGramAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final Object src;
        if(this.getVersion().onOrAfter(Version.LUCENE_4_7_0)) {
            StandardTokenizer tok = new StandardTokenizer();
            tok.setMaxTokenLength(this.maxTokenLength);
            src = tok;
        } else {
            StandardTokenizer40 tok1 = new StandardTokenizer40();
            tok1.setMaxTokenLength(this.maxTokenLength);
            src = tok1;
        }

        StandardFilter tok2 = new StandardFilter((TokenStream)src);
        LowerCaseFilter tok3 = new LowerCaseFilter(tok2);
        StopFilter tok4 = new StopFilter(tok3, this.stopwords);
        PorterStemFilter tok5 = new PorterStemFilter(tok4);
        ShingleFilter tok6 = new ShingleFilter(tok5, 2, 3);
        tok6.setOutputUnigrams(false);
        Pattern pattern = Pattern.compile(".*_.*");
        final PatternReplaceFilter tok7 = new PatternReplaceFilter(tok6, pattern, "", true);
        return new TokenStreamComponents((Tokenizer)src, tok7) {
            protected void setReader(Reader reader) {
                int m = BiGramAnalyzer.this.maxTokenLength;
                if(src instanceof StandardTokenizer) {
                    ((StandardTokenizer)src).setMaxTokenLength(m);
                } else {
                    ((StandardTokenizer40)src).setMaxTokenLength(m);
                }

                super.setReader(reader);
            }
        };
    }

    static {
        STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
}
