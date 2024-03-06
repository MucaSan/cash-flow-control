package controllers.cashflowcontrol;
import generalPurposesClasses.cashflowcontrol.ChangeWindow;
import generalPurposesClasses.cashflowcontrol.Manage;
import alerts.classes.cashflowcontrol.AlertManage;
import generalPurposesClasses.cashflowcontrol.Session;
import generalVariables.cashflowcontrol.GlobalVariables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
public class ManageMenuController extends AlertManage implements Initializable{
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imgEdit;
    @FXML
    private ImageView imgDelete;
    @FXML
    private TextField textName;
    @FXML
    private TextField textPrice;
    @FXML
    private TextField textStartDate;
    @FXML
    private TextField textEndDate;
    @FXML
    private TextField textSession;
    @FXML
    private TextField textPayment;
    @FXML
    private Button buttonGoBack;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonFilter;
    @FXML
    private Button buttonClear;
    @FXML
    private TableView<Manage> tableManage;
    @FXML
    private TableColumn<Manage, Double> colPrice;
    @FXML
    private TableColumn<Manage, Integer> colID;
    @FXML
    private TableColumn<Manage, String > colName;
    @FXML
    private TableColumn<Manage, String> colSession;
    @FXML
    private TableColumn<Manage, String> colPayment;
    @FXML
    private TableColumn<Manage, LocalDateTime> colDate;
    @FXML
    private TableColumn<Manage, List<ImageView>> colAction;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Manage> listData = FXCollections.observableArrayList();
        try {
            GlobalVariables.SQL = "SELECT TransactionDB.name, SessionDB.name, PaymentDB.name, TransactionDB.date, TransactionDB.amountAccount from TransactionDB INNER JOIN SessionDB" +
            "on TransactionDB.idSession = SessionDB.id INNER JOIN PaymentDB on TransactionDB.idPayment = PaymentDB.id where TransactionDB.userAssociated = '"+ GlobalVariables.userLogged +  "';";
            GlobalVariables.connection = GlobalVariables.database.getConnection();
            GlobalVariables.statement = GlobalVariables.connection.createStatement();
            GlobalVariables.resultSet = GlobalVariables.statement.executeQuery(GlobalVariables.SQL);
            GlobalVariables.nIterations = 0;
            while (GlobalVariables.resultSet.next()) {

                String name = GlobalVariables.resultSet.getNString(1);
                String nameSession = GlobalVariables.resultSet.getNString(2);
                String namePayment = GlobalVariables.resultSet.getNString(3);
                String date = GlobalVariables.resultSet.getString(4);
                double price = GlobalVariables.resultSet.getDouble(5);
                ImageView tempEdit = new ImageView() {
                    {
                        setId(String.format("edit%d", GlobalVariables.nIterations));
                        setImage(imgEdit.getImage());
                        setFitWidth(25);
                        setFitHeight(25);
                        final int j = GlobalVariables.nIterations;
                        setOnMouseClicked(mouseEvent -> {
                            try{
                                FXMLLoader loader = new FXMLLoader(getClass()
                                        .getResource("/fxml.controllers.session/manage.fxml"));
                                root = loader.load();
                                SessionController sessionController = loader.getController();
                                sessionController.displayName(name);
                                Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                sessionController.buttonSave.setVisible(true);
                                sessionController.buttonCreate.setVisible(false);
                                sessionController.buttonSave.setDisable(false);
                                sessionController.buttonCreate.setDisable(true);
                                sessionController.nameAssociated = name;
                            } catch (IOException e){
                                e.getCause();
                                e.printStackTrace();
                            }
                        });
                    }

                };
                ImageView tempDelete = new ImageView() {
                    {
                        setId(String.format("delete%d", GlobalVariables.nIterations));
                        setImage(imgDelete.getImage());
                        setFitWidth(25);
                        setFitHeight(25);
                        final int j = GlobalVariables.nIterations;
                        setOnMouseClicked(mouseEvent ->{
                            try{
                                alertMessage(name);
                                GlobalVariables.window = new ChangeWindow<MouseEvent>(mouseEvent,"/fxml.controllers.session/manageMenu.fxml");
                                GlobalVariables.window.setNewWindowFromMouseClick(GlobalVariables.window.getActionMouse(),
                                        GlobalVariables.window.getPathToFXMLFile());
                            } catch(IOException e){
                                e.printStackTrace();
                                e.getCause();
                            }
                        });
                    }
                };
                GlobalVariables.nIterations+=1;
                listData.add(new Manage(GlobalVariables.nIterations, name,nameSession, namePayment,date, price
                        new HBox(tempEdit, tempDelete)));
            }
        } catch (SQLException e) {
            e.getCause();
            e.printStackTrace();
        }
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("actions"));
        tableManage.setItems(listData);
    }
    public void buttonGoBackClick(MouseEvent event) throws IOException {
        GlobalVariables.window = new ChangeWindow<MouseEvent>(event,"/fxml.controllers.menu/menu.fxml");
        GlobalVariables.window.setNewWindowFromMouseClick(GlobalVariables.window.getActionMouse(),
                GlobalVariables.window.getPathToFXMLFile());
    }
    @FXML
    private void buttonClearClick(MouseEvent event){
        textEndDate.setText("");
        textName.setText("");
        textPayment.setText("");
        textPrice.setText("");
        textSession.setText("");
        textStartDate.setText("");
 }
 public void buttonCreateClick(MouseEvent event){
     FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("/fxml.controllers.payment/manage.fxml"));
     try{
         Parent root1 =  fxmlLoader1.load();
         Stage stage = new Stage();
         stage.setTitle("CREATE TRANSACTION");
         stage.setScene(new Scene(root1));
         stage.show();
     } catch(IOException e){
         e.printStackTrace();
         e.getCause();
     }
 }
}
