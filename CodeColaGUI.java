import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class CodeColaGUI extends JFrame {
    private JTextArea codeInput;
    private JTextArea output;
    private JButton convertButton;
    private JButton clearButton;
    private JButton loadExampleButton;
    private JComboBox<String> themeComboBox;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Map<String, String> variables;
    private List<String> recipeSteps;
    
    // Color themes
    private static final Color DARK_BG = new Color(40, 44, 52);
    private static final Color DARK_FG = new Color(171, 178, 191);
    private static final Color LIGHT_BG = Color.WHITE;
    private static final Color LIGHT_FG = Color.BLACK;
    private static final Color ACCENT_COLOR = new Color(97, 175, 239);
    
    public CodeColaGUI() {
        variables = new HashMap<>();
        recipeSteps = new ArrayList<>();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyTheme("Light");
    }
    
    private void initializeComponents() {
        setTitle("Code-Cola Pro - Java to Recipe Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Initialize text areas with better default content
        codeInput = new JTextArea(getDefaultCode());
        codeInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeInput.setTabSize(4);
        codeInput.setLineWrap(false);
        
        output = new JTextArea();
        output.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        
        // Initialize buttons with icons (using Unicode symbols)
        convertButton = new JButton("🍹 In Rezept umwandeln");
        convertButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        convertButton.setPreferredSize(new Dimension(200, 40));
        
        clearButton = new JButton("🗑️ Löschen");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        loadExampleButton = new JButton("📝 Beispiel laden");
        loadExampleButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Theme selector
        themeComboBox = new JComboBox<>(new String[]{"Light", "Dark"});
        themeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Status components
        statusLabel = new JLabel("Bereit");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Theme:"));
        topPanel.add(themeComboBox);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(loadExampleButton);
        topPanel.add(clearButton);
        
        // Input panel
        JScrollPane inputScroll = new JScrollPane(codeInput);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Java-Code eingeben"));
        inputScroll.setPreferredSize(new Dimension(880, 280));
        
        // Add line numbers to input
        inputScroll.setRowHeaderView(new LineNumberPanel(codeInput));
        
        // Center panel with convert button
        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.add(convertButton);
        
        // Output panel
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Rezept-Ausgabe"));
        outputScroll.setPreferredSize(new Dimension(880, 280));
        
        // Bottom panel with status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        
        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(inputScroll, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.SOUTH);
        
        // Split pane for input and output
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(mainPanel);
        splitPane.setBottomComponent(outputScroll);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);
        
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        convertButton.addActionListener(e -> convertCodeWithProgress());
        clearButton.addActionListener(e -> clearAll());
        loadExampleButton.addActionListener(e -> loadExample());
        themeComboBox.addActionListener(e -> applyTheme((String) themeComboBox.getSelectedItem()));
        
        // Add keyboard shortcuts
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "convert");
        actionMap.put("convert", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCodeWithProgress();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
        actionMap.put("help", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
    }
    
    private void convertCodeWithProgress() {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        convertButton.setEnabled(false);
        statusLabel.setText("Konvertiere Code...");
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Thread.sleep(500); // Simulate processing time
                return convertCode();
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    output.setText(result);
                    statusLabel.setText("Konvertierung abgeschlossen - " + recipeSteps.size() + " Schritte gefunden");
                } catch (Exception e) {
                    statusLabel.setText("Fehler bei der Konvertierung: " + e.getMessage());
                    output.setText("Fehler bei der Konvertierung:\n" + e.getMessage());
                }
                progressBar.setVisible(false);
                convertButton.setEnabled(true);
            }
        };
        worker.execute();
    }
    
    private String convertCode() {
        String code = codeInput.getText();
        variables.clear();
        recipeSteps.clear();
        
        StringBuilder recipe = new StringBuilder();
        recipe.append("🍹 Code-Cola Rezept 🍹\n");
        recipe.append("=" .repeat(30)).append("\n\n");
        
        String[] lines = code.split("\n");
        int stepNumber = 1;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
                continue;
            }
            
            String result = parseLine(line, i + 1);
            if (!result.isEmpty()) {
                recipe.append(String.format("%d. %s\n", stepNumber++, result));
                recipeSteps.add(result);
            }
        }
        
        if (recipeSteps.isEmpty()) {
            recipe.append("⚠️ Keine verwertbaren Anweisungen gefunden!\n");
            recipe.append("Stelle sicher, dass dein Code Java-Syntax verwendet.\n");
        } else {
            recipe.append("\n🎉 Fertig ist dein Code-Cola!\n");
            recipe.append("Gefundene Zutaten: ").append(variables.size()).append("\n");
            recipe.append("Verarbeitete Schritte: ").append(recipeSteps.size()).append("\n");
        }
        
        return recipe.toString();
    }
    
    private String parseLine(String line, int lineNumber) {
        try {
            // Variable declarations
            if (line.matches("(int|double|float|long)\\s+\\w+\\s*=\\s*[\\d.]+;")) {
                return parseNumericVariable(line);
            } else if (line.matches("String\\s+\\w+\\s*=\\s*\"[^\"]*\";")) {
                return parseStringVariable(line);
            } else if (line.matches("boolean\\s+\\w+\\s*=\\s*(true|false);")) {
                return parseBooleanVariable(line);
            }
            // Method calls
            else if (line.matches("\\w+\\([^)]*\\);")) {
                return parseMethodCall(line);
            }
            // Control structures
            else if (line.matches("if\\s*\\([^)]+\\)\\s*\\{?")) {
                return parseIfStatement(line);
            } else if (line.matches("for\\s*\\([^)]+\\)\\s*\\{?")) {
                return parseForLoop(line);
            } else if (line.matches("while\\s*\\([^)]+\\)\\s*\\{?")) {
                return parseWhileLoop(line);
            }
            // Class and method definitions
            else if (line.contains("class ")) {
                return parseClassDefinition(line);
            } else if (line.matches("(public|private|protected)?\\s*(static)?\\s*\\w+\\s+\\w+\\([^)]*\\)\\s*\\{?")) {
                return parseMethodDefinition(line);
            }
        } catch (Exception e) {
            return "⚠️ Fehler in Zeile " + lineNumber + ": " + e.getMessage();
        }
        
        return "";
    }
    
    private String parseNumericVariable(String line) {
        Pattern pattern = Pattern.compile("(int|double|float|long)\\s+(\\w+)\\s*=\\s*([\\d.]+);");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String type = matcher.group(1);
            String name = matcher.group(2);
            String value = matcher.group(3);
            variables.put(name, value);
            
            String unit = getUnitForVariable(name, type);
            return String.format("Bereite %s %s %s vor", value, unit, name);
        }
        return "";
    }
    
    private String parseStringVariable(String line) {
        Pattern pattern = Pattern.compile("String\\s+(\\w+)\\s*=\\s*\"([^\"]*)\";");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            variables.put(name, value);
            return String.format("Wähle \"%s\" als %s", value, name);
        }
        return "";
    }
    
    private String parseBooleanVariable(String line) {
        Pattern pattern = Pattern.compile("boolean\\s+(\\w+)\\s*=\\s*(true|false);");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            variables.put(name, value);
            return String.format("Setze %s auf %s", name, value.equals("true") ? "an" : "aus");
        }
        return "";
    }
    
    private String parseMethodCall(String line) {
        Pattern pattern = Pattern.compile("(\\w+)\\(([^)]*)\\);");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String method = matcher.group(1);
            String args = matcher.group(2);
            
            switch (method.toLowerCase()) {
                case "boil":
                case "erhitzen":
                    return String.format("Erhitze %s", formatArguments(args));
                case "mix":
                case "mischen":
                    return String.format("Mische %s", formatArguments(args));
                case "add":
                case "hinzufuegen":
                case "addflavor":
                    return String.format("Füge %s hinzu", formatArguments(args));
                case "serve":
                case "servieren":
                    return String.format("Serviere %s", formatArguments(args));
                case "wait":
                case "warten":
                    return String.format("Warte %s", formatArguments(args));
                case "stir":
                case "ruehren":
                    return String.format("Rühre %s um", formatArguments(args));
                default:
                    return String.format("Führe %s mit %s aus", method, formatArguments(args));
            }
        }
        return "";
    }
    
    private String parseIfStatement(String line) {
        Pattern pattern = Pattern.compile("if\\s*\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String condition = matcher.group(1);
            return String.format("Falls %s, dann:", condition);
        }
        return "";
    }
    
    private String parseForLoop(String line) {
        return "Wiederhole die folgenden Schritte:";
    }
    
    private String parseWhileLoop(String line) {
        Pattern pattern = Pattern.compile("while\\s*\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String condition = matcher.group(1);
            return String.format("Solange %s, wiederhole:", condition);
        }
        return "";
    }
    
    private String parseClassDefinition(String line) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String className = matcher.group(1);
            return String.format("📋 Starte Rezept: %s", className);
        }
        return "";
    }
    
    private String parseMethodDefinition(String line) {
        Pattern pattern = Pattern.compile("\\w+\\s+(\\w+)\\([^)]*\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String methodName = matcher.group(1);
            if (!methodName.equals("main")) {
                return String.format("🔧 Definiere Arbeitsschritt: %s", methodName);
            }
        }
        return "";
    }
    
    private String formatArguments(String args) {
        if (args.trim().isEmpty()) {
            return "alles";
        }
        
        // Replace variable names with their values if known
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            args = args.replace(entry.getKey(), entry.getValue());
        }
        
        // Remove quotes from string literals
        args = args.replaceAll("\"", "");
        
        return args;
    }
    
    private String getUnitForVariable(String name, String type) {
        String lowerName = name.toLowerCase();
        if (lowerName.contains("water") || lowerName.contains("wasser") || 
            lowerName.contains("liquid") || lowerName.contains("fluessigkeit")) {
            return "ml";
        } else if (lowerName.contains("sugar") || lowerName.contains("zucker") || 
                   lowerName.contains("salt") || lowerName.contains("salz")) {
            return "g";
        } else if (lowerName.contains("temp") || lowerName.contains("grad")) {
            return "°C";
        } else if (lowerName.contains("time") || lowerName.contains("zeit")) {
            return "Minuten";
        }
        return type.equals("int") || type.equals("double") ? "Einheiten" : "";
    }
    
    private void clearAll() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Möchten Sie wirklich alles löschen?",
            "Bestätigung",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            codeInput.setText("");
            output.setText("");
            variables.clear();
            recipeSteps.clear();
            statusLabel.setText("Bereit");
        }
    }
    
    private void loadExample() {
        String[] examples = {
            "Einfaches Beispiel",
            "Erweiterte Cola",
            "Cocktail Rezept",
            "Kaffee Zubereitung"
        };
        
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Wählen Sie ein Beispiel:",
            "Beispiel laden",
            JOptionPane.QUESTION_MESSAGE,
            null,
            examples,
            examples[0]
        );
        
        if (choice != null) {
            switch (choice) {
                case "Einfaches Beispiel":
                    codeInput.setText(getDefaultCode());
                    break;
                case "Erweiterte Cola":
                    codeInput.setText(getAdvancedColaCode());
                    break;
                case "Cocktail Rezept":
                    codeInput.setText(getCocktailCode());
                    break;
                case "Kaffee Zubereitung":
                    codeInput.setText(getCoffeeCode());
                    break;
            }
            statusLabel.setText("Beispiel geladen: " + choice);
        }
    }
    
    private void applyTheme(String theme) {
        Color bgColor, fgColor;
        
        if ("Dark".equals(theme)) {
            bgColor = DARK_BG;
            fgColor = DARK_FG;
        } else {
            bgColor = LIGHT_BG;
            fgColor = LIGHT_FG;
        }
        
        codeInput.setBackground(bgColor);
        codeInput.setForeground(fgColor);
        codeInput.setCaretColor(fgColor);
        
        output.setBackground(bgColor);
        output.setForeground(fgColor);
        
        getContentPane().setBackground(bgColor);
        
        // Update button colors
        convertButton.setBackground(ACCENT_COLOR);
        convertButton.setForeground(Color.WHITE);
    }
    
    private void showHelp() {
        String helpText = """
            Code-Cola Pro - Hilfe
            
            Tastenkürzel:
            F5 - Code konvertieren
            F1 - Diese Hilfe anzeigen
            
            Unterstützte Java-Konstrukte:
            • Variablen (int, double, String, boolean)
            • Methodenaufrufe
            • Kontrollstrukturen (if, for, while)
            • Klassen- und Methodendefinitionen
            
            Tipps:
            • Verwende aussagekräftige Variablennamen
            • Nutze Methodennamen wie 'boil', 'mix', 'add'
            • Kommentare werden ignoriert
            """;
        
        JOptionPane.showMessageDialog(this, helpText, "Hilfe", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getDefaultCode() {
        return """
            public class Main {
                public static void main(String[] args) {
                    int water = 200;
                    int sugar = 50;
                    String flavor = "lime";
                    boolean carbonated = true;
                    
                    boil(water);
                    add(sugar);
                    addFlavor(flavor);
                    if (carbonated) {
                        mix();
                    }
                    serve();
                }
                
                public static void boil(int ml) {}
                public static void add(int grams) {}
                public static void addFlavor(String flavor) {}
                public static void mix() {}
                public static void serve() {}
            }""";
    }
    
    private String getAdvancedColaCode() {
        return """
            public class ColaRecipe {
                public static void main(String[] args) {
                    int water = 500;
                    int sugar = 100;
                    int caramel = 20;
                    String spices = "vanilla and cinnamon";
                    int temperature = 80;
                    
                    boil(water, temperature);
                    add(sugar);
                    stir();
                    add(caramel);
                    addFlavor(spices);
                    
                    for (int i = 0; i < 5; i++) {
                        stir();
                    }
                    
                    wait(10);
                    serve();
                }
            }""";
    }
    
    private String getCocktailCode() {
        return """
            public class CocktailMixer {
                public static void main(String[] args) {
                    int rum = 50;
                    int lime = 30;
                    int sugar = 20;
                    int ice = 100;
                    boolean garnish = true;
                    
                    add(rum);
                    add(lime);
                    add(sugar);
                    mix();
                    add(ice);
                    
                    if (garnish) {
                        addFlavor("mint leaves");
                    }
                    
                    serve();
                }
            }""";
    }
    
    private String getCoffeeCode() {
        return """
            public class CoffeeMaker {
                public static void main(String[] args) {
                    int water = 200;
                    int coffee = 15;
                    int temperature = 95;
                    String milk = "oat milk";
                    
                    boil(water, temperature);
                    add(coffee);
                    wait(4);
                    
                    if (milk != null) {
                        add(milk);
                    }
                    
                    stir();
                    serve();
                }
            }""";
    }
    
    // Simple line number panel
    private static class LineNumberPanel extends JPanel {
        private JTextArea textArea;
        
        public LineNumberPanel(JTextArea textArea) {
            this.textArea = textArea;
            setPreferredSize(new Dimension(50, 0));
            setBackground(new Color(240, 240, 240));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.GRAY);
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            FontMetrics fm = g.getFontMetrics();
            int lineHeight = fm.getHeight();
            int lines = textArea.getLineCount();
            
            for (int i = 1; i <= lines; i++) {
                String lineNumber = String.valueOf(i);
                int x = getWidth() - fm.stringWidth(lineNumber) - 5;
                int y = i * lineHeight - 2;
                g.drawString(lineNumber, x, y);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CodeColaGUI().setVisible(true);
        });
    }
}