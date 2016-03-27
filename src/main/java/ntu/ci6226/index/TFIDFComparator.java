package ntu.ci6226.index;

import org.apache.lucene.misc.TermStats;

import java.util.Comparator;

/**
 * Created by alexto on 27/3/16.
 */
public class TFIDFComparator implements Comparator<TermStats> {
    private int numDocs;

    public TFIDFComparator(int numDocs) {
        this.numDocs = numDocs;
    }

    public int compare(TermStats a, TermStats b) {
        double tfa = Math.sqrt(a.totalTermFreq);
        double tfb = Math.sqrt(b.totalTermFreq);
        double idfa = 1 + Math.log(this.numDocs / (a.docFreq + 1));
        double idfb = 1 + Math.log(this.numDocs / (b.docFreq + 1));

        int res = Double.compare(tfa * idfa, tfb * idfb);
        if (res == 0) {
            res = a.field.compareTo(b.field);
            if (res == 0) {
                res = a.termtext.compareTo(b.termtext);
            }
        }

        return res;
    }
}
