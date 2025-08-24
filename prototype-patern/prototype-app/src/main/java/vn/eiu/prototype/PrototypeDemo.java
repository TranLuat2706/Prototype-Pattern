package vn.eiu.prototype;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class PrototypeDemo {
    private static PrototypeRegistry registry = new PrototypeRegistry();
    private static Scanner scanner = new Scanner(System.in);
    private static List<Prototype> clones = new ArrayList<>();
    public static void main(String[] args) {
        seedRegistry();
        System.out.println("=== Prototype Pattern Demo (Console) ===");
        boolean running = true;
        while (running) {
            System.out.println("1. List registry\n2. Clone\n3. List clones\n4. Exit\n> ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1": for (String k: registry.keys()) System.out.println(k + " -> " + registry.getPrototype(k).getDescription()); break;
                case "2": System.out.print("Key: "); String key = scanner.nextLine().trim(); Prototype m = registry.getPrototype(key); if (m!=null) { Prototype cl = m.clonePrototype(); clones.add(cl); System.out.println("Cloned: " + cl.getDescription()); } else System.out.println("Not found."); break;
                case "3": for (int i=0;i<clones.size();++i) System.out.println(i + ": " + clones.get(i).getDescription()); break;
                case "4": running=false; break;
            }
        }
        System.out.println("Bye"); 
    }
    private static void seedRegistry() {
        Resume resume = new Resume("Nguyen Van A", "Junior Java Developer", "Objective: ...");
        resume.addExperience("Internship at Company X"); resume.addExperience("Volunteer - Open Source project"); resume.addTag("CV"); resume.addTag("Template");
        Report report = new Report("Tran Thi B", "Quarterly Report Q1", "Summary: ...", 10); report.addTag("Report"); report.addTag("Finance");
        Invoice invoice = new Invoice("INV-2025-001", "Invoice for Project A", "Bill for services", 1250.0); invoice.addTag("Invoice"); invoice.addTag("Billing");
        registry.registerPrototype("resume-basic", resume); registry.registerPrototype("report-q1", report); registry.registerPrototype("invoice-sample", invoice);
    }
}
