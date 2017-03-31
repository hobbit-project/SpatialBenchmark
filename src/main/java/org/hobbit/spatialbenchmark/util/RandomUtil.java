package org.hobbit.spatialbenchmark.util;

import java.util.Random;
import java.util.UUID;
import org.hobbit.spatialbenchmark.main.Main;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * A utility class, for producing random values, strings, sentences, uris, etc.
 */
public class RandomUtil {

    private static final char[] symbols = new char[62];

    private Random randomGenerator;

    public RandomUtil() {
    }

    /**
     * Generates a random URI, using nextInt() method.
     *
     * @param baseURI
     * @param domain is the baseURI parameter
     * @param appendBrackets - appends < >
     * @param appendSuffixId - appends the string "#id" at the end, useful for
     * Creative Works URI format
     * @return the generated URI
     */
    public String randomURI(String baseURI, String domain, boolean appendBrackets, boolean appendSuffixId) {
        String nextIntStr = Integer.toString(Math.abs(randomGenerator.nextInt()));
        StringBuilder sb = new StringBuilder();

        if (appendBrackets) {
            sb.append("<");
        }

        sb.append(baseURI);
        sb.append(domain);
        sb.append("/");
        sb.append(nextIntStr);

        if (appendSuffixId) {
            sb.append("#id");
        }

        if (appendBrackets) {
            sb.append(">");
        }

        return sb.toString();
    }

    /**
     * Generates random chars, using the source URI.
     *
     * @param num
     * @return
     */
    public String randomChars(int num) {
        StringBuilder sb = new StringBuilder();

        char[] chars = "abcdefghijklmnopqrstuvwxyzABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        Random r = new Random(System.currentTimeMillis());
        char[] id = new char[num];
        for (int i = 0; i < (num); i++) {
            id[i] = chars[r.nextInt(chars.length)];
        }
        sb.append(new String(id));
        return sb.toString();
    }

    public static char pickChar() {
        String chars = "ABCDEFGHILMNOPGRSTUVZKJXYWabcdefghilmnopqrstuvzjkwxy";
        Random g = new Random();
        int index = g.nextInt(chars.length());
        return chars.charAt(index);
    }

    public String GenerateUniqueID(String URI_) {
        String URI = URI_;
        URI = URI.replaceAll("[0-9]+/*\\.*[0-9]*", generateUniqueId());
        return URI;
    }

    public static String generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return str;
    }

    public URI randomUniqueURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.normalizePath(Main.getConfigurations().getString(Configurations.NEW_URI_NAMESPACE)));
        sb.append(UUID.randomUUID().toString());

        return ValueFactoryImpl.getInstance().createURI(sb.toString());
    }

    public BNode randomUniqueBNode() {
        StringBuilder sb = new StringBuilder();
        sb.append(UUID.randomUUID().toString());

        return ValueFactoryImpl.getInstance().createBNode(sb.toString());
    }
}
