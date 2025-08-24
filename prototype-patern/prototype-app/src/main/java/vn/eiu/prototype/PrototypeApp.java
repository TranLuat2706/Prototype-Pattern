package vn.eiu.prototype;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PrototypeApp {
    private PrototypeRegistry registry = new PrototypeRegistry();
    private DefaultListModel<String> registryModel = new DefaultListModel<>();
    private DefaultListModel<String> clonesModel = new DefaultListModel<>();
    private List<Prototype> clones = new ArrayList<>();
    private JFrame frame;

    // default auto-save file for registry
    private final Path DEFAULT_REGISTRY_PATH =
            Paths.get(System.getProperty("user.home"), ".prototype_app", "prototypes.json");

    public PrototypeApp() {
        // try to auto-load saved registry; if not present, seed defaults
        try {
            if (Files.exists(DEFAULT_REGISTRY_PATH)) {
                loadPrototypesFromJsonFile(DEFAULT_REGISTRY_PATH.toFile(), false);
            } else {
                seedRegistry();
            }
        } catch(Exception ex) {
            // fallback to seed if load fails
            seedRegistry();
        }
        SwingUtilities.invokeLater(this::createAndShowGui);
    }

    private void seedRegistry() {
        // only seed if registry empty
        if (!registry.keys().isEmpty()) return;

        Resume resume = new Resume("Nguyen Van A", "Junior Java Developer", "Objective: ...");
        resume.addExperience("Internship at Company X");
        resume.addExperience("Volunteer - Open Source project");
        resume.addTag("CV"); resume.addTag("Template");

        Report report = new Report("Tran Thi B", "Quarterly Report Q1", "Summary: ...", 10);
        report.addTag("Report"); report.addTag("Finance");

        Invoice invoice = new Invoice("INV-2025-001", "Invoice for Project A", "Bill for services", 1250.0);
        invoice.addTag("Invoice"); invoice.addTag("Billing");

        registry.registerPrototype("resume-basic", resume);
        registry.registerPrototype("report-q1", report);
        registry.registerPrototype("invoice-sample", invoice);

        for (String k: registry.keys()) registryModel.addElement(k);
    }

    private void createAndShowGui() {
        frame = new JFrame("Prototype Pattern - Demo (GUI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,520);
        frame.setLayout(new BorderLayout());

        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Registry (prototypes)"));
        JList<String> regList = new JList<>(registryModel);
        regList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        left.add(new JScrollPane(regList), BorderLayout.CENTER);

        JPanel leftTopButtons = new JPanel(new GridLayout(1,5));
        JButton btnClone = new JButton("Clone ->"); leftTopButtons.add(btnClone);
        JButton btnNewProto = new JButton("New Prototype"); leftTopButtons.add(btnNewProto);
        JButton btnSaveRegistryFile = new JButton("Save Registry"); leftTopButtons.add(btnSaveRegistryFile);
        JButton btnLoadRegistryFile = new JButton("Load Registry"); leftTopButtons.add(btnLoadRegistryFile);
        JButton btnSaveClones = new JButton("Save Clones"); leftTopButtons.add(btnSaveClones);
        left.add(leftTopButtons, BorderLayout.NORTH);

        JPanel leftButtons = new JPanel(new GridLayout(1,2));
        JButton btnRefresh = new JButton("Refresh"); leftButtons.add(btnRefresh);
        JButton btnExportProtos = new JButton("Export Prototypes"); leftButtons.add(btnExportProtos);
        left.add(leftButtons, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Clones"));
        JList<String> clonesList = new JList<>(clonesModel);
        clonesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        right.add(new JScrollPane(clonesList), BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new GridLayout(1,4));
        JButton btnEdit = new JButton("Edit");
        JButton btnShow = new JButton("Show");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear All");
        rightButtons.add(btnEdit); rightButtons.add(btnShow);
        rightButtons.add(btnDelete); rightButtons.add(btnClear);
        right.add(rightButtons, BorderLayout.SOUTH);

        frame.add(left, BorderLayout.WEST);
        frame.add(right, BorderLayout.CENTER);

        // --------- actions ----------
        btnClone.addActionListener(e -> {
            String key = regList.getSelectedValue();
            if (key==null) {
                JOptionPane.showMessageDialog(frame, "Select a prototype from registry first.");
                return;
            }
            Prototype master = registry.getPrototype(key);
            if (master==null) return;
            Prototype c = master.clonePrototype();
            clones.add(c);
            clonesModel.addElement(c.getDescription());
            JOptionPane.showMessageDialog(frame, "Cloned: " + c.getDescription());
        });

        btnNewProto.addActionListener(e -> {
            String[] types = {"Resume","Report","Invoice"};
            String type = (String) JOptionPane.showInputDialog(
                    frame, "Choose prototype type:", "New Prototype",
                    JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
            if (type==null) return;
            try {
                Prototype p = createPrototypeByDialog(type);
                if (p==null) return;
                String key = JOptionPane.showInputDialog(
                        frame, "Enter key to register prototype (e.g. 'resume-mytemplate'):");
                if (key==null || key.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Invalid key.");
                    return;
                }
                registry.registerPrototype(key.trim(), p);
                registryModel.addElement(key.trim());
                JOptionPane.showMessageDialog(frame, "Registered prototype with key: " + key.trim());
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error creating prototype: " + ex.getMessage());
            }
        });

        btnSaveClones.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showSaveDialog(frame);
            if (res==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try { saveClonesToJsonFile(f);
                    JOptionPane.showMessageDialog(frame, "Saved clones to: " + f.getAbsolutePath());
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Save error: " + ex.getMessage());
                }
            }
        });

        btnSaveRegistryFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showSaveDialog(frame);
            if (res==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try { saveRegistryToJsonFile(f);
                    JOptionPane.showMessageDialog(frame, "Saved registry to: " + f.getAbsolutePath());
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Save registry error: " + ex.getMessage());
                }
            }
        });

        btnLoadRegistryFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(frame);
            if (res==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    loadPrototypesFromJsonFile(f, true);
                    refreshRegistryModel();
                    JOptionPane.showMessageDialog(frame, "Loaded registry from: " + f.getAbsolutePath());
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Load registry error: " + ex.getMessage());
                }
            }
        });

        btnRefresh.addActionListener(e -> refreshRegistryModel());
        btnExportProtos.addActionListener(e -> {
            String all = registry.keys().stream().collect(Collectors.joining(", "));
            JOptionPane.showMessageDialog(frame, "Prototype keys: " + all);
        });

        btnShow.addActionListener(e -> {
            int idx = clonesList.getSelectedIndex();
            if (idx<0) {
                JOptionPane.showMessageDialog(frame, "Select a clone to view.");
                return;
            }
            JOptionPane.showMessageDialog(frame, clones.get(idx).getDescription());
        });
        btnDelete.addActionListener(e -> {
            int idx = clonesList.getSelectedIndex();
            if (idx<0) return;
            clones.remove(idx);
            clonesModel.remove(idx);
        });
        btnClear.addActionListener(e -> {
            clones.clear();
            clonesModel.clear();
        });

        btnEdit.addActionListener(e -> {
            int idx = clonesList.getSelectedIndex();
            if (idx<0) {
                JOptionPane.showMessageDialog(frame, "Select a clone to edit.");
                return;
            }
            Prototype p = clones.get(idx);
            if (p instanceof Resume) editResumeDialog((Resume)p, idx);
            else if (p instanceof Report) editReportDialog((Report)p, idx);
            else if (p instanceof Invoice) editInvoiceDialog((Invoice)p, idx);
            else JOptionPane.showMessageDialog(frame, "Type not supported for editing.");
        });

        // auto-save registry on close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Path parent = DEFAULT_REGISTRY_PATH.getParent();
                    if (!Files.exists(parent)) Files.createDirectories(parent);
                    saveRegistryToJsonFile(DEFAULT_REGISTRY_PATH.toFile());
                } catch(Exception ex) {
                    // ignore save errors
                }
                super.windowClosing(e);
            }
        });

        frame.setVisible(true);
    }

    private void refreshRegistryModel() {
        registryModel.clear();
        for (String k: registry.keys()) registryModel.addElement(k);
    }

    // create prototype by asking user for fields
    private Prototype createPrototypeByDialog(String type) {
        switch(type) {
            case "Resume": {
                String candidate = JOptionPane.showInputDialog(frame, "Candidate name:");
                if (candidate==null) return null;
                String title = JOptionPane.showInputDialog(frame, "Title:");
                String content = JOptionPane.showInputDialog(frame, "Content:");
                String exps = JOptionPane.showInputDialog(frame, "Experiences (comma-separated):");
                String tags = JOptionPane.showInputDialog(frame, "Tags (comma-separated):");
                Resume r = new Resume(candidate, title==null? "":title, content==null? "":content);
                if (exps!=null && !exps.trim().isEmpty()) {
                    for (String e: exps.split(",")) r.addExperience(e.trim());
                }
                if (tags!=null && !tags.trim().isEmpty()) {
                    for (String t: tags.split(",")) r.addTag(t.trim());
                }
                return r;
            }
            case "Report": {
                String author = JOptionPane.showInputDialog(frame, "Author:");
                if (author==null) return null;
                String title = JOptionPane.showInputDialog(frame, "Title:");
                String content = JOptionPane.showInputDialog(frame, "Content:");
                String pagesS = JOptionPane.showInputDialog(frame, "Pages (number):");
                int pages = 1;
                try { pages = Integer.parseInt(pagesS); } catch(Exception e) {}
                String tags = JOptionPane.showInputDialog(frame, "Tags (comma-separated):");
                Report rep = new Report(author, title==null? "":title, content==null? "":content, pages);
                if (tags!=null && !tags.trim().isEmpty())
                    for (String t: tags.split(",")) rep.addTag(t.trim());
                return rep;
            }
            case "Invoice": {
                String inv = JOptionPane.showInputDialog(frame, "Invoice number:");
                if (inv==null) return null;
                String title = JOptionPane.showInputDialog(frame, "Title:");
                String content = JOptionPane.showInputDialog(frame, "Content:");
                String amountS = JOptionPane.showInputDialog(frame, "Amount (number):");
                double amount = 0.0;
                try { amount = Double.parseDouble(amountS); } catch(Exception e) {}
                String tags = JOptionPane.showInputDialog(frame, "Tags (comma-separated):");
                Invoice invoice = new Invoice(inv, title==null? "":title, content==null? "":content, amount);
                if (tags!=null && !tags.trim().isEmpty())
                    for (String t: tags.split(",")) invoice.addTag(t.trim());
                return invoice;
            }
            default: return null;
        }
    }

    // Edit dialogs update models
    private void editResumeDialog(Resume r, int idx) {
        String newName = JOptionPane.showInputDialog(
                frame, "Enter new candidate name:", r.getDescription());
        if (newName!=null && !newName.trim().isEmpty()) {
            r.setCandidateName(newName.trim());
            clonesModel.set(idx, r.getDescription());
        }
    }
    private void editReportDialog(Report rep, int idx) {
        String newAuthor = JOptionPane.showInputDialog(frame, "Enter new author:", null);
        if (newAuthor!=null && !newAuthor.trim().isEmpty()) {
            rep.setAuthor(newAuthor.trim());
            clonesModel.set(idx, rep.getDescription());
        }
    }
    private void editInvoiceDialog(Invoice inv, int idx) {
        String newNum = JOptionPane.showInputDialog(frame, "Enter new invoice number:", null);
        if (newNum!=null && !newNum.trim().isEmpty()) {
            inv.setInvoiceNumber(newNum.trim());
            clonesModel.set(idx, inv.getDescription());
        }
    }

    // --- JSON save/load helpers for clones ---
    private void saveClonesToJsonFile(File f) throws IOException {
        JsonArray arr = new JsonArray();
        for (Prototype p: clones) {
            JsonObject obj = protoToJson(p);
            arr.add(obj);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            gson.toJson(arr, w);
        }
    }

    private void loadClonesFromJsonFile(File f) throws IOException {
        String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        JsonArray arr = JsonParser.parseString(content).getAsJsonArray();
        for (JsonElement el: arr) {
            JsonObject obj = el.getAsJsonObject();
            Prototype p = jsonToPrototype(obj);
            if (p!=null) {
                clones.add(p);
                clonesModel.addElement(p.getDescription());
            }
        }
    }

    // --- Save/Load registry prototypes ---
    private void saveRegistryToJsonFile(File f) throws IOException {
        JsonArray arr = new JsonArray();
        for (String key: registry.keys()) {
            Prototype p = registry.getPrototype(key);
            JsonObject obj = protoToJson(p);
            obj.addProperty("registryKey", key);
            arr.add(obj);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            gson.toJson(arr, w);
        }
    }

    private void loadPrototypesFromJsonFile(File f, boolean interactive) throws IOException {
        String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        JsonArray arr = JsonParser.parseString(content).getAsJsonArray();
        for (JsonElement el: arr) {
            JsonObject obj = el.getAsJsonObject();
            Prototype p = jsonToPrototype(obj);
            String key = obj.has("registryKey") ? obj.get("registryKey").getAsString() : null;
            if (key==null || key.trim().isEmpty()) {
                key = obj.has("type") ?
                        obj.get("type").getAsString().toLowerCase()+"-"+System.currentTimeMillis()
                        : "proto-"+System.currentTimeMillis();
            }
            // Handle duplicate keys
            if (registry.getPrototype(key) != null) {
                if (interactive && frame != null) {
                    String[] options = {"Overwrite", "Skip", "Rename"};
                    int choice = JOptionPane.showOptionDialog(frame,
                            "Prototype key '" + key + "' already exists. What do you want to do?",
                            "Duplicate prototype key",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    if (choice == 0) { // Overwrite
                        registry.registerPrototype(key, p);
                    } else if (choice == 1) { // Skip
                        // do nothing
                    } else if (choice == 2) { // Rename
                        String newKey = null;
                        while (true) {
                            newKey = JOptionPane.showInputDialog(frame,
                                    "Enter new unique key:", key + "-copy");
                            if (newKey == null) break; // cancel
                            newKey = newKey.trim();
                            if (newKey.isEmpty()) continue;
                            if (registry.getPrototype(newKey) == null) {
                                registry.registerPrototype(newKey, p);
                                break;
                            } else {
                                JOptionPane.showMessageDialog(frame,
                                        "Key already exists, choose a different one.");
                            }
                        }
                    }
                } else {
                    // Non-interactive: auto-rename until unique
                    String base = key;
                    int i = 1;
                    while (registry.getPrototype(key) != null) {
                        key = base + "-copy" + i++;
                    }
                    registry.registerPrototype(key, p);
                }
            } else {
                registry.registerPrototype(key, p);
            }
        }
    }

    // Convert prototype instance to JSON
    private JsonObject protoToJson(Prototype p) {
        JsonObject obj = new JsonObject();
        try {
            if (p instanceof Resume) {
                obj.addProperty("type", "Resume");
                Resume r = (Resume)p;
                java.lang.reflect.Field fname = Resume.class.getDeclaredField("candidateName");
                fname.setAccessible(true);
                obj.addProperty("candidateName", (String)fname.get(r));
                java.lang.reflect.Field fex = Resume.class.getDeclaredField("experiences");
                fex.setAccessible(true);
                List exs = (List)fex.get(r);
                JsonArray jex = new JsonArray();
                for (Object e: exs) jex.add(e.toString());
                obj.add("experiences", jex);

                java.lang.reflect.Field ftags = Document.class.getDeclaredField("tags");
                ftags.setAccessible(true);
                List tags = (List)ftags.get(r);
                JsonArray jtags = new JsonArray();
                for (Object t: tags) jtags.add(t.toString());
                obj.add("tags", jtags);

                java.lang.reflect.Field ftitle = Document.class.getDeclaredField("title");
                ftitle.setAccessible(true);
                obj.addProperty("title", (String)ftitle.get(r));

                java.lang.reflect.Field fcontent = Document.class.getDeclaredField("content");
                fcontent.setAccessible(true);
                obj.addProperty("content", (String)fcontent.get(r));

            } else if (p instanceof Report) {
                obj.addProperty("type", "Report");
                Report rep = (Report)p;
                java.lang.reflect.Field fa = Report.class.getDeclaredField("author");
                fa.setAccessible(true);
                obj.addProperty("author", (String)fa.get(rep));
                java.lang.reflect.Field fp = Report.class.getDeclaredField("pages");
                fp.setAccessible(true);
                obj.addProperty("pages", (int)fp.get(rep));

                java.lang.reflect.Field ftags = Document.class.getDeclaredField("tags");
                ftags.setAccessible(true);
                List tags = (List)ftags.get(rep);
                JsonArray jtags = new JsonArray();
                for (Object t: tags) jtags.add(t.toString());
                obj.add("tags", jtags);

                java.lang.reflect.Field ftitle = Document.class.getDeclaredField("title");
                ftitle.setAccessible(true);
                obj.addProperty("title", (String)ftitle.get(rep));

                java.lang.reflect.Field fcontent = Document.class.getDeclaredField("content");
                fcontent.setAccessible(true);
                obj.addProperty("content", (String)fcontent.get(rep));

            } else if (p instanceof Invoice) {
                obj.addProperty("type", "Invoice");
                Invoice inv = (Invoice)p;
                java.lang.reflect.Field fn = Invoice.class.getDeclaredField("invoiceNumber");
                fn.setAccessible(true);
                obj.addProperty("invoiceNumber", (String)fn.get(inv));
                java.lang.reflect.Field fa = Invoice.class.getDeclaredField("amount");
                fa.setAccessible(true);
                obj.addProperty("amount", (double)fa.get(inv));

                java.lang.reflect.Field ftags = Document.class.getDeclaredField("tags");
                ftags.setAccessible(true);
                List tags = (List)ftags.get(inv);
                JsonArray jtags = new JsonArray();
                for (Object t: tags) jtags.add(t.toString());
                obj.add("tags", jtags);

                java.lang.reflect.Field ftitle = Document.class.getDeclaredField("title");
                ftitle.setAccessible(true);
                obj.addProperty("title", (String)ftitle.get(inv));

                java.lang.reflect.Field fcontent = Document.class.getDeclaredField("content");
                fcontent.setAccessible(true);
                obj.addProperty("content", (String)fcontent.get(inv));
            } else {
                obj.addProperty("type", "Unknown");
                obj.addProperty("desc", p.getDescription());
            }
        } catch(Exception ex) {}
        return obj;
    }

    // Convert JSON object back to Prototype
    private Prototype jsonToPrototype(JsonObject obj) {
        String type = obj.has("type") ? obj.get("type").getAsString() : "";
        switch(type) {
            case "Resume": {
                String candidate = obj.has("candidateName") ? obj.get("candidateName").getAsString() : "";
                String title = obj.has("title") ? obj.get("title").getAsString() : "";
                String cont = obj.has("content") ? obj.get("content").getAsString() : "";
                Resume r = new Resume(candidate, title, cont);
                if (obj.has("experiences")) {
                    JsonArray exs = obj.getAsJsonArray("experiences");
                    for (JsonElement e: exs) r.addExperience(e.getAsString());
                }
                if (obj.has("tags")) {
                    JsonArray tags = obj.getAsJsonArray("tags");
                    for (JsonElement t: tags) r.addTag(t.getAsString());
                }
                return r;
            }
            case "Report": {
                String author = obj.has("author") ? obj.get("author").getAsString() : "";
                String title = obj.has("title") ? obj.get("title").getAsString() : "";
                String cont = obj.has("content") ? obj.get("content").getAsString() : "";
                int pages = obj.has("pages") ? obj.get("pages").getAsInt() : 1;
                Report rep = new Report(author, title, cont, pages);
                if (obj.has("tags")) {
                    for (JsonElement t: obj.getAsJsonArray("tags")) rep.addTag(t.getAsString());
                }
                return rep;
            }
            case "Invoice": {
                String invoiceNumber = obj.has("invoiceNumber") ? obj.get("invoiceNumber").getAsString() : "";
                String title = obj.has("title") ? obj.get("title").getAsString() : "";
                String cont = obj.has("content") ? obj.get("content").getAsString() : "";
                double amount = obj.has("amount") ? obj.get("amount").getAsDouble() : 0.0;
                Invoice inv = new Invoice(invoiceNumber, title, cont, amount);
                if (obj.has("tags")) {
                    for (JsonElement t: obj.getAsJsonArray("tags")) inv.addTag(t.getAsString());
                }
                return inv;
            }
            default: return null;
        }
    }

    public static void main(String[] args) {
        new PrototypeApp();
    }
}
