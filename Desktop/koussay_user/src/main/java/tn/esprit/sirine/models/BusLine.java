package tn.esprit.sirine.models;

public class BusLine {
    private int id;
    private String name;
    private String startPoint;
    private String endPoint;
    private int chauffeurId;  // ID of the driver assigned to this line

    public BusLine() {}

    public BusLine(int id, String name, String startPoint, String endPoint, int chauffeurId) {
        this.id = id;
        this.name = name;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.chauffeurId = chauffeurId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }

    public String getEndPoint() { return endPoint; }
    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }

    public int getChauffeurId() { return chauffeurId; }
    public void setChauffeurId(int chauffeurId) { this.chauffeurId = chauffeurId; }

    @Override
    public String toString() {
        return name + " (" + startPoint + " - " + endPoint + ")";
    }
}