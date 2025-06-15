import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CodeColaGUI extends JFrame {
    private JTextArea codeInput;
    private JTextArea output;
    private JButton convertButton;
    private JButton clearButton;
    private JButton loadExampleButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Map<String, String> variables;
    private List<String> recipeSteps;
    
    // Minimalistic color palette
    private static final Color BG_COLOR = new Color(250, 250, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color SECONDARY_TEXT = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color PRIMARY_COLOR = new Color(52, 58, 64);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color HOVER_COLOR = new Color(248, 249, 250);
    
    public CodeColaGUI() {
        variables = new HashMap<>();
        recipeSteps = new ArrayList<>();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setMinimumSize(new Dimension(800, 500));
        // Changed from MAXIMIZED_BOTH to a smaller default size
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        setTitle("CodeCola - Java to Recipe Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BG_COLOR);
        
        // Text areas with clean styling
        codeInput = new JTextArea(getDefaultCode());
        codeInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeInput.setTabSize(4);
        codeInput.setLineWrap(false);
        codeInput.setBorder(new EmptyBorder(15, 15, 15, 15));
        codeInput.setBackground(PANEL_COLOR);
        codeInput.setForeground(TEXT_COLOR);
        codeInput.setCaretColor(TEXT_COLOR);
        codeInput.setSelectionColor(new Color(0, 123, 255, 30));
        
        output = new JTextArea();
        output.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 15));
        output.setBackground(PANEL_COLOR);
        output.setForeground(TEXT_COLOR);
        output.setSelectionColor(new Color(0, 123, 255, 30));
        
        // Clean, minimal buttons
        convertButton = new MinimalButton("Convert to Recipe", PRIMARY_COLOR);
        convertButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        convertButton.setPreferredSize(new Dimension(160, 36));
        
        clearButton = new MinimalButton("Clear", DANGER_COLOR);
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clearButton.setPreferredSize(new Dimension(80, 36));
        
        loadExampleButton = new MinimalButton("Example", SUCCESS_COLOR);
        loadExampleButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadExampleButton.setPreferredSize(new Dimension(120, 36));
        
        // Simple status components
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(SECONDARY_TEXT);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 4));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(BORDER_COLOR);
        progressBar.setForeground(PRIMARY_COLOR);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Title
        JLabel titleLabel = new JLabel("CodeCola");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Transform Java code into readable recipes");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        
        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setBackground(BG_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(BG_COLOR);
        controlPanel.add(loadExampleButton);
        controlPanel.add(clearButton);
        controlPanel.add(convertButton); // Added convert button to control panel
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        // Content panels with clean cards - SIDE BY SIDE layout
        JPanel inputCard = new CleanCard("Java Code Input");
        inputCard.setLayout(new BorderLayout());
        
        JScrollPane inputScroll = new JScrollPane(codeInput);
        inputScroll.setBorder(null);
        inputScroll.setBackground(PANEL_COLOR);
        inputScroll.getViewport().setBackground(PANEL_COLOR);
        
        inputCard.add(inputScroll, BorderLayout.CENTER);
        
        // Output panel
        JPanel outputCard = new CleanCard("Recipe Output");
        outputCard.setLayout(new BorderLayout());
        
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setBorder(null);
        outputScroll.setBackground(PANEL_COLOR);
        outputScroll.getViewport().setBackground(PANEL_COLOR);
        
        outputCard.add(outputScroll, BorderLayout.CENTER);
        
        // Content layout - Changed from GridLayout(2, 1) to GridLayout(1, 2) for side-by-side
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.add(inputCard);
        contentPanel.add(outputCard);
        
        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(progressBar, BorderLayout.CENTER);
        
        // Assembly - Removed convert button panel since it's now in header
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        convertButton.addActionListener(e -> convertCodeWithProgress());
        clearButton.addActionListener(e -> clearAll());
        loadExampleButton.addActionListener(e -> loadExample());
        
        // Keyboard shortcuts
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
        statusLabel.setText("Converting code...");
        statusLabel.setForeground(WARNING_COLOR.darker());
        
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
                    statusLabel.setText("Conversion completed - " + recipeSteps.size() + " steps found");
                    statusLabel.setForeground(SUCCESS_COLOR.darker());
                    
                } catch (Exception e) {
                    statusLabel.setText("Error during conversion");
                    statusLabel.setForeground(DANGER_COLOR.darker());
                    output.setText("Error during conversion:\n" + e.getMessage());
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
        recipe.append("Recipe Generated from Code\n");
        recipe.append("=".repeat(35)).append("\n\n");
        
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
            recipe.append("No convertible instructions found!\n");
            recipe.append("Make sure your code uses Java syntax.\n");
        } else {
            recipe.append("\nRecipe completed!\n");
            recipe.append("=".repeat(35)).append("\n");
            recipe.append("Statistics:\n");
            recipe.append("   - Ingredients found: ").append(variables.size()).append("\n");
            recipe.append("   - Processing steps: ").append(recipeSteps.size()).append("\n");
            recipe.append("   - Estimated time: ").append(recipeSteps.size() * 2).append(" minutes\n");
        }
        
        return recipe.toString();
    }
    
    private String parseLine(String line, int lineNumber) {
        try {
            if (line.matches("(int|double|float|long)\\s+\\w+\\s*=\\s*[\\d.]+;")) {
                return parseNumericVariable(line);
            } else if (line.matches("String\\s+\\w+\\s*=\\s*\"[^\"]*\";")) {
                return parseStringVariable(line);
            } else if (line.matches("boolean\\s+\\w+\\s*=\\s*(true|false);")) {
                return parseBooleanVariable(line);
            } else if (line.matches("\\w+\\([^)]*\\);")) {
                return parseMethodCall(line);
            } else if (line.matches("if\\s*\\([^)]+\\)\\s*\\{?")) {
                return parseIfStatement(line);
            } else if (line.matches("for\\s*\\([^)]+\\)\\s*\\{?")) {
                return parseForLoop(line);
            } else if (line.matches("while\\s*\\([^)]+\\)\\s*\\{?")) {
                return parseWhileLoop(line);
            } else if (line.contains("class ")) {
                return parseClassDefinition(line);
            } else if (line.matches("(public|private|protected)?\\s*(static)?\\s*\\w+\\s+\\w+\\([^)]*\\)\\s*\\{?")) {
                return parseMethodDefinition(line);
            }
        } catch (Exception e) {
            return "Error in line " + lineNumber + ": " + e.getMessage();
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
            return String.format("Prepare %s %s %s", value, unit, name);
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
            return String.format("Select \"%s\" as %s", value, name);
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
            return String.format("Set %s to %s", name, value.equals("true") ? "on" : "off");
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
                    return String.format("Heat %s", formatArguments(args));
                case "mix":
                    return String.format("Mix %s", formatArguments(args));
                case "add":
                case "addflavor":
                    return String.format("Add %s", formatArguments(args));
                case "serve":
                    return String.format("Serve %s", formatArguments(args));
                case "wait":
                    return String.format("Wait %s", formatArguments(args));
                case "stir":
                    return String.format("Stir %s", formatArguments(args));
                default:
                    return String.format("Execute %s with %s", method, formatArguments(args));
            }
        }
        return "";
    }
    
    private String parseIfStatement(String line) {
        Pattern pattern = Pattern.compile("if\\s*\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String condition = matcher.group(1);
            return String.format("If %s, then:", condition);
        }
        return "";
    }
    
    private String parseForLoop(String line) {
        return "Repeat the following steps:";
    }
    
    private String parseWhileLoop(String line) {
        Pattern pattern = Pattern.compile("while\\s*\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String condition = matcher.group(1);
            return String.format("While %s, repeat:", condition);
        }
        return "";
    }
    
    private String parseClassDefinition(String line) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String className = matcher.group(1);
            return String.format("Start recipe: %s", className);
        }
        return "";
    }
    
    private String parseMethodDefinition(String line) {
        Pattern pattern = Pattern.compile("\\w+\\s+(\\w+)\\([^)]*\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String methodName = matcher.group(1);
            if (!methodName.equals("main")) {
                return String.format("Define process: %s", methodName);
            }
        }
        return "";
    }
    
    private String formatArguments(String args) {
        if (args.trim().isEmpty()) {
            return "everything";
        }
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            args = args.replace(entry.getKey(), entry.getValue());
        }
        
        args = args.replaceAll("\"", "");
        return args;
    }
    
    private String getUnitForVariable(String name, String type) {
        String lowerName = name.toLowerCase();
        if (lowerName.contains("water") || lowerName.contains("liquid")) {
            return "ml";
        } else if (lowerName.contains("sugar") || lowerName.contains("salt")) {
            return "g";
        } else if (lowerName.contains("temp")) {
            return "°C";
        } else if (lowerName.contains("time")) {
            return "minutes";
        }
        return type.equals("int") || type.equals("double") ? "units" : "";
    }
    
    private void clearAll() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Do you really want to clear everything?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            codeInput.setText("");
            output.setText("");
            variables.clear();
            recipeSteps.clear();
            statusLabel.setText("Ready");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }
    
    private void loadExample() {
        String[] examples = {
            "Simple Example",
            "Advanced Cola",
            "Cocktail Recipe",
            "Coffee Preparation"
        };
        
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Select an example:",
            "Load Example",
            JOptionPane.QUESTION_MESSAGE,
            null,
            examples,
            examples[0]
        );
        
        if (choice != null) {
            switch (choice) {
                case "Simple Example":
                    codeInput.setText(getDefaultCode());
                    break;
                case "Advanced Cola":
                    codeInput.setText(getAdvancedColaCode());
                    break;
                case "Cocktail Recipe":
                    codeInput.setText(getCocktailCode());
                    break;
                case "Coffee Preparation":
                    codeInput.setText(getCoffeeCode());
                    break;
            }
            statusLabel.setText("Example loaded: " + choice);
            statusLabel.setForeground(SUCCESS_COLOR.darker());
        }
    }
    
    private void showHelp() {
        String helpText = """
            CodeCola - Help
            
            Keyboard Shortcuts:
            • F5 - Convert code
            • F1 - Show this help
            
            Supported Java Constructs:
            • Variables (int, double, String, boolean)
            • Method calls
            • Control structures (if, for, while)
            • Class and method definitions
            
            Tips:
            • Use descriptive variable names
            • Use method names like 'boil', 'mix', 'add'
            • Comments are intelligently ignored
            """;
        
        JOptionPane.showMessageDialog(this, helpText, "CodeCola Help", JOptionPane.INFORMATION_MESSAGE);
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
    
    // Minimal button with clean design
    private static class MinimalButton extends JButton {
        private Color buttonColor;
        private boolean isHovered = false;
        
        public MinimalButton(String text, Color color) {
            super(text);
            this.buttonColor = color;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setForeground(Color.WHITE);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            Color bgColor = buttonColor;
            if (isHovered) {
                bgColor = buttonColor.brighter();
            }
            if (getModel().isPressed()) {
                bgColor = buttonColor.darker();
            }
            
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, 6, 6));
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Clean card component with minimal styling
    private static class CleanCard extends JPanel {
        private String title;
        
        public CleanCard(String title) {
            this.title = title;
            setBackground(PANEL_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
            ));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Title
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, 15, fm.getAscent() + 5);
            
            // Underline
            g2.setColor(BORDER_COLOR);
            g2.drawLine(15, fm.getHeight() + 8, getWidth() - 15, fm.getHeight() + 8);
            
            g2.dispose();
        }
        
        @Override
        public Insets getInsets() {
            return new Insets(35, 15, 15, 15);
        }
    }
    
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default if system look and feel is not available
            }
            
            new CodeColaGUI().setVisible(true);
        });
    }
}
