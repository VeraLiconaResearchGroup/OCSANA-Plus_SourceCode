/**
 * Widget to let users select a set of nodes with a list
 *
 * Copyright Vera-Licona Research Group (C) 2016
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.ui.control.widgets;

// Java imports
import java.util.*;
import java.util.stream.Collectors;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

// Cytoscape imports
import org.cytoscape.model.CyNode;

// OCSANA imports
import org.compsysmed.ocsana.internal.util.tunables.NodeHandler;

public class ListNodeSetSelecter
    extends AbstractNodeSetSelecter {
    private static final String toolTipText = "Ctrl+click to select multiple nodes";

    private JList<CyNode> nodeSetListField;

    public ListNodeSetSelecter (String label,
                                Set<CyNode> availableNodes,
                                Set<CyNode> selectedNodes,
                                NodeHandler nodeHandler) {
        super(label, availableNodes, selectedNodes, nodeHandler);
        draw();
    }

    public ListNodeSetSelecter (String label,
                                Set<CyNode> availableNodes,
                                NodeHandler nodeHandler) {
        super(label, availableNodes, nodeHandler);
        draw();
    }

    public ListNodeSetSelecter (AbstractNodeSetSelecter other) {
        super(other);
        draw();
    }

    public ListNodeSetSelecter (AbstractNodeSetSelecter other,
                                NodeHandler nodeHandler) {
        super(other, nodeHandler);
        draw();
    }

    /**
     * Build the JPanel after the constructors populate the data
     **/
    private void draw () {
        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        nodeSetListField.setCellRenderer(new NodeListCellRenderer(nodeHandler));

        JLabel title = new JLabel(label);
        add(title);

        JScrollPane listPane = new JScrollPane(nodeSetListField);
        listPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(listPane);

        revalidate();
        repaint();
    }

    @Override
    protected void handleAvailableNodesUpdate () {
        CyNode[] availableNodesArray = getAvailableNodes().toArray(new CyNode[0]);
        nodeSetListField = new JList<>(availableNodesArray);
        nodeSetListField.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        nodeSetListField.setToolTipText(toolTipText);

        draw();
    }

    @Override
    public Set<CyNode> getSelectedNodes () {
        return nodeSetListField.getSelectedValuesList().stream().collect(Collectors.toSet());
    }

    @Override
    public void setSelectedNodes (Set<CyNode> selectedNodes) {
        Objects.requireNonNull(selectedNodes, "Set of selected nodes cannot be empty");

        // Select specified nodes
        nodeSetListField.clearSelection();

        ListModel<CyNode> nodeSetListModel = nodeSetListField.getModel();
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < nodeSetListModel.getSize(); i++) {
            if (selectedNodes.contains(nodeSetListModel.getElementAt(i))) {
                selectedIndices.add(i);
            }
        }

        int[] indices = selectedIndices.stream().mapToInt(i -> i).toArray();
        nodeSetListField.setSelectedIndices(indices);
    }

    private class NodeListCellRenderer
        extends DefaultListCellRenderer {
        private NodeHandler nodeHandler;
        public NodeListCellRenderer (NodeHandler nodeHandler) {
            this.nodeHandler = nodeHandler;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list,
                                                      Object nodeObj,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel cell = (JLabel) super.getListCellRendererComponent(list, nodeObj, index, isSelected, cellHasFocus);

            CyNode node = (CyNode) nodeObj;
            cell.setText(nodeHandler.getNodeName(node));

            return cell;
        }
    }
}
