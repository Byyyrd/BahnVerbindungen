package control;

import model.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class MainController {

    //Attribute

    //Referenzen
    private Graph trainNetwork;

    public MainController() {
        trainNetwork = new Graph();
        createSomeStations();
    }

    /**
     * Fügt Stationen dem Bahnnetzwerk hinzu.
     */
    private void createSomeStations() {
        insertStation("Dortmund");
        insertStation("Münster");
        insertStation("Bochum");
        insertStation("Berlin");
        connect("Dortmund", "Münster", 60);
        connect("Dortmund", "Bochum", 15);
    }

    /**
     * Fügt eine Station hinzu, falls diese noch nicht existiert.
     *
     * @param name
     * @return true, falls eine neue Station hinzugefügt wurde, sonst false.
     */
    public boolean insertStation(String name) {
        //COMPLETE 05: Station dem Netzwerk hinzufügen.
        if (trainNetwork.getVertex(name) == null) {
            trainNetwork.addVertex(new Vertex(name));
            return true;
        }
        return false;
    }

    /**
     * Löscht eine Station, falls dieser existiert. Alle Verbindungen zu anderen Stationen werden ebenfalls gelöscht.
     *
     * @param name
     * @return true, falls eine Station gelöscht wurde, sonst false.
     */
    public boolean deleteStation(String name) {
        //COMPLETE 07: Station aus dem Netzwerk entfernen.
        Vertex vertex = trainNetwork.getVertex(name);
        if (vertex != null) {
            trainNetwork.removeVertex(vertex);
            return true;
        }
        return false;
    }

    /**
     * Falls Stationen vorhanden sind, so werden ihre Namen in einem String-Array gespeichert und zurückgegeben. Ansonsten wird null zurückgegeben.
     *
     * @return
     */
    public String[] getTrainNetwork() {
        //COMPLETE 06: String-Array mit allen Stationennamen erstellen.
        if (trainNetwork.isEmpty()) {
            return null;
        }
        List<Vertex> vertices = trainNetwork.getVertices();
        String[] result = new String[countList(vertices)];
        vertices.toFirst();
        for (int i = 0; i < result.length; i++) {
            result[i] = vertices.getContent().getID();
            vertices.next();
        }
        return result;
    }

    /**
     * Falls die Station vorhanden ist und Verbindungen hat, so werden deren Namen in einem String-Array gespeichert und zurückgegeben. Ansonsten wird null zurückgegeben.
     *
     * @param name
     * @return
     */
    public String[] getAllConnectedStationsFrom(String name) {
        //COMPLETE 09: Verbundene Stationen einer Station als String-Array erstellen.
        Vertex station = trainNetwork.getVertex(name);
        List<Vertex> connectedStations = trainNetwork.getNeighbours(station);
        if (connectedStations.isEmpty()) {
            return null;
        }
        String[] result = new String[countList(connectedStations)];
        connectedStations.toFirst();
        for (int i = 0; i < result.length; i++) {
            result[i] = connectedStations.getContent().getID();
            connectedStations.next();
        }
        return result;

    }

    /**
     * Bestimmt den Zentralitätsgrad einer Station im Netzwerk, falls sie vorhanden ist. Sonst wird -1.0 zurückgegeben.
     * Der Zentralitätsgrad ist der Quotient aus der Anzahl der direkten Verbindungen einer Station und der um die Station selbst verminderten Anzahl an Stationen im Netzwerk.
     * Gibt also den Prozentwert an Stationen im Netzwerk an, mit der die Station verbunden ist.
     *
     * @param name
     * @return
     */
    public double centralityDegreeOfStation(String name) {
        //COMPLETE 10: Prozentsatz der vorhandenen direkten Verbindungen einer Station von allen theoretisch möglichen Verbindungen der Station.
        if(trainNetwork.getVertex(name) == null){
            return -1;
        }
        String[] connectedStations = getAllConnectedStationsFrom(name);
        if (connectedStations == null) {
            return 0;
        }
        double allVerticesCount = countList(trainNetwork.getVertices()) - 1;
        if (allVerticesCount == 0) {
            return 1;
        }
        return (double) connectedStations.length / allVerticesCount;
    }

    /**
     * Zwei Stationen des Netzwerkes werden (gewichtet) verbunden, falls sie sich im Netzwerk befinden und noch keine Verbindung existiert sind.
     *
     * @param name01
     * @param name02
     * @param weight
     * @return true, falls eine neue Verbindung entstanden ist, ansonsten false.
     */
    public boolean connect(String name01, String name02, int weight) {
        //COMPLETE 08: Verbindung bauen.
        Vertex vertex1 = trainNetwork.getVertex(name01);
        Vertex vertex2 = trainNetwork.getVertex(name02);
        if (vertex1 != null && vertex2 != null && trainNetwork.getEdge(vertex1, vertex2) == null) {
            trainNetwork.addEdge(new Edge(vertex1, vertex2, weight));
            return true;
        }
        return false;
    }

    /**
     * Die Verbindung zweier Stationen wird gekappt (Baustelle oder so), falls sie sich im Netzwerk befinden und verbunden sind.
     *
     * @param name01
     * @param name02
     * @return true, falls ihre Verbindung entfernt wurde, ansonsten false.
     */
    public boolean disconnect(String name01, String name02) {
        //COMPLETE 11: Freundschaften beenden.
        Vertex vertex1 = trainNetwork.getVertex(name01);
        Vertex vertex2 = trainNetwork.getVertex(name02);
        Edge edge = trainNetwork.getEdge(vertex1, vertex2);
        if (edge != null) {
            trainNetwork.removeEdge(edge);
            return true;
        }
        return false;
    }

    /**
     * Bestimmt die Dichte des Netzwerks und gibt diese zurück.
     * Die Dichte ist der Quotient aus der Anzahl aller vorhandenen Verbindungen und der Anzahl der maximal möglichen Verbindungen.
     *
     * @return
     */
    public double dense() {
        //COMPLETE 12: Dichte berechnen.
        if(trainNetwork.isEmpty()){
            return 0;
        }
        int edgeCount = countList(trainNetwork.getEdges());
        int possibleConnections = sum(countList(trainNetwork.getVertices()) - 1);
        if (possibleConnections == 0) {
            return 0;
        }
        return (double) edgeCount / possibleConnections;
    }

    /**
     * Gibt eine mögliche Verbindung zwischen zwei Stationen im Netzwerk als String-Array zurück,
     * falls die Stationen vorhanden sind und sie über eine oder mehrere Verbindungen miteinander verbunden sind.
     *
     * @param name01
     * @param name02
     * @return
     */
    public String[] getLinksBetween(String name01, String name02) {
        Vertex station01 = trainNetwork.getVertex(name01);
        Vertex station02 = trainNetwork.getVertex(name02);
        if (station01 != null && station02 != null) {
            //COMPLETE 13: Schreibe einen Algorithmus, der mindestens eine Verbindung von einer Station (über ggf. Zwischenstationen) zu einer anderen Station bestimmt. Happy Kopfzerbrechen!
            trainNetwork.setAllVertexMarks(false);
            String[] result = getLinksRec(name02, station01).split(",");
            if(result.length == 0){
                return null;
            }
            return result;
        }
        return null;
    }

    private String getLinksRec(String destination, Vertex vertex) {
        vertex.setMark(true);
        List<Vertex> neighbours = trainNetwork.getNeighbours(vertex);
        neighbours.toFirst();
        while (neighbours.hasAccess()) {
            if (neighbours.getContent().getID().equals(destination)) {
                return vertex.getID() + "," + destination;
            }
            if (!neighbours.getContent().isMarked()) {
                String path = getLinksRec(destination, neighbours.getContent());
                if (!path.isEmpty()) {
                    return vertex.getID() + "," + path;
                }
            }
            neighbours.next();
        }
        return "";
    }

    /**
     * Gibt eine kürzeste Verbindung zwischen zwei Stationen des Netzwerkes als String-Array zurück,
     * falls die Stationen vorhanden sind und sie über eine oder mehrere Stationen miteinander verbunden sind. (Dijkstra)
     *
     * @param name01
     * @param name02
     * @return Verbindung als String-Array oder null, falls es keine Verbindung gibt.
     */
    public String[] shortestPath(String name01, String name02) {
        Vertex station01 = trainNetwork.getVertex(name01);
        Vertex station02 = trainNetwork.getVertex(name02);
        if (station01 != null && station02 != null) {
            trainNetwork.setAllVertexMarks(false);
            String[] result = dijkstra(station01,station02).split(",");
            if(result.length <= 1){
                return null;
            }
            return result;
            //COMPLETE 14: Schreibe einen Algorithmus, der die kürzeste Verbindung zwischen den Stationen name01 und name02 als String-Array zurückgibt. Beachte die Kantengewichte!
        }
        return null;
    }
    private String dijkstra(Vertex start,Vertex end)
    {
        List<Vertex> vertices = trainNetwork.getVertices();
        int length = countList(vertices);
        Vertex[][] stations = new Vertex[length][2];
        double[] lengths = new double[length];
        vertices.toFirst();
        //Fill stations array and init shortest paths to infinity
        for (int i = 0; i < length; i++) {
            stations[i][0] = vertices.getContent();
            lengths[i] = Integer.MAX_VALUE;
            if(vertices.getContent() == start){
                lengths[i] = 0;
            }
            vertices.next();
        }
        //Choose Node with the shortest path until all are marked
        int smallest = findSmallest(stations,lengths);
        while (smallest != -1){
            //Markiere Vertex und speichere seine Nachbarn
            Vertex currentVertex = stations[smallest][0];
            currentVertex.setMark(true);
            List<Vertex> neighbours = trainNetwork.getNeighbours(currentVertex);
            neighbours.toFirst();
            //Finde raus an welcher Stelle der Vertex im Array ist
            int currentIndex = getIndex(stations,currentVertex);
            //Wenn der Vertex gefunden wurde
            if(currentIndex != -1){
                double lengthToCurrent = lengths[currentIndex];
                //Gehe alle Nachbarn durch
                while (neighbours.hasAccess()){
                    int index = getIndex(stations,neighbours.getContent());
                    if(index != -1){
                        double pathLength = lengths[index];
                        //Wenn der aktuell kürzeste Weg zum Nachbar länger ist als der Weg zum Aktuellen Knoten + Weg zum Nachbar, dann wird der zweite Weg zum neuen kürzesten Weg
                        if(lengthToCurrent + trainNetwork.getEdge(neighbours.getContent(),currentVertex).getWeight() < pathLength){
                            lengths[index] = lengthToCurrent + trainNetwork.getEdge(neighbours.getContent(),currentVertex).getWeight();
                            stations[index][1] = currentVertex;
                        }
                    }
                    neighbours.next();
                }
            }
            //Nächsten aktuellen Knoten finden
            smallest = findSmallest(stations,lengths);
        }
        Stack<String> path = new Stack<>();
        Vertex previous = stations[getIndex(stations,end)][1];
        path.push(end.getID());
        while (previous != null){
            path.push(previous.getID());
            previous = stations[getIndex(stations,previous)][1];
        }
        StringBuilder result = new StringBuilder();
        while (!path.isEmpty()){
            result.append(path.top()).append(",");
            path.pop();
        }
        result.replace(result.length() - 1,result.length(),"");
        return result.toString();
    }
    private int findSmallest(Vertex[][] stations,double[] lengths){
        int shortestIndex = 0;
        for (int i = 0; i < stations.length; i++) {
            if(lengths[i] < lengths[shortestIndex] && !stations[i][0].isMarked()){
                shortestIndex = i;
            }
        }
        if(stations[0][0].isMarked()){
            return -1;
        }
        return shortestIndex;
    }
    private int getIndex(Vertex[][] vertices,Vertex vertex){
        for (int i = 0; i < vertices.length; i++) {
            if(vertices[i][0].getID().equals(vertex.getID())){
                return i;
            }
        }
        return -1;
    }

    private int countList(List list) {
        int count = 0;
        list.toFirst();
        while (list.hasAccess()) {
            count++;
            list.next();
        }
        return count;
    }

    private int sum(int i) {
        return (i * (i + 1)) / 2;
    }
//
}
