package dev.emberline.game.world.roads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utility.Pair;
import utility.IntegerPoint2D;

public class Roads implements Renderable {
    
    /*
     * graph data structure, represents the walkable roads on the map
     */
    private final Map<IntegerPoint2D, Node> posToNode = new HashMap<>();
    
    public Roads(String wavePath) {
        loadGraph(wavePath + "roads.txt");
    }
    
    public Optional<IntegerPoint2D> getNextNode(IntegerPoint2D pos) {
        return posToNode.get(pos).getNext();
    }

    private void loadGraph(String file) {
        try {
            final BufferedReader r = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = r.readLine()) != null) {
                
                String[] numbers = line.split(" ");
                
                Node fromNode = new Node(new IntegerPoint2D(Integer.parseInt(numbers[1]), Integer.parseInt(numbers[0])));
                Node toNode = new Node(new IntegerPoint2D(Integer.parseInt(numbers[3]), Integer.parseInt(numbers[2])));
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

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/debug/map.png")));
        
        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(image, screenX, screenY, 32*cs.getScale(), 18*cs.getScale());
        }));
    }
}
