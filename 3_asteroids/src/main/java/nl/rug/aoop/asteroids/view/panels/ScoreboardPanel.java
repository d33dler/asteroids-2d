package nl.rug.aoop.asteroids.view.panels;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.controls.MainMenuControl;
import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import nl.rug.aoop.asteroids.util.database.Score;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * Panel that will display the scores registered in the database
 */
public class ScoreboardPanel extends JPanel {

    /*
        Instance of DataBase Manager to retrieve scores
     */
    public static final DatabaseManager dbManager = DatabaseManager.getInstance();

    /**
     * Sets Panel attributes and components
     *
     * @param viewController Controller needed for Button actions
     */
    public ScoreboardPanel(ViewController viewController, String iconFileName) {
        JLabel icon = new JLabel(new ImageIcon(iconFileName));
        JTable scoreTable = new JTable(new ScoreTable(dbManager.getAllScores()));
        scoreTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scoreTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(100, 250));
        JButton returnButton = new JButton(new AbstractAction("Return") {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewController.displayPane(new MainMenuControl(viewController));
            }
        });
        setLayout(new BorderLayout(150, 50));
        add(icon, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);

    }

    /**
     * Internal model of a table to represent scores
     */
    private class ScoreTable implements TableModel {

        private final String[] columnNames = new String[]{"Player Name", "Score"};
        private final Object[][] dataTable;

        /**
         * Initialize data to display in the table
         *
         * @param scores A list of registered scores
         */
        protected ScoreTable(List<Score> scores){
            dataTable = new Object[scores.size()][columnNames.length];
            for (int idx = 0; idx < scores.size(); idx++) {
                dataTable[idx][0] = scores.get(idx).getPlayerName();
                dataTable[idx][1] = scores.get(idx).getScore();
            }
        }

        @Override
        public int getRowCount() {
            return dataTable.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Nls
        @Override
        public String getColumnName(int i) {
            return columnNames[i];
        }

        @Override
        public Class<?> getColumnClass(int i) {
            return Objects.requireNonNull(getValueAt(0, i)).getClass();
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            return false;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            return dataTable[i][i1];
        }

        @Override
        public void setValueAt(Object o, int i, int i1) {
            // Table is not editable
        }

        @Override
        public void addTableModelListener(TableModelListener tableModelListener) {
        }

        @Override
        public void removeTableModelListener(TableModelListener tableModelListener) {
        }
    }
}
