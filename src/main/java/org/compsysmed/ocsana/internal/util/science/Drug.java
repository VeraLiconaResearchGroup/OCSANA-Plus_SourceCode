/**
 * Class representing a drug
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science;

// Java imports
import java.util.*;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Class representing a drug
 **/
public class Drug {
    private static final String DB_URL_BASE = "http://www.drugbank.ca/drugs/";

    private final String name;
    private final String drugBankPrimaryID;
    private final Set<String> drugBankIDs;
    private final Set<FDACategory> categories;

    /**
     * Constructor
     *
     * @param name  the drug's name
     * @param drugBankPrimaryID  the primary DrugBank ID of this drug
     * @param drugBankIDs  a list of all DrugBank IDs associated to
     * this drug (can be empty, but {@code drugBankPrimaryID} will be added)
     * @param categories  a list of FDA approval categories of this
     * drug (can be empty)
     **/
    public Drug (String name,
                 String drugBankPrimaryID,
                 Set<String> drugBankIDs,
                 Set<FDACategory> categories) {
    	Objects.requireNonNull(name, "Drug name cannot be null");
        this.name = name;

        Objects.requireNonNull(drugBankPrimaryID, "Drug primary ID cannot be null");
        this.drugBankPrimaryID = drugBankPrimaryID;

        Objects.requireNonNull(drugBankIDs, "DrugBank ID collection cannot be null");
        if (!drugBankIDs.contains(drugBankPrimaryID)) {
            drugBankIDs = new HashSet<>(drugBankIDs);
            drugBankIDs.add(drugBankPrimaryID);
        }
        this.drugBankIDs = drugBankIDs;

        Objects.requireNonNull(categories, "Drug approval category collection cannot be null");
        this.categories = categories;
    }

    /**
     * Return this drug's name
     **/
    public String getName () {
        return name;
    }

    /**
     * Return this drug's primary DrugBank ID
     **/
    public String getDrugBankPrimaryID () {
        return drugBankPrimaryID;
    }

    /**
     * Return all of the DrugBank IDs for this drug
     **/
    public Set<String> getAllDrugBankIDs () {
        return drugBankIDs;
    }

    /**
     * Return all of the FDA approval categories for this drug
     **/
    public Set<FDACategory> getFDACategories () {
        return categories;
    }

    /**
     * Return the URL for the online database entry of this drug
     **/
    public URL getDrugBankURL () {
        String drugBankURL = DB_URL_BASE + drugBankPrimaryID;
        try {
            return new URL(drugBankURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("Drug URL %s is malformed", drugBankURL));
        }
    }
}
