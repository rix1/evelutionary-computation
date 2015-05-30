package org.rix1;

import com.sun.deploy.net.CanceledDownloadException;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.*;

import javax.swing.*;

public class TSP {

    /**
     * How many cities to use.
     */
    protected static int cityCount;

    /**
     * How many chromosomes to use.
     */
    protected static int populationSize;

    private static final double pSelectTresHold = 0.5;
    private static final int TOURNAMENT_SIZE = 5;
    private static final int NUM_OF_CHILDREN = 50;
    private static final int CROSSOVER = 3;

    private static Random r = new Random();

    /**
     * The part of the population eligable for mateing.
     */
    protected static int matingPopulationSize;

    /**
     * The part of the population selected for mating.
     */
    protected static int selectedParents;

    /**
     * The current generation
     */
    protected static int generation;

    /**
     * The list of cities.
     */
    protected static City[] cities;
    private static ArrayList<Chromosome> candidates = new ArrayList<Chromosome>();


    /**
     * The list of chromosomes.
     */
    protected static Chromosome[] chromosomes;

    /**
    * Frame to display cities and paths
    */
    private static JFrame frame;

    /**
     * Integers used for statistical data
     */
    private static double min;
    private static double avg;
    private static double max;
    private static double sum;

    /**
     * Width and Height of City Map, DO NOT CHANGE THESE VALUES!
     */
    private static int width = 600;
    private static int height = 600;


    private static Panel statsArea;
    private static TextArea statsText;


