package igrica;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class main {

    public static void main(String[] args) {
        JFrame game = new JFrame();
        game.setTitle("2048 Igrica");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(350, 420);
        game.setResizable(false);

        game.add(new Emir2048());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

}