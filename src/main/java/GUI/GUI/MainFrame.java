package GUI.GUI;
import javax.swing.*;
import java.awt.*;


/**
 * Created by Trevor on 4/23/2017.
 */
public class MainFrame extends JFrame {
    private WESTPanel westPanel;
    private EASTPanel eastPanel;
    public MainFrame(String myStockManager) {
        super(myStockManager);
        //Set Layout manager
        setLayout(new GridBagLayout());

        //Swing Components
        //final JTextArea textArea = new JTextArea();
        //JButton button = new JButton("Enter ticker here");
        eastPanel = new EASTPanel();
        westPanel = new WESTPanel();
        //Add swing components to content panel
        Container c = getContentPane();
        //c.add(textArea, BorderLayout.CENTER);
        //c.add(button, BorderLayout.SOUTH);

        c.add(westPanel, BorderLayout.WEST);
        c.add(eastPanel, BorderLayout.EAST);




        //Add behavior
        /*button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                //******************************Input action for button press here example: *************************************
                textArea.append("Hello\n");
            }
        });
        */

    }
}



