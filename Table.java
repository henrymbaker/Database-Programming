/* Name: Henry Baker
 * Date: 2/10/2023
 * 
 * Class Table takes data from a ResultSet from an SQL query and stores
 * it for easier and repeated access. Users can choose between a normal
 * table or a selection table, which includes a column of increasing integers
 * for easy selection. Users can also print the table or get a tuple at a 
 * specified index.
 */

import java.sql.*;
import java.util.ArrayList;

public class Table {
    private ResultSet result;
    private ArrayList<ArrayList<String>> table; /* Stores data from ResultSet. */
    private ArrayList<Integer> lengths; /* Stores attribute lengths for formatted printing. */

    /* Stores give ResultSet in result and initializes
     * table and lengths. */
    public Table(ResultSet result) {
        this.result = result;
        lengths = new ArrayList<>();
        table = new ArrayList<>();
    }

    /* Returns the table structure. */
    public ArrayList<ArrayList<String>> getTable() throws SQLException {
        return get(false);
    }

    /* Returns the table structure with an extra column for row numbers. */
    public ArrayList<ArrayList<String>> getSelectionTable() throws SQLException {
        return get(true);
    }

    /* Prints the table. */
    public void printTable() throws SQLException {
        print(false);
    }

    /* Prints the table with an extra column for row numbers. */
    public void printSelectionTable() throws SQLException {
        print(true);
    }

    /* Returns the tuple at the given index of the table. */
    public ArrayList<String> getTuple(int index) {
        return table.get(index);
    }

    /* Creates the table with the specified format and returns it. */
    private ArrayList<ArrayList<String>> get(boolean isSelection) throws SQLException {
        if (table.isEmpty()) {
            createTable(isSelection);    
        }
        return table;
    }

    /* Creates the table with the specified format. */
    private void createTable(boolean isSelection) throws SQLException {
        table = new ArrayList<>();
        getColumnNames(isSelection);
        getTuples(isSelection);
    }

    /* Parses the column names from the ResultSet and adds them to the table. */
    private void getColumnNames(boolean isSelection) throws SQLException {
        ArrayList<String> columnNames = new ArrayList<>();
        ResultSetMetaData metaData = result.getMetaData();
        int numColumns = metaData.getColumnCount();
        if (isSelection) {
            lengths.add(1);
            columnNames.add("#");
        }
        for (int i = 1; i<= numColumns; i++) {
            String columnName = metaData.getColumnName(i);
            int columnLength = columnName.length();
            lengths.add(columnLength);
            columnNames.add(columnName);
        }
        table.add(columnNames);
    }

    /* Parses the tuples from the ResultSet and adds them to the table. */
    private void getTuples(boolean isSelection) throws SQLException {
        ArrayList<String> columnNames = table.get(0);
        ArrayList<String> tuple = new ArrayList<>();
        int count = 0;
        // get tuples
        while(result.next()) {
            int startIndex = 0;
            if (isSelection) {
                tuple.add(String.valueOf(count + 1));
                startIndex++;
            }

            for (int i = startIndex; i<columnNames.size(); i++) {
                String attribute = result.getString(columnNames.get(i));
                if (attribute == null) {
                   tuple.add("null");
                } else {
                    tuple.add(attribute);
                    if (attribute.length() > lengths.get(i)) {
                        if (!isSelection || i != 0) {
                            lengths.set(i, attribute.length());
                        }
                    }
                }
            }
            table.add(tuple);
            tuple = new ArrayList<>();
            count++;
        }
    }

    /* Prints the table with the specified format. */
    private void print(boolean isSelection) throws SQLException {
        if (table.isEmpty()) {
            createTable(isSelection);    
        }
        int totalLength = -1;
        for (int length : lengths) {
            totalLength += length+3;
        }
        System.out.println("*".repeat(totalLength));
        printColumnNames();
        System.out.println("-".repeat(totalLength));
        printTuples();
        System.out.println("*".repeat(totalLength));
    }

    /* Prints the table's column names. */
    private void printColumnNames() {
        ArrayList<String> columnNames = table.get(0);
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i).substring(0,1).toUpperCase() + 
                                columnNames.get(i).substring(1);
            System.out.printf("%-"+ (lengths.get(i)+1) +"s", columnName);
            if (i < columnNames.size()-1) {
                System.out.print("| ");
            }
        }
        System.out.println();
    }

    /* Prints the table's tuples. */
    private void printTuples() {
        ArrayList<String> columnNames = table.get(0);
        int numTuples = table.size();
        for (int i = 1; i < numTuples; i++) {
            for (int j = 0; j<columnNames.size(); j++) {
                System.out.printf("%-"+ (lengths.get(j)+1) +"s", table.get(i).get(j));
                if (j < columnNames.size()-1) {
                    System.out.print("| ");
                }
                
            }
            System.out.println();   
        }
    }
}
