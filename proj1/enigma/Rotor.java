package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Mohamed Maazin Sudheer
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _ringSetting = _permutation.alphabet().toChar(0);
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }
    /** Returns the ringsetting. */
    char ringSetting() {
        return _ringSetting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = _permutation.wrap(posn);
    }
    /** Sets the ring setting.
     * @param r is a char
     */
    void setRing(char r) {
        _ringSetting = r;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        if (_permutation.alphabet().contains(cposn)) {
            _setting = _permutation.alphabet().toInt(cposn);
        }
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int ring = alphabet().toInt(ringSetting());
        int contact = _permutation.permute(p + setting() - ring);
        return _permutation.wrap(contact - setting() + ring);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int ring = alphabet().toInt(ringSetting());
        int contact = _permutation.invert(e + setting() - ring);
        return _permutation.wrap(contact - setting() + ring);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;
    /** The setting. */
    private int _setting;
    /** The ring setting. */
    private char _ringSetting;
}
