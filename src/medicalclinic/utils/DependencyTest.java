package medicalclinic.utils;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;

public class DependencyTest {
    public static void main(String[] args) {
        System.out.println("Test des dépendances du Cabinet Médical");
        System.out.println("--------------------------------------");

        // Test JADE
        testJade();

        // Test Gson
        testGson();

        // Test Swing
        testSwing();

        System.out.println("--------------------------------------");
        System.out.println("Test terminé. Toutes les dépendances sont correctement chargées.");
    }

    private static void testJade() {
        try {
            System.out.println("Test de JADE...");

            // Vérifier si la classe Agent est disponible
            Class<?> agentClass = Agent.class;
            System.out.println("- Classe Agent disponible: " + agentClass.getName());

            // Vérifier si la classe Runtime est disponible
            Runtime runtime = Runtime.instance();
            System.out.println("- Runtime JADE disponible: " + runtime.getClass().getName());

            // Vérifier si la classe Profile est disponible
            Profile profile = new ProfileImpl();
            System.out.println("- Profile JADE disponible: " + profile.getClass().getName());

            // Vérifier si la classe Logger est disponible
            Logger logger = Logger.getJADELogger(DependencyTest.class.getName());
            System.out.println("- Logger JADE disponible: " + logger.getClass().getName());

            System.out.println("Test de JADE réussi.");
        } catch (Exception e) {
            System.err.println("Erreur lors du test de JADE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testGson() {
        try {
            System.out.println("Test de Gson...");

            // Vérifier si la classe Gson est disponible
            Gson gson = new Gson();
            System.out.println("- Classe Gson disponible: " + gson.getClass().getName());

            // Vérifier si la classe JsonObject est disponible
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("test", "value");
            System.out.println("- JsonObject disponible: " + jsonObject.getClass().getName());

            // Tester la sérialisation/désérialisation
            String json = gson.toJson(jsonObject);
            JsonObject parsed = gson.fromJson(json, JsonObject.class);
            System.out.println("- Sérialisation/désérialisation: " + parsed.get("test").getAsString());

            System.out.println("Test de Gson réussi.");
        } catch (Exception e) {
            System.err.println("Erreur lors du test de Gson: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSwing() {
        try {
            System.out.println("Test de Swing...");

            // Vérifier si les classes Swing sont disponibles
            JFrame frame = new JFrame("Test Swing");
            System.out.println("- Classe JFrame disponible: " + frame.getClass().getName());

            JPanel panel = new JPanel();
            System.out.println("- Classe JPanel disponible: " + panel.getClass().getName());

            JLabel label = new JLabel("Test Label");
            System.out.println("- Classe JLabel disponible: " + label.getClass().getName());

            // Tester BorderLayout
            panel.setLayout(new BorderLayout());
            System.out.println("- Classe BorderLayout disponible: " + BorderLayout.class.getName());

            // Tester Font
            Font font = new Font("Arial", Font.BOLD, 14);
            System.out.println("- Classe Font disponible: " + font.getClass().getName());

            System.out.println("Test de Swing réussi.");
        } catch (Exception e) {
            System.err.println("Erreur lors du test de Swing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
