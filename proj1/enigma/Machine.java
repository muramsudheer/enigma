package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Mohamed Maazin Sudheer
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        if (numRotors > 1) {
            _numRotors = numRotors;
        } else {
            throw new EnigmaException("Machine requires more than 1 rotor");
        }
        if (pawls >= 0 && pawls < _numRotors) {
            _pawls = pawls;
        } else {
            throw new EnigmaException("Incorrect number of pawls");
        }
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return my list of rotors. */
    ArrayList<Rotor> getRotors() {
        return _myRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }
    /** Returns the alphabet. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(ArrayList<String> rotors) {
        _myRotors.clear();
        for (int i = 0; i < rotors.size(); i++) {
            for (Rotor r : _allRotors) {
                if (r.name().equals(rotors.get(i))) {
                    _myRotors.add(r);
                    break;
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() == numRotors() - 1) {
            char[] settingvals = setting.toCharArray();
            for (int i = 0; i < settingvals.length; i++) {
                _myRotors.get(i + 1).set(settingvals[i]);
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        String cycles = plugboard.cycles();
        Pattern twoCycles =
                Pattern.compile("(\\([^\\*\\(\\)][^\\*\\(\\)]\\))+");
        Matcher twoCycleMatch = twoCycles.matcher(cycles);
        if (twoCycleMatch.matches()) {
            _plugboard = plugboard;
        } else {
            throw new EnigmaException("Invalid input for plugboard cycles");
        }
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        int result = c;
        ArrayList<Rotor> toAdvance = new ArrayList<>();
        for (int i = _myRotors.size() - 1; i > 0; i--) {
            Rotor r = _myRotors.get(i);
            Rotor neighbor = _myRotors.get(i - 1);
            if (i == _myRotors.size() - 1) {
                if (r.atNotch() && neighbor.rotates()) {
                    toAdvance.add(neighbor);
                    toAdvance.add(r);
                } else {
                    toAdvance.add(r);
                }
            } else if (r.atNotch() && neighbor.rotates()) {
                toAdvance.add(neighbor);
                if (!toAdvance.contains(r)) {
                    toAdvance.add(r);
                }
            }
        }
        for (Rotor r : toAdvance) {
            r.advance();
        }
        if (_plugboard != null) {
            result = _plugboard.permute(result);
        }
        for (int i = _myRotors.size() - 1; i > 0; i--) {
            Rotor current = _myRotors.get(i);
            result = current.convertForward(result);
        }
        for (int i = 0; i < _myRotors.size(); i++) {
            Rotor current = _myRotors.get(i);
            result = current.convertBackward(result);
        }
        if (_plugboard != null) {
            result = _plugboard.invert(result);
        }
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] msgarray = msg.toCharArray();
        String result = "";
        for (int i = 0; i < msgarray.length; i++) {
            if (Character.isWhitespace(msgarray[i])) {
                int a = 1;
            } else {
                int change = convert(_alphabet.toInt(msgarray[i]));
                result += _alphabet.toChar(change);
            }
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private int _numRotors;
    /** Number of pawls. */
    private int _pawls;
    /** All rotors. */
    private final Collection<Rotor> _allRotors;
    /** The plugboard. */
    private Permutation _plugboard;
    /** Rotors in use. */
    private ArrayList<Rotor> _myRotors = new ArrayList<>();
}
