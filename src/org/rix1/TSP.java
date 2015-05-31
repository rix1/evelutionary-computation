package org.rix1;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.util.List;

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
//            child.calculateCost(cities);
//            System.out.println("Cost b: " + child.getCost());
            if(r.nextFloat() < 0.5){
                child = shift(child);
            }
//            child = transposition(child);
            child.calculateCost(cities);
//            System.out.println("Cost a: " + child.getCost());
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

    private static ArrayList<Integer> split(int[] cities, int[] splits){
        int size = cities.length;



        return null;
    }

    private static ArrayList<Integer> getArrayList(int[] cities){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int city : cities) {
            ret.add(city);
        }
        return ret;
    }

    private static int[] getArray(ArrayList<Integer> list){
        int[] ret = new int[list.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    private static Chromosome shift(Chromosome c){

        int[] rand = createRandom(2, cityCount);
        ArrayList<Integer> cities = getArrayList(c.cityList);

        ArrayList<Integer> subset = new ArrayList<Integer>(cities.subList(rand[0], rand[1]));
        cities.subList(rand[0], rand[1]).clear();

        int pivot = r.nextInt(cities.size());
        cities.addAll(pivot, subset);

        c.setCities(getArray(cities));

        return c;
    }


    // 3-point exchange
    private static Chromosome shift2(Chromosome chrom) {

        int[] rand = createRandom(2, cityCount);
        int[] shifted = new int[rand[1]-rand[0]];

        int offset = rand[0];
        for (int i = 0; i < shifted.length; i++) {
            shifted[i] = chrom.getCity(offset);
            offset++;
        }

        int pos = r.nextInt(cityCount);

        int localPos = 0;


        for (int i = 0; i < shifted.length; i++) {
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


    // REPRODUCTION
    public static Chromosome reproduction321(Chromosome parent1, Chromosome parent2) {
        Chromosome child = new Chromosome(cities);

        int[] rand = createRandom(2, cityCount);



        return child;
    }

    private static Chromosome setCity(Chromosome parent, Chromosome child, int index) {
        int localIndex = index;
        int city = parent.getCity(localIndex);
        boolean found = false;

//        System.out.println("TRYING TO SET " + city + " at index " + index + " \nparent: " + Arrays.toString(parent.cityList) + " \nchild " + Arrays.toString(child.cityList));

        if (city == 0) {
            child.setCity(index, city);
            return child;
        } else {
            if (child.containsCity(city)) {
//                System.out.println("WOPS, child already contains city " + city);

                while(!found){
                    if(!Arrays.asList(child.cityList).contains(parent.getCity(localIndex))) {
                        child.setCity(index, parent.getCity(localIndex));
//                        System.out.println("FOUND");
                        found = true;
                        return child;
                    }else{
                        localIndex++;
                        if(localIndex >= cityCount){
                            localIndex -= cityCount;
                        }
                    }
                }return child;
            } else {
                child.setCity(index, city);
                return child;
            }
        }
    }


    private static int findNextCity(Integer[] firstHalf, int[] parent2, int pivot){
        int local = pivot;
        for (int i = 0; i < parent2.length; i++) {
            if(local >= parent2.length){
                local = 0;
            }
            if(contains(firstHalf, parent2[local])){
                local ++;
                continue;
            }else{
                return parent2[local];
            }
        }
        return 0;
    }

    private static boolean contains(Integer[] list, int number){
        for (int i = 0; i < list.length; i++) {
            if(list[i] == number){
                return true;
            }
        }
        return false;
    }

    private static Integer[] getInteger(int[] list){
        Integer[] ret = new Integer[list.length];

        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i];
        }
        return ret;
    }

    private static Chromosome reproduce(Chromosome parent1, Chromosome parent2) {

        Chromosome child = new Chromosome(cities);

        ArrayList<Integer> all = new ArrayList<>();

        int pivot = r.nextInt(cityCount);

        Integer[] p1_temp = new Integer[pivot];
        Integer[] p2_temp = new Integer[cityCount-pivot];

        for (int i = 0; i < p1_temp.length; i++) {
            p1_temp[i] = parent1.cityList[i];
        }

        for (int i = 0; i < p2_temp.length; i++) {
            p2_temp[i] = parent2.cityList[i+pivot];
        }

        Integer[] p1 = getInteger(parent1.cityList);

        Collections.addAll(all, p1_temp); // Add the first half from parent 1
        Collections.addAll(all, p2_temp); // Add the second half from parent 2
        Collections.addAll(all, p1); // Just in case...

        List<Integer> dup =
                new ArrayList<>(new LinkedHashSet<>(all)); // Remove all duplicates


        Integer[] last = dup.toArray(new Integer[dup.size()]);
        child.setCities(last);

        return  child;
    }




    private static Chromosome reproduce13(Chromosome parent1, Chromosome parent2) {
        int[] cities = parent1.cityList;

        int pivot = r.nextInt(cityCount);
        int startPoint = pivot;
        int candidateCity;

        for (int i = pivot; i < cityCount; i++) {
            candidateCity = parent2.getCity(i);

//            if(contains(candidateCity, cities, pivot)){
//                cities[i] = findNextCity();
//            }else{
//                cities[i] = candidateCity;
//            }
        }

        return null;
    }

    private static int[] getHalf(int[] cityList, int pivot){
        int[] neu = new int[pivot];

        // Copies the first half of the list.
        for (int i = 0; i < neu.length; i++) {
            neu[i] = cityList[i];
        }
        return neu;
    }


    private static Chromosome reproduce123(Chromosome parent1, Chromosome parent2) {
        Chromosome child = new Chromosome(cities);

        int pivot = r.nextInt(cityCount);
        int p2city = 0;

        for (int i = 0; i < cityCount; i++) {
            if(i < pivot){
                child.setCity(i, parent1.getCity(i));
            }else{
                p2city = parent2.getCity(i);

                int[] temp = getHalf(child.cityList, pivot);

                if(Arrays.asList(temp).contains(p2city)){
                    int rounds = i;
                    while (Arrays.asList(temp).contains(p2city)){
                        p2city = parent2.getCity(rounds);
                        if(rounds >= cityCount){
                            rounds -= cityCount;
                        }
                        rounds++;
                    }
                    child.setCity(i, parent2.getCity(rounds));
                }else{
                    child.setCity(i, p2city);
                }
            }

        }
        return child;
    }

    // Todo: Add probability stuff
    private static Chromosome reproduce2(Chromosome parent1, Chromosome parent2) {
        Chromosome child = new Chromosome(cities);
        int[] crossPoints = createRandom(CROSSOVER, cityCount);

        int crossIndex = 0;


        // n-point crossover
        for (int i = 0; i < cityCount; i++) {
//            System.out.println("CITYCOUNT: "+ i + " last crossPoint and Index " + crossPoints[crossPoints.length-1] + " : " + crossIndex);
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
//                System.out.println("\nCROSSING!\n");
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

            } catch (NumberFormatException e) {
                System.out.println("Please ensure you enter integers for cities and population size");
                System.out.println(formatMessage);
            }
        }
    }
}