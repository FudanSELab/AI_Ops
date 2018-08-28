package hello.hello;

public class Greeting {

    private final long id;
    private final boolean result;

    public Greeting() {
        this.id = 1231232112;
        this.result = true;
    }

    public Greeting(long id, boolean result) {
        this.id = id;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public boolean getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Value{" +
                "id=" + id +
                ", result='" + result + '\'' +
                '}';
    }
}
