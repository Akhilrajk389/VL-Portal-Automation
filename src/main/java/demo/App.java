package demo;
import java.net.MalformedURLException;
// import demo.LoginUtils;


public class App {
    public void getGreeting() throws InterruptedException, MalformedURLException {
        System.out.println("Hello Autmation Wizards!");
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException {
        new App().getGreeting();

    }
}
