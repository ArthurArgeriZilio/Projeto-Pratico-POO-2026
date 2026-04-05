import gui.TelaPrincipal;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // usa o padrao
                }
                TelaPrincipal tela = new TelaPrincipal();
                tela.setVisible(true);
            }
        });
    }
}
