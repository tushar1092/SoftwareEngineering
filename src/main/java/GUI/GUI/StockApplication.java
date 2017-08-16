package GUI.GUI;
import javax.swing.*;


/**
 * Created by Trevor on 4/19/2017.
 */
public class StockApplication {

    //private JPanel MyStockManager;
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new MainFrame("MyStockManager");

                frame.setSize(1024, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
