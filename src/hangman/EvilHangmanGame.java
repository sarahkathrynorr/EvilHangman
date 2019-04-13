package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class EvilHangmanGame implements IEvilHangmanGame {

    private HashMap<String, TreeSet<String>> availableWords = new HashMap<>();
    TreeSet<Character> haveGuessed = new TreeSet<>(); // need to instantiate this somewhere? //String or Character?
    private int wordLength;
    public StringBuilder base;
    public String winner;

    @Override
    public void startGame(File dictionary, int wordLength) {

        this.wordLength = wordLength;
        if (haveGuessed.size() > 0) {
            haveGuessed.clear();
        }

       base = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            base.append('-');
        }

        winner = base.toString();
        String starterKey = "start";
        TreeSet<String> allWords = new TreeSet<>();

        // read in all the words from the dictionary
        try {
            Scanner sc = new Scanner(dictionary);

            while (sc.hasNext()) {
                String currentWord = sc.next();
                if (currentWord.length() == wordLength) {
                    allWords.add(currentWord.toLowerCase());
                }
            }

            availableWords.put(starterKey, allWords);

        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        HashMap tempMap = new HashMap<String, TreeSet<String>>();

        if (haveGuessed.contains(guess)) {
            throw new GuessAlreadyMadeException();
        }
        else {
            haveGuessed.add(guess);


            for (Map.Entry<String, TreeSet<String>> words : availableWords.entrySet()) {
                for (String word : words.getValue()) {
                    int firstLocation = word.indexOf(guess);
                    int lastLocation = word.lastIndexOf(guess);

                    StringBuilder tempBase = new StringBuilder(base.toString());

                    if (firstLocation == -1) {
                        TreeSet<String> atCurrentLocation = (TreeSet<String>) tempMap.get(tempBase.toString());
                        if (atCurrentLocation == null) {
                            atCurrentLocation = new TreeSet<>();
                        }
                        atCurrentLocation.add(word);
                        tempMap.put(tempBase.toString(), atCurrentLocation);
                    } else if (firstLocation != lastLocation) {
                        StringBuilder tempKey = tempBase;
                        for (int i = 0; i < wordLength; i++) {
                            if (word.charAt(i) == guess) {
                                tempKey.setCharAt(i, word.charAt(i));
                            }
                        }
                        TreeSet<String> atCurrentLocation = (TreeSet<String>) tempMap.get(tempKey.toString());
                        if (!tempMap.containsKey(tempKey.toString())) {
                            atCurrentLocation = new TreeSet<>();
                        }
                        atCurrentLocation.add(word);
                        tempMap.put(tempKey.toString(), atCurrentLocation);
                        //find all the letters repeated in the word
                    } else { //the guess only appears once in the word
                        String tempKey = tempBase.substring(0, firstLocation) + guess + tempBase.substring(firstLocation + 1);
                        TreeSet<String> atCurrentLocation = (TreeSet<String>) tempMap.get(tempKey);
                        if (atCurrentLocation == null) {
                            atCurrentLocation = new TreeSet<>();
                        }
                        atCurrentLocation.add(word);
                        tempMap.put(tempKey, atCurrentLocation);
                    }
                }
            }


            availableWords = (HashMap<String, TreeSet<String>>) tempMap.clone();
            tempMap.clear();

            int maxSize = 0;
            for (Map.Entry<String, TreeSet<String>> words : availableWords.entrySet()) {
                if (words.getValue().size() > maxSize) {
                    maxSize = words.getValue().size();
                }
            }

            HashMap<String, TreeSet<String>> anotherTemp = (HashMap<String, TreeSet<String>>) availableWords.clone();
            for (Map.Entry<String, TreeSet<String>> words : anotherTemp.entrySet()) {
                if (words.getValue().size() < maxSize) {
                    availableWords.remove(words.getKey());
                }
            }

            if (availableWords.size() == 1) {
                base = new StringBuilder(availableWords.keySet().toArray()[0].toString());


                /*testing*/
                Collection<TreeSet<String>> checker = availableWords.values();
                TreeSet<String> winners = (TreeSet<String>) checker.toArray()[0];
                winner = winners.first();

                return winners;
            } else {
                return checkForLeastAmount(guess);
            }
        }
    }


    private Set<String> checkForLeastAmount(char guess) {
        int leastAmount = wordLength;
        for (Map.Entry<String, TreeSet<String>> words : availableWords.entrySet()) {
            //StringBuilder currentSB = new StringBuilder(words.getKey());
            //String current = currentSB.toString();
            int amount = (int) words.getKey().chars().filter(ch -> ch == guess).count();
            if (amount < leastAmount) {
                leastAmount = amount;
            }
        }

        HashMap<String, TreeSet<String>> temp = (HashMap<String, TreeSet<String>>) availableWords.clone();
        //int sizeOfMap = availableWords.size();
        //for (int i = 0; i < sizeOfMap; i++) {
        for (Map.Entry<String, TreeSet<String>> words : temp.entrySet()) {
            //StringBuilder currentSB = new StringBuilder(words.getKey());
            //String current = currentSB.toString();
            int amount = (int) words.getKey().chars().filter(ch -> ch == guess).count();
            if (amount > leastAmount) {
                availableWords.remove(words.getKey());
            }
        }

        if (availableWords.size() == 1) {
            base = new StringBuilder (availableWords.keySet().toArray()[0].toString());

            /*testing*/
            Collection<TreeSet<String>> checker = availableWords.values();
            TreeSet<String> winners = (TreeSet<String>) checker.toArray()[0];
            winner = winners.first();

            return winners;
        }
        //loooppp
        else return checkForRightmost(guess);
    }

    private Set<String> checkForRightmost(char guess) {
        int rightmost = -1;
        for (Map.Entry<String, TreeSet<String>> words : availableWords.entrySet()) {
            int pos = words.getKey().lastIndexOf(guess);
            if (pos > rightmost) {
                rightmost = pos;
            }
        }

        for (Map.Entry<String, TreeSet<String>> words : availableWords.entrySet()) {
            int pos = words.getKey().lastIndexOf(guess);
            if (pos < rightmost) {
                availableWords.remove(words.getKey());
            }
        }

        if (availableWords.size() == 1) {
            String toReturn = (String) availableWords.keySet().toArray()[0];
            base = new StringBuilder((String) availableWords.keySet().toArray()[0]);

            /*testing*/
            Collection<TreeSet<String>> checker = availableWords.values();
            TreeSet<String> winners = (TreeSet<String>) checker.toArray()[0];
            winner = winners.first();

            return winners;
        }
        else {
            int leftMost = 0;
            for (Map.Entry<String, TreeSet<String>> words : availableWords.entrySet()) {
                int pos = words.getKey().indexOf(guess);
                if (pos > leftMost) {
                    rightmost = pos;
                }
            }

            HashMap<String, TreeSet<String>> tempytemp = (HashMap<String, TreeSet<String>>) availableWords.clone();
            for (Map.Entry<String, TreeSet<String>> words : tempytemp.entrySet()) {
                int pos = words.getKey().indexOf(guess);
                if (pos < rightmost) {
                    availableWords.remove(words.getKey());
                }
            }
          //  if (availableWords.size() == 1) {
            //4:38 PM
            //String toReturn = (String) availableWords.keySet().toArray()[0];
                base = new StringBuilder((String) availableWords.keySet().toArray()[0]);

                /*testing*/
                Collection<TreeSet<String>> checker = availableWords.values();
                TreeSet<String> winners = (TreeSet<String>) checker.toArray()[0];
                winner = winners.first();

                return winners;
           // }
        }
    }
}

//TODO:
// - get the multiple guesses exception fixed
// - what about situations where there with _e_e and _eee?
