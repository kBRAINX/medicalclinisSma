import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Classe principale pour démarrer le système du cabinet médical
 * Offre une interface pour lancer le système complet ou des patients individuels
 */
public class Main {
    public static void main(String[] args) {
        // Créer une interface graphique pour choisir quoi lancer
        JFrame frame = new JFrame("Cabinet Médical - Système Multi-Agent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Cabinet Médical - Système Multi-Agent", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 100, 170));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panneau de boutons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 20));
        buttonPanel.setBackground(new Color(240, 245, 255));

        JButton systemButton = createButton("Lancer le Système Complet",
            "Démarre les agents Réceptionniste, Infirmier et Médecins");

        JButton patientButton = createButton("Lancer un Patient",
            "Crée un nouvel agent Patient qui se connecte au système");

        JButton exitButton = createButton("Quitter",
            "Ferme l'application");

        // Ajouter les boutons au panneau
        buttonPanel.add(systemButton);
        buttonPanel.add(patientButton);
        buttonPanel.add(exitButton);

        // Panel d'information
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(230, 235, 250));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea infoText = new JTextArea(
            "Comment utiliser le système:\n\n" +
                "1. Lancez d'abord le 'Système Complet' pour démarrer les agents du cabinet médical\n" +
                "2. Lancez ensuite un ou plusieurs 'Patient(s)' qui se connecteront au système\n" +
                "3. Suivez les instructions dans l'interface de chaque patient\n\n" +
                "Note: Assurez-vous que le système est lancé avant de créer des patients."
        );
        infoText.setEditable(false);
        infoText.setBackground(new Color(230, 235, 250));
        infoText.setFont(new Font("Arial", Font.PLAIN, 14));
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoPanel.add(infoText, BorderLayout.CENTER);

        // Ajouter les panneaux au panneau principal
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        // Actions des boutons
        systemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame,
                            "Lancement du système multi-agent du cabinet médical...\n" +
                                "Une console va s'ouvrir pour afficher les logs.",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                        medicalclinic.utils.StartSystem.main(null);
                    }
                }).start();
            }
        });

        patientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        medicalclinic.utils.StartPatient.main(null);
                    }
                }).start();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Crée un bouton stylisé avec info-bulle
     */
    private static JButton createButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(100, 150, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 130, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(100, 150, 255));
            }
        });

        return button;
    }
}
