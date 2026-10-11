import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class Main {

    // =========================================================
    // COLORS
    // =========================================================
    static final Color DARK_GREEN = new Color(13, 82, 76);
    static final Color GREEN = new Color(25, 130, 119);
    static final Color LIGHT_GREEN = new Color(225, 244, 240);
    static final Color WHITE = Color.WHITE;
    static final Color BACKGROUND = new Color(245, 248, 249);
    static final Color TEXT = new Color(40, 50, 55);
    static final Color GRAY = new Color(110, 120, 125);
    static final Color RED = new Color(211, 47, 47);
    static final Color ORANGE = new Color(245, 124, 0);
    static final Color BLUE = new Color(30, 100, 180);
    static final Color SUCCESS = new Color(46, 125, 50);

    // =========================================================
    // FONTS
    // =========================================================
    static final Font TITLE = new Font("Segoe UI", Font.BOLD, 28);
    static final Font HEADER = new Font("Segoe UI", Font.BOLD, 20);
    static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // =========================================================
    // DATA CLASSES
    // =========================================================
    static class User {
        String username;
        String passwordHash;
        String fullName;
        String role;

        User(String username, String password, String fullName, String role) {
            this.username = username;
            this.passwordHash = hashPassword(password);
            this.fullName = fullName;
            this.role = role;
        }
    }

    static class Medicine {
        int id;
        String code;
        String name;
        String category;
        String supplier;
        String batch;
        LocalDate expiry;
        int quantity;
        int reorderLevel;
        double price;
        boolean prescriptionRequired;

        Medicine(int id, String code, String name, String category, String supplier,
                 String batch, LocalDate expiry, int quantity, int reorderLevel,
                 double price, boolean prescriptionRequired) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.category = category;
            this.supplier = supplier;
            this.batch = batch;
            this.expiry = expiry;
            this.quantity = quantity;
            this.reorderLevel = reorderLevel;
            this.price = price;
            this.prescriptionRequired = prescriptionRequired;
        }
    }

    static class Sale {
        String invoice;
        String medicine;
        int quantity;
        double total;
        String cashier;
        LocalDateTime date;

        Sale(String invoice, String medicine, int quantity, double total, String cashier) {
            this.invoice = invoice;
            this.medicine = medicine;
            this.quantity = quantity;
            this.total = total;
            this.cashier = cashier;
            this.date = LocalDateTime.now();
        }
    }

    // =========================================================
    // DATA STORAGE
    // =========================================================
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Medicine> medicines = new ArrayList<>();
    static ArrayList<Sale> sales = new ArrayList<>();

    static User loggedUser;
    static int medicineID = 1001;
    static int failedAttempts = 0;
    static long lastActivity = System.currentTimeMillis();

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initializeData();
            showLogin();
        });
    }

    // =========================================================
    // INITIAL DATA
    // =========================================================
    static void initializeData() {
        // USERS
        users.add(new User("admin", "Admin@123", "System Administrator", "ADMIN"));
        users.add(new User("pharmacist", "Pharma@123", "Main Pharmacist", "PHARMACIST"));
        users.add(new User("cashier", "Cash@123", "Front Desk Cashier", "CASHIER"));

        // MEDICINES
        medicines.add(new Medicine(medicineID++, "MED001", "Paracetamol 500mg", "Pain Relief", "HealthCare Pharma", "PCM-2026", LocalDate.of(2027, 8, 30), 150, 30, 25.00, false));
        medicines.add(new Medicine(medicineID++, "MED002", "Amoxicillin 500mg", "Antibiotic", "MediLife", "AMX-2026", LocalDate.of(2027, 5, 15), 80, 20, 65.00, true));
        medicines.add(new Medicine(medicineID++, "MED003", "Cetirizine 10mg", "Allergy", "PharmaCare", "CTZ-2026", LocalDate.of(2027, 3, 20), 15, 25, 35.00, false));
        medicines.add(new Medicine(medicineID++, "MED004", "Vitamin C 500mg", "Vitamins", "Wellness Labs", "VTC-2026", LocalDate.of(2027, 11, 10), 200, 40, 45.00, false));
        medicines.add(new Medicine(medicineID++, "MED005", "Omeprazole 20mg", "Gastric", "MediLife", "OMP-2026", LocalDate.of(2026, 11, 20), 12, 20, 55.00, false));
        medicines.add(new Medicine(medicineID++, "MED006", "Insulin Injection", "Diabetes", "LifeMed", "INS-2026", LocalDate.of(2027, 1, 15), 35, 10, 850.00, true));
    }

    // =========================================================
    // LOGIN WINDOW
    // =========================================================
    static void showLogin() {
        JFrame frame = new JFrame("Let's Heal | Secure Login");
        frame.setSize(520, 650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(DARK_GREEN);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(45, 20, 40, 20));

        JLabel logo = new JLabel("✚  LET'S HEAL");
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setForeground(WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 35));

        JLabel subtitle = new JLabel("PHARMACY MANAGEMENT SYSTEM");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(new Color(210, 235, 231));
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));

        header.add(logo);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        // LOGIN FORM
        JPanel form = new JPanel();
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(35, 55, 35, 55));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(TEXT);

        form.add(welcome);
        form.add(Box.createVerticalStrut(25));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(BOLD);
        form.add(userLabel);

        JTextField username = createTextField();
        form.add(username);

        form.add(Box.createVerticalStrut(15));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(BOLD);
        form.add(passLabel);

        JPasswordField password = new JPasswordField();
        styleField(password);
        form.add(password);

        form.add(Box.createVerticalStrut(25));

        JButton login = createButton("SIGN IN", GREEN);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        form.add(login);

        form.add(Box.createVerticalStrut(20));

        JLabel security = new JLabel(
                "<html><center>" +
                        "🔒 Secure Authentication<br>" +
                        "Role-Based Access Control<br>" +
                        "Login Attempt Protection" +
                        "</center></html>"
        );
        security.setAlignmentX(Component.CENTER_ALIGNMENT);
        security.setForeground(GRAY);
        security.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        form.add(security);

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);

        frame.add(main);

        login.addActionListener(e -> login(frame, username, password));
        password.addActionListener(e -> login(frame, username, password));

        frame.setVisible(true);
    }

    // =========================================================
    // LOGIN PROCESS
    // =========================================================
    static void login(JFrame frame, JTextField username, JPasswordField password) {
        if (failedAttempts >= 5) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Too many failed login attempts.\nAccess has been temporarily locked.",
                    "Security Alert",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String user = username.getText().trim();
        String pass = new String(password.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter username and password.",
                    "Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        for (User u : users) {
            if (u.username.equalsIgnoreCase(user) && verifyPassword(pass, u.passwordHash)) {
                loggedUser = u;
                failedAttempts = 0;
                lastActivity = System.currentTimeMillis();
                frame.dispose();
                showDashboard();
                return;
            }
        }

        failedAttempts++;
        JOptionPane.showMessageDialog(
                frame,
                "Invalid username or password.\nRemaining attempts: " + (5 - failedAttempts),
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
        );

        password.setText("");
    }

    // =========================================================
    // DASHBOARD
    // =========================================================
    static void showDashboard() {
        JFrame frame = new JFrame("Let's Heal | Pharmacy Management System");
        frame.setSize(1400, 800);
        frame.setMinimumSize(new Dimension(1100, 700));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        JPanel sidebar = createSidebar(frame);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);

        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);

        frame.add(root);

        showHome(content, frame);

        // SESSION TIMEOUT
        Timer timer = new Timer(30000, e -> {
            long inactive = System.currentTimeMillis() - lastActivity;
            if (inactive > 15 * 60 * 1000) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Your session has expired.",
                        "Security",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loggedUser = null;
                frame.dispose();
                showLogin();
            }
        });

        timer.start();
        frame.setVisible(true);
    }

    // =========================================================
    // SIDEBAR
    // =========================================================
    static JPanel createSidebar(JFrame frame) {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(DARK_GREEN);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 18, 20, 18));

        JLabel logo = new JLabel("✚  LET'S HEAL");
        logo.setForeground(WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sidebar.add(logo);

        JLabel system = new JLabel("PHARMACY MANAGEMENT");
        system.setForeground(new Color(160, 205, 198));
        system.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sidebar.add(system);

        sidebar.add(Box.createVerticalStrut(35));

        addMenuButton(sidebar, "▣   Dashboard", () -> {
            JPanel content = getContent(frame);
            showHome(content, frame);
        });

        if (loggedUser.role.equals("ADMIN") || loggedUser.role.equals("PHARMACIST")) {
            addMenuButton(sidebar, "▤   Medicine Inventory", () -> {
                JPanel content = getContent(frame);
                showInventory(content, frame);
            });
        }

        addMenuButton(sidebar, "▣   New Sale", () -> {
            JPanel content = getContent(frame);
            showSale(content, frame);
        });

        addMenuButton(sidebar, "▤   Sales History", () -> {
            JPanel content = getContent(frame);
            showSalesHistory(content, frame);
        });

        if (loggedUser.role.equals("ADMIN")) {
            addMenuButton(sidebar, "♙   User Management", () -> {
                JPanel content = getContent(frame);
                showUsers(content, frame);
            });
        }

        addMenuButton(sidebar, "⚙   Change Password", () -> changePassword(frame));

        sidebar.add(Box.createVerticalGlue());

        JLabel currentUser = new JLabel(
                "<html>" +
                        "<font color='white'>" + loggedUser.fullName + "</font><br>" +
                        "<font color='#A0CDC6'>" + loggedUser.role + "</font>" +
                        "</html>"
        );

        sidebar.add(currentUser);
        sidebar.add(Box.createVerticalStrut(15));

        addMenuButton(sidebar, "⇥   Logout", () -> {
            loggedUser = null;
            frame.dispose();
            showLogin();
        });

        return sidebar;
    }

    static void addMenuButton(JPanel sidebar, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(215, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(WHITE);
        button.setBackground(DARK_GREEN);
        button.setFont(NORMAL);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 12, 10, 10));

        button.addActionListener(e -> {
            lastActivity = System.currentTimeMillis();
            action.run();
        });

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    // =========================================================
    // HOME DASHBOARD
    // =========================================================
    static void showHome(JPanel content, JFrame frame) {
        content.removeAll();

        JPanel top = createTopBar();
        content.add(top, BorderLayout.NORTH);

        JPanel page = new JPanel(new BorderLayout(20, 20));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(25, 30, 25, 30));

        // WELCOME
        JPanel welcome = new JPanel(new BorderLayout());
        welcome.setOpaque(false);

        JLabel title = new JLabel("Good Day, " + loggedUser.fullName);
        title.setFont(TITLE);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Here is your pharmacy overview.");
        subtitle.setForeground(GRAY);
        subtitle.setFont(NORMAL);

        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));

        welcomeText.add(title);
        welcomeText.add(Box.createVerticalStrut(5));
        welcomeText.add(subtitle);

        welcome.add(welcomeText, BorderLayout.WEST);
        page.add(welcome, BorderLayout.NORTH);

        // STATISTICS
        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 15));
        cards.setOpaque(false);

        cards.add(card("TOTAL MEDICINES", String.valueOf(medicines.size()), "Inventory items", GREEN));
        cards.add(card("LOW STOCK", String.valueOf(lowStock()), "Need attention", ORANGE));
        cards.add(card("EXPIRING SOON", String.valueOf(expiring()), "Within 30 days", RED));
        cards.add(card("TODAY'S SALES", "Rs. " + money(todaySales()), "Revenue", SUCCESS));

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);

        // ALERTS
        JPanel alert = createAlerts();
        center.add(alert, BorderLayout.CENTER);

        page.add(center, BorderLayout.CENTER);
        content.add(page, BorderLayout.CENTER);

        content.revalidate();
        content.repaint();
    }

    // =========================================================
    // TOP BAR
    // =========================================================
    static JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);
        top.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Pharmacy Control Center");
        title.setFont(HEADER);
        title.setForeground(TEXT);

        JLabel user = new JLabel("●  " + loggedUser.fullName + "   |   " + loggedUser.role);
        user.setFont(BOLD);
        user.setForeground(GREEN);

        top.add(title, BorderLayout.WEST);
        top.add(user, BorderLayout.EAST);

        return top;
    }

    // =========================================================
    // DASHBOARD CARD
    // =========================================================
    static JPanel card(String title, String value, String subtitle, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 232)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(color);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(GRAY);

        panel.add(t, BorderLayout.NORTH);
        panel.add(v, BorderLayout.CENTER);
        panel.add(s, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================
    // ALERTS
    // =========================================================
    static JPanel createAlerts() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 232)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Inventory Alerts");
        title.setFont(HEADER);
        title.setForeground(TEXT);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Medicine", "Stock", "Expiry", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Medicine m : medicines) {
            String status = medicineStatus(m);
            if (!status.equals("OK")) {
                model.addRow(new Object[]{m.name, m.quantity, m.expiry, status});
            }
        }

        JTable table = new JTable(model);
        styleTable(table);

        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"All medicines", "-", "-", "✓ HEALTHY"});
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // INVENTORY WITH SEARCH & FILTER
    // =========================================================
    static void showInventory(JPanel content, JFrame frame) {
        content.removeAll();
        content.add(createTopBar(), BorderLayout.NORTH);

        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(20, 25, 25, 25));

        JLabel title = new JLabel("Medicine Inventory");
        title.setFont(TITLE);
        title.setForeground(TEXT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        JButton add = createButton("＋ Add Medicine", GREEN);
        header.add(add, BorderLayout.EAST);

        // SEARCH AND FILTER BAR
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(BOLD);
        JTextField searchField = createTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(header, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.SOUTH);

        page.add(topContainer, BorderLayout.NORTH);

        String[] columns = {"ID", "Code", "Medicine", "Category", "Supplier", "Expiry", "Stock", "Price", "Rx", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        loadMedicineTable(model);

        JTable table = new JTable(model);
        styleTable(table);

        // Table Sorter for Dynamic Search Filter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        page.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton update = createButton("↻ Update Stock", BLUE);
        JButton delete = createButton("Delete", RED);

        bottom.add(update);
        bottom.add(delete);

        page.add(bottom, BorderLayout.SOUTH);
        content.add(page, BorderLayout.CENTER);

        content.revalidate();
        content.repaint();

        // ADD
        add.addActionListener(e -> addMedicine(frame, model));

        // UPDATE
        update.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                message(frame, "Please select a medicine.", "Selection", ORANGE);
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);
            int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            Medicine m = findMedicine(id);
            if (m == null) return;

            String input = JOptionPane.showInputDialog(frame, "Enter new stock quantity:", m.quantity);
            if (input == null) return;

            try {
                int newQty = Integer.parseInt(input);
                if (newQty < 0) throw new Exception();

                m.quantity = newQty;
                loadMedicineTable(model);
            } catch (Exception ex) {
                message(frame, "Enter a valid positive number.", "Invalid", RED);
            }
        });

        // DELETE
        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                message(frame, "Please select a medicine.", "Selection", ORANGE);
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);
            int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            Medicine m = findMedicine(id);
            if (m == null) return;

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Delete " + m.name + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                medicines.remove(m);
                loadMedicineTable(model);
            }
        });
    }

    static void loadMedicineTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Medicine m : medicines) {
            model.addRow(new Object[]{
                    m.id,
                    m.code,
                    m.name,
                    m.category,
                    m.supplier,
                    m.expiry,
                    m.quantity,
                    "Rs. " + money(m.price),
                    m.prescriptionRequired ? "YES" : "NO",
                    medicineStatus(m)
            });
        }
    }

    // =========================================================
    // ADD MEDICINE
    // =========================================================
    static void addMedicine(JFrame frame, DefaultTableModel model) {
        JTextField code = createTextField();
        JTextField name = createTextField();
        JTextField category = createTextField();
        JTextField supplier = createTextField();
        JTextField expiry = createTextField();
        JTextField quantity = createTextField();
        JTextField reorder = createTextField();
        JTextField price = createTextField();
        JCheckBox prescription = new JCheckBox("Prescription Required (Rx)");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Medicine Code")); form.add(code);
        form.add(new JLabel("Medicine Name")); form.add(name);
        form.add(new JLabel("Category")); form.add(category);
        form.add(new JLabel("Supplier")); form.add(supplier);
        form.add(new JLabel("Expiry Date (YYYY-MM-DD)")); form.add(expiry);
        form.add(new JLabel("Quantity")); form.add(quantity);
        form.add(new JLabel("Reorder Level")); form.add(reorder);
        form.add(new JLabel("Unit Price")); form.add(price);
        form.add(new JLabel("Prescription Status")); form.add(prescription);

        int result = JOptionPane.showConfirmDialog(
                frame, form, "Add New Medicine", JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            if (name.getText().isBlank() || code.getText().isBlank()) {
                throw new Exception("Medicine code and name are required.");
            }

            LocalDate expiryDate = LocalDate.parse(expiry.getText());
            int qty = Integer.parseInt(quantity.getText());
            int reorderLevel = Integer.parseInt(reorder.getText());
            double unitPrice = Double.parseDouble(price.getText());

            if (qty < 0 || reorderLevel < 0 || unitPrice < 0) {
                throw new Exception("Numbers cannot be negative.");
            }

            Medicine medicine = new Medicine(
                    medicineID++,
                    code.getText().trim(),
                    name.getText().trim(),
                    category.getText().trim(),
                    supplier.getText().trim(),
                    "BATCH-" + medicineID,
                    expiryDate,
                    qty,
                    reorderLevel,
                    unitPrice,
                    prescription.isSelected()
            );

            medicines.add(medicine);
            loadMedicineTable(model);
            message(frame, "Medicine added successfully!", "Success", SUCCESS);

        } catch (Exception ex) {
            message(frame, "Please check your entered values.", "Invalid Data", RED);
        }
    }

    // =========================================================
    // SALES WITH RECEIPT GENERATION & RX VALIDATION
    // =========================================================
    static void showSale(JPanel content, JFrame frame) {
        content.removeAll();
        content.add(createTopBar(), BorderLayout.NORTH);

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(35, 60, 35, 60));

        JLabel title = new JLabel("New Pharmacy Sale");
        title.setFont(TITLE);
        title.setForeground(TEXT);
        page.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Medicine> medicineBox = new JComboBox<>();
        for (Medicine m : medicines) {
            if (m.quantity > 0) medicineBox.addItem(m);
        }

        medicineBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Medicine) {
                    Medicine m = (Medicine) value;
                    setText(m.name + "  |  Stock: " + m.quantity + "  |  Rs. " + money(m.price) + (m.prescriptionRequired ? " [Rx Required]" : ""));
                }
                return this;
            }
        });

        JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JCheckBox rxVerified = new JCheckBox("Prescription Verified by Pharmacist");
        rxVerified.setFont(BOLD);
        rxVerified.setForeground(RED);
        rxVerified.setVisible(false);

        JLabel total = new JLabel("Rs. 0.00");
        total.setFont(new Font("Segoe UI", Font.BOLD, 28));
        total.setForeground(SUCCESS);

        addRow(form, gbc, 0, "Medicine", medicineBox);
        addRow(form, gbc, 1, "Quantity", qty);
        addRow(form, gbc, 2, "Prescription Check", rxVerified);
        addRow(form, gbc, 3, "Total", total);

        JButton complete = createButton("✓  COMPLETE SALE", SUCCESS);
        gbc.gridx = 1;
        gbc.gridy = 4;
        form.add(complete, gbc);

        page.add(form, BorderLayout.CENTER);
        content.add(page, BorderLayout.CENTER);

        content.revalidate();
        content.repaint();

        Runnable calculate = () -> {
            Medicine m = (Medicine) medicineBox.getSelectedItem();
            if (m != null) {
                int q = (Integer) qty.getValue();
                total.setText("Rs. " + money(m.price * q));
                rxVerified.setVisible(m.prescriptionRequired);
            }
        };

        medicineBox.addActionListener(e -> calculate.run());
        qty.addChangeListener(e -> calculate.run());

        complete.addActionListener(e -> {
            Medicine m = (Medicine) medicineBox.getSelectedItem();
            if (m == null) return;

            if (m.prescriptionRequired && !rxVerified.isSelected()) {
                message(frame, "This medicine requires a valid prescription. Please verify before proceeding.", "Rx Warning", ORANGE);
                return;
            }

            int q = (Integer) qty.getValue();
            if (q > m.quantity) {
                message(frame, "Not enough stock available.", "Stock Error", RED);
                return;
            }

            double amount = m.price * q;
            String invoice = "LH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            m.quantity -= q;
            Sale sale = new Sale(invoice, m.name, q, amount, loggedUser.username);
            sales.add(sale);

            // Print Receipt Popup
            showReceiptDialog(frame, sale, m.price);

            medicineBox.removeAllItems();
            for (Medicine medicine : medicines) {
                if (medicine.quantity > 0) medicineBox.addItem(medicine);
            }

            qty.setValue(1);
            rxVerified.setSelected(false);
            calculate.run();
        });

        calculate.run();
    }

    // =========================================================
    // RECEIPT GENERATOR DIALOG
    // =========================================================
    static void showReceiptDialog(JFrame frame, Sale sale, double unitPrice) {
        JDialog dialog = new JDialog(frame, "Sales Receipt", true);
        dialog.setSize(380, 480);
        dialog.setLocationRelativeTo(frame);

        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("               LET'S HEAL                \n");
        sb.append("       PHARMACY MANAGEMENT SYSTEM        \n");
        sb.append("=========================================\n");
        sb.append("Invoice:   ").append(sale.invoice).append("\n");
        sb.append("Date:      ").append(sale.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("Cashier:   ").append(sale.cashier).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append("Item:      ").append(sale.medicine).append("\n");
        sb.append("Qty:       ").append(sale.quantity).append("\n");
        sb.append("Unit Price:Rs. ").append(money(unitPrice)).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append("TOTAL:     Rs. ").append(money(sale.total)).append("\n");
        sb.append("=========================================\n");
        sb.append("        Thank you & Get Well Soon!       \n");
        sb.append("=========================================\n");

        text.setText(sb.toString());

        JButton close = createButton("Print / Close", GREEN);
        close.addActionListener(e -> dialog.dispose());

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(text), BorderLayout.CENTER);
        p.add(close, BorderLayout.SOUTH);

        dialog.add(p);
        dialog.setVisible(true);
    }

    // =========================================================
    // SALES HISTORY WITH CSV EXPORT
    // =========================================================
    static void showSalesHistory(JPanel content, JFrame frame) {
        content.removeAll();
        content.add(createTopBar(), BorderLayout.NORTH);

        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Sales History");
        title.setFont(TITLE);
        title.setForeground(TEXT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        JButton export = createButton("🗎 Export CSV", BLUE);
        header.add(export, BorderLayout.EAST);

        page.add(header, BorderLayout.NORTH);

        String[] columns = {"Invoice", "Medicine", "Quantity", "Total", "Cashier", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Sale s : sales) {
            model.addRow(new Object[]{
                    s.invoice,
                    s.medicine,
                    s.quantity,
                    "Rs. " + money(s.total),
                    s.cashier,
                    s.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        page.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(page, BorderLayout.CENTER);

        content.revalidate();
        content.repaint();

        export.addActionListener(e -> {
            try {
                File file = new File("sales_report.csv");
                FileWriter writer = new FileWriter(file);
                writer.write("Invoice,Medicine,Quantity,Total,Cashier,Date\n");
                for (Sale s : sales) {
                    writer.write(s.invoice + "," + s.medicine + "," + s.quantity + "," + s.total + "," + s.cashier + "," + s.date.toString() + "\n");
                }
                writer.close();
                message(frame, "Exported sales history to sales_report.csv successfully!", "Exported", SUCCESS);
            } catch (IOException ex) {
                message(frame, "Failed to write CSV file.", "Error", RED);
            }
        });
    }

    // =========================================================
    // USER MANAGEMENT
    // =========================================================
    static void showUsers(JPanel content, JFrame frame) {
        content.removeAll();
        content.add(createTopBar(), BorderLayout.NORTH);

        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("User Management");
        title.setFont(TITLE);
        title.setForeground(TEXT);
        page.add(title, BorderLayout.NORTH);

        String[] columns = {"Username", "Full Name", "Role"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (User u : users) {
            model.addRow(new Object[]{u.username, u.fullName, u.role});
        }

        JTable table = new JTable(model);
        styleTable(table);

        page.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = createButton("＋ Create User", GREEN);
        JButton delete = createButton("Delete User", RED);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(add);
        bottom.add(delete);

        page.add(bottom, BorderLayout.SOUTH);
        content.add(page, BorderLayout.CENTER);

        content.revalidate();
        content.repaint();

        add.addActionListener(e -> {
            JTextField username = createTextField();
            JTextField fullName = createTextField();
            JPasswordField password = new JPasswordField();
            JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "PHARMACIST", "CASHIER"});

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Username")); form.add(username);
            form.add(new JLabel("Full Name")); form.add(fullName);
            form.add(new JLabel("Password")); form.add(password);
            form.add(new JLabel("Role")); form.add(role);

            int result = JOptionPane.showConfirmDialog(frame, form, "Create User", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String pass = new String(password.getPassword());
            if (username.getText().isBlank() || pass.isBlank()) {
                message(frame, "Username and password are required.", "Error", RED);
                return;
            }

            if (!strongPassword(pass)) {
                message(frame, "Password must contain at least:\n8 characters, uppercase, lowercase,\nnumber and special character.", "Weak Password", RED);
                return;
            }

            users.add(new User(username.getText().trim(), pass, fullName.getText().trim(), role.getSelectedItem().toString()));
            model.addRow(new Object[]{username.getText().trim(), fullName.getText().trim(), role.getSelectedItem().toString()});
            message(frame, "User created successfully.", "Success", SUCCESS);
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                message(frame, "Please select a user to delete.", "Selection", ORANGE);
                return;
            }

            String uname = model.getValueAt(row, 0).toString();
            if (uname.equalsIgnoreCase(loggedUser.username)) {
                message(frame, "You cannot delete your own account while logged in.", "Denied", RED);
                return;
            }

            users.removeIf(u -> u.username.equalsIgnoreCase(uname));
            model.removeRow(row);
            message(frame, "User deleted.", "Success", SUCCESS);
        });
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================
    static void changePassword(JFrame frame) {
        JPasswordField current = new JPasswordField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Current Password")); form.add(current);
        form.add(new JLabel("New Password")); form.add(newPassword);
        form.add(new JLabel("Confirm Password")); form.add(confirm);

        int result = JOptionPane.showConfirmDialog(frame, form, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String oldPass = new String(current.getPassword());
        String newPass = new String(newPassword.getPassword());
        String confirmPass = new String(confirm.getPassword());

        if (!verifyPassword(oldPass, loggedUser.passwordHash)) {
            message(frame, "Current password is incorrect.", "Security Error", RED);
            return;
        }

        if (!strongPassword(newPass)) {
            message(frame, "New password is not strong enough.", "Weak Password", RED);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            message(frame, "Passwords do not match.", "Error", RED);
            return;
        }

        loggedUser.passwordHash = hashPassword(newPass);
        message(frame, "Password changed successfully.", "Success", SUCCESS);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================
    static JPanel getContent(JFrame frame) {
        Container root = frame.getContentPane();
        if (root.getComponentCount() < 2) return null;
        return (JPanel) root.getComponent(1);
    }

    static void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    static JTextField createTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    static void styleField(JTextField field) {
        field.setFont(NORMAL);
        field.setPreferredSize(new Dimension(220, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 222)),
                new EmptyBorder(7, 10, 7, 10)
        ));
    }

    static JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    static void styleTable(JTable table) {
        table.setFont(NORMAL);
        table.setRowHeight(32);
        table.setGridColor(new Color(230, 235, 237));
        table.setSelectionBackground(LIGHT_GREEN);
        table.setSelectionForeground(TEXT);

        table.getTableHeader().setFont(BOLD);
        table.getTableHeader().setBackground(DARK_GREEN);
        table.getTableHeader().setForeground(WHITE);
    }

    static Medicine findMedicine(int id) {
        for (Medicine m : medicines) {
            if (m.id == id) return m;
        }
        return null;
    }

    static int lowStock() {
        int count = 0;
        for (Medicine m : medicines) {
            if (m.quantity <= m.reorderLevel) count++;
        }
        return count;
    }

    static int expiring() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (Medicine m : medicines) {
            if (!m.expiry.isAfter(today.plusDays(30))) count++;
        }
        return count;
    }

    static double todaySales() {
        double total = 0;
        for (Sale s : sales) {
            if (s.date.toLocalDate().equals(LocalDate.now())) {
                total += s.total;
            }
        }
        return total;
    }

    static String medicineStatus(Medicine m) {
        LocalDate today = LocalDate.now();
        if (m.expiry.isBefore(today)) return "EXPIRED";
        if (!m.expiry.isAfter(today.plusDays(30))) return "EXPIRING";
        if (m.quantity <= m.reorderLevel) return "LOW STOCK";
        return "OK";
    }

    static String money(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }

    static void message(Component parent, String text, String title, Color color) {
        JOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // PASSWORD SECURITY
    // =========================================================
    static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    static boolean strongPassword(String password) {
        if (password.length() < 8) return false;

        boolean upper = false, lower = false, number = false, special = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            if (Character.isLowerCase(c)) lower = true;
            if (Character.isDigit(c)) number = true;
            if (!Character.isLetterOrDigit(c)) special = true;
        }

        return upper && lower && number && special;
    }
}