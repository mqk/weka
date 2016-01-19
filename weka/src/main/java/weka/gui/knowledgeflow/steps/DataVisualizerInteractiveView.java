/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*`
 *    DataVisualizerInteractiveView.java
 *    Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.knowledgeflow.steps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import weka.core.WekaException;
import weka.gui.ResultHistoryPanel;
import weka.gui.knowledgeflow.BaseInteractiveViewer;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;
import weka.knowledgeflow.steps.DataVisualizer;

/**
 * Interactive viewer for the DataVisualizer step
 *
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: $
 */
public class DataVisualizerInteractiveView extends BaseInteractiveViewer {

  private static final long serialVersionUID = 5345799787154266282L;
  protected ResultHistoryPanel m_history;
  protected VisualizePanel m_visPanel = new VisualizePanel();
  protected JButton m_clearButton = new JButton("Clear results");

  protected JSplitPane m_splitPane;

  protected PlotData2D m_currentPlot;

  @Override
  public String getViewerName() {
    return "Data Visualizer";
  }

  @Override
  public void init() throws WekaException {
    addButton(m_clearButton);

    m_history = new ResultHistoryPanel(null);
    m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
    m_history.setHandleRightClicks(false);
    m_history.getList().addMouseListener(
      new ResultHistoryPanel.RMouseAdapter() {
        private static final long serialVersionUID = -5174882230278923704L;

        @Override
        public void mouseClicked(MouseEvent e) {
          int index = m_history.getList().locationToIndex(e.getPoint());
          if (index != -1) {
            String name = m_history.getNameAtIndex(index);
            // doPopup(name);
            Object plotD = m_history.getNamedObject(name);
            if (plotD instanceof PlotData2D) {
              try {
                if (m_currentPlot != null && m_currentPlot != plotD) {
                  m_currentPlot.setXindex(m_visPanel.getXIndex());
                  m_currentPlot.setYindex(m_visPanel.getYIndex());
                  m_currentPlot.setCindex(m_visPanel.getCIndex());
                }

                m_currentPlot = (PlotData2D) plotD;
                int x = m_currentPlot.getXindex();
                int y = m_currentPlot.getYindex();
                int c = m_currentPlot.getCindex();
                if (x == y && x == 0
                  && m_currentPlot.getPlotInstances().numAttributes() > 1) {
                  y++;
                }
                m_visPanel.setMasterPlot((PlotData2D) plotD);
                m_visPanel.setXIndex(x);
                m_visPanel.setYIndex(y);
                m_visPanel.setColourIndex(c, true);
                m_visPanel.repaint();
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }
        }
      });

    m_history.getList().getSelectionModel()
      .addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            ListSelectionModel lm = (ListSelectionModel) e.getSource();
            for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
              if (lm.isSelectedIndex(i)) {
                // m_AttSummaryPanel.setAttribute(i);
                if (i != -1) {
                  String name = m_history.getNameAtIndex(i);
                  Object plotD = m_history.getNamedObject(name);
                  if (plotD != null && plotD instanceof PlotData2D) {
                    try {
                      if (m_currentPlot != null && m_currentPlot != plotD) {
                        m_currentPlot.setXindex(m_visPanel.getXIndex());
                        m_currentPlot.setYindex(m_visPanel.getYIndex());
                        m_currentPlot.setCindex(m_visPanel.getCIndex());
                      }

                      m_currentPlot = (PlotData2D) plotD;
                      int x = m_currentPlot.getXindex();
                      int y = m_currentPlot.getYindex();
                      int c = m_currentPlot.getCindex();
                      if (x == y && x == 0
                        && m_currentPlot.getPlotInstances().numAttributes() > 1) {
                        y++;
                      }
                      m_visPanel.setMasterPlot((PlotData2D) plotD);
                      m_visPanel.setXIndex(x);
                      m_visPanel.setYIndex(y);
                      m_visPanel.setColourIndex(c, true);
                      m_visPanel.repaint();
                    } catch (Exception ex) {
                      ex.printStackTrace();
                    }
                  }
                }
                break;
              }
            }
          }
        }
      });

    m_splitPane =
      new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_history, m_visPanel);

    add(m_splitPane, BorderLayout.CENTER);
    boolean first = true;
    for (PlotData2D pd : ((DataVisualizer) getStep()).getPlots()) {
      m_history.addResult(pd.getPlotName(), new StringBuffer());
      m_history.addObject(pd.getPlotName(), pd);
      if (first) {
        try {
          int x = pd.getXindex();
          int y = pd.getYindex();
          int c = pd.getCindex();
          if (x == 0 && y == 0 && pd.getPlotInstances().numAttributes() > 1) {
            y++;
          }

          m_visPanel.setMasterPlot(pd);
          m_currentPlot = pd;
          m_visPanel.setXIndex(x);
          m_visPanel.setYIndex(y);
          if (pd.getPlotInstances().classIndex() >= 0) {
            m_visPanel.setColourIndex(pd.getPlotInstances().classIndex(), true);
          } else {
            m_visPanel.setColourIndex(c, true);
          }
          m_visPanel.repaint();
          first = false;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }

      m_visPanel.setPreferredSize(new Dimension(800, 600));
    }

    m_clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        m_history.clearResults();
        ((DataVisualizer) getStep()).getPlots().clear();
        m_splitPane.remove(m_visPanel);
      }
    });
  }
}