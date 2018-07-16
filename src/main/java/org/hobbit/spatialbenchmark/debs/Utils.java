/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.debs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Utils {

//    public Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public Utils() {
    }

//    public Utils(Logger logger) {
//        this.logger = logger;
//    }

    public String[] readFile(Path filepath, int linesLimit) throws IOException {
        System.out.println("Reading " + filepath);

        //URL url  = Resources.getResource(filepath.toString());
        //List<String> lines = Resources.readLines(url,CHARSET);
        List<String> lines = Files.readAllLines(filepath, CHARSET);
        if (linesLimit > 0 && lines.size() > linesLimit) {
            lines = lines.subList(0, linesLimit + 1);
        } else if (linesLimit == 0) {
            lines = lines.subList(0, lines.size());
        }
        System.out.println("File reading finished ({"+lines.size()+"})");
        return lines.toArray(new String[0]);
    }

    public Map<String, Map<String, List<DataPoint>>> getTripsPerShips(String[] lines) throws Exception {
        System.out.println("Processing {"+lines.length+"} lines");
        Map<String, Map<String, List<DataPoint>>> ret = new LinkedHashMap<>();

        Map<String, List<String>> destsPerShip = new HashMap<>();

        String headLine = lines[0].replace("\uFEFF", "").toLowerCase();
        String[] separators = new String[]{"\t", ";", ","};
        int sepIndex = 0;
        String[] splitted = headLine.split(separators[sepIndex]);
        while (splitted.length == 1) {
            sepIndex++;
            splitted = headLine.split(separators[sepIndex]);
        }

        List<String> headings = Arrays.asList(splitted);
        String separator = separators[sepIndex];

        Map<String, String> prevShipDepName = new HashMap<>();
        int tuplesCount = 0;

        for (int i = 1; i < lines.length; i++) {
            //try {
            DataPoint point = new DataPoint(lines[i], headings, separator);
            String shipId = point.getValue("ship_id").toString();
//            System.out.println("shipId " +shipId);
            
            Map<String, List<DataPoint>> shipTrips = new LinkedHashMap<String, List<DataPoint>>();
            List<DataPoint> lastTrip = new ArrayList<>();
            String lastTripId = "";
            if (ret.containsKey(shipId)) {
                shipTrips = ret.get(shipId);
                lastTripId = shipTrips.keySet().toArray(new String[0])[shipTrips.size() - 1];
                lastTrip = shipTrips.get(lastTripId);
            }

            String tupleDepPortName = point.getValue("departure_port_name").toString();
            String tupleTimestamp = point.getValue("timestamp").toString();
            //String tupleArrivalTimestamp = point.getValue("arrival_timestamp").toString();
            //String tupleArrivalTimestamp = (point.containsKey("arrival_timestamp")?point.getValue("arrival_timestamp").toString(): (point.containsKey("arrival_calc") ? point.getValue("arrival_calc").toString():""));

//                if(/*!shipId.startsWith("0x43b14") || */!tupleDepPortName.startsWith("LIVORNO") /*|| !tupleTimestamp.startsWith("01-05-15 6")*/)
//                    continue;
//            String tripId = (lastTrip.size() > 0 ? lastTrip.get(0).get("timestamp") : tupleTimestamp)+"_"+tupleDepPortName;
//            String tripId = shipId.substring(0,7)+"_"+ (lastTripId.endsWith(tupleDepPortName) && lastTrip.size()>0?lastTrip.get(0).get("timestamp").split(":")[0]:tupleTimestamp.split(":")[0])+"_"+tupleDepPortName;
            String tripId = shipId.substring(0,7)+"_"+tupleDepPortName+"_"+(lastTripId.startsWith(shipId.substring(0,7)+"_"+tupleDepPortName)?shipTrips.size():shipTrips.size()+1);
//            String tripId = shipId.substring(0,7)+"_"+ tupleArrivalTimestamp.substring(0, tupleArrivalTimestamp.length()-2) +"xx_"+tupleDepPortName;
//            String tripId = point.get("trip_id");
            if(tripId==null){
                tripId = Integer.toString(i);
//                String test="123";
            }
//            System.out.println("tripId " +tripId);
            List<DataPoint> tripToAddPoint = lastTrip;
            if (!tripId.equals(lastTripId)) {
                tripToAddPoint = new ArrayList<>();
                //tripsCount++;
            }
            tripToAddPoint.add(point);
            shipTrips.put(tripId, tripToAddPoint);
            tuplesCount++;
            ret.put(shipId, shipTrips);

//                List shipDepartures = new ArrayList();
//                if (destsPerShip.containsKey(shipId)){
//                    shipDepartures = destsPerShip.get(shipId);
//                    if (!shipDepartures.get(shipDepartures.size() - 1).equals(tupleDepPortName)) {
//                        shipDepartures.add(tupleDepPortName);
//                        tripsCount++;
//                    }
//                } else {
//                    shipDepartures.add(tupleDepPortName);
//                    tripsCount++;
//                }
//
//                //shipDepartures.put(depPortName, null);
//                destsPerShip.put(shipId, shipDepartures);
//                prevShipDepName.put(shipId, tupleDepPortName);
            //}
//            catch (Exception e){
//                logger.error(e.getMessage());
//                throw e;
//            }
        }

        int tripsCount = ret.values().stream().map(list -> list.size()).mapToInt(Integer::intValue).sum();
        System.out.println("Processing finished: "+tuplesCount+" tuples, "+tripsCount+" trips ");
        return ret;
    }

    public static String encryptString(String string, String encryptionKey) {
        return string + "_encr";
    }
}
