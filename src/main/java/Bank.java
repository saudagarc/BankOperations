import com.exception.InvalidAccountTypeException;
import com.exception.InvalidCustomerException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Bank {
    private static ConcurrentHashMap<Customer, Set<BankAccount>> bankdetails;

    public static ConcurrentHashMap<Customer, Set<BankAccount>> getBankdetails() {
        if (bankdetails == null) {
            bankdetails =new ConcurrentHashMap<>();
            return bankdetails;
        }
        return bankdetails;
    }

    public static Map<Customer, Set<BankAccount>> createAccountDetails(String accountType, Customer customer)
            throws InvalidAccountTypeException, InvalidCustomerException {
        if (customer == null) {
            throw new InvalidCustomerException(accountType + " Customer details are not valid");
        } else if (!accountType.equalsIgnoreCase("savings")
                && !accountType.equalsIgnoreCase("current")) {
            throw new InvalidAccountTypeException(accountType + " is Invalid Account type");
        }
        ConcurrentHashMap<Customer, Set<BankAccount>> bankdetails = Bank.getBankdetails();
        if (bankdetails.get(customer) == null) {
            HashSet<BankAccount> bankAccounts = new HashSet<>();
            bankAccounts.add(new BankAccount(accountType));
            bankdetails.put(customer, bankAccounts);
            return bankdetails;
        } else if (accountType.equalsIgnoreCase("savings")) {
            throw new InvalidAccountTypeException("Saving account already exists for this customer");
        } else {
            bankdetails.get(customer).add(new BankAccount(accountType));
        }
        return bankdetails;
    }

    public void withdrawFromAccount(long accountNo, long amount) throws InvalidAccountTypeException {
        Optional<BankAccount> bankAccount = Bank.bankdetails.values()
                .stream().flatMap(bankAccounts -> bankAccounts.stream())
                .filter(s -> s.getAccountNo() == accountNo)
                .reduce((bankAccount1, bankAccount2) -> bankAccount2);
        if (bankAccount.isPresent()) {
            try {
                bankAccount.get().withdraw(amount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            throw new InvalidAccountTypeException(accountNo + " is Invalid Account no");
        }

    }

    public void deposit(long accountNo, long amount) throws InvalidAccountTypeException {
        Optional<BankAccount> bankAccount = Bank.bankdetails.values()
                .stream().flatMap(bankAccounts -> bankAccounts.stream())
                .filter(s -> s.getAccountNo() == accountNo)
                .reduce((bankAccount1, bankAccount2) -> bankAccount2);
        if (bankAccount.isPresent()) {
            System.out.println("before deposit::"+bankAccount.get().getBalance());
            bankAccount.get().deposit(amount);
            System.out.println("After deposit::"+bankAccount.get().getBalance());
        } else {
            throw new InvalidAccountTypeException(accountNo + " is Invalid Account no");
        }

    }

    public void transfer(long accountNoFrom, long accountNoTo, long amount) throws InvalidAccountTypeException {
        Optional<BankAccount> bankAccountFrom = Bank.bankdetails.values()
                .stream().flatMap(bankAccounts -> bankAccounts.stream())
                .filter(s -> s.getAccountNo() == accountNoFrom)
                .reduce((bankAccount1, bankAccount2) -> bankAccount2);
        Optional<BankAccount> bankAccountTo = Bank.bankdetails.values()
                .stream().flatMap(bankAccounts -> bankAccounts.stream())
                .filter(s -> s.getAccountNo() == accountNoTo)
                .reduce((bankAccount1, bankAccount2) -> bankAccount2);
        if (bankAccountFrom.isPresent() && bankAccountTo.isPresent()) {
            BankAccount bankAccountToDebit = bankAccountFrom.get();
            BankAccount bankAccountCredit = bankAccountTo.get();

            Object lock1 = bankAccountToDebit.getAccountNo() < bankAccountCredit.getAccountNo() ? bankAccountToDebit.getBalanceChangeLock() : bankAccountCredit.getBalanceChangeLock();
            Object lock2 = bankAccountToDebit.getAccountNo() < bankAccountCredit.getAccountNo() ? bankAccountCredit.getBalanceChangeLock() : bankAccountToDebit.getBalanceChangeLock();
            synchronized (lock1) {
                synchronized (lock2) {
                    try {
                        bankAccountToDebit.withdraw(amount);
                        bankAccountCredit.deposit(amount);
                        System.out.println("bankAccountToDebit::"+bankAccountToDebit.getBalance());
                        System.out.println("bankAccountCredit::"+bankAccountCredit.getBalance());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        } else {
            throw new InvalidAccountTypeException(accountNoFrom+" or "+accountNoTo + " is Invalid Account no");
        }

    }

    public static void main(String[] args) {
        try {
            Bank bank = new Bank();
            Scanner sc= new Scanner(System.in);
            boolean operationContinuty = true;
            while(operationContinuty){
            System.out.println("Welcome to banking");
            System.out.println("Please enter following choice");
            System.out.println("1: Create Account");
            System.out.println("2: Withdrawn from Account");
            System.out.println("3: Deposit to Account");
            System.out.println("4: Transfer to Account");
            System.out.println("5: Get all details");
            System.out.println("6: Exit");
            int a= sc.nextInt();
            sc.nextLine();
                switch (a){
                    case 1:
                        System.out.println("Enter the type of Account Savings/Current");
                        String accountType= sc.nextLine();
                        System.out.println("Enter the Fist Name");
                        String firstName= sc.nextLine();
                        System.out.println("Enter the Last Name");
                        String lastName= sc.nextLine();
                        System.out.println("Enter the PAN number");
                        String PAN= sc.nextLine();
                        Customer customer = new Customer(firstName,lastName,PAN);
                        try {
                            createAccountDetails(accountType, customer);
                        } catch (InvalidAccountTypeException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        System.out.println(" Enter account no from which you want to withdrawn");
                        long accountNo = sc.nextLong();
                        System.out.println(" Enter amount to be withdrawn from the account");
                        long amount = sc.nextLong();
                        bank.withdrawFromAccount(accountNo, amount);
                        break;
                    case 3:
                        System.out.println(" Enter account no to which you want to deposit");
                        long accountNoDep = sc.nextLong();
                        System.out.println(" Enter amount to be deposit to the account");
                        long amountDep = sc.nextLong();
                        bank.deposit(accountNoDep, amountDep);
                        break;
                    case 4:
                        System.out.println(" Enter account no from which you want to withdrawn");
                        long accountNoFrom = sc.nextLong();
                        System.out.println(" Enter account no to which you want to deposit");
                        long accountNoTo = sc.nextLong();
                        System.out.println(" Enter amount to be transfer to the account");
                        long amountTransfer = sc.nextLong();
                        bank.transfer(accountNoFrom,  accountNoTo,  amountTransfer);
                        break;
                    case 5:
                        System.out.println("Getting all account and balance information");
                        bank.getAllBankDetails();
                        break;
                    case 6:
                        operationContinuty=false;
                    default:
                        System.out.println("Thank you");
                        break;
                }
            }


            /*createAccountDetails("savings", new Customer("1", "1", "Pan"));
            createAccountDetails("savings", new Customer("2", "2", "Pan2"));
            bank.deposit(1, 20000);
            bank.deposit(0, 10000);
            bank.withdrawFromAccount(1, 5000);
            bank.transfer(1, 0, 3000);
            System.out.println(bankdetails.toString());*/
        } catch (InvalidAccountTypeException e) {
            e.printStackTrace();
        } catch (InvalidCustomerException e) {
            e.printStackTrace();
        }
    }

    private void getAllBankDetails() {
        ConcurrentHashMap<Customer, Set<BankAccount>> bankdetails = Bank.getBankdetails();
        if(bankdetails.isEmpty()){
            System.out.println("No data to show, Please create the account");
        }else {
            Map<Long, Double> allBankdetails = bankdetails.values()
                    .stream()
                    .flatMap(bankAccounts -> bankAccounts.stream())
                    .collect(Collectors.toMap(BankAccount::getAccountNo, BankAccount::getBalance, (o1, o2) -> o2));
            System.out.println(allBankdetails);
        }
    }

}
