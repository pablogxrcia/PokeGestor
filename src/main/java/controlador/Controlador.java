package controlador;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.exit;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import modelo.Entrenador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import java.io.*;
import java.util.Base64;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import modelo.Captura;
import modelo.Pokemon;


public class Controlador implements Initializable{
    
    Connection conexion;
    Statement st;
    ResultSet rs;

    @FXML
    private TableView<Entrenador> entrenadorTable;
    @FXML
    private TableView<Captura> capturaTable;
    @FXML
    private TableView<Pokemon> pokemonTable;
    @FXML
    private TableColumn<Entrenador, String> colID;
    @FXML
    private TableColumn<Entrenador, String> colNombre;
    @FXML
    private TableColumn<Entrenador, String> colEdad;
    @FXML
    private TableColumn<Entrenador, String> colMedallas;
    @FXML
    private TableColumn<Entrenador, String> colRegion;
    @FXML
    private TableColumn<Entrenador, String> colSexo;
    @FXML
    private TableColumn<Captura, String> colEntrenadorCaptura;
    @FXML
    private TableColumn<Captura, String> colFechaCaptura;
    @FXML
    private TableColumn<Pokemon, String> colIDPokemon;
    @FXML
    private TableColumn<Pokemon, ImageView> colImagenPokemon;
    @FXML
    private TableColumn<Captura, String> colLocalizacionCaptura;
    @FXML
    private TableColumn<Pokemon, String> colNivelPokemon;
    @FXML
    private TableColumn<Pokemon, String> colNombrePokemon;
    @FXML
    private TableColumn<Pokemon, String> colPokedexPokemon;
    @FXML
    private TableColumn<Captura, String> colPokemonCaptura;
    @FXML
    private TableColumn<Pokemon, ImageView> colTipoPokemon;
    @FXML
    private TextField searchFieldCaptura;
    @FXML
    private TextField searchFieldEntrenador;
    @FXML
    private TextField searchFieldPokemon;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnExit;
    @FXML
    private ImageView imgAñadir;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgSalir;
    @FXML
    private TabPane tabPane;
    
    // Declaración de las variables de instancia Entrenador
    private TextField txtNombreEntrenador;
    private Spinner<Integer> spinerEdadEntrenador;
    private ComboBox<Integer> comboMedallasEntrenador;
    private TextField txtRegionEntrenador;
    private RadioButton selectedSexoEntrenador;
    
    // Declaración de las variables de instancia Pokemon
    private TextField txtNombrePokemon;
    private Spinner<Integer> spinerNivelPokemon;
    private Spinner<Integer> spinerPokedex;
    private ComboBox<String> comboTipoPokemon;
    private File imagenSeleccionadaPokemon;
    
    //Declaracion de las variables de instancia Captura
    private ComboBox<String> entrenadorComboBox;
    private ComboBox<String> pokemonComboBox;
    private DatePicker fechaPicker;
    private TextField localizacionField;
    
    //Declaracion Observables
    ObservableList<Entrenador> ObservableEntrenadores = FXCollections.observableArrayList();
    ObservableList<Pokemon> ObservablePokemons = FXCollections.observableArrayList();
    ObservableList<Captura> ObservableCapturas = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        try {
            conexion = this.getConnection();
            if (conexion != null) {
                this.st = conexion.createStatement();
            }
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
        
        if (conexion != null) {
            establecerIconos();
            inicializarTablaEntrenadores();
            inicializarTablaPokemons();
            inicializarTablaCapturas();
         
            btnExit.setOnAction(e -> salirPrograma());
            
            configurarBotonesPorTab();
            configurarBuscadorEntrenador();
            configurarBuscadorPokemon();
            configurarBuscadorCaptura();
    
            tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
                configurarBotonesPorTab();
            });
            
