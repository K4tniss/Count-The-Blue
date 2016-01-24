/*
 * Copyright (C) 2016 Katniss
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package k4tniss.counttheblue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Katniss
 */
public class CountTheBlue extends Application {
    
    List<File> fileList;
    
    static int R = 0;
    static int G = 1;
    static int B = 2;
    static int A = 3;
    
    boolean stopCounting = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        //Layout component
        CheckBox r = new CheckBox("R");
        CheckBox g = new CheckBox("G");
        CheckBox b = new CheckBox("B");
        CheckBox a = new CheckBox("A");
        
        FileChooser imageChooser = new FileChooser();
            imageChooser.getExtensionFilters().add(
                 new FileChooser.ExtensionFilter("Images (.png, .jpg, .gif)", "*.png", "*.jpg", "*.gif")
                    );
        Button countBtn = new Button("Count!");
            countBtn.setDisable(true);
        
        Button stopCountingBtn = new Button("Stop");
            stopCountingBtn.setDisable(true);
            
        Button chooseImageBtn = new Button("Open Images");
        
        TextArea selectedImages = new TextArea("");
            selectedImages.wrapTextProperty().set(true);
            selectedImages.setDisable(true);
            selectedImages.setEditable(false);
        
        TextArea out = new TextArea("The output will go here");
            out.setEditable(false);
            out.setMaxWidth(Double.MAX_VALUE);
            out.setMaxHeight(Double.MAX_VALUE);
        
        //Actions
        chooseImageBtn.setOnAction((ActionEvent event) -> {
            selectedImages.setText("");
            fileList = imageChooser.showOpenMultipleDialog(primaryStage);
            if (fileList != null && !fileList.isEmpty()) {
                String selectedFiles = "";
                for (File f : fileList) {
                    if (f != null)
                        selectedFiles += f.getPath() + "\n";
                }
                if (selectedFiles.length() > 0) {
                    selectedImages.setText(selectedFiles);
                    selectedImages.setDisable(false);
                    countBtn.setDisable(false);
                }
            }
        });
            
        countBtn.setOnAction((ActionEvent event) -> {
            setStopCounting(false);
            chooseImageBtn.setDisable(true);
            //Task
            Task countTask = new Task() {
                
                ArrayList<Shade> shades = new ArrayList<>();
                    
                @Override
                protected Object call() {
                    System.out.println("CountThread: Beginning count");
                    
                    for (final File imageFile : fileList) {
                        try {
                            ArrayList<Shade> newShades = countShades(ImageIO.read(imageFile), r.isSelected(), b.isSelected(), g.isSelected(), a.isSelected());
                            if (newShades == null) //If needs to exit, exit
                                return null;
                        } catch (IOException e) {
                            System.err.printf(e.toString());
                        }
                        
                        Platform.runLater(() -> {
                            countBtn.setDisable(false);
                            System.out.println("Done! outputting to 'out' field");
                            out.setText(formatShades(shades));
                            chooseImageBtn.setDisable(false);
                            stopCountingBtn.setDisable(true);
                        });
                    }
                    return null;
                }
                
                ArrayList<Shade> countShades(BufferedImage image, boolean r, boolean b, boolean g, boolean a) {
                    System.out.println("Image: "+image.toString());
                    
                    for (int row = 0; row < image.getHeight(); row++) {
                        for (int col = 0; col < image.getWidth(); col++) {
                            if (stopCounting)
                                return null;
                            final int progress = Math.round( ( ((float)row*image.getWidth()+col) / (image.getWidth()*image.getHeight()) ) * 100);
                            if (progress % 5 == 0) {
                                Platform.runLater(() -> {
                                    out.setText(progress+"%");
                                });
                            }
                            Color pixelColor = new Color(image.getRGB(col, row));
                            int red = pixelColor.getRed();
                            int blue = pixelColor.getBlue();
                            int green = pixelColor.getGreen();
                            int alpha = pixelColor.getAlpha();

                            if (r) {
                                Shade s = new Shade(R, red, 1);
                                int index = alreadyExists(s);
                                if (index != -1) { //if it even exists
                                    shades.get(index).incrementCount();
                                } else {
                                    System.out.println("Adding shade of red:" + red);
                                    shades.add(s);
                                }
                            }
                            if (g) {
                                Shade s = new Shade(G, green, 1);
                                int index = alreadyExists(s);
                                if (index != -1) { //if it even exists
                                    shades.get(index).incrementCount();
                                } else {
                                    System.out.println("Adding shade of green:" + green);
                                    shades.add(s);
                                }
                            }
                            if (b) {   
                                Shade s = new Shade(B, blue, 1);
                                int index = alreadyExists(s);
                                if (index != -1) { //if it even exists
                                    shades.get(index).incrementCount();
                                } else {
                                    System.out.println("Adding shade of blue:" + blue);
                                    shades.add(s);
                                }
                            }
                            if (a) {
                                Shade s = new Shade(A, alpha, 1);
                                int index = alreadyExists(s);
                                if (index != -1) { //if it even exists
                                    shades.get(index).incrementCount();
                                    System.out.println("Adding shade of alpha:" + alpha);
                                } else {
                                    shades.add(s);
                                }
                            }
                        }
                    }
                    System.out.println("Shades processed");

                    return shades;
                }
                
                int alreadyExists(Shade in) {
                    for (int i = 0; i < shades.size(); i++) {
                        Shade exists = shades.get(i);
                        if (exists.color == in.color && in.shade == exists.shade) {
                            return i;
                        }
                    }
                    System.out.println("Shade doesn't exist: "+in.toString());
                    return -1; //none found
                }
                
                String formatShades(ArrayList<Shade> shades) {
                    String outString = "";
                    
                    shades.sort(null);
                    for (Shade s : shades) {
                        System.out.println("Shade: "+s.shade);
                        outString += s.toString()+"\n";
                    }
                    
                    return outString;
                }
            };
            
            countBtn.setDisable(true);
            stopCountingBtn.setDisable(false);
            Thread countThread = new Thread(countTask);
            countThread.setDaemon(true);
            countThread.start();
        });
        
        stopCountingBtn.setOnAction((ActionEvent event) -> {
            setStopCounting(true);
            out.setText("Output will go here");
            stopCountingBtn.setDisable(true);
            countBtn.setDisable(false);
        });
        
        VBox root = new VBox();
            root.alignmentProperty().set(Pos.TOP_CENTER);
            root.paddingProperty().set(new Insets(5.0));
            root.spacingProperty().set(5.0);
        root.getChildren().addAll(r,g,b,a,chooseImageBtn,selectedImages,countBtn,stopCountingBtn, out);
        
        Scene scene = new Scene(root, 320, 450);
        
        primaryStage.setTitle("CountTheBlue by K4tniss");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    void setStopCounting(boolean val) {
        stopCounting = val;
    }
}
