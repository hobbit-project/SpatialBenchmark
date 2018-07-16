/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.debs;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.AreaPoint;
import static org.hobbit.spatialbenchmark.data.Generator.getAtomicLong;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

/**
 *
 * @author jsaveta
 */
public class CSVtoWKT {

    private static final URI debsTrace = ValueFactoryImpl.getInstance().createURI("http://www.debs.com/ontologies/traces#Trace");
    private static final URI debsURI = ValueFactoryImpl.getInstance().createURI("http://www.debs.com/trace-data/");
    private static final URI rdfs = ValueFactoryImpl.getInstance().createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static final URI hasGeometry = ValueFactoryImpl.getInstance().createURI("http://strdf.di.uoa.gr/ontology#hasGeometry");

    private static Map<String, Map<String, List<DataPoint>>> shipTrips;
    private static List<String> shipsToSend = new ArrayList<>();
    private static final Map<String, Integer> shipTuplesCount = new HashMap<>();
    private static final Map<String, List<String>> shipTasks = new HashMap<>();
//    public static List<DataPoint> allPoints = new ArrayList<DataPoint> ();
    private static boolean sequental = true;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static Map<String, List<Coordinate>> CSVtoWKT(Path pathToFile) {
        try {
            Map<String, List<Coordinate>> traces = new HashMap<String, List<Coordinate>>();
            List<Coordinate> values = new ArrayList<Coordinate>();
            long currentFilesCount = getAtomicLong().incrementAndGet();
            String sourceFileName = String.format("./datasets/givenDatasets/debs/trace" + currentFilesCount + ".ttl");
            FileOutputStream givenFos = new FileOutputStream(sourceFileName);
            Model givenTraceModel = new LinkedHashModel();

            Utils utils = new Utils();
            String[] lines = utils.readFile(pathToFile, 0);

            shipTrips = utils.getTripsPerShips(lines);
//            String tripId = null;
//            System.out.println("shipTrips " + shipTrips.toString());

            //gia kathe trip (pare to trip ID!!! gia to URI.. krata seiriaka ta points gia na ftiakseis to trace)
            for (Map<String, List<DataPoint>> tripPoints : shipTrips.values()) {

                for (List<DataPoint> point : tripPoints.values()) {

                    for (DataPoint raw : point) {
                        System.out.println("raw " + raw.toString());

                        String shipId = raw.getValue("ship_id").toString();
                        String tupleDepPortName = raw.getValue("departure_port_name").toString();
                        String tripId = shipId.substring(0, 7) + "_" + tupleDepPortName + "_" + (shipTrips.size() + 1);

                        //fix trip id
                        System.out.println("tripId: " + tripId);
                        String[] split = raw.toString().split(",");
                        String lon = split[8];
                        String lat = split[9];

                        //tsekare an einai ontos long lat kai oxi lat long
                        double longitude = Double.parseDouble(lon);
                        double latitude = Double.parseDouble(lat);
//                        System.out.println("split[8] " + lon);
//                        System.out.println("split[9] " + lat);
                        Coordinate coordinate = new AreaPoint(longitude, latitude).getTracePoint();

                        if (!traces.containsKey(tripId) && !traces.isEmpty()) {
//                            System.out.println("if (!traces.containsKey(tripId) && !traces.isEmpty())");

                            givenTraceModel.setNamespace("trace", "http://www.debs.com/trace-data#");
                            givenTraceModel.setNamespace("prop", "http://www.debs.com/ontologies/traces#");
                            givenTraceModel.setNamespace("point", "http://www.debs.com/point#");
                            givenTraceModel.setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
                            System.out.println("givenTraceModel.size() " + givenTraceModel.size());
                            if (givenTraceModel.size() > 4) { //avoid files that contain only one point, a traces should have > 2 points
                                Rio.write(givenTraceModel, givenFos, RDFFormat.TURTLE);
//                                System.out.println("givenTraceModel------ " + givenTraceModel.toString());

                            } else {
                                new File(sourceFileName).delete();
                            }

                            values = new ArrayList<Coordinate>();
                            currentFilesCount = getAtomicLong().incrementAndGet();
                            sourceFileName = String.format("./datasets/givenDatasets/debs/trace" + currentFilesCount + ".ttl");
                            givenFos = new FileOutputStream(sourceFileName);
                            givenTraceModel = new LinkedHashModel();
                            givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.debs.com/trace-data#" + tripId), rdfs, ValueFactoryImpl.getInstance().createURI("http://www.debs.com/ontologies/traces#Trace"));

                        }
                        if (!values.contains(coordinate)) {
//                            System.out.println(" if (!values.contains(coordinate))");
//                            System.out.println("coordinate " + coordinate);
                            values.add(coordinate); //skip duplicates

                            givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.debs.com/trace-data#" + tripId), ValueFactoryImpl.getInstance().createURI("http://www.debs.com/ontologies/traces#hasPoint"), ValueFactoryImpl.getInstance().createURI("http://www.debs.com/point#" + values.size()));
                            givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.debs.com/point#" + values.size()), ValueFactoryImpl.getInstance().createURI("http://www.debs.com/ontologies/traces#lat"), ValueFactoryImpl.getInstance().createLiteral(latitude));
                            givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.debs.com/point#" + values.size()), ValueFactoryImpl.getInstance().createURI("http://www.debs.com/ontologies/traces#long"), ValueFactoryImpl.getInstance().createLiteral(longitude));

                        }
                        //ta traces pou exoun apla ena point paraleipontai etsi ki allios edo 
                        traces.put(tripId, values);
                    }

                }

            }
//            System.out.println("givenTraceModel " + givenTraceModel.toString());

            givenFos.flush();
            givenFos.close();

            File directory = new File("./datasets/givenDatasets/debs/");
            final File[] files = directory.listFiles();
            for (final File file : files) {
                System.out.println("sourceFileName " + sourceFileName);
                System.out.println("sourceFileName " + sourceFileName.isEmpty());
                if (file.length() == 0) { //avoid files that contain only one point, a traces should have > 2 points
                    file.delete();
                }
            }
            return traces;

        } catch (IOException ex) {
            Logger.getLogger(org.hobbit.spatialbenchmark.debs.CSVtoWKT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(org.hobbit.spatialbenchmark.debs.CSVtoWKT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CSVtoWKT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String[] readFile(Path filepath) throws IOException {
        System.out.println("Reading " + filepath);
        List<String> lines = Files.readAllLines(filepath, CHARSET);
        lines = lines.subList(0, lines.size());
        System.out.println("File reading finished ({}) " + lines.size());
        return lines.toArray(new String[0]);
    }

    private static void print(Map<String, List<Coordinate>> map) {
        for (Map.Entry e : map.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

    }

    public static void main(String[] args) {
        // 1000rowspublic_fixed
//        Path path = Paths.get("./datasets/vessel24hpublic_fixed.csv");
        Path path = Paths.get("./datasets/debs2018_training_fixed_7.csv");
        Map<String, List<Coordinate>> traces = CSVtoWKT(path);
        print(traces);

    }

}
