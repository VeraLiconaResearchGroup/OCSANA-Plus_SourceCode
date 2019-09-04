/**
 * Proteins database (from UniProt)
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science.uniprot;

// Java imports
import java.io.*;
import java.util.*;
// JSON imports
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

/**
 * Singleton class representing the UniProt database
 **/
public class ProteinDatabase {
    private static final String UNIPROT_PATH = "/uniprot/proteins.json";
    private static final ProteinDatabase internalDB = new ProteinDatabase();

    private final Map<String, Protein> proteinByUniProtID = new HashMap<>();
    private final Map<String, String> primaryIDBySecondaryID = new HashMap<>();

    private final Map<String, Isoform> isoformByRefSeqID = new HashMap<>();

    private ProteinDatabase () {
        JSONObject proteinsJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(UNIPROT_PATH)) {
            proteinsJSON = new JSONObject(new JSONTokener(jsonFileStream));
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not find or read UniProt JSON file");
        }

        // Process protein records
        Iterator<String> uniProtKeys = proteinsJSON.keys();
        while (uniProtKeys.hasNext()) {
            String uniProtID = uniProtKeys.next();
            JSONObject proteinData = proteinsJSON.getJSONObject(uniProtID);

            Collection<String> uniProtIDs = new HashSet<>();
            JSONArray uniProtIDsJSON = proteinData.getJSONArray("upids");
            for (int i = 0; i < uniProtIDsJSON.length(); i++) {
                String secondaryID = uniProtIDsJSON.getString(i);
                uniProtIDs.add(secondaryID);
                primaryIDBySecondaryID.put(secondaryID, uniProtID);
            }

            Collection<String> geneNames = new HashSet<>();
            JSONArray geneNamesJSON = proteinData.getJSONArray("geneNames");
            for (int i = 0; i < geneNamesJSON.length(); i++) {
                String geneName = geneNamesJSON.getString(i);
                geneNames.add(geneName);
                primaryIDBySecondaryID.put(geneName, uniProtID);
            }

            String name = proteinData.getString("name");
            String function = proteinData.getString("function");

            Protein protein = new Protein(uniProtID, uniProtIDs, name, geneNames, function);
            proteinByUniProtID.put(uniProtID, protein);


            JSONObject isoformJSON = proteinData.getJSONObject("isoforms");
            Iterator<String> isoformKeys = isoformJSON.keys();
            while (isoformKeys.hasNext()) {
                String isoformKey = isoformKeys.next();
                Integer isoformNumber = (isoformKey.equals("null")) ? null : Integer.parseInt(isoformKey);

                JSONArray refSeqIDJSON = isoformJSON.getJSONArray(isoformKey);
                Collection<String> refSeqIDs = new HashSet<>();
                for (int i = 0; i < refSeqIDJSON.length(); i++) {
                    refSeqIDs.add(refSeqIDJSON.getString(i));
                }

                Isoform isoform = new Isoform(protein, isoformNumber, refSeqIDs);

                protein.addIsoform(isoform);
                for (String refSeqID: refSeqIDs) {
                    isoformByRefSeqID.put(refSeqID, isoform);
                    primaryIDBySecondaryID.put(refSeqID, uniProtID);
                }
            }
        }
    }

    /**
     * Retrieve the singleton database instance
     **/
    public static ProteinDatabase getDB () {
        return internalDB;
    }

    /**
     * Return all proteins in the database
     **/
    public Collection<Protein> getAllProteins () {
        return proteinByUniProtID.values();
    }

    /**
     * Get the protein with a particular UniProt, Ensembl, or RefSeq ID
     *
     * @param proteinID  the ID
     * @return the protein, if found, or null if not
     **/
    public Protein getProtein (String proteinID) {
        String cleanedID = proteinID.trim().toUpperCase();

        if (primaryIDBySecondaryID.containsKey(cleanedID)) {
            String primaryID = primaryIDBySecondaryID.get(cleanedID);
            Protein protein = proteinByUniProtID.get(primaryID);

            assert (protein.getGeneNames().contains(primaryID) || protein.getAllUniProtIDs().contains(primaryID));

            return protein;
        }

        if (proteinByUniProtID.containsKey(cleanedID)) {
            Protein protein = proteinByUniProtID.get(cleanedID);
            return protein;
        }

        return null;
    }

    /**
     * Get the isoform with a particular RefSeq ID
     *
     * @param isoformID  the ID
     * @return the isoform, if found, or null if not
     **/
    public Isoform getIsoform (String isoformID) {
        return isoformByRefSeqID.get(isoformID);
    }

    /**
     * Return true if the given ID matches a known isoform
     **/
    public boolean isKnownIsoform (String isoformID) {
        return isoformByRefSeqID.containsKey(isoformID);
    }

    /**
     * Return true if the given ID matches a known protein
     **/
    public boolean isKnownProtein (String proteinID) {
        return proteinByUniProtID.containsKey(proteinID);
    }
}
