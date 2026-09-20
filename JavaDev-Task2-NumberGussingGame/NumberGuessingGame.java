import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// ─────────────────────────────────────────────────────────────────────────────
//  Difficulty levels (Bonus feature)
//  Each level defines: display name, number range (1..maxNumber), max attempts
// ─────────────────────────────────────────────────────────────────────────────
enum Difficulty {
    EASY  ("Easy",   1,  50, 10),
    MEDIUM("Medium", 1, 100,  7),
    HARD  ("Hard",   1, 200,  5);

    final String label;
    final int    minNumber;
    final int    maxNumber;
    final int    maxAttempts;

    Difficulty(String label, int minNumber, int maxNumber, int maxAttempts) {
        this.label       = label;
        this.minNumber   = minNumber;
        this.maxNumber   = maxNumber;
        this.maxAttempts = maxAttempts;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Stores the result of one completed round for the scoreboard
// ─────────────────────────────────────────────────────────────────────────────
class RoundResult {
    final int        roundNumber;
    final Difficulty difficulty;
    final int        attemptsUsed;
    final boolean    won;
    final int        secretNumber;

    RoundResult(int roundNumber, Difficulty difficulty,
                int attemptsUsed, boolean won, int secretNumber) {
        this.roundNumber  = roundNumber;
        this.difficulty   = difficulty;
        this.attemptsUsed = attemptsUsed;
        this.won          = won;
        this.secretNumber = secretNumber;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Main game class
// ─────────────────────────────────────────────────────────────────────────────
public class NumberGuessingGame {

    // ── Shared resources ────────────────────────────────────────────────────
    private static final Scanner sc     = new Scanner(System.in);
    private static final Random  random = new Random();

    // ── Round history ────────────────────────────────────────────────────────
    private static final List<RoundResult> history = new ArrayList<>();

    // ════════════════════════════════════════════════════════════════════════
    //  Entry point
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        showWelcomeBanner();

        boolean playAgain = true;
        while (playAgain) {
            playRound();
            showScoreboard();
            playAgain = askPlayAgain();
        }

        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║   Thanks for playing! Goodbye!   ║");
        System.out.println("╚══════════════════════════════════╝\n");
        sc.close();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Play one complete round
    // ════════════════════════════════════════════════════════════════════════
    private static void playRound() {
        int roundNumber = history.size() + 1;
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("              ROUND " + roundNumber);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // ── 1. Select difficulty ─────────────────────────────────────────────
        Difficulty diff = selectDifficulty();

        // ── 2. Generate the secret number (done ONCE per round — fair!) ──────
        int secretNumber = random.nextInt(diff.maxNumber - diff.minNumber + 1)
                           + diff.minNumber;

        System.out.println("\n  I've picked a number between "
                + diff.minNumber + " and " + diff.maxNumber + ".");
        System.out.println("  You have " + diff.maxAttempts + " attempts. Good luck!\n");

        // ── 3. Guess loop ────────────────────────────────────────────────────
        int     attempts = 0;
        boolean won      = false;

        while (attempts < diff.maxAttempts) {
            int remaining = diff.maxAttempts - attempts;
            System.out.printf("  [Attempt %d / %d — %d remaining] Enter your guess: ",
                    attempts + 1, diff.maxAttempts, remaining);

            int guess = readInt(diff.minNumber, diff.maxNumber);
            attempts++;

            if (guess < secretNumber) {
                System.out.println("  ↑  Too Low!   Try higher.\n");
            } else if (guess > secretNumber) {
                System.out.println("  ↓  Too High!  Try lower.\n");
            } else {
                System.out.println("\n  ✔  Correct! 🎉  The number was " + secretNumber + ".");
                System.out.println("     You guessed it in " + attempts + " attempt(s)!\n");
                won = true;
                break;
            }
        }

        // ── 4. Handle loss ───────────────────────────────────────────────────
        if (!won) {
            System.out.println("\n  ✘  You Lost!  You used all " + diff.maxAttempts
                    + " attempts.");
            System.out.println("     The number was ► " + secretNumber + " ◄\n");
        }

        // ── 5. Record result ─────────────────────────────────────────────────
        history.add(new RoundResult(roundNumber, diff, attempts, won, secretNumber));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Difficulty selection menu
    // ════════════════════════════════════════════════════════════════════════
    private static Difficulty selectDifficulty() {
        System.out.println("  Select difficulty:");
        System.out.println("    1. Easy   — 1 to 50,  10 attempts");
        System.out.println("    2. Medium — 1 to 100,  7 attempts");
        System.out.println("    3. Hard   — 1 to 200,  5 attempts");

        while (true) {
            System.out.print("  Enter choice (1 / 2 / 3): ");
            int choice = readInt(1, 3);
            switch (choice) {
                case 1: return Difficulty.EASY;
                case 2: return Difficulty.MEDIUM;
                case 3: return Difficulty.HARD;
                default:
                    System.out.println("  ⚠  Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Scoreboard — printed after every round
    // ════════════════════════════════════════════════════════════════════════
    private static void showScoreboard() {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│                    SCOREBOARD                   │");
        System.out.println("├────────┬──────────┬─────────────┬───────────────┤");
        System.out.println("│ Round  │ Diff     │ Attempts    │ Result        │");
        System.out.println("├────────┼──────────┼─────────────┼───────────────┤");

        int wins = 0;
        for (RoundResult r : history) {
            String result = r.won
                    ? "✔ Won"
                    : "✘ Lost (" + r.secretNumber + ")";
            System.out.printf("│ %-6d │ %-8s │ %2d / %-6d │ %-13s │%n",
                    r.roundNumber,
                    r.difficulty.label,
                    r.attemptsUsed,
                    r.difficulty.maxAttempts,
                    result);
            if (r.won) wins++;
        }

        System.out.println("└────────┴──────────┴─────────────┴───────────────┘");
        System.out.printf("  Total: %d round(s) played — %d win(s), %d loss(es)%n%n",
                history.size(), wins, history.size() - wins);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Ask the user whether to play another round
    // ════════════════════════════════════════════════════════════════════════
    private static boolean askPlayAgain() {
        while (true) {
            System.out.print("  Play again? (Y / N): ");
            String input = sc.next().trim().toUpperCase();
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            System.out.println("  ⚠  Please enter Y or N.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Safe integer reader with range validation
    //  Keeps prompting until the user enters an integer in [min, max]
    // ════════════════════════════════════════════════════════════════════════
    private static int readInt(int min, int max) {
        while (true) {
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  ⚠  Please enter a number between %d and %d: ", min, max);
            } else {
                System.out.print("  ⚠  Invalid input — numbers only: ");
                sc.next(); // discard the bad token
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Welcome banner
    // ════════════════════════════════════════════════════════════════════════
    private static void showWelcomeBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║       NUMBER GUESSING GAME  v2.0          ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║  Rules:                                   ║");
        System.out.println("║  • Guess the secret number in range.      ║");
        System.out.println("║  • After each guess you'll see:           ║");
        System.out.println("║      ↑ Too Low!   ↓ Too High!  ✔ Correct ║");
        System.out.println("║  • Run out of attempts → You Lose!        ║");
        System.out.println("║  • Scoreboard updates after every round.  ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
