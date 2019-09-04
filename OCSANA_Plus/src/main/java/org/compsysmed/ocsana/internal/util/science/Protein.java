/**
 * Class representing a protein molecule
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
drddrc * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science;

// Java imports
import java.util.*;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Class representing a protein molecule
 **/
public class Protein {
    private static final String UNIPROT_URL_BASE = "http://www.uniprot.org/uniprot/";
    private static final String DRUGBANK_BIODB_URL_BASE = "http://www.drugbank.ca/biodb/polypeptides/";

    private final String uniProtID;
    private final Collection<String> allUniProtIDs;
    private final String name;
    private final Collection<String> geneNames;
    private final String functionDescription;

    private Collection<Isoform> isoforms = new HashSet<>();

    /**
     * Constructor
     *
     * @param uniProtID  the primary UniProt ID of the protein
     * @param allUniProtIDs  the collection of UniProt IDs of the protein
     * @param name  the human-readable name of the protein
     * @param geneNames  the ENSEMBL IDs of the genes associated to the protein
     * @param functionDescription  string description of the function
     * of the protein (can be null, in which case an empty string is
     * stored)
     **/
    public Protein (String uniProtID,
                    Collection<String> allUniProtIDs,
                    String name,
                    Collection<String> geneNames,
                    String functionDescription) {
    	Objects.requireNonNull(uniProtID, "Protein UniProt ID cannot be null");
        this.uniProtID = uniProtID;

        Objects.requireNonNull(allUniProtIDs, "Protein UniProt ID collection cannot be null");
        this.allUniProtIDs = allUniProtIDs;

        if (!this.allUniProtIDs.contains(uniProtID)) {
            this.allUniProtIDs.add(uniProtID);
        }

        Objects.requireNonNull(name, "Protein name cannot be null");
        this.name = name;

        Objects.requireNonNull(geneNames, "List of gene names cannot be null");
        this.geneNames = geneNames;

        Objects.requireNonNull(isoforms, "Collection of isoforms cannot be null");
        this.isoforms = isoforms;

        if (functionDescription == null) {
            functionDescription = "";
        }
        this.functionDescription = functionDescription;
    }

    /**
     * Get the UniProt ID of this protein
     **/
    public String getUniProtID () {
        return uniProtID;
    }

    /**
     * Get the name of this protein
     **/
    public String getName () {
        return name;
    }

    /**
     * Get the gene names associated to this protein
     **/
    public Collection<String> getGeneNames () {
        return geneNames;
    }

    /**
     * Get all the UniProt IDs associated with this protein
     **/
    public Collection<String> getAllUniProtIDs () {
        return allUniProtIDs;
    }

    /**
     * Add an isoform of this protein
     *
     * @param isoform  the new isoform
     **/
    public void addIsoform (Isoform isoform) {
    	Objects.requireNonNull(isoform, "Cannot add null isoform");

        if (!isoform.getProtein().equals(this)) {
            throw new IllegalArgumentException("Cannot add isoform for different protein");
        }

        isoforms.add(isoform);
    }

    /**
     * Get all the isoforms associated with this protein
     **/
    public Collection<Isoform> getIsoforms () {
        return isoforms;
    }

    /**
     * Get a description of the function of this protein (may be
     * empty)
     **/
    public String getFunctionDescription () {
        return functionDescription;
    }

    /**
     * Return a string representation of this protein
     **/
    public String toString () {
        return String.format("%s (%s)", name, uniProtID);
    }

    /**
     * Return the URL for the online UniProt entry of this protein
     **/
    public URL getUniProtURL () {
        String uniProtURL = UNIPROT_URL_BASE + uniProtID;
        try {
            return new URL(uniProtURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("Protein URL %s is malformed", uniProtURL));
        }
    }

    /**
     * Return the URL for the online DrugBank entry of this protein
     **/
    public URL getDrugBankBioDBURL () {
        String drugBankURL = DRUGBANK_BIODB_URL_BASE + uniProtID;
        try {
            return new URL(drugBankURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format("Protein URL %s is malformed", drugBankURL));
        }
    }
}
