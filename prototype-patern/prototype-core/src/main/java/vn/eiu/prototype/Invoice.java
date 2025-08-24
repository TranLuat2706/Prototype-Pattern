package vn.eiu.prototype;
import java.util.ArrayList;
public class Invoice extends Document {
    private String invoiceNumber;
    private double amount;
    public Invoice(String invoiceNumber, String title, String content, double amount) {
        super(title, content);
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
    }
    public void setInvoiceNumber(String n) { this.invoiceNumber = n; }
    public void setAmount(double a) { this.amount = a; }
    @Override
    public Prototype clonePrototype() {
        Invoice clone = new Invoice(this.invoiceNumber, this.title, this.content, this.amount);
        clone.tags = new ArrayList<>(this.tags);
        return clone;
    }
    @Override
    public String getDescription() {
        return String.format("[Invoice] No: %s | Amount: %.2f | %s", invoiceNumber, amount, super.getDescription());
    }
}
