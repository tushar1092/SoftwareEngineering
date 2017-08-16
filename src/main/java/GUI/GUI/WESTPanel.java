package GUI.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Trevor on 4/23/2017.
 */
public class WESTPanel extends JPanel {
    public WESTPanel(){
        Dimension size = getPreferredSize();
        size.width = 250;
        setPreferredSize(size);

        setBorder(BorderFactory.createTitledBorder("Menu"));

        JLabel SearchLabel = new JLabel("Ticker Search");
        JTextField SearchField = new JTextField(10);
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();


        ///////////////////// WEST PANEL /////////////////////////////
        gc.anchor = GridBagConstraints.LINE_START;
        gc.weightx = 0.5;
        gc.weighty = 0.5;

        gc.gridx = 0;
        gc.gridy = 0;
        add(SearchLabel, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        add(SearchField, gc);


        JButton button = new JButton("Enter");
        gc.weighty = 10;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.gridx = 1;
        gc.gridy = 2;


        //Add behavior
         button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                //******************************Input action for button press here example: *************************************
                String search = SearchField.getText();

            }
        });

        add(button, gc);



    }
}
