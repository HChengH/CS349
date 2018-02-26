
public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        DrawingOptionsModel drmodel = new DrawingOptionsModel();
        MyMenuBar mainMenuBar = new MyMenuBar(drmodel);
        MyToolBar mainToolBar = new MyToolBar(drmodel);
        mainCanvasView mcv = new mainCanvasView(drmodel);

        MainView mainView = new MainView(model, mainMenuBar, mainToolBar, mcv);
    }
}
