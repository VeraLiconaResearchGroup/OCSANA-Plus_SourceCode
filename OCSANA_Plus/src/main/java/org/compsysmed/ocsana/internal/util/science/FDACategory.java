/**
 * Enum of FDA drug approval categories
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.science;

// Java imports
import java.util.*;

/**
 * Enum of FDA drug approval categories
 **/
public enum FDACategory {
    APPROVED ("approved"),
    INVESTIGATIONAL ("investigational"),
    EXPERIMENTAL ("experimental"),
    NUTRACEUTICAL ("nutraceutical"),
    ILLICIT ("illicit"),
    WITHDRAWN ("withdrawn");

    private static final Map<String, FDACategory> lookupByDescription = new HashMap<>();

    static {
        for (FDACategory category: EnumSet.allOf(FDACategory.class)) {
            lookupByDescription.put(category.getDescription(), category);
        }
    }

    /**
     * Return the FDACategory with a specified description
     **/
    public static FDACategory getByDescription (String description) {
        return lookupByDescription.getOrDefault(description.toLowerCase(), null);
    }

    private final String description;

    private FDACategory (String description) {
        this.description = description;
    }

    /**
     * Return the description of this category
     **/
    public String getDescription () {
        return description;
    }
}
