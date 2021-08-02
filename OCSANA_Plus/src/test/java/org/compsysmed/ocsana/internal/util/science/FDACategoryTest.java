/**
 * Test cases for the FDA approval category enum
 *
 * Copyright Vera-Licona Research Group (C) 2016
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science;

// JUnit imports
import org.junit.*;
import static org.junit.Assert.*;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.science.FDACategory;

public class FDACategoryTest {
    @Test
    public void retrieveByDescriptionShouldWork () {
        FDACategory approvedCategory = FDACategory.getByDescription("approved");
        assertEquals("Approval category description", "approved", approvedCategory.getDescription());
    }
}
