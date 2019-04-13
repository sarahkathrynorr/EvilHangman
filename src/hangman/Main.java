package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import static java.lang.Character.isLetter;


public class Main {

    public static void main(String[] args) throws IOException, IEvilHangmanGame.GuessAlreadyMadeException {
        String dictionary = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);

        EvilHangmanGame soEvil = new EvilHangmanGame();

        if (wordLength < 2) {
            System.out.println("Invalid Input\n");
        }
        if (guesses < 1) {
            System.out.println("Invalid Input\n");
        }

        soEvil.startGame(new File(dictionary), wordLength);

        boolean won = false;

        int attempts = 0;

        while (attempts < guesses) {
            if (guesses - attempts == 1) {
                System.out.println("You have " + (guesses - attempts) + " guess left");
            } else {
                System.out.println("You have " + (guesses - attempts) + " guesses left");
            }
            StringBuilder usedLetters = new StringBuilder();
            for (char g : soEvil.haveGuessed) {
                usedLetters.append(g + " ");
            }
            System.out.println("Used Letters: " + usedLetters);

            System.out.println("Word: " + soEvil.base);
            System.out.println("Enter Guess: ");

            char guess;

            Scanner sin = new Scanner(System.in);

            String guessLine = sin.nextLine();

            if ( guessLine.length() == 0) {
                System.out.println("Invalid Input");
            }
            else if (guessLine.length() > 1 || !isLetter(guessLine.charAt(0)) || guessLine.contains("\n")) {
                System.out.println("Invalid Input");
            } else {
                guessLine = guessLine.toLowerCase();
                guess = guessLine.charAt(0);

                Set<String> results;
                try {
                   results = soEvil.makeGuess(guess);
                } catch (IEvilHangmanGame.GuessAlreadyMadeException e) {
                    System.out.println("You already used that letter");
                    continue;
                }

                int first = soEvil.base.indexOf(guessLine);
                int howMany = 0;

                if (first == -1) {
                    System.out.println("Sorry, there are no " + guess + "'s");
                    attempts++;
                } else {
                    for (int e = 0; e < soEvil.base.length(); e++) {
                        if (soEvil.base.charAt(e) == guess) {
                            howMany++;
                        }
                    }

                    if (howMany > 1) {
                        System.out.println("Yes, there are " + howMany + " " + guess + "'s");
                    } else {
                        System.out.println("Yes, there is " + howMany + " " + guess);
                    }
                }

                if (soEvil.base.indexOf("-") == -1) {
                    won = true;
                    System.out.println("You win!");
                    System.out.println("The word was: " + results.toArray()[0]);
                    break;
                }
            }
        }
        if (!won) {
            System.out.println("You lose!");
            System.out.println("The word was: " + soEvil.winner);
        }
    }
}

/*
TODO:
 //create exception for multiple guesses
 - more error checking
 //check whether the letters are in order and etc EDEN vs DEER vs TREE
 - also if there is a line and then a char entered
 - learn about how to look at the Java Docs before the exam
 - will there ever be a \n character first thing???



 // 8 letters, 16 guesses java 146, 132, 116 - cannot convert from stringbuilder to string
 */
