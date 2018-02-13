/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.spaten;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.AreaPoint;
import static org.hobbit.spatialbenchmark.data.Generator.getAtomicLong;
import org.joda.time.DateTimeZone;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

/**
 *
 * @author jsaveta
 */
public class CSVtoWKT {

    private static final URI spatenTrace = ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/traces#Trace");
    private static final URI spatenURI = ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/trace-data/");
    private static final URI rdfs = ValueFactoryImpl.getInstance().createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static final URI hasGeometry = ValueFactoryImpl.getInstance().createURI("http://strdf.di.uoa.gr/ontology#hasGeometry");

    private static Map<String, List<Coordinate>> CSVtoWKT(String csvPath) {

        try {

            Map<String, List<Coordinate>> traces = new HashMap<String, List<Coordinate>>();
            List<Coordinate> values = new ArrayList<Coordinate>();

            long currentFilesCount = getAtomicLong().incrementAndGet();
            String sourceFileName = String.format("./datasets/givenDatasets/spaten/trace" + currentFilesCount + ".ttl");
            FileOutputStream givenFos = new FileOutputStream(sourceFileName);
            Model givenTraceModel = new LinkedHashModel();

            Path pathToFile = Paths.get(csvPath);
            BufferedReader br = Files.newBufferedReader(pathToFile);
//            int count = 1;
            while (br.ready()) {
                String line = br.readLine();
//                System.out.println("line "+(count++)+"" +line);
                StringTokenizer tok = new StringTokenizer(line, ",", false);
                String id = tok.nextToken();
                id = id.replaceAll(" ", ""); //remove spaces
                String date = tok.nextToken().replace("\"", ""); //date format: "Sat Jan 10 18:49:14 UTC 2015"
                date = date.replace("	 ", "");
//                System.out.println("d " + date);

                String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Date theDate = sdf.parse(date);
                Calendar myCal = new GregorianCalendar();
                myCal.setTime(theDate);
                int day = myCal.get(Calendar.DAY_OF_MONTH);

//                System.out.println("Day: " + day);
                String lon = tok.nextToken();
                lon = lon.replace("(", "");
                lon = lon.replace("\"", "");
                double longitude = Double.parseDouble(lon);

                String lat = tok.nextToken();
                lat = lat.replace(")", "");
                lat = lat.replace("\"", "");
                double latitude = Double.parseDouble(lat);

                Coordinate point = new AreaPoint(longitude, latitude).getTracePoint();

                if (!traces.containsKey(id + day) && !traces.isEmpty()) {

                    try {

                        givenTraceModel.setNamespace("trace", "http://www.spaten.com/trace-data#");
                        givenTraceModel.setNamespace("prop", "http://www.spaten.com/ontologies/traces#");
                        givenTraceModel.setNamespace("point", "http://www.spaten.com/point#");
                        givenTraceModel.setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");

                        if (givenTraceModel.size() > 4) { //avoid files that contain only one point, a traces should have > 2 points
                            Rio.write(givenTraceModel, givenFos, RDFFormat.TURTLE);
                        } else {
                            new File(sourceFileName).delete();
                        }
                    } catch (RDFHandlerException ex) {
                        Logger.getLogger(CSVtoWKT.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    values = new ArrayList<Coordinate>();
                    sourceFileName = String.format("./datasets/givenDatasets/spaten/trace" + currentFilesCount + ".ttl");
                    givenFos = new FileOutputStream(sourceFileName);
                    givenTraceModel = new LinkedHashModel();

                    givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/trace-data#" + id + day), rdfs, ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/traces#Trace"));
                    currentFilesCount = getAtomicLong().incrementAndGet();

                }
                if (!values.contains(point)) {
                    values.add(point); //skip duplicates

                    givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/trace-data#" + id + day), ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/traces#hasPoint"), ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/point#" + values.size()));
                    givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/point#" + values.size()), ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/traces#lat"), ValueFactoryImpl.getInstance().createLiteral(latitude));
                    givenTraceModel.add((Resource) ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/point#" + values.size()), ValueFactoryImpl.getInstance().createURI("http://www.spaten.com/ontologies/traces#long"), ValueFactoryImpl.getInstance().createLiteral(longitude));

                }
                traces.put(id + day, values);

            }

            givenFos.flush();
            givenFos.close();

            return traces;

        } catch (IOException ex) {
            Logger.getLogger(CSVtoWKT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(CSVtoWKT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void print(Map<String, List<Coordinate>> map) {
        for (Map.Entry e : map.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

    }

    public static void main(String[] args) {
        String path = "./datasets/all_gps-traces_1-9464.csv";
        Map<String, List<Coordinate>> traces = CSVtoWKT(path);
        print(traces);

    }

}
