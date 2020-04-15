package enigma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Mohamed Maazin Sudheer
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        Pattern goodCycles = Pattern.compile("(\\s*\\(.+\\)\\s*)*");
        Matcher forcycles = goodCycles.matcher(cycles);
        if (cycles.equals("")) {
            mapToSelf(_alphabet);
        } else if (forcycles.matches() && isValidCycle(cycles)) {
            _cycles = cycles;
        } else {
            throw new EnigmaException("The cycle passed in is invalid");
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        if (isValidCycle(cycle)) {
            _cycles = _cycles + " " + cycle;
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char curr;
        char first = 'a';
        if (p < _alphabet.size() && p >= 0) {
            curr = _alphabet.toChar(p);
            for (int i = 0; i < _cycles.length(); i++) {
                if (_cycles.charAt(i) == curr) {
                    if (_cycles.charAt(i + 1) == ')') {
                        for (int x = i; _cycles.charAt(x) != '('; x--) {
                            first = _cycles.charAt(x);
                        }
                        return _alphabet.toInt(first);
                    } else {
                        return _alphabet.toInt(_cycles.charAt(i + 1));
                    }
                }
            }
            return p;
        } else {
            return permute(wrap(p));
        }
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char curr;
        char first = 'a';
        if (c < _alphabet.size() && c >= 0) {
            curr = _alphabet.toChar(c);
            for (int i = 0; i < _cycles.length(); i++) {
                if (_cycles.charAt(i) == curr) {
                    if (_cycles.charAt(i - 1) == '(') {
                        for (int x = i; _cycles.charAt(x) != ')'; x++) {
                            first = _cycles.charAt(x);
                        }
                        return _alphabet.toInt(first);
                    } else {
                        return _alphabet.toInt(_cycles.charAt(i - 1));
                    }
                }
            }
            return c;
        } else {
            return invert(wrap(c));
        }
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        char first = 'a';
        for (int i = 0; i < _cycles.length(); i++) {
            if (_cycles.charAt(i) == p) {
                if (_cycles.charAt(i + 1) == ')') {
                    for (int x = i; _cycles.charAt(x) != '('; x--) {
                        first = _cycles.charAt(x);
                    }
                    return first;
                } else {
                    return _cycles.charAt(i + 1);
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        char first = 'a';
        for (int i = 0; i < _cycles.length(); i++) {
            if (_cycles.charAt(i) == c) {
                if (_cycles.charAt(i - 1) == '(') {
                    for (int x = i; _cycles.charAt(x) != ')'; x++) {
                        first = _cycles.charAt(x);
                    }
                    return first;
                } else {
                    return _cycles.charAt(i - 1);
                }
            }
        }
        return c;
    }
    /** Maps every item in alphabet to itself. */
    /** @param a an alphabet */
    void mapToSelf(Alphabet a) {
        char[] chararray = a.chars().toCharArray();
        for (char c : chararray) {
            addCycle("(" + c + ")");
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }
    /** Returns the cycles. */
    String cycles() {
        return _cycles;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (permute(i) == i) {
                return false;
            }
        }
        return true;
    }

    /** Checks if cycle is valid.
     * @return if valid cycle.
     * @param cycle a cycle. */
    public boolean isValidCycle(String cycle) {
        return !hasRepeat(cycle) && !hasBadWhitespace(cycle)
                && !hasInvalidChars(cycle) && !hasNestedCycle(cycle);
    }

    /** Checks if cycle has repeats.
     * @return has repeat
     * @param cycle a cycle */
    boolean hasRepeat(String cycle) {
        char[] charlist = cycle.toCharArray();
        char curr = charlist[0];
        int count = 0;
        while (count < charlist.length - 1) {
            if (curr == '(' || curr == ')' || Character.isWhitespace(curr)) {
                int rand = 0;
            } else {
                for (int i = 0; i < charlist.length; i++) {
                    if (i == count) {
                        continue;
                    } else if (curr == charlist[i]) {
                        return true;
                    }
                }
            }
            count++;
            curr = charlist[count];
        }
        return false;
    }

    /** Checks if there are invalid chars in cycle.
     * @return has invalid chars
     * @param cycle is a cycle */
    boolean hasInvalidChars(String cycle) {
        char[] charlist = cycle.toCharArray();
        for (int i = 0; i < charlist.length; i++) {
            if (charlist[i] != '(' && charlist[i] != ')'
                    && !Character.isWhitespace(charlist[i])) {
                if (!_alphabet.contains(charlist[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Checks if cycle contains bad white space.
     * @return has bad white space
     * @param cycle is a cycle */
    boolean hasBadWhitespace(String cycle) {
        char[] charlist = cycle.toCharArray();
        for (int i = 0; i < charlist.length; i++) {
            if (Character.isWhitespace(charlist[i])) {
                if (charlist[i - 1] != ')' && charlist[i + 1] != '(') {
                    return true;
                }
            } else if (charlist[i] == '(' && charlist[i + 1] == ')') {
                return true;
            }
        }
        return false;
    }

    /** Checks whether there are nested cycles.
     * @return has nested cycle
     * @param cycle is a cycle */
    boolean hasNestedCycle(String cycle) {
        char[] charlist = cycle.toCharArray();
        if (charlist[0] != '(') {
            return true;
        }
        char mark = '(';
        for (int i = 1; i < charlist.length; i++) {
            if (charlist[i] == '(' && mark != ')') {
                return true;
            } else if (charlist[i] == ')' && mark != '(') {
                return true;
            } else if (charlist[i] == ')' || charlist[i] == '(') {
                mark = charlist[i];
            }
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycle of this permutation. */
    private String _cycles;
}
