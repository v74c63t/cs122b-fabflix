import java.sql.Date;

public class Star {
    private String name;
    private int birthYear;

    public Star() {
    }

    public Star ( String name, int birthYear ) {
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

    public setBirthYear( int birthYear ) {
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