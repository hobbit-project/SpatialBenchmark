package org.hobbit.spatialbenchmark.util;

import org.openrdf.rio.RDFFormat;

public class SesameUtils {

    public static RDFFormat parseRdfFormat(String serializationFormat) {
        RDFFormat rdfFormat = RDFFormat.TURTLE;

        if (serializationFormat.equalsIgnoreCase("BinaryRDF")) {
            rdfFormat = RDFFormat.BINARY;
        } else if (serializationFormat.equalsIgnoreCase("TriG")) {
            rdfFormat = RDFFormat.TRIG;
        } else if (serializationFormat.equalsIgnoreCase("TriX")) {
            rdfFormat = RDFFormat.TRIX;
        } else if (serializationFormat.equalsIgnoreCase("NTriples")) {
            rdfFormat = RDFFormat.NTRIPLES;
        } else if (serializationFormat.equalsIgnoreCase("N-Triples")) {
            rdfFormat = RDFFormat.NTRIPLES;
        } else if (serializationFormat.equalsIgnoreCase("NQuads")) {
            rdfFormat = RDFFormat.NQUADS;
        } else if (serializationFormat.equalsIgnoreCase("N3")) {
            rdfFormat = RDFFormat.N3;
        } else if (serializationFormat.equalsIgnoreCase("RDFXML")) {
            rdfFormat = RDFFormat.RDFXML;
        } else if (serializationFormat.equalsIgnoreCase("RDFJSON")) {
            rdfFormat = RDFFormat.RDFJSON;
        } else if (serializationFormat.equalsIgnoreCase("Turtle")) {
            rdfFormat = RDFFormat.TURTLE;
        } else {
            throw new IllegalArgumentException("Warning : unknown serialization format : " + serializationFormat + ", defaulting to Turtle");
        }

        return rdfFormat;
    }
}
