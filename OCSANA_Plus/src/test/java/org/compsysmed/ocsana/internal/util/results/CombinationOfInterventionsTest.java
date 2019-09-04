/**
 * Test cases for the SignedIntervention class
 *
 * Copyright Vera-Licona Research Group (C) 2016
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.results;

// Java imports
import java.io.*;
import java.util.*;
import java.util.function.Function;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// Cytoscape imports
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

import org.compsysmed.ocsana.internal.helpers.SIFFileConverter;

public class CombinationOfInterventionsTest {
    CyNetwork toyNetwork;
    Set<CyNode> toyNetworkSources;
    Set<CyNode> toyNetworkTargets;

    Function<CyNode, String> toyNetworkNodeNameFunction;
    Function<CyNode, String> toyNetworkNodeBiomoleculeIDFunction;

    @Before
    public void setUp ()
        throws IOException {
        // Set up the test environment here
        // In particular, initialize any shared variables

        // Toy network
        File toyFile = new File(getClass().getResource("/network-data/ToyNetwork.sif").getFile());
        SIFFileConverter toyConverter = new SIFFileConverter(toyFile);
        toyNetwork = toyConverter.getNetwork();

        toyNetworkSources = new HashSet<>();
        List<String> toyNetworkSourceNames = Arrays.asList("I1", "I2");
        for (String sourceName: toyNetworkSourceNames) {
            toyNetworkSources.add(toyConverter.getNode(sourceName));
        }
        assert toyNetworkSources.size() == 2;

        toyNetworkTargets = new HashSet<>();
        List<String> toyNetworkTargetNames = Arrays.asList("O1", "O2");
        for (String targetName: toyNetworkTargetNames) {
            toyNetworkTargets.add(toyConverter.getNode(targetName));
        }
        assert toyNetworkTargets.size() == 2;

        toyNetworkNodeNameFunction = (node -> toyNetwork.getDefaultNodeTable().getRow(node).get(CyNetwork.NAME, String.class));
        toyNetworkNodeBiomoleculeIDFunction = (node -> toyNetwork.getDefaultNodeTable().getRow(node).get(CyNetwork.NAME, String.class));


    }

    @After
    public void tearDown () {
        // Tear down the test environment here
        // In particular, null out any shared variables so the garbage
        // collector can trash them
        toyNetwork = null;
        toyNetworkSources = null;
        toyNetworkTargets = null;
    }

    @Test
    public void constructorShouldWork () {
        CombinationOfInterventions ci = new CombinationOfInterventions(toyNetworkSources, toyNetworkTargets, toyNetworkNodeNameFunction, toyNetworkNodeBiomoleculeIDFunction, 5d,5d,5d);
        assertEquals("CI size", (Integer) 2, ci.size());
    }
}
