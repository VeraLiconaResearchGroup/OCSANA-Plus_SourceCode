/**
 * Panel to contain OCSANA CIs report
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.results.subpanels;

// Java imports
import java.util.*;

import java.awt.BorderLayout;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SortOrder;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// OCSANA imports

import org.compsysmed.ocsana.internal.util.context.ContextBundle;
import org.compsysmed.ocsana.internal.util.results.ResultsBundle;
import org.compsysmed.ocsana.internal.util.results.CombinationOfInterventions;

public class CIListSubpanel
    extends JPanel {
    private final ContextBundle contextBundle;
    private final ResultsBundle resultsBundle;
    private final JFrame cytoscapeFrame;

    public CIListSubpanel (ContextBundle contextBundle,
                           ResultsBundle resultsBundle,
                           JFrame cytoscapeFrame) {
        Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
        this.contextBundle = contextBundle;

        Objects.requireNonNull(resultsBundle, "Context results cannot be null");
        this.resultsBundle = resultsBundle;

        Objects.requireNonNull(cytoscapeFrame, "Cytoscape frame cannot be null");
        this.cytoscapeFrame = cytoscapeFrame;

        if (resultsBundle.getCIs() != null) {
            MHSTable mhsTable = new MHSTable();

            JScrollPane mhsScrollPane = new JScrollPane(mhsTable);

            setLayout(new BorderLayout());
            String mhsText = String.format("Found %d optimal CIs in %f s.", resultsBundle.getCIs().size(), resultsBundle.getMHSExecutionSeconds());
            add(new JLabel(mhsText), BorderLayout.PAGE_START);
            add(mhsScrollPane, BorderLayout.CENTER);
        }
    }

    private class MHSTable extends JTable {
        private final List<CombinationOfInterventions> CIs;

        public MHSTable () {
            this.CIs = new ArrayList<>(resultsBundle.getCIs());
            Collections.sort(CIs, new SortbyOcsanaScore());
            MHSTableModel mhsModel = new MHSTableModel(contextBundle, resultsBundle, CIs);
            setModel(mhsModel);

////            // Set up the sorter
////            TableRowSorter<TableModel> mhsSorter = new TableRowSorter<>(mhsModel);
////            mhsSorter.setSortable(0, false); 
////            
////            mhsSorter.setSortable(1, false); // Disable sorting by CI size TEMPORARY
////            mhsSorter.setSortable(2, false); // Disable sorting by CI size TEMPORARY
////            mhsSorter.setSortable(3, false); // Disable sorting by best SI priority score TEMPORARY
////            
//            setRowSorter(mhsSorter);

            // Handle double click events per row
            MouseListener mouseListener = new MouseAdapter() {
                    public void mousePressed(MouseEvent me) {
                        Point p = me.getPoint();
                        int row = rowAtPoint(p);

                        if (me.getClickCount() == 2 && row != -1) {
                            handleUserDoubleClick(row);
                        }
                    }};

            addMouseListener(mouseListener);

            setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }

        public void handleUserDoubleClick (Integer row) {
        		
            CombinationOfInterventions ci = CIs.get(row);
        }
    }

    class SortbyOcsanaScore implements Comparator<CombinationOfInterventions>
    {

		@Override
		public int compare(CombinationOfInterventions o1, CombinationOfInterventions o2) {
			return Double.compare(o1.getOCSANAScore(), o2.getOCSANAScore());

		}
    }
    private static class MHSTableModel extends AbstractTableModel {
        private final ContextBundle contextBundle;
        private final ResultsBundle resultsBundle;
        private final List<CombinationOfInterventions> CIs;

        public MHSTableModel (ContextBundle contextBundle,
                              ResultsBundle resultsBundle,
                              List<CombinationOfInterventions> CIs) {
            Objects.requireNonNull(contextBundle, "Context bundle cannot be null");
            this.contextBundle = contextBundle;

            Objects.requireNonNull(resultsBundle, "Context results cannot be null");
            this.resultsBundle = resultsBundle;

            Objects.requireNonNull(CIs, "CIs collection cannot be null");
            this.CIs = CIs;
        }
        String[] colNames = {"CI", "Size", "OCSANA score","Target Score","Side-Effect Score"};

        @Override
        public String getColumnName (int col) {
            return colNames[col];
        }

        @Override
        public int getRowCount () {
            return CIs.size();
        }

        @Override
        public int getColumnCount () {
            return colNames.length;
        }

        @Override
        public Object getValueAt (int row, int col) {
            CombinationOfInterventions ci = CIs.get(row);
            switch (col) {
            case 0:
                return ci.interventionNodesString();

            case 1:
                return ci.size();

            case 2:
                return ci.getOCSANAScore();
            case 3:
            	return ci.getTargetScore();
            case 4:
            	return ci.getSideEffectScore();
            

            default:
                throw new IllegalArgumentException(String.format("Table does not have %d columns", col));
            }
        }

        

        @Override
        public Class<?> getColumnClass(int column) {
            try {
                return getValueAt(0, column).getClass();
            } catch (IndexOutOfBoundsException|NullPointerException exception) {
                return Object.class;
            }
        }

        // Disable cell editing
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
