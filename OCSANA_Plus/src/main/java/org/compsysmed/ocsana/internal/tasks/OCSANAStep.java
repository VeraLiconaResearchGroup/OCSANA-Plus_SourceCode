/**
 * Enum of steps in the OCSANA algorithm
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.tasks;

public enum OCSANAStep {
    GET_SETS,
    FIND_PATHS_TO_TARGETS,
    FIND_PATHS_TO_OFF_TARGETS,
    SCORE_PATHS,
    FIND_MHSES,
    PRESENT_RESULTS
}
