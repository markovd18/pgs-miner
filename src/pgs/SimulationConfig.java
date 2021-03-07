package pgs;

/**
 * Configuration of the resource mining simulation.
 *
 * @author <a href="mailto:markovd@students.zcu.cz">David Markov</a>
 * @since 7.3.2021
 */
public class SimulationConfig {

    private String inputFilePath;

    private String outputFilePath;

    private int workerCount;

    private int maxWorkerResourceProcessingTime;

    private int maxLorryTransportTime;

    private int lorryCapacity;

    private int ferryCapacity;

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(final String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(final String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(final int workerCount) {
        this.workerCount = workerCount;
    }

    public int getMaxWorkerResourceProcessingTime() {
        return maxWorkerResourceProcessingTime;
    }

    public void setMaxWorkerResourceProcessingTime(final int maxWorkerResourceProcessingTime) {
        this.maxWorkerResourceProcessingTime = maxWorkerResourceProcessingTime;
    }

    public int getMaxLorryTransportTime() {
        return maxLorryTransportTime;
    }

    public void setMaxLorryTransportTime(final int maxLorryTransportTime) {
        this.maxLorryTransportTime = maxLorryTransportTime;
    }

    public int getLorryCapacity() {
        return lorryCapacity;
    }

    public void setLorryCapacity(final int lorryCapacity) {
        this.lorryCapacity = lorryCapacity;
    }

    public int getFerryCapacity() {
        return ferryCapacity;
    }

    public void setFerryCapacity(final int ferryCapacity) {
        this.ferryCapacity = ferryCapacity;
    }
}
