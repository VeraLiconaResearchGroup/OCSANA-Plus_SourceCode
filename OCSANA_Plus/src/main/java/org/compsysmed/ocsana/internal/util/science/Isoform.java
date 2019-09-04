/**
 * Class representing an isoform of a protein

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
 * Class representing an isoform of a protein
 **/
public class Isoform {
    private static final String NCBI_URL_BASE = "http://www.ncbi.nlm.nih.gov/protein/";

    private final Protein protein;
    private final Integer isoformNumber;
    private final Collection<String> refSeqIDs;

    /**
     * Constructor
     *
     * @param protein  the underlying protein of this isoform
     * @param isoformNumber  the index of this isoform on UniProt (can be null)
     * @param refSeqIDs  the RefSeq IDs of this isoform (must be nonempty)
     **/
    public Isoform (Protein protein,
                    Integer isoformNumber,
                    Collection<String> refSeqIDs) {
    	Objects.requireNonNull(protein, "Protein cannot be null");
        this.protein = protein;

        if (isoformNumber != null && isoformNumber < 1) {
            throw new IllegalArgumentException("Isoform number must be positive if non-null");
        }
        this.isoformNumber = isoformNumber;

        if (refSeqIDs == null || refSeqIDs.isEmpty()) {
            throw new IllegalArgumentException("RefSeq ID collection cannot be null or empty");
        }
        this.refSeqIDs = refSeqIDs;
    }

    /**
     * Get the protein of which this is an isoform
     **/
    public Protein getProtein () {
        return protein;
    }

    /**
     * Get the index of this isoform for its protein (can be null)
     **/
    public Integer getIndex () {
        return isoformNumber;
    }

    /**
     * Get the RefSeq IDs of this isoform (guaranteed non-empty)
     **/
    public Collection<String> getRefSeqIDs () {
        return refSeqIDs;
    }

    /**
     * Get one RefSeq ID of this isoform
     * <p>
     * NOTE: no guarantees are made about <em>which</em> ID is returned.
     **/
    public String getRefSeqID () {
        return refSeqIDs.stream().findFirst().get();
    }

    /**
     * Return the URLs for the NCBI entries of this isoform
     **/
    public Collection<URL> getNCBIURL () {
        Collection<URL> urls = new ArrayList<>();
        for (String refSeqID: refSeqIDs) {
            String url = NCBI_URL_BASE + refSeqID;
            try {
                urls.add(new URL(url));
            } catch (MalformedURLException e) {
                throw new IllegalStateException(String.format("NCBI URL %s is malformed", url));
            }
        }
        return urls;
    }

    /**
     * Get the name of this isoform
     **/
    public String getName () {
        return String.format("%s isoform %d", protein.getName(), isoformNumber);
    }

    /**
     * Return a string representation of this isoform
     **/
    public String toString () {
        return String.format("%s (%s)", getName(), refSeqIDs);
    }
}
