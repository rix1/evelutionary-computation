package org.rix1;

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

    /**
     * The current generation
     */
    protected static int generation;

    /**
     * The list of cities.
     */
    protected static City[] cities;


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

    private static long avgMutationTime;
    private static long avgRecombinationTime;
    private static long avgTournamentTime;
    private static long avgEvolveTime;
    private static long avgTMutationTime;

    private static int mutationCounter = 0;
    private static int recombinationCounter = 0;
    private static int tournamentCounter = 0;
    private static int evolveCounter = 0;
    private static int tMutationCounter = 0;


    /**
     * Width and Height of City Map, DO NOT CHANGE THESE VALUES!
     */
    private static int width = 600;
    private static int height = 600;


    private static Panel statsArea;
    private static TextArea statsText;

    private static final int TOURNAMENT_SIZE = 5;
    private static final int NUM_OF_CHILDREN = 2000;
    private static Random r = new Random();
    private static ArrayList<Chromosome> candidates = new ArrayList<Chromosome>();


    public static void resetCandidates(){
        candidates.clear();
        Collections.addAll(candidates, chromosomes);
    }

    public static void evolve() {

        resetCandidates();

        ArrayList<Chromosome> children = new ArrayList<Chromosome>();

        // This is the 
        for (int i = 0; i < NUM_OF_CHILDREN; i++) {
            long start = System.nanoTime();
            evolveCounter++;
            Chromosome child = reproduce(psTournament(), psTournament());

            if(r.nextFloat() < 0.5){
                child = shift(child);
            }else if(r.nextFloat() < 0.1){
                child = transposition(child);
            }
            child.calculateCost(cities);
            children.add(child);
            avgEvolveTime += System.nanoTime()-start;
        }

        Collections.sort(children);
        Chromosome.sortChromosomes(chromosomes, populationSize);

        int populationPortion = populationSize/5;

        for (int i = 1; i < populationSize-populationPortion; i++) {
            chromosomes[populationPortion+i] = children.get(i);
        }

    }

    /**
     * Select fittest parent with a tournament selection strategy
     * @return The winner of the tournament
     * rix1
     * */

    private static Chromosome psTournament(){
        long start = System.nanoTime();
        tournamentCounter++;

        Random r = new Random();
        ArrayList<Chromosome> tournament = new ArrayList<>();
        Chromosome fittest;

        if(candidates.size() < TOURNAMENT_SIZE){
            resetCandidates(); // Experimental feature
        }

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            tournament.add(candidates.get(r.nextInt(candidates.size())));
        }

        fittest = getFittest(tournament);

        candidates.remove(fittest);
        avgTournamentTime += System.nanoTime() - start;
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


    // MUTATION: Two-point exchange (Transposition)

    private static Chromosome transposition(Chromosome chrom){
        long start = System.nanoTime();
        tMutationCounter++;

        int posA = r.nextInt(cityCount);
        int posB = r.nextInt(cityCount);
        int cityA = chrom.getCity(posA);
        int cityB = chrom.getCity(posB);

        chrom.setCity(posA, cityB);
        chrom.setCity(posB, cityA);

        avgTMutationTime += System.nanoTime() - start;
        return chrom;
    }


    // MUTATAION: Three-point exchange (Shifting)

    private static Chromosome shift(Chromosome c){


        // ----- 15884 ----
        int[] rand = createRandom(2, cityCount);


        // ----- 13066 ----

        ArrayList<Integer> cities = arrayToArrayList(c.cityList);



        // ----- 1759 ----


        ArrayList<Integer> subset = new ArrayList<Integer>(cities.subList(rand[0], rand[1]));
        cities.subList(rand[0], rand[1]).clear();

        // ----- 1759 ----


        int pivot = r.nextInt(cities.size());
        cities.addAll(pivot, subset);



        // ----- 13066 ----


        c.setCities(ArrayListToInteger(cities));

        // ----- 15884 ----

        return c;
    }

    private static int[] ArrayListToInteger(ArrayList<Integer> list){
        int[] ret = new int[list.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }


    private static ArrayList<Integer> arrayToArrayList(int[] cities){
        ArrayList<Integer> ret = new ArrayList<Integer>();

        long start = System.nanoTime();
        mutationCounter++;

        // --- fore loop ~11000

        for (int i = 0; i < cities.length; i++) {
            ret.add(cities[i]);
        }

        avgMutationTime += System.nanoTime() - start;
        return ret;
    }

    private static Integer[] arrayToInteger(int[] list){
        Integer[] ret = new Integer[list.length];

        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i];
        }
        return ret;
    }


    /**
     * I need a way of creating x uniqu
     recombinationCounter++;
     * e random numbers.
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


    // REPRODUCTION

    private static Chromosome reproduce(Chromosome parent1, Chromosome parent2) {


        Chromosome child = new Chromosome(cities);

        ArrayList<Integer> all = new ArrayList<>();

        int pivot = r.nextInt(cityCount);

        Integer[] p1_temp = new Integer[pivot];
        Integer[] p2_temp = new Integer[cityCount - pivot];

        for (int i = 0; i < p1_temp.length; i++) {
            p1_temp[i] = parent1.cityList[i];
        }

        for (int i = 0; i < p2_temp.length; i++) {
            p2_temp[i] = parent2.cityList[i + pivot];
        }

        Integer[] p1 = arrayToInteger(parent1.cityList);

        Collections.addAll(all, p1_temp); // Add the first half from parent 1
        Collections.addAll(all, p2_temp); // Add the second half from parent 2
        Collections.addAll(all, p1); // Just in case...

        long start = System.nanoTime();
        recombinationCounter++;


        // THIS IS EXPENSIVE SHIT
//        List<Integer> dup =
//                new ArrayList<>(new LinkedHashSet<>(all)); // Remove all duplicates
//        Integer[] last = dup.toArray(new Integer[dup.size()]);

        child.setCities(removeDups(all));
//        child.setCities(last);

        avgRecombinationTime += System.nanoTime() - start;
        return child;
    }

    private static int[] removeDups(ArrayList<Integer> aList) {
        int[] cities = new int[cityCount];
        boolean[] inList = new boolean[cityCount];
        int index = 0;

        for (Integer i : aList){
            if(!inList[i]){
                cities[index] = i;
                index++;
                inList[i] = true;
            }
        }
        return cities;
    }


    // REPRODUCTION
    public static Chromosome reproductionSiri(Chromosome parent1, Chromosome parent2) {
        long start1 = System.nanoTime();
        recombinationCounter++;

        Chromosome child = new Chromosome(cities);
        child.clearCities();

        int start = (int) (Math.random() * parent1.cityList.length);
        int end = (int) (Math.random() * parent1.cityList.length);

        for (int i = 0; i < child.cityList.length; i++) {
            // If start is less than end
            if (start < end && i > start && i < end) {
                child.setCity(i, parent1.getCity(i));
            } // If start is larger
            else if (start > end) {
                if (!(i < start && i > end)) {
                    child.setCity(i, parent1.getCity(i));
                }
            }
        }

        for (int i = 0; i < parent1.cityList.length; i++) {
            // If child does not have the city - add it
            if (!child.containsCity(parent2.getCity(i))) {
                // Loop to find a spare position in the child's tour
                for (int j = 0; j < child.cityList.length; j++) {
                    // Spare position found, add city
                    if (child.getCity(j) == 0) {
                        child.setCity(j, parent2.getCity(i));
                        break;
                    }
                }
            }
        }
        avgRecombinationTime += System.nanoTime() - start1;

        return child;
    }







    /* =============== MAIN AND HELPER METHODS ======================= */




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

                    Chromosome.sortChromosomes(chromosomes, populationSize);

                    System.out.println(Arrays.toString(chromosomes[0].cityList));


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

                print(display, "Shift MutationTime: " + avgMutationTime/mutationCounter + " transp mutationTime:  " + avgTMutationTime /tMutationCounter + " RecombinationTime: " + avgRecombinationTime/recombinationCounter+ " TournamentTime: " + avgTournamentTime/tournamentCounter + " childGenTime: " + avgEvolveTime/evolveCounter+ "\n");
            } catch (NumberFormatException e) {
                System.out.println("Please ensure you enter integers for cities and population size");
                System.out.println(formatMessage);
            }
        }
    }


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

}

