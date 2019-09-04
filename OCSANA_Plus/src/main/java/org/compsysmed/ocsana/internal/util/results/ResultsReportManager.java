/**
 * Manager to generate various user reports for OCSANA results
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

import java.io.*;
import java.util.*;

// Templating engine imports
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import com.mitchellbosecke.pebble.loader.ClasspathLoader;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.sfa.SFABundle;
import org.compsysmed.ocsana.internal.util.sfa.SFAResultsBundle;

/**
 * Manager to generate user reports for OCSANA results
 **/

public class ResultsReportManager {
    private static String REPORT_HTML_TEMPLATE = "ResultsReport.html";
    private static String REPORT_TXT_TEMPLATE = "ResultsReport.txt";

    private final PebbleTemplate reportHTMLTemplate;
    private final PebbleTemplate reportTXTTemplate;

    private ContextBundle contextBundle;
    private ResultsBundle resultsBundle;
    private SFABundle sfaBundle;
    private SFAResultsBundle sfaresultsBundle;
    public ResultsReportManager () {
        // Compile templates
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix("templates/");

        PebbleEngine htmlEngine = new PebbleEngine.Builder().loader(loader).strictVariables(true).build();
        PebbleEngine textEngine = new PebbleEngine.Builder().loader(loader).strictVariables(true).autoEscaping(false).build();
        try {
            reportHTMLTemplate = htmlEngine.getTemplate(REPORT_HTML_TEMPLATE);
            reportTXTTemplate = textEngine.getTemplate(REPORT_TXT_TEMPLATE);
        } catch (PebbleException e) {
            throw new IllegalStateException("Could not load result report templates. Please report the following error to the plugin author: " + e.getMessage());
        }
    }

    public void update (ContextBundle contextBundle,
                        ResultsBundle resultsBundle) {
        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;
    }

    private Map<String, Object> getData () {
        Map<String, Object> data = new HashMap<>();
        data.put("contextBundle", contextBundle);
        data.put("resultsBundle", resultsBundle);

        return data;
    }

    public String reportAsHTML () {
        Writer writer = new StringWriter();
        try {
            reportHTMLTemplate.evaluate(writer, getData());
        }  catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not produce HTML report. Please report the following error to the plugin author: " + e.getMessage());
        }

        return writer.toString();
    }

    public String reportAsText () {
        Writer writer = new StringWriter();
        try {
            reportTXTTemplate.evaluate(writer, getData());
        }  catch (PebbleException|IOException e) {
            throw new IllegalStateException("Could not produce text report. Please report the following error to the plugin author: " + e.getMessage());
        }

        return writer.toString();
    }

	public void update2(SFABundle sfaBundle, SFAResultsBundle sfaresultsBundle) {
		Objects.requireNonNull(sfaBundle, "sfa bundle cannot be null");
        this.sfaBundle = sfaBundle;

        Objects.requireNonNull(sfaresultsBundle, "sfa results cannot be null");
        this.sfaresultsBundle = sfaresultsBundle;
    
		
	}
}