            configurarSeleccionTablaEntrenador();
            configurarSeleccionTablaPokemon();
            configurarSeleccionTablaCaptura();
        }
    }
    
    private void salirPrograma() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Salir de la aplicación");
        alert.setHeaderText("¿Estás seguro de que deseas salir?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
    private void mostrarAlerta(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void establecerIconos(){
        imgAñadir.setImage(new Image(getClass().getClassLoader().getResourceAsStream("boton-añadir.png")));
        imgEditar.setImage(new Image(getClass().getClassLoader().getResourceAsStream("boton-editar.png")));
        imgEliminar.setImage(new Image(getClass().getClassLoader().getResourceAsStream("boton-eliminar.png")));
        imgSalir.setImage(new Image(getClass().getClassLoader().getResourceAsStream("boton-salir.png")));
    }
    
    private void configurarBotonesPorTab() {
        switch (tabPane.getSelectionModel().getSelectedItem().getText()) {
            case "Entrenadores" -> configurarBotonesEntrenadores();
            case "Pokemons" -> configurarBotonesPokemon();
            case "Capturas" -> configurarBotonesCaptura();
        }
    }
    
    public Connection getConnection() throws IOException {
        //Importante: hay que separar los datos de conexión del programa, así, al cambiar, no tendría
        //que cambiar nada internamente, o al menos, el mínimo posible.
        Properties properties = new Properties();
        String IP, PORT, BBDD, USER, PWD;
        //Se lee IP desde fuera del jar
        try {
            InputStream input_ip = new FileInputStream("ip.properties");//archivo debe estar junto al jar
            properties.load(input_ip);
            IP = (String) properties.get("IP");
        } catch (FileNotFoundException e) {
            System.out.println("No se pudo encontrar el archivo de propiedades para IP, se establece localhost por defecto");
            IP = "localhost";
        }

        InputStream input = getClass().getClassLoader().getResourceAsStream("bbdd.properties");
        if (input == null) {
            System.out.println("No se pudo encontrar el archivo de propiedades");
            return null;
        } else {
            // Cargar las propiedades desde el archivo
            properties.load(input);
            // String IP = (String) properties.get("IP"); //Tiene sentido leerlo desde fuera del Jar por si cambiamos la IP, el resto no debería de cambiar
            //ni debería ser público
            PORT = (String) properties.get("PORT");//En vez de crear con new, lo crea por asignación + casting
            BBDD = (String) properties.get("BBDD");
            USER = (String) properties.get("USER");//USER de MARIADB en LAMP 
            PWD = (String) properties.get("PWD");//PWD de MARIADB en LAMP 

            Connection conn;
            try {
                String cadconex = "jdbc:mariadb://" + IP + ":" + PORT + "/" + BBDD + " USER:" + USER + "PWD:" + PWD;
                System.out.println(cadconex);
                //Si usamos LAMP Funciona con ambos conectores
                conn = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + "/" + BBDD, USER, PWD);
                //conn = DriverManager.getConnection(cadconex);
                return conn;
            } catch (SQLException e) {
                System.out.println("Error SQL: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Ha ocurrido un error de conexión");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                exit(0);
                return null;
            }
        }
    }
    
    private void gestionarEntrenador(Entrenador entrenadorSeleccionado) {
        Stage dialog = new Stage();
        String title = (entrenadorSeleccionado == null) ? "Añadir Entrenador" : "Editar Entrenador";
        dialog.setTitle(title);

        VBox mainLayout = new VBox(15); 
        mainLayout.setPadding(new Insets(15));

        // Nombre
        HBox nombreBox = new HBox(10); 
        Label lblNombre = new Label("Nombre:");
        lblNombre.setMinWidth(100);
        txtNombreEntrenador = new TextField(entrenadorSeleccionado != null ? entrenadorSeleccionado.getNombre() : "");
        //txtNombre.setId("txtNombre");
        txtNombreEntrenador.setPrefWidth(200);
        nombreBox.getChildren().addAll(lblNombre, txtNombreEntrenador);

        // Edad
        HBox edadBox = new HBox(10);
        Label lblEdad = new Label("Edad:");
        lblEdad.setMinWidth(100);
        spinerEdadEntrenador = new Spinner<>(1, 120, entrenadorSeleccionado != null ? entrenadorSeleccionado.getEdad() : 18);
        //spinerEdad.setId("spinerEdad");
        spinerEdadEntrenador.setPrefWidth(100);
        edadBox.getChildren().addAll(lblEdad, spinerEdadEntrenador);
        spinerEdadEntrenador.setEditable(false);

        // Número de Medallas
        HBox medallasBox = new HBox(10);
        Label lblMedallas = new Label("Num. Medallas:");
        lblMedallas.setMinWidth(100);
        comboMedallasEntrenador = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));
        //comboMedallas.setId("comboMedallas");
        comboMedallasEntrenador.setValue(entrenadorSeleccionado != null ? entrenadorSeleccionado.getNumMedallas() : 1);
        comboMedallasEntrenador.setPrefWidth(100); 
        medallasBox.getChildren().addAll(lblMedallas, comboMedallasEntrenador);

        // Región
        HBox regionBox = new HBox(10);
        Label lblRegion = new Label("Región:");
        lblRegion.setMinWidth(100);
        txtRegionEntrenador = new TextField(entrenadorSeleccionado != null ? entrenadorSeleccionado.getRegion() : "");
        //txtRegion.setId("txtRegion");
        txtRegionEntrenador.setPrefWidth(200); 
        regionBox.getChildren().addAll(lblRegion, txtRegionEntrenador);

        // Sexo
        HBox sexoBox = new HBox(10);
        Label lblSexo = new Label("Sexo:");
        lblSexo.setMinWidth(100);
        
        ToggleGroup sexoGroup = new ToggleGroup();
        
        RadioButton rbHombre = new RadioButton("Hombre");
        rbHombre.setToggleGroup(sexoGroup);
        rbHombre.setUserData("Hombre");
        
        RadioButton rbMujer = new RadioButton("Mujer");
        rbMujer.setToggleGroup(sexoGroup);
        rbMujer.setUserData("Mujer");
        
        RadioButton rbOtro = new RadioButton("Otro");
        rbOtro.setToggleGroup(sexoGroup);
        rbOtro.setUserData("Otro");

        if (entrenadorSeleccionado != null) {
            switch (entrenadorSeleccionado.getSexo()) {
                case "Hombre" -> rbHombre.setSelected(true);
                case "Mujer" -> rbMujer.setSelected(true);
                case "Otro" -> rbOtro.setSelected(true);
            }
        }

        sexoBox.getChildren().addAll(lblSexo, rbHombre, rbMujer, rbOtro);

        // Botones
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT); 
        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.setPrefWidth(80); 
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefWidth(80);
        buttonBox.getChildren().addAll(btnConfirmar, btnCancelar);

        mainLayout.getChildren().addAll(nombreBox, edadBox, medallasBox, regionBox, sexoBox, buttonBox);

        // Eventos
        btnConfirmar.setOnAction(e -> {
            if (txtNombreEntrenador.getText().isEmpty() || txtRegionEntrenador.getText().isEmpty() || sexoGroup.getSelectedToggle() == null) {
                mostrarAlerta("Error", "Todos los campos deben estar rellenos.");
            } else {
                selectedSexoEntrenador = (RadioButton) sexoGroup.getSelectedToggle();
                selectedSexoEntrenador.setId("selectedSexo");

                if (entrenadorSeleccionado == null) {
                    añadirEntrenador();
                } else {
                    editarEntrenador(entrenadorSeleccionado);
                }
                dialog.close();
            }
        });

        btnCancelar.setOnAction(e -> dialog.close());

        Scene scene = new Scene(mainLayout, 340, 270);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
    
    public ObservableList<Entrenador> dameListaEntrenadores() {
        if (conexion != null) {
            ObservableEntrenadores.clear(); //Limpiamos el contenido actual
            String query = "SELECT * FROM ENTRENADOR";
            try {
                rs = st.executeQuery(query);
                Entrenador entrenador;
                while (rs.next()) { //Se usan los identificadores propios en la BBDD (no es case sensitive). Revisar en phpmyadmin
                    entrenador = new Entrenador(rs.getInt("id_entrenador"), rs.getString("nombre"), rs.getInt("edad"), rs.getInt("medallas"), rs.getString("region"),rs.getString("sexo"));
                    ObservableEntrenadores.add(entrenador);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return ObservableEntrenadores;
        }
        return null;
    }
    
    private void inicializarTablaEntrenadores(){
        colID.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colEdad.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getEdad())));
        colMedallas.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumMedallas())));
        colRegion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegion()));
        colSexo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSexo()));
        
        entrenadorTable.setItems(dameListaEntrenadores());
    }
    
    private void configurarBuscadorEntrenador() {
        searchFieldEntrenador.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarEntrenadores(newValue);
        });
    }
    
    private void filtrarEntrenadores(String criterio) {
        ObservableList<Entrenador> listaCompleta = dameListaEntrenadores();
        
        FilteredList<Entrenador> listaFiltrada = new FilteredList<>(listaCompleta, entrenador -> {
            if (criterio == null || criterio.isEmpty()) {
                return true; 
            }
            String criterioEnMinusculas = criterio.toLowerCase();

            return entrenador.getNombre().toLowerCase().contains(criterioEnMinusculas) ||
                   entrenador.getRegion().toLowerCase().contains(criterioEnMinusculas);
        });

        entrenadorTable.setItems(listaFiltrada);
    }
    
    private void añadirEntrenadorVentana() {
        gestionarEntrenador(null);
    }
    
    private void editarEntrenadorVentana() {
        Entrenador entrenadorSeleccionado = entrenadorTable.getSelectionModel().getSelectedItem();
        if (entrenadorSeleccionado != null) {
            gestionarEntrenador(entrenadorSeleccionado);
        }
    }
    
    private void eliminarEntrenadorVentana() {
        
        Entrenador entrenadorSeleccionado = entrenadorTable.getSelectionModel().getSelectedItem();
        if (entrenadorSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Entrenador");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar al entrenador seleccionado?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eliminarEntrenador(entrenadorSeleccionado);
        }
    }
    
    private void configurarBotonesEntrenadores() {
        btnAdd.setOnAction(event -> añadirEntrenadorVentana()); 
        btnEdit.setOnAction(event -> editarEntrenadorVentana());
        btnDelete.setOnAction(event -> eliminarEntrenadorVentana());
    }
    
    private void configurarSeleccionTablaEntrenador() {
        entrenadorTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean itemSelected = newValue != null;
            btnEdit.setDisable(!itemSelected);
            btnDelete.setDisable(!itemSelected);
        });
    }
    
    private void añadirEntrenador() {
    String query = "INSERT INTO ENTRENADOR (nombre, edad, sexo, region, medallas) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            
            preparedStatement.setString(1, txtNombreEntrenador.getText());  
            preparedStatement.setInt(2, spinerEdadEntrenador.getValue());  
            preparedStatement.setString(3,(String) selectedSexoEntrenador.getUserData());  
            preparedStatement.setString(4, txtRegionEntrenador.getText());  
            preparedStatement.setInt(5, comboMedallasEntrenador.getValue());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        entrenadorTable.setItems(dameListaEntrenadores());
    }

    private void editarEntrenador(Entrenador entrenadorSeleccionado) {
        String query = "UPDATE ENTRENADOR SET nombre=?, edad=?, sexo=?, region=?, medallas=? WHERE id_entrenador=?";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            
            preparedStatement.setString(1, txtNombreEntrenador.getText());  
            preparedStatement.setInt(2, spinerEdadEntrenador.getValue());  
            preparedStatement.setString(3, (String) selectedSexoEntrenador.getUserData()); 
            preparedStatement.setString(4, txtRegionEntrenador.getText());  
            preparedStatement.setInt(5, comboMedallasEntrenador.getValue()); 
            preparedStatement.setInt(6, entrenadorSeleccionado.getId());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        entrenadorTable.setItems(dameListaEntrenadores());
    }
    
    private void eliminarEntrenador(Entrenador entrenadorSeleccionado) {
        String query = "DELETE FROM ENTRENADOR WHERE id_entrenador=?";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            
            preparedStatement.setInt(1,entrenadorSeleccionado.getId());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: "+e.getMessage());
        }
        entrenadorTable.setItems(dameListaEntrenadores());
    }
    
    
    
    
    private void gestionarPokemon(Pokemon pokemonSeleccionado) { 
        Stage dialog = new Stage();
        String title = (pokemonSeleccionado == null) ? "Añadir Pokémon" : "Editar Pokémon";
        dialog.setTitle(title);

        VBox mainLayout = new VBox(15); 
        mainLayout.setPadding(new Insets(15));

        // Nombre
        HBox nombreBox = new HBox(10); 
        Label lblNombre = new Label("Nombre:");
        lblNombre.setMinWidth(100);
        txtNombrePokemon = new TextField(pokemonSeleccionado != null ? pokemonSeleccionado.getNombrePokemon(): "");
        txtNombrePokemon.setPrefWidth(150);
        nombreBox.getChildren().addAll(lblNombre, txtNombrePokemon);

        //Nivel
        HBox nivelBox = new HBox(10);
        Label lblNivel = new Label("Nivel:");
        lblNivel.setMinWidth(100);
        spinerNivelPokemon = new Spinner<>(1, 100, pokemonSeleccionado != null ? pokemonSeleccionado.getNivelPokemon(): 1);
        spinerNivelPokemon.setPrefWidth(100);
        nivelBox.getChildren().addAll(lblNivel, spinerNivelPokemon);
        spinerNivelPokemon.setEditable(false);

        // Número Pokédex
        HBox pokedexBox = new HBox(10);
        Label lblPokedex = new Label("Nº Pokédex:");
        lblPokedex.setMinWidth(100);
        spinerPokedex = new Spinner<>(1, 1000, pokemonSeleccionado != null ? pokemonSeleccionado.getNumPokedex(): 1);
        spinerPokedex.setPrefWidth(100);
        pokedexBox.getChildren().addAll(lblPokedex, spinerPokedex);
        spinerPokedex.setEditable(false);

        // Tipo
        HBox tipoBox = new HBox(10);
        Label lblTipo = new Label("Tipo:");
        lblTipo.setMinWidth(100);
        comboTipoPokemon = new ComboBox<>(FXCollections.observableArrayList(
            "Normal", "Fuego", "Agua", "Electrico", "Planta", "Hielo", "Lucha", "Veneno", 
            "Tierra", "Volador", "Psiquico", "Bicho", "Roca", "Fantasma", "Dragon", "Siniestro", 
            "Acero", "Hada"));
        comboTipoPokemon.setValue(pokemonSeleccionado != null ? pokemonSeleccionado.getTipo() : "Normal");
        comboTipoPokemon.setPrefWidth(150);
        tipoBox.getChildren().addAll(lblTipo, comboTipoPokemon);

        // Imagen
        HBox imagenBox = new HBox(10);
        Label lblImagen = new Label("Imagen:");
        lblImagen.setMinWidth(100);
        Button btnSeleccionarImagen = new Button("Seleccionar archivo");
        btnSeleccionarImagen.setPrefWidth(150);

        btnSeleccionarImagen.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(dialog);
            if (file != null) {
                imagenSeleccionadaPokemon = file;
            }
        });
        imagenBox.getChildren().addAll(lblImagen, btnSeleccionarImagen);

        // Botones
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT); 
        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.setPrefWidth(80); 
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefWidth(80);
        buttonBox.getChildren().addAll(btnConfirmar, btnCancelar);

        mainLayout.getChildren().addAll(nombreBox, nivelBox, pokedexBox, tipoBox, imagenBox, buttonBox);

        // Eventos
        btnConfirmar.setOnAction(e -> {
            if (txtNombrePokemon.getText().isEmpty() || comboTipoPokemon.getValue() == null || imagenSeleccionadaPokemon == null) {
                mostrarAlerta("Error", "Todos los campos deben estar rellenos.");
            } else {
                String imagenBase64 = convertirImagenA64(imagenSeleccionadaPokemon); 
                
                if (pokemonSeleccionado == null) {
                    añadirPokemon(imagenBase64);
                } else {
                    editarPokemon(pokemonSeleccionado, imagenBase64);
                }
                dialog.close();
            }
        });

        btnCancelar.setOnAction(e -> dialog.close());

        Scene scene = new Scene(mainLayout, 290, 280);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
    
    public ObservableList<Pokemon> dameListaPokemons() {
        if (conexion != null) {
            ObservablePokemons.clear(); //Limpiamos el contenido actual
            String query = "SELECT * FROM POKEMON";
            try {
                rs = st.executeQuery(query);
                Pokemon pokemon;
                while (rs.next()) { //Se usan los identificadores propios en la BBDD (no es case sensitive). Revisar en phpmyadmin
                    pokemon = new Pokemon(rs.getInt("id_pokemon"), rs.getString("nombre_pokemon"), rs.getInt("nivel_pokemon"), rs.getString("tipo"), rs.getInt("num_pokedex"),rs.getString("imagen"));
                    ObservablePokemons.add(pokemon);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return ObservablePokemons;
        }
        return null;
    }
    
    private void inicializarTablaPokemons(){        
        colIDPokemon.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdPokemon())));
        colImagenPokemon.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView(base64ToImage(cellData.getValue().getImagen()));
            imageView.setFitHeight(60);
            imageView.setFitWidth(60);
            return new SimpleObjectProperty<>(imageView);
        });
        //colTipoPokemon.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTipo())));
        colTipoPokemon.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView(obtenerImagenPorTipo(cellData.getValue().getTipo()));
            imageView.setFitHeight(60);
            imageView.setFitWidth(60);
            return new SimpleObjectProperty<>(imageView);
        });      
        colNombrePokemon.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNombrePokemon())));
        colNivelPokemon.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNivelPokemon())));
        colPokedexPokemon.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumPokedex())));
        pokemonTable.setItems(dameListaPokemons());
    }
    
    private Image obtenerImagenPorTipo(String tipo) {
        return switch (tipo) {
            case "Normal"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Normal.jpg"));
            case "Fuego"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Fuego.jpg"));
            case "Agua"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Agua.jpg"));
            case "Electrico"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_El3Fctrico.jpg"));
            case "Planta"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Planta.jpg"));
            case "Hielo"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Hielo.jpg"));
            case "Lucha"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Lucha.jpg"));
            case "Veneno"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Veneno.jpg"));
            case "Tierra"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Tierra.jpg"));
            case "Volador"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Volador.jpg"));
            case "Psiquico"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Ps3Fquico.jpg"));
            case "Bicho"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Bicho.jpg"));
            case "Roca"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Roca.jpg"));
            case "Fantasma"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Fantasma.jpg"));
            case "Dragon"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Drag3Fn.jpg"));
            case "Siniestro"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Siniestro.jpg"));
            case "Acero"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Acero.jpg"));
            case "Hada"-> new Image(getClass().getClassLoader().getResourceAsStream("Type_Hada.jpg"));
            default-> null;
        };
    }
    
    private void configurarBuscadorPokemon() {
        searchFieldPokemon.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarPokemons(newValue);
        });
    }
    
    private void filtrarPokemons(String criterio) {
        ObservableList<Pokemon> listaCompleta = dameListaPokemons();

        FilteredList<Pokemon> listaFiltrada = new FilteredList<>(listaCompleta, pokemon -> {
            if (criterio == null || criterio.isEmpty()) {
                return true; 
            }
            String criterioEnMinusculas = criterio.toLowerCase();

            return pokemon.getNombrePokemon().toLowerCase().contains(criterioEnMinusculas) ||
                   pokemon.getTipo().toLowerCase().contains(criterioEnMinusculas);
        });

        pokemonTable.setItems(listaFiltrada);
    }
    
    private void añadirPokemonVentana() {
        gestionarPokemon(null);
    }
    
    private void editarPokemonVentana() {
        Pokemon pokemonSeleccionado = pokemonTable.getSelectionModel().getSelectedItem();
        if (pokemonSeleccionado != null) {
            gestionarPokemon(pokemonSeleccionado);
        }
    }
    
    private void eliminarPokemonVentana() {
        Pokemon pokemonSeleccionado = pokemonTable.getSelectionModel().getSelectedItem();
        if (pokemonSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Pokemon");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar al Pokemon seleccionado?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eliminarPokemon(pokemonSeleccionado);
        }
    }
    
    private String convertirImagenA64(File archivoImagen) {
        try (
            FileInputStream fileInputStream = new FileInputStream(archivoImagen);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes); 
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private Image base64ToImage(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            return image;
            
	} catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
	}
    }
    
    private void configurarBotonesPokemon() {
        btnAdd.setOnAction(event -> añadirPokemonVentana()); 
        btnEdit.setOnAction(event -> editarPokemonVentana());
        btnDelete.setOnAction(event -> eliminarPokemonVentana());
    }
    
    private void configurarSeleccionTablaPokemon() {
        pokemonTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean itemSelected = newValue != null;
            btnEdit.setDisable(!itemSelected);
            btnDelete.setDisable(!itemSelected);
        });
    }
    
    private void añadirPokemon(String imagenBase64) {
        String query = "INSERT INTO POKEMON (nombre_pokemon, nivel_pokemon, tipo, num_pokedex, imagen) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);

            preparedStatement.setString(1, txtNombrePokemon.getText());  
            preparedStatement.setInt(2, spinerNivelPokemon.getValue()); 
            preparedStatement.setString(3, comboTipoPokemon.getValue());  
            preparedStatement.setInt(4, spinerPokedex.getValue()); 
            preparedStatement.setString(5, imagenBase64); 

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        pokemonTable.setItems(dameListaPokemons());  
    }
    
    private void editarPokemon(Pokemon pokemonSeleccionado,  String imagenBase64) {
        String query = "UPDATE POKEMON SET nombre_pokemon=?, nivel_pokemon=?, tipo=?, num_pokedex=?, imagen=? WHERE id_pokemon=?";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);

            preparedStatement.setString(1, txtNombrePokemon.getText());  
            preparedStatement.setInt(2, spinerNivelPokemon.getValue()); 
            preparedStatement.setString(3, comboTipoPokemon.getValue());  
            preparedStatement.setInt(4, spinerPokedex.getValue()); 
            preparedStatement.setString(5, imagenBase64); 
            preparedStatement.setInt(6, pokemonSeleccionado.getIdPokemon());

            preparedStatement.executeUpdate();

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        pokemonTable.setItems(dameListaPokemons()); 
    }

    private void eliminarPokemon(Pokemon pokemonSeleccionado) {
        String query = "DELETE FROM POKEMON WHERE id_pokemon=?";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            
            preparedStatement.setInt(1,pokemonSeleccionado.getIdPokemon());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: "+e.getMessage());
        }
        pokemonTable.setItems(dameListaPokemons());
    }
    
    
    
    private void gestionarCaptura(Captura capturaSeleccionada) {
        Stage dialog = new Stage();
        String title = (capturaSeleccionada == null) ? "Añadir Captura" : "Editar Captura";
        dialog.setTitle(title);

        VBox mainLayout = new VBox(15); 
        mainLayout.setPadding(new Insets(15));

        // Entrenador
        HBox entrenadorBox = new HBox(10); 
        Label lblEntrenador = new Label("Entrenador:");
        lblEntrenador.setMinWidth(100);
        entrenadorComboBox = new ComboBox<>();
        ObservableList<String> listaEntrenadores = dameListaNombresEntrenadores(); 
        entrenadorComboBox.setItems(listaEntrenadores);
        entrenadorComboBox.setPromptText("Nombre Entrenador");
        entrenadorComboBox.setValue(capturaSeleccionada != null ? capturaSeleccionada.getNombreEntrenador() : null);
        entrenadorComboBox.setPrefWidth(150);
        entrenadorBox.getChildren().addAll(lblEntrenador, entrenadorComboBox);

        // Pokémon
        HBox pokemonBox = new HBox(10);
        Label lblPokemon = new Label("Pokémon:");
        lblPokemon.setMinWidth(100);
        pokemonComboBox = new ComboBox<>();
        ObservableList<String> listaPokemons = dameListaNombresPokemons(); 
        pokemonComboBox.setItems(listaPokemons);
        pokemonComboBox.setPromptText("Nombre Pokemon");
        pokemonComboBox.setValue(capturaSeleccionada != null ? capturaSeleccionada.getNombrePokemon() : null);
        pokemonComboBox.setPrefWidth(150);
        pokemonBox.getChildren().addAll(lblPokemon, pokemonComboBox);

        // Fecha
        HBox fechaBox = new HBox(10);
        Label lblFecha = new Label("Fecha:");
        lblFecha.setMinWidth(100);
        fechaPicker = new DatePicker();
        fechaPicker.setValue(capturaSeleccionada != null ? capturaSeleccionada.getFecha().toLocalDate() : LocalDate.now());
        fechaPicker.setPrefWidth(150);
        fechaBox.getChildren().addAll(lblFecha, fechaPicker);
        fechaPicker.setEditable(false);

        // Localización
        HBox localizacionBox = new HBox(10);
        Label lblLocalizacion = new Label("Localización:");
        lblLocalizacion.setMinWidth(100);
        localizacionField = new TextField(capturaSeleccionada != null ? capturaSeleccionada.getLocalizacion() : "");
        localizacionField.setPrefWidth(150);
        localizacionBox.getChildren().addAll(lblLocalizacion, localizacionField);

        // Botones
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT); 
        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.setPrefWidth(80); 
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefWidth(80);
        buttonBox.getChildren().addAll(btnConfirmar, btnCancelar);

        mainLayout.getChildren().addAll(entrenadorBox, pokemonBox, fechaBox, localizacionBox, buttonBox);

        // Eventos
        btnConfirmar.setOnAction(e -> {
            if (entrenadorComboBox.getValue() == null || pokemonComboBox.getValue() == null || 
                fechaPicker.getValue() == null || localizacionField.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Todos los campos deben estar rellenos.");
            } else {
                if (capturaSeleccionada == null) {
                    añadirCaptura();
                } else {
                    editarCaptura(capturaSeleccionada);
                }
                dialog.close();
            }
        });

        btnCancelar.setOnAction(e -> dialog.close());

        Scene scene = new Scene(mainLayout, 290, 230);
        dialog.setScene(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
    
    public ObservableList<String> dameListaNombresEntrenadores() {
        ObservableList<String> listaNombres = FXCollections.observableArrayList();
        for (Entrenador entrenador : dameListaEntrenadores()) {
            listaNombres.add(entrenador.getNombre());
        }
        return listaNombres;
    }
    
    public ObservableList<String> dameListaNombresPokemons() {
        ObservableList<String> listaNombres = FXCollections.observableArrayList();
        for (Pokemon pokemon : dameListaPokemons()) {
            listaNombres.add(pokemon.getNombrePokemon());
        }
        return listaNombres;
    }
    
    public Entrenador obtenerEntrenadorPorNombre(String nombre) {
        for (Entrenador entrenador : dameListaEntrenadores()) {
            if (entrenador.getNombre().equals(nombre)) {
                return entrenador;
            }
        }
        return null; 
    }

    public Pokemon obtenerPokemonPorNombre(String nombre) {
        for (Pokemon pokemon : dameListaPokemons()) {
            if (pokemon.getNombrePokemon().equals(nombre)) {
                return pokemon;
            }
        }
        return null; 
    }

    
    public ObservableList<Captura> dameListaCapturas() {
        if (conexion != null) {
            ObservableCapturas.clear(); //Limpiamos el contenido actual
            String query = "SELECT ENTRENADOR.nombre AS nombreEntrenador , POKEMON.nombre_pokemon AS nombrePokemon, CAPTURA.fecha, CAPTURA.localizacion FROM CAPTURA JOIN ENTRENADOR ON CAPTURA.id_entrenador = ENTRENADOR.id_entrenador JOIN POKEMON ON CAPTURA.id_pokemon = POKEMON.id_pokemon;";
            try {
                rs = st.executeQuery(query);
                Captura captura;
                while (rs.next()) { //Se usan los identificadores propios en la BBDD (no es case sensitive). Revisar en phpmyadmin
                        captura = new Captura(rs.getString("nombreEntrenador"), rs.getString("nombrePokemon"), rs.getDate("fecha"), rs.getString("localizacion"));
                    ObservableCapturas.add(captura);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: "+e.getMessage());
            }
            return ObservableCapturas;
        }
        return null;
    }
    
    private void inicializarTablaCapturas(){
        colEntrenadorCaptura.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNombreEntrenador())));
        colPokemonCaptura.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNombrePokemon())));
        colLocalizacionCaptura.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocalizacion()));
        colFechaCaptura.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFecha())));
        
        capturaTable.setItems(dameListaCapturas());
    }
    
    private void configurarBuscadorCaptura() {
        searchFieldCaptura.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarCapturas(newValue);
        });
    }
    
    private void filtrarCapturas(String criterio) {
        ObservableList<Captura> listaCompleta = dameListaCapturas();

        FilteredList<Captura> listaFiltrada = new FilteredList<>(listaCompleta, captura -> {
            if (criterio == null || criterio.isEmpty()) {
                return true; 
            }
            String criterioEnMinusculas = criterio.toLowerCase();

            return captura.getLocalizacion().toLowerCase().contains(criterioEnMinusculas);
        });

        capturaTable.setItems(listaFiltrada);
    }
    
    private void añadirCapturaVentana() {
        gestionarCaptura(null);
    }
    
    private void editarCapturaVentana() {
        Captura capturaSeleccionada = capturaTable.getSelectionModel().getSelectedItem();
        if (capturaSeleccionada != null) {
            gestionarCaptura(capturaSeleccionada);
        }
    }
    
    private void eliminarCapturaVentana() {
        Captura capturaSeleccionada = capturaTable.getSelectionModel().getSelectedItem();
        if (capturaSeleccionada == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Captura");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar la captura seleccionada?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eliminarCaptura(capturaSeleccionada);
        }
    }
    
    private void configurarBotonesCaptura() {
        btnAdd.setOnAction(event -> añadirCapturaVentana()); 
        btnEdit.setOnAction(event -> editarCapturaVentana());
        btnDelete.setOnAction(event -> eliminarCapturaVentana());
    }
    
    private void configurarSeleccionTablaCaptura() {
        capturaTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean itemSelected = newValue != null;
            btnEdit.setDisable(!itemSelected);
            btnDelete.setDisable(!itemSelected);
        }); 
    }
    
    private void añadirCaptura() {
        String nombreEntrenador = entrenadorComboBox.getValue();
        String nombrePokemon = pokemonComboBox.getValue();
        LocalDate fechaCaptura = fechaPicker.getValue();
        String localizacion = localizacionField.getText().trim();
        
        Entrenador entrenador = obtenerEntrenadorPorNombre(nombreEntrenador);
        Pokemon pokemon = obtenerPokemonPorNombre(nombrePokemon);
        
        String query = "INSERT INTO CAPTURA (id_entrenador, id_pokemon, fecha, localizacion) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            preparedStatement.setInt(1, entrenador.getId());  
            preparedStatement.setInt(2, pokemon.getIdPokemon()); 
            preparedStatement.setDate(3, java.sql.Date.valueOf(fechaCaptura));  
            preparedStatement.setString(4, localizacion); 

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        capturaTable.setItems(dameListaCapturas());  
    }
    
    private void editarCaptura(Captura capturaSeleccionada) {
        String nombreEntrenadorAnterior = capturaSeleccionada.getNombreEntrenador();
        String nombrePokemonAnterior = capturaSeleccionada.getNombrePokemon();
        String nombrePokemonNuevo = pokemonComboBox.getValue(); 
        String nombreEntrenadorNuevo = entrenadorComboBox.getValue();
        String nuevaLocalizacion = localizacionField.getText();  // La nueva localización
        LocalDate nuevaFecha = fechaPicker.getValue();  // La nueva fecha

        Entrenador entrenadorAnterior = obtenerEntrenadorPorNombre(nombreEntrenadorAnterior);
        Entrenador entrenadorNuevo = obtenerEntrenadorPorNombre(nombreEntrenadorNuevo);
        Pokemon pokemonAnterior = obtenerPokemonPorNombre(nombrePokemonAnterior);
        Pokemon pokemonNuevo = obtenerPokemonPorNombre(nombrePokemonNuevo);

        String query = "UPDATE CAPTURA SET id_entrenador=?, id_pokemon=?, fecha=?, localizacion=? WHERE id_entrenador=? AND id_pokemon=? AND fecha=? AND localizacion=?";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            preparedStatement.setInt(1, entrenadorNuevo.getId());  
            preparedStatement.setInt(2, pokemonNuevo.getIdPokemon()); 
            preparedStatement.setDate(3, java.sql.Date.valueOf(nuevaFecha));  
            preparedStatement.setString(4, nuevaLocalizacion);  
            preparedStatement.setInt(5, entrenadorAnterior.getId()); 
            preparedStatement.setInt(6, pokemonAnterior.getIdPokemon());  
            preparedStatement.setDate(7, capturaSeleccionada.getFecha());
            preparedStatement.setString(8, capturaSeleccionada.getLocalizacion());  

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        capturaTable.setItems(dameListaCapturas()); 
    }



    private void eliminarCaptura(Captura capturaSeleccionada) {
        String nombreEntrenador = capturaSeleccionada.getNombreEntrenador();
        String nombrePokemon = capturaSeleccionada.getNombrePokemon();

        Entrenador entrenador = obtenerEntrenadorPorNombre(nombreEntrenador);
        Pokemon pokemon = obtenerPokemonPorNombre(nombrePokemon);

        String query = "DELETE FROM CAPTURA WHERE id_entrenador=? AND id_pokemon=? AND fecha=?";
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(query);
            preparedStatement.setInt(1, entrenador.getId());  
            preparedStatement.setInt(2, pokemon.getIdPokemon());  
            preparedStatement.setDate(3, capturaSeleccionada.getFecha()); 

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: " + e.getMessage());
        }
        capturaTable.setItems(dameListaCapturas()); 
    }

}
