package dev.emberline.game.world.roads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import dev.emberline.core.GameLoop;
import dev.emberline.core.animations.OneShotAnimation;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utility.IntegerPoint2D;

public class Roads implements Renderable, Updatable {
    
    /*
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<IntegerPoint2D, Node> posToNode = new HashMap<>();
    private Optional<OneShotAnimation> mapAnimation;
    private Image mapImage;

    public Roads(String wavePath) {
        loadMapAnimation(wavePath + "mapAnimation/");
        loadGraph(wavePath + "roads.txt");
        loadMapImage(wavePath + "map.png");
    }

    public Optional<IntegerPoint2D> getNextNode(IntegerPoint2D pos) {
        return posToNode.get(pos).getNext();
    }

    private void loadMapImage(String file) {
        mapImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(file)));
    }

    private void loadMapAnimation(String file) {
        try {
            List<Image> animationStates = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                Image image = new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream(file + "map" + i + ".png")));
                animationStates.add(image);
            }
            mapAnimation = Optional.of(new OneShotAnimation(animationStates, 1_000_000_000L));
        } catch (NullPointerException e){
            mapAnimation = Optional.empty();
        }
    }

    private void loadGraph(String file) {
        try {
            URL fileURL = Objects.requireNonNull(getClass().getResource(file));
            final BufferedReader r = new BufferedReader(new FileReader(fileURL.getPath()));
            String line = null;
            while ((line = r.readLine()) != null) {
                
                String[] numbers = line.split(" ");
                
                Node fromNode = new Node(new IntegerPoint2D(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1])));
                Node toNode = new Node(new IntegerPoint2D(Integer.parseInt(numbers[2]), Integer.parseInt(numbers[3])));
                Integer weight = Integer.parseInt(numbers[4]);
                
                posToNode.putIfAbsent(fromNode.getPosition(), fromNode);
                posToNode.get(fromNode.getPosition()).addNeighbour(toNode, weight);

                posToNode.putIfAbsent(toNode.getPosition(), toNode);
            }
            r.close();
        } catch (IOException e) {
            System.out.println("error loading file: " + file);
        }
    }

    /*
     * loads the map as one png since all the components of it do not perform any action/interaction with the user
     */
    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();
        
        double screenX = cs.toScreenX(0);
        double screenY = cs.toScreenY(0);

        Image imageToRender;
        if (mapAnimation.isEmpty() || mapAnimation.get().hasEnded()) {
            imageToRender = mapImage;
        } else {
            imageToRender = mapAnimation.get().getAnimationState();
        }

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(imageToRender, screenX, screenY, 32*cs.getScale(), 18*cs.getScale());
        }));
    }

    @Override
    public void update(long elapsed) {
        mapAnimation.ifPresent(oneShotAnimation -> oneShotAnimation.update(elapsed));
    }
}
