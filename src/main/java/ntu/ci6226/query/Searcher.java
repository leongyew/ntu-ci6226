package ntu.ci6226.query;

import ntu.ci6226.models.PublicationSearchHit;
import ntu.ci6226.models.YearVenueSearchHit;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alex on 26/3/16.
 */
public interface Searcher {
    ArrayList<PublicationSearchHit> searchByPublication(String query) throws IOException, ParseException;
    ArrayList<YearVenueSearchHit> searchByVenueYear(String query) throws IOException, ParseException;
    ArrayList<YearVenueSearchHit> searchByVenueYear2(String query) throws IOException, ParseException;
}
