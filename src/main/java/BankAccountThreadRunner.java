public class BankAccountThreadRunner {
    final double AMOUNT = 100;
    final int REPETITIONS = 100;
    final int THREADS = 100;

    public static void main(String[] args) {
        Customer customer = new Customer("1","1","AAMP");

        for (int i = 1; i <= 10; i++) {
            BankAccount account = new BankAccount("Saving");
            System.out.println(account.toString());
            DepositRunnable d = new DepositRunnable(account, 100,1);
            WithdrawRunnable w = new WithdrawRunnable(account, 100,1);
            Thread dt = new Thread(d);
            Thread wt = new Thread(w);
            dt.start();
            wt.start();
        }
    }
}



