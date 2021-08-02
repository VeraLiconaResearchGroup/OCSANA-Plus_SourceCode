/**
 * Test cases for the proteins database class
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science.uniprot;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.*;

public class ProteinDatabaseTest {
    @Test
    public void buildDatabaseShouldWork () {
        ProteinDatabase proteinDB = ProteinDatabase.getDB();
        assertEquals("Number of proteins in database", 71785, proteinDB.getAllProteins().size());
    }

    @Test
    public void getProteinByPrimaryUniProtIDShouldWork () {
        ProteinDatabase proteinDB = ProteinDatabase.getDB();
        Protein protein = proteinDB.getProtein("P30613");

        assertEquals("UniProt ID", "P30613", protein.getUniProtID());
        assertEquals("Protein name", "Pyruvate kinase PKLR", protein.getName());
        assertEquals("Number of genes", 3, protein.getGeneNames().size());
    }

    @Test
    public void getProteinBySeconaryUniProtIDShouldWork () {
        ProteinDatabase proteinDB = ProteinDatabase.getDB();
        Protein protein = proteinDB.getProtein("O75758");

        assertEquals("UniProt ID", "P30613", protein.getUniProtID());
        assertEquals("Protein name", "Pyruvate kinase PKLR", protein.getName());
        assertEquals("Number of genes", 3, protein.getGeneNames().size());
    }

    @Test
    public void getProteinByEnsemblIDShouldWork () {
        ProteinDatabase proteinDB = ProteinDatabase.getDB();
        Protein protein = proteinDB.getProtein("PK1");

        assertEquals("UniProt ID", "P30613", protein.getUniProtID());
        assertEquals("Protein name", "Pyruvate kinase PKLR", protein.getName());
        assertEquals("Number of genes", 3, protein.getGeneNames().size());
    }
}
