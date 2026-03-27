import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Auditor {
    private int frequencyRating;
    private int amountRating;
    private int locationRating;
    private int oddHoursRating;
    private int compositeRating;
    private int riskLevel;
    private String riskTitle;
    private String actionToTake;

    public int getFrequencyRating() {
        return frequencyRating;
    }

    public int getAmountRating() {
        return amountRating;
    }

    public int getLocationRating() {
        return locationRating;
    }

    public int getOddHoursRating() {
        return oddHoursRating;
    }

    public int getCompositeRating() {
        return compositeRating;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public String getRiskTitle() {
        return riskTitle;
    }

    public String getActionToTake() {
        return actionToTake;
    }

    public void accountNumberInput() {
        Scanner scanner = new Scanner(System.in);
        String accountNumber = null;
        boolean isAccountExist = false;

        System.out.println("=== All Bank of Orion Accounts ===");
        for (AccountsEnum account : AccountsEnum.values()) {
            System.out.println(account);
            System.out.println("----------------------------------");
        }

        while (!isAccountExist) {
            System.out.print("Enter account number to investigate fraud risk: ");
            accountNumber = scanner.nextLine().trim();

            for (AccountsEnum account : AccountsEnum.values()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    isAccountExist = true;
                    break;
                }
            }

            if (!isAccountExist) {
                System.out.println("Error: Account number does not exist! Please enter a correct one.\n");
            }
        }

        auditTransactions(accountNumber);
        scanner.close();
    }

    public void auditTransactions(String accountNumber) {
        this.frequencyRating = 0;
        this.amountRating = 0;
        this.locationRating = 0;
        this.oddHoursRating = 0;

        evaluateFrequency(accountNumber);
        evaluateAmount(accountNumber);
        evaluateLocation(accountNumber);
        evaluateOddHour(accountNumber);

        this.compositeRating = frequencyRating + amountRating + locationRating + oddHoursRating;

        evaluateRiskLevel(compositeRating);
    }

    private void evaluateFrequency(String accountNumber) {
        List<LocalDateTime> transactionTimes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (TransactionsEnum tx : TransactionsEnum.values()) {
            if (tx.getAccountNumber().equals(accountNumber)) {
                LocalDateTime time = LocalDateTime.parse(tx.getTimestamp(), formatter);
                transactionTimes.add(time);
            }
        }

        for (int i = 0; i < transactionTimes.size() - 1; i++) {
            LocalDateTime t1 = transactionTimes.get(i);
            LocalDateTime t2 = transactionTimes.get(i + 1);
            long minutesDiff = Duration.between(t1, t2).toMinutes();

            if (minutesDiff <= 1440) {
                this.frequencyRating += 5;
            } else if (minutesDiff > 1440 && minutesDiff <= 2880) {
                this.frequencyRating += 2;
            }
        }
    }

    private void evaluateAmount(String accountNumber) {
        for (TransactionsEnum tx : TransactionsEnum.values()) {
            if (tx.getAccountNumber().equals(accountNumber)) {
                double amount = tx.getAmount();
                if (amount > 5000) {
                    this.amountRating += 5;
                } else if (amount > 100 && amount < 500) {
                    this.amountRating += 1;
                }
            }
        }
    }

    private void evaluateLocation(String accountNumber) {
        for (TransactionsEnum tx : TransactionsEnum.values()) {
            if (tx.getAccountNumber().equals(accountNumber)) {
                if ("International".equals(tx.getTransactionCategory())) {
                    this.locationRating += 5;
                }
            }
        }
    }

    private void evaluateOddHour(String accountNumber) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for (TransactionsEnum tx : TransactionsEnum.values()) {
            if (tx.getAccountNumber().equals(accountNumber)) {
                LocalDateTime txTime = LocalDateTime.parse(tx.getTimestamp(), formatter);
                int hour = txTime.getHour();
                if (hour < 6 || hour > 22) {
                    this.oddHoursRating += 5;
                }
            }
        }
    }

    private void evaluateRiskLevel(int compositeRating) {
        RiskEnum matchedRisk;
        if (compositeRating < 40) {
            matchedRisk = RiskEnum.NO_RISK;
        } else if (compositeRating < 50) {
            matchedRisk = RiskEnum.NEGLIGIBLE_RISK;
        } else if (compositeRating < 60) {
            matchedRisk = RiskEnum.LOW_RISK;
        } else if (compositeRating < 70) {
            matchedRisk = RiskEnum.MEDIUM_RISK;
        } else if (compositeRating < 85) {
            matchedRisk = RiskEnum.HIGH_RISK;
        } else {
            matchedRisk = RiskEnum.CRITICAL;
        }

        this.riskLevel = matchedRisk.getRiskLevel();
        this.riskTitle = matchedRisk.getRiskTitle();
        this.actionToTake = matchedRisk.getActionToTake();
    }

    @Override
    public String toString() {
        return "Risk Audit Result:\n" +
                "Transaction Frequency Risk: " + frequencyRating + "\n" +
                "Transaction Amount Risk: " + amountRating + "\n" +
                "Transaction Location Risk: " + locationRating + "\n" +
                "Transaction Timing Risk: " + oddHoursRating + "\n" +
                "Composite Risk: " + compositeRating + "\n" +
                "Estimated Risk Level: " + riskLevel + "\n" +
                "Overall Risk: " + riskTitle + "\n" +
                "Recommended Action: " + actionToTake + "\n" +
                "NOTE: Lower values are better.";
    }
}
