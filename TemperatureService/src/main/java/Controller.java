public class Controller {
    public void run() {
        new WebService(new AemetDatamartReader()).startAPI();
    }
}
