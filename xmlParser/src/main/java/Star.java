import java.sql.Date;

public class Star {
    private String id;

    private String name;
//    private int birthYear; // maybe consider using string for this to deal with nulls
                            // can do checking that it is an int before it is set so it wont be a problem
    private String birthYear;

    public Star() {
    }

//    public Star ( String name, int birthYear ) {
    public Star ( String id, String name, String birthYear ) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Setters
    public void setName( String name ) {
        this.name = name;
    }

    public void setBirthYear( String birthYear ) {
        this.birthYear = birthYear;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Id:" + getId());
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Birth Year:" + getBirthYear());
        sb.append(".");

        return sb.toString();
    }

}