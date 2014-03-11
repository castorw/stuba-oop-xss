package ctr.stuba.xss.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Digest utilities. Adapted from XM2 essential library.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class DigestUtils {

    /**
     * Create hash from bytes. Creates a string representation from byte input
     * using desired algorithm.
     *
     * @param algorithm desired algorithm
     * @param source source byte array
     * @return hash string
     * @throws NoSuchAlgorithmException
     */
    static public String fromBytes(String algorithm, byte[] source) throws NoSuchAlgorithmException {
        MessageDigest shaMessageDigest = MessageDigest.getInstance(algorithm);
        byte[] rawHash = shaMessageDigest.digest(source);
        Formatter formatter = new Formatter();
        for (byte b : rawHash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    /**
     * Calculates MD5 checksum of file.
     *
     * @param file file to create checksum for
     * @return hash string of file
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    static public String calculateFileMd5Checksum(File file) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        MessageDigest md5md = MessageDigest.getInstance("MD5");
        InputStream fileInputStream = new FileInputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            fileInputStream = new DigestInputStream(fileInputStream, md5md);
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            }
        } finally {
            fileInputStream.close();
        }
        byte[] rawHash = md5md.digest();
        Formatter formatter = new Formatter();
        for (byte b : rawHash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
