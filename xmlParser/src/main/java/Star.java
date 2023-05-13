import java.sql.Date;

public class Star {
    private String name;
//    private int birthYear; // maybe consider using string for this to deal with nulls
                            // can do checking that it is an int before it is set so it wont be a problem
    private String birthYear;

    public Star() {
    }

//    public Star ( String name, int birthYear ) {
    public Star ( String name, String birthYear ) {
        this.name = name;
        this.birthYear = birthYear;
    }

    // Getters
    public getName() {
        return name;
    }

    public getBirthYear() {
        return birthYear;
    }

    // Setters
    public setName( String name ) {
        this.name = name;
    }

    public setBirthYear( String birthYear ) {
        this.birthYear = birthYear;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Birth Year:" + getBirthYear());
        sb.append(".");

        return sb.toString();
    }

}