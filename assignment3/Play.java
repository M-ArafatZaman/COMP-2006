class Play {
    public static void main(String[] args) {
        System.out.println(isGuessValid("1 2 3 4"));
    }

    // because it makes life easier!
    private static void toConsole(String message) {
        System.out.println(message);
    }

    private static Boolean isGuessValid(String guess) {
        toConsole("Length: "+guess.length());
        if (guess.length() != 7) return false;

        for (int i = 0; i < guess.length(); i++) {
            toConsole("i: "+i);
            if (i % 2 == 1) {
                if (guess.charAt(i) != ' ') return false;
            } else {
                if ((guess.charAt(i) - '0') < 0 || (guess.charAt(i) - '0') > 9 ) return false;
            }
        }
        return true;
    }
}