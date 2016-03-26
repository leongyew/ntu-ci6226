package ntu.ci6226.web.controllers;

import ntu.ci6226.models.PublicationSearchHit;
import ntu.ci6226.models.YearVenueSearchHit;
import ntu.ci6226.query.Searcher;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alexto on 8/3/16.
 */

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private Searcher searcher;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView index(@RequestParam String query, @RequestParam String mode) throws IOException, ParseException {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("query", query);
        mav.addObject("mode", mode);
        int count = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if ("publication".equals(mode)) {
            ArrayList<PublicationSearchHit> publications = searcher.searchByPublication(query);
            count = publications.size();
            mav.addObject("publications", publications);
        } else if ("venueyear".equals(mode)) {
            ArrayList<YearVenueSearchHit> yearVenueSearchHits = searcher.searchByVenueYear(query);
            count = yearVenueSearchHits.size();
            mav.addObject("yearVenueSearchHits", yearVenueSearchHits);
        }
        stopWatch.stop();
        mav.addObject("count", count);
        mav.addObject("elapsed", stopWatch.toString());

        return mav;
    }
}