    /*
     * Writing to an output file with the costs.
     */
    private static void writeLog(String content) {
        String filename = "results.out";
        FileWriter out;

        try {
            out = new FileWriter(filename, true);
            out.write(content + "\n");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     *  Deals with printing same content to System.out and GUI
     */
    private static void print(boolean guiEnabled, String content) {
        if(guiEnabled) {
            statsText.append(content + "\n");
        }

        System.out.println(content);
    }

    private static void resetCandidates(){
        candidates.clear();
        Collections.addAll(candidates, chromosomes);
    }

    public static void evolve() {
        resetCandidates();

        ArrayList<Chromosome> children = new ArrayList<Chromosome>();

        // This is the 
        for (int i = 0; i < NUM_OF_CHILDREN; i++) {
//            System.out.println("Generated " +i + " children");
            Chromosome child = reproduce(psTournament(), psTournament());
            child.calculateCost(cities);
            System.out.println("Cost before: " + child.getCost());
            child = shift(child);
            child.calculateCost(cities);
            System.out.println("Cost after: " + child.getCost());
            children.add(child);
        }

        Chromosome.sortChromosomes(chromosomes, populationSize);

        for (int i = 0; i < populationSize/2; i++) {
            chromosomes[(populationSize/2)+i] = children.get(i);
        }

        /*TOOD:
        * 1. Select parents from chromosones list with UNIFORM RANDOM
        * 2. Recombine pairs of parents
        * 3. Mutate
        *
        * */
    }

    /**
     * Select parents with uniform random distribution
     * @return the selected parents
     * rix1
     * */

    private static int[] psUniform(){
        // Select parents with uniform random distribution

        ArrayList<Chromosome> candidates = new ArrayList<Chromosome>();

        // TODO: Could improve so that high fitness has higher probablility
        for (Chromosome chromosome : chromosomes) {
            if (Math.random() > pSelectTresHold) {
                candidates.add(chromosome);
            }
        }

        System.out.println("Parents selected: " + candidates.size());

        return null;
    }

    /**
     * Select fittest parent with a tournament selection strategy
     * @return The winner of the tournament
     * rix1
     * */

    private static Chromosome psTournament(){

        Random r = new Random();
        ArrayList<Chromosome> tournament = new ArrayList<Chromosome>();
        Chromosome fittest;

        if(candidates.size() < TOURNAMENT_SIZE){
            fittest = getFittest(candidates); // Select fittest among remaining...
        }else{
            for (int i = 0; i < TOURNAMENT_SIZE; i++) {
                tournament.add(candidates.get(r.nextInt(candidates.size())));
            }
            fittest = getFittest(tournament);
        }

        candidates.remove(fittest);

        return fittest;
    }

    private static Chromosome getFittest(ArrayList<Chromosome> tournament){
        Chromosome best = tournament.get(0);

        for (Chromosome c : tournament){
            if(c.getCost() < best.getCost()){ // Lower cost is obviously better
                best = c;
            }
        }
        return best;
    }


    // Two-point exchange
    private static Chromosome transposition(Chromosome chrom){

        int posA = r.nextInt(cityCount);
        int posB = r.nextInt(cityCount);
        int cityA = chrom.getCity(posA);
        int cityB = chrom.getCity(posB);

        chrom.setCity(posA, cityB);
        chrom.setCity(posB, cityA);

        return chrom;
    }

    // 3-point exchange
    private static Chromosome shift(Chromosome chrom) {

        int[] rand = createRandom(2, cityCount);
        int[] shifted = new int[rand[1]-rand[0]];

        for (int i = 0; i < shifted.length; i++) {
            shifted[i] = chrom.getCity(rand[0]+i);
        }

        int pos = r.nextInt(cityCount);
        int localPos = 0;

        for (int i = 0; i < shifted.length; i++) {
            localPos = pos + i;

            if(localPos >= cityCount){
                localPos -= cityCount;
            }
            chrom.setCity(localPos, shifted[i]);
        }
        return chrom;
    }



    //

    /**
     * I need a way of creating x unique random numbers.
     * This one should do the trick.
     * @param noOfNo number of random numbers to be generated
     * @param range range of the random numbers need to be in
     * @return a sorted list of unique random numbers from 0 to range
     */

    private static int[] createRandom(int noOfNo, int range){
        int[] numbers = new int[noOfNo];
        ArrayList<Integer> usedNo = new ArrayList<Integer>();
        int no = 0;

        for (int i = 0; i < numbers.length; i++) {
            no = r.nextInt(range);
            if(!usedNo.contains(no)){
                numbers[i] = no;
            }else {
                while (usedNo.contains(no)) {
                    no = r.nextInt(range);
                }
                numbers[i] = no;
            }
        }
        Arrays.sort(numbers);
        return numbers;
    }

    private static Chromosome setCity(Chromosome parent, Chromosome child, int index) {
        int localIndex = index;
        int city = parent.getCity(localIndex);
        boolean flag = false;
        if (city == 0) {
            child.setCity(index, city);
        } else {
            if (child.containsCity(city)) {
                while (child.containsCity(city)) {
//                    System.out.println("Trying to find a spot for " + city);
                    localIndex++;
                    if(localIndex >= cityCount){
                        flag = true;

//                        System.out.println("IM STUCK");
                        localIndex -= cityCount;
                        if(flag){
                            System.out.println("IM SUPER STUCK at " + index + "\n child contains: " + Arrays.toString(child.cityList));
                            flag = false;
                        }
                    }else{
                        flag = false;
                    }
                    city = parent.getCity(localIndex);
                }
                child.setCity(index, parent.getCity(localIndex));
            } else {
                child.setCity(index, city);
            }
        }
        return child;
    }


    // Todo: Add probability stuff
    private static Chromosome reproduce(Chromosome parent1, Chromosome parent2) {
        Chromosome child = new Chromosome(cities);
        child.clearCities();
        int[] crossPoints = createRandom(CROSSOVER, cityCount);

        int crossIndex = 0;

        // n-point crossover
        for (int i = 0; i < cityCount; i++) {
            System.out.println("CITYCOUNT: "+ i);
            if(i <= crossPoints[crossIndex] || (i > crossPoints[crossPoints.length-1] && i < cityCount)){
                if(crossIndex%2 == 0){
                    // Select from parent 1
                    child = setCity(parent1, child, i);
                    // Check if number already is contained in list
                }else{
                    child = setCity(parent2, child, i);
                    // Select from parent 2
                }
            }else{
                crossIndex++;
            }
        }
        return child;
    }

    private static int[] selectParents(){
        Chromosome.sortChromosomes(chromosomes, populationSize);
        return null;
    }

    /**
     * Update the display
     */
    public static void updateGUI() {
        Image img = frame.createImage(width, height);
        Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        if (cities != null) {
            g.setColor(Color.green);
            for (int i = 0; i < cityCount; i++) {
                int xpos = cities[i].getx();
                int ypos = cities[i].gety();
                g.fillOval(xpos - 5, ypos - 5, 10, 10);
            }

            g.setColor(Color.gray);
            for (int i = 0; i < cityCount; i++) {
                int icity = chromosomes[0].getCity(i);
                if (i != 0) {
                    int last = chromosomes[0].getCity(i - 1);
                    g.drawLine(
                        cities[icity].getx(),
                        cities[icity].gety(),
                        cities[last].getx(),
                        cities[last].gety());
                }
            }
        }
        frame.getGraphics().drawImage(img, 0, 0, frame);
    }


    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String currentTime  = df.format(today);

        int runs;
        boolean display = false;
        String formatMessage = "Usage: java TSP 100 500 1 [gui] \n java TSP [NumCities] [PopSize] [Runs] [gui]";

        if (args.length < 3) {
            System.out.println("Please enter the arguments");
            System.out.println(formatMessage);
            display = false;
        } else {

            if (args.length > 3) {
                display = true; 
            }

            try {
                cityCount = Integer.parseInt(args[0]);
                populationSize = Integer.parseInt(args[1]);
                runs = Integer.parseInt(args[2]);

                if(display) {
                    frame = new JFrame("Traveling Salesman");
                    statsArea = new Panel();

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setSize(width + 300, height);
                    frame.setResizable(false);
                    frame.setLayout(new BorderLayout());
                    
                    statsText = new TextArea(35, 35);
                    statsText.setEditable(false);

                    statsArea.add(statsText);
                    frame.add(statsArea, BorderLayout.EAST);
                    
                    frame.setVisible(true);
                }


                min = 0;
                avg = 0;
                max = 0;
                sum = 0;

                // create a random list of cities
                // Note: This is outside the run loop so that the multiple runs
                // are tested on the same city set
                cities = new City[cityCount];
                for (int i = 0; i < cityCount; i++) {
                    cities[i] = new City(
                        (int) (Math.random() * (width - 10) + 5),
                        (int) (Math.random() * (height - 50) + 30));
                }

                writeLog("Run Stats for experiment at: " + currentTime);
                for (int y = 1; y <= runs; y++) {
                    print(display,  "Run " + y + "\n");

                // create the initial population of chromosomes
                    chromosomes = new Chromosome[populationSize];
                    for (int x = 0; x < populationSize; x++) {
                        chromosomes[x] = new Chromosome(cities);
                    }

                    generation = 0;
                    double thisCost = 0.0;

                    while (generation < 100) {
                        evolve();
                        generation++;


                        Chromosome.sortChromosomes(chromosomes, populationSize);
                        double cost = chromosomes[0].getCost();
                        thisCost = cost;

                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);

                        print(display, "Gen: " + generation + " Cost: " + (int) thisCost);

                        if(display) {
                            updateGUI();
                        }
                    }

                    writeLog(thisCost + "");

                    if (thisCost > max) {
                        max = thisCost;
                    }

                    if (thisCost < min || min == 0) {
                        min = thisCost;
                    }

                    sum +=  thisCost;

                    print(display, "");
                }

                avg = sum / runs;
                print(display, "Statistics after " + runs + " runs");
                print(display, "Solution found after " + generation + " generations." + "\n");
                print(display, "MIN: " + min + " AVG: " + avg + " MAX: " + max + "\n");

            } catch (NumberFormatException e) {
                System.out.println("Please ensure you enter integers for cities and population size");
                System.out.println(formatMessage);
            }
        }
    }
}