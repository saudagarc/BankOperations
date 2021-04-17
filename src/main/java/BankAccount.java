import com.exception.InvalidAccountTypeException;
import com.exception.InvalidCustomerException;
import com.sun.istack.internal.NotNull;
import lombok.NonNull;
import lombok.Setter;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {

    private double balance;
    private String accountType;
    private Lock balanceChangeLock;
    private Condition sufficientFundsCondition;
    private static long nextAccoutNumber = 1;
    private long accountNo;

    public BankAccount(String accountType) {
        this.accountType=accountType;
        balance = 0;
        balanceChangeLock = new ReentrantLock();
        sufficientFundsCondition = balanceChangeLock.newCondition();
        accountNo=nextAccoutNumber++;
    }

    public void deposit(double amount) {
        balanceChangeLock.lock();
        try {
            System.out.print("Depositing " + amount);
            double newBalance = balance + amount;
            System.out.println(", new balance is " + newBalance);
            balance = newBalance;
            sufficientFundsCondition.signalAll();
        } finally {
            balanceChangeLock.unlock();
        }
    }


    public void withdraw(double amount)
            throws InterruptedException {
        balanceChangeLock.lock();
        try {
            while (balance < amount) {
                sufficientFundsCondition.await();
            }
            System.out.print("Withdrawing " + amount);
            double newBalance = balance - amount;
            System.out.println(", new balance is " + newBalance);
            balance = newBalance;
        } finally {
            balanceChangeLock.unlock();
        }
    }
    public String getAccountType() {
        return accountType;
    }

    public long getAccountNo() {
        return accountNo;
    }
    public double getBalance() {
        return balance;
    }

    public Lock getBalanceChangeLock() {
        return balanceChangeLock;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "balance=" + balance +
                ", accountType='" + accountType + '\'' +
                ", balanceChangeLock=" + balanceChangeLock +
                ", sufficientFundsCondition=" + sufficientFundsCondition +
                ", accountNo=" + accountNo +
                '}';
    }
}
