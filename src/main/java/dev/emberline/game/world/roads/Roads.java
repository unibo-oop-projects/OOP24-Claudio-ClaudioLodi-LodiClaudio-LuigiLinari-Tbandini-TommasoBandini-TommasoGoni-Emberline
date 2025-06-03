package dev.emberline.game.world.roads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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
import utility.Coordinate2D;
import utility.Vector2D;

public class Roads implements Renderable, Updatable {
    
    /*
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<Vector2D, Node> posToNode = new HashMap<>();
    private OneShotAnimation mapAnimation;

    public Roads(String wavePath) {
        loadMapAnimation(wavePath + "mapAnimation/");
        loadGraph(wavePath + "roads.txt");
    }

    /**
     * @param pos is the current position.
     * @return the next node of the graph based on the current state.
     */
    public Optional<Vector2D> getNextNode(Vector2D pos) {
        return posToNode.get(pos).getNext();
    }

    private Image loadMapImage(String file) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(file)));
    }

    private void loadMapAnimation(String file) {
        List<Image> animationStates = new ArrayList<>();
        try {
            for (int i = 1; i <= 4; i++) {
                Image image = loadMapImage(file + "map" + i + ".png");
                animationStates.add(image);
            }
        } catch (NullPointerException e) {
            //nothing needed here, either 1 or 4 images are found in the mapAnimation folder
        }
        mapAnimation = new OneShotAnimation(animationStates, 1_000_000_000L);
    }

    private void loadGraph(String file) {
        try {
            URL fileURL = Objects.requireNonNull(getClass().getResource(file));
            final BufferedReader r = new BufferedReader(new FileReader(fileURL.getPath()));
            String line = null;
            while ((line = r.readLine()) != null) {
                
                String[] numbers = line.split(" ");
                //summing (0.5, 0.5) to center
                Node fromNode = new Node(
                        new Coordinate2D(Double.parseDouble(numbers[0]) + 0.5, Double.parseDouble(numbers[1]) + 0.5));
                Node toNode = new Node(
                        new Coordinate2D(Double.parseDouble(numbers[2]) + 0.5, Double.parseDouble(numbers[3]) + 0.5));
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

    /**
     * Adds the map png based on its current state.
     */
    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();
        
        double screenX = cs.toScreenX(0);
        double screenY = cs.toScreenY(0);

        Image imageToRender = mapAnimation.getAnimationState();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(imageToRender, screenX, screenY, 32*cs.getScale(), 18*cs.getScale());
        }));
    }

    /**
     * Updates any animation in the map if present.
     * @param elapsed
     */
    @Override
    public void update(long elapsed) {
        mapAnimation.update(elapsed);
    }
}
